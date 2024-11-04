import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class StockExchange {
    // Buy and Sell maps organized by company and price for faster lookups
    private final Map<String, TreeMap<Double, Queue<Order>>> buyOrders = new ConcurrentHashMap<>();
    private final Map<String, TreeMap<Double, Queue<Order>>> sellOrders = new ConcurrentHashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();

    private final ReentrantLock matchLock = new ReentrantLock();
    private final Condition newOrderCondition = matchLock.newCondition();
    private volatile boolean running = true;

    private final OrderLockManager lockManager = new OrderLockManager();

    public StockExchange() {
        Thread matcherThread = new Thread(this::runMatcher);
        matcherThread.start();
    }

    public void placeBuyOrder(Order order) {
        buyOrders
                .computeIfAbsent(order.company, k -> new TreeMap<>(Collections.reverseOrder())) // Descending order for buys
                .computeIfAbsent(order.price, k -> new ConcurrentLinkedQueue<>())
                .offer(order);
        System.out.println("Placed Buy Order: " + order);
        notifyMatcher();
    }

    public void placeSellOrder(Order order) {
        sellOrders
                .computeIfAbsent(order.company, k -> new TreeMap<>()) // Ascending order for sells
                .computeIfAbsent(order.price, k -> new ConcurrentLinkedQueue<>())
                .offer(order);
        System.out.println("Placed Sell Order: " + order);
        notifyMatcher();
    }

    private void notifyMatcher() {
        matchLock.lock();
        try {
            newOrderCondition.signal();
        } finally {
            matchLock.unlock();
        }
    }

    private void runMatcher() {
        while (running) {
            matchOrders();
            try {
                matchLock.lock();
                newOrderCondition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                matchLock.unlock();
            }
        }
    }

    private void matchOrders() {
        boolean matchFound;
        do {
            matchFound = false;

            for (String company : buyOrders.keySet()) {
                TreeMap<Double, Queue<Order>> companyBuyOrders = buyOrders.get(company);
                TreeMap<Double, Queue<Order>> companySellOrders = sellOrders.get(company);

                if (companyBuyOrders == null || companySellOrders == null) continue;

                Map.Entry<Double, Queue<Order>> highestBuyEntry = companyBuyOrders.firstEntry();
                Map.Entry<Double, Queue<Order>> lowestSellEntry = companySellOrders.firstEntry();

                if (highestBuyEntry == null || lowestSellEntry == null) continue;

                double buyPrice = highestBuyEntry.getKey();
                double sellPrice = lowestSellEntry.getKey();

                if (buyPrice >= sellPrice) {
                    Order buyOrder = highestBuyEntry.getValue().peek();
                    Order sellOrder = lowestSellEntry.getValue().peek();

                    if (buyOrder != null && sellOrder != null) {
                        ReentrantLock sellLock = lockManager.getLock(sellOrder.orderId);
                        ReentrantLock buyLock = lockManager.getLock(buyOrder.orderId);

                        sellLock.lock();
                        buyLock.lock();
                        try {
                            int quantityTraded = Math.min(buyOrder.quantity, sellOrder.quantity);
                            if (quantityTraded > 0) {
                                double transactionPrice = sellPrice;

                                Transaction transaction = new Transaction(sellOrder.company, quantityTraded, transactionPrice);
                                transactions.add(transaction);
                                System.out.println("Executed Transaction: " + transaction);

                                sellOrder.quantity -= quantityTraded;
                                buyOrder.quantity -= quantityTraded;

                                if (sellOrder.quantity <= 0) {
                                    lowestSellEntry.getValue().poll();
                                    if (lowestSellEntry.getValue().isEmpty()) {
                                        companySellOrders.remove(sellPrice);
                                    }
                                }
                                if (buyOrder.quantity <= 0) {
                                    highestBuyEntry.getValue().poll();
                                    if (highestBuyEntry.getValue().isEmpty()) {
                                        companyBuyOrders.remove(buyPrice);
                                    }
                                }

                                matchFound = true;
                            }
                        } finally {
                            sellLock.unlock();
                            buyLock.unlock();
                            lockManager.releaseLock(sellOrder.orderId);
                            lockManager.releaseLock(buyOrder.orderId);
                        }
                    }
                }
            }
        } while (matchFound);
    }

    public void stopMatcher() {
        running = false;
        notifyMatcher();
    }
}
