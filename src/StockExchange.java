import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class StockExchange {

    private List<Order> buyOrders = new ArrayList<>();
    private List<Order> sellOrders = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    public void placeBuyOrder(Order order) {
        lock.lock();
        try {
            System.out.println("\n========== Buy Order Placed ==========");
            System.out.println(order);
            System.out.println("======================================\n");
            buyOrders.add(order);
            matchOrders();
        } finally {
            lock.unlock();
        }
    }

    public void placeSellOrder(Order order) {
        lock.lock();
        try {
            System.out.println("\n========== Sell Order Placed ==========");
            System.out.println(order);
            System.out.println("=======================================\n");
            sellOrders.add(order);
            matchOrders();
        } finally {
            lock.unlock();
        }
    }

    private void matchOrders() {
        Iterator<Order> sellIterator = sellOrders.iterator();
        while (sellIterator.hasNext()) {
            Order sellOrder = sellIterator.next();

            Iterator<Order> buyIterator = buyOrders.iterator();
            while (buyIterator.hasNext()) {
                Order buyOrder = buyIterator.next();

                if (sellOrder.company.equals(buyOrder.company) && sellOrder.price == buyOrder.price) {
                    int quantityTraded = Math.min(buyOrder.quantity, sellOrder.quantity);
                    double transactionPrice = sellOrder.price;

                    System.out.println("\n****** Matching Orders ******");
                    System.out.println("Matching Buy Order: " + buyOrder);
                    System.out.println("With Sell Order: " + sellOrder);
                    System.out.println("*****************************\n");

                    Transaction transaction = new Transaction(sellOrder.company, quantityTraded, transactionPrice);
                    transactions.add(transaction);
                    System.out.println("Transaction Executed: " + transaction + "\n");

                    sellOrder.quantity -= quantityTraded;
                    buyOrder.quantity -= quantityTraded;

                    if (sellOrder.quantity == 0) {
                        System.out.println("Sell Order Fulfilled: " + sellOrder + "\n");
                        sellIterator.remove();
                    }
                    if (buyOrder.quantity == 0) {
                        System.out.println("Buy Order Fulfilled: " + buyOrder + "\n");
                        buyIterator.remove();
                    }

                } else {
                    System.out.println("No Match for Buy: " + buyOrder + " and Sell: " + sellOrder + "\n");
                }
            }
        }
    }

    public void displayOrderBook() {
        lock.lock();
        try {
            System.out.println("\n========== ORDER BOOK ==========");
            System.out.println("Buy Orders:");
            for (Order order : buyOrders) {
                System.out.println(order);
            }
            System.out.println("\nSell Orders:");
            for (Order order : sellOrders) {
                System.out.println(order);
            }
            System.out.println("================================\n");
        } finally {
            lock.unlock();
        }
    }

    public void displayTransactions() {
        lock.lock();
        try {
            System.out.println("\n========== TRANSACTION HISTORY ==========");
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
            System.out.println("=========================================\n");
        } finally {
            lock.unlock();
        }
    }
}
