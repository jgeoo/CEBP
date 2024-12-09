package com.stock.stock.services;

import com.stock.stock.models.Order;
import com.stock.stock.models.Transaction;
import com.stock.stock.repositories.OrderRepository;
import com.stock.stock.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.stock.stock.utils.OrderLockManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class StockExchangeService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // Thread-safe maps for buy and sell orders
    private final Map<String, TreeMap<Double, Queue<Order>>> buyOrders = new ConcurrentHashMap<>();
    private final Map<String, TreeMap<Double, Queue<Order>>> sellOrders = new ConcurrentHashMap<>();

    private final ReentrantLock matchLock = new ReentrantLock();
    private final Condition newOrderCondition = matchLock.newCondition();
    private volatile boolean running = true;

    private final OrderLockManager lockManager = new OrderLockManager();

    public StockExchangeService() {
        // Start a background thread to process matches
        Thread matcherThread = new Thread(this::runMatcher);
        matcherThread.start();
    }

    public void placeBuyOrder(Order order) {
        order.setBuyOrder(true);
        orderRepository.save(order); // Save order to database
        buyOrders
                .computeIfAbsent(order.getCompany(), k -> new TreeMap<>(Collections.reverseOrder())) // Descending order
                .computeIfAbsent(order.getPrice(), k -> new ConcurrentLinkedQueue<>())
                .offer(order);
        System.out.println("Placed Buy Order: " + order);
        notifyMatcher();
    }

    public void placeSellOrder(Order order) {
        order.setBuyOrder(false);
        orderRepository.save(order); // Save order to database
        sellOrders
                .computeIfAbsent(order.getCompany(), k -> new TreeMap<>()) // Ascending order
                .computeIfAbsent(order.getPrice(), k -> new ConcurrentLinkedQueue<>())
                .offer(order);
        System.out.println("Placed Sell Order: " + order);
        notifyMatcher();
    }

    public List<Transaction> getTransactions() {
        return transactionRepository.findAll(); // Retrieve transactions from the database
    }

    private void notifyMatcher() {
        matchLock.lock();
        try {
            newOrderCondition.signal(); // Signal the matcher thread
        } finally {
            matchLock.unlock();
        }
    }

    private void runMatcher() {
        while (running) {
            matchOrders();
            try {
                matchLock.lock();
                newOrderCondition.await(); // Wait for new orders
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
                        ReentrantLock sellLock = lockManager.getLock(sellOrder.getId());
                        ReentrantLock buyLock = lockManager.getLock(buyOrder.getId());

                        sellLock.lock();
                        buyLock.lock();
                        try {
                            int quantityTraded = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());
                            if (quantityTraded > 0) {
                                // Save the transaction
                                Transaction transaction = new Transaction(sellOrder.getCompany(), quantityTraded, sellPrice);
                                transactionRepository.save(transaction);
                                System.out.println("Executed Transaction: " + transaction);

                                // Update order quantities
                                sellOrder.setQuantity(sellOrder.getQuantity() - quantityTraded);
                                buyOrder.setQuantity(buyOrder.getQuantity() - quantityTraded);

                                if (sellOrder.getQuantity() <= 0) {
                                    lowestSellEntry.getValue().poll();
                                    if (lowestSellEntry.getValue().isEmpty()) {
                                        companySellOrders.remove(sellPrice);
                                    }
                                    orderRepository.delete(sellOrder); // Delete fulfilled sell order
                                } else {
                                    orderRepository.save(sellOrder); // Update partial sell order
                                }

                                if (buyOrder.getQuantity() <= 0) {
                                    highestBuyEntry.getValue().poll();
                                    if (highestBuyEntry.getValue().isEmpty()) {
                                        companyBuyOrders.remove(buyPrice);
                                    }
                                    orderRepository.delete(buyOrder); // Delete fulfilled buy order
                                } else {
                                    orderRepository.save(buyOrder); // Update partial buy order
                                }

                                matchFound = true;
                            }
                        } finally {
                            sellLock.unlock();
                            buyLock.unlock();
                            lockManager.releaseLock(sellOrder.getId());
                            lockManager.releaseLock(buyOrder.getId());
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

    public List<Order> getBuyOrders() {
        return orderRepository.findByIsBuyOrder(Boolean.TRUE);
    }

    public List<Order> getSellOrders() {
        return orderRepository.findByIsBuyOrder(Boolean.FALSE);
    }
}
