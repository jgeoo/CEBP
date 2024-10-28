import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
public class StockExchange {
    // Concurrent queues to hold buy and sell orders (thread-safe for multiple clients)
    private final ConcurrentLinkedQueue<Order> buyOrders = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Order> sellOrders = new ConcurrentLinkedQueue<>();

    // List to keep a history of all completed transactions
    private final List<Transaction> transactions = new ArrayList<>();

    // Lock and condition for coordinating matching thread with order placement
    private final ReentrantLock matchLock = new ReentrantLock();
    private final Condition newOrderCondition = matchLock.newCondition();

    // Flag to control the lifecycle of the matching thread
    private volatile boolean running = true;

    // OrderLockManager handles individual locks for each order by ID (avoids whole-system locks)
    private final OrderLockManager lockManager = new OrderLockManager();

    // Constructor starts the matcher thread, which continuously checks for matching orders
    public StockExchange() {
        Thread matcherThread = new Thread(this::runMatcher);
        matcherThread.start();
    }

    // Method to place a buy order and notify the matcher thread of new order activity
    public void placeBuyOrder(Order order) {
        buyOrders.offer(order); // Adds order to buy queue
        System.out.println("Placed Buy Order: " + order);
        notifyMatcher(); // Signals the matcher thread to check for possible transactions
    }

    // Method to place a sell order and notify matcher similarly
    public void placeSellOrder(Order order) {
        sellOrders.offer(order);
        System.out.println("Placed Sell Order: " + order);
        notifyMatcher();
    }

    // Notify matcher to wake up and process orders
    private void notifyMatcher() {
        matchLock.lock();
        try {
            newOrderCondition.signal(); // Signals matcher that a new order has been placed
        } finally {
            matchLock.unlock();
        }
    }

    // Main loop for matching orders, executed continuously by the matcher thread
    private void runMatcher() {
        while (running) {
            matchOrders(); // Attempt to match available buy/sell orders
            try {
                // Wait for new orders if no matching transactions are found
                matchLock.lock();
                newOrderCondition.await(); // Blocks until a new order is placed
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status if interrupted
            } finally {
                matchLock.unlock();
            }
        }
    }

    // Attempts to match buy and sell orders for transaction execution
    private void matchOrders() {
        boolean matchFound;
        do {
            matchFound = false;
            Iterator<Order> sellIterator = sellOrders.iterator();

            while (sellIterator.hasNext()) {
                Order sellOrder = sellIterator.next();
                Iterator<Order> buyIterator = buyOrders.iterator();

                while (buyIterator.hasNext()) {
                    Order buyOrder = buyIterator.next();

                    // Check if the company and price match for a possible transaction
                    if (sellOrder.company.equals(buyOrder.company) && sellOrder.price == buyOrder.price) {
                        // Lock individual orders to prevent concurrent modifications during transaction
                        ReentrantLock sellLock = lockManager.getLock(sellOrder.orderId);
                        ReentrantLock buyLock = lockManager.getLock(buyOrder.orderId);

                        sellLock.lock();
                        buyLock.lock();
                        try {
                            // Double-check quantity in case it was modified by another process
                            int quantityTraded = Math.min(buyOrder.quantity, sellOrder.quantity);
                            if (quantityTraded > 0) { // Only proceed if quantities match for a trade
                                double transactionPrice = sellOrder.price;

                                // Create a new transaction and add to transaction history
                                Transaction transaction = new Transaction(sellOrder.company, quantityTraded, transactionPrice);
                                transactions.add(transaction);
                                System.out.println("Executed Transaction: " + transaction);

                                // Adjust remaining quantities for buy and sell orders
                                sellOrder.quantity -= quantityTraded;
                                buyOrder.quantity -= quantityTraded;

                                // Remove orders if quantity reaches zero
                                if (sellOrder.quantity <= 0) {
                                    sellIterator.remove();
                                }
                                if (buyOrder.quantity <= 0) {
                                    buyIterator.remove();
                                }

                                matchFound = true; // Signal that a match was found
                            }
                        } finally {
                            // Release locks for both orders after transaction
                            sellLock.unlock();
                            buyLock.unlock();
                            lockManager.releaseLock(sellOrder.orderId);
                            lockManager.releaseLock(buyOrder.orderId);
                        }
                    }
                    if (matchFound) {
                        break; // Exit buy iterator loop to restart matching if a match was found
                    }
                }
                if (matchFound) {
                    break; // Exit sell iterator loop similarly
                }
            }
        } while (matchFound); // Continue matching while there are transactions to process
    }

    // Stop matcher by setting running flag to false and notifying matcher thread
    public void stopMatcher() {
        running = false;
        notifyMatcher(); // Ensure matcher stops waiting on condition variable
    }

    // Placeholder function for order transaction check (adjust based on further transaction management)
    private boolean isOrderInTransaction(Order order) {
        return false; // Currently not in use, can be implemented as needed
    }

    // Update an order's quantity or price, ensuring it’s not currently in a transaction
    public void updateOrder(Order updatedOrder) {
        if (isOrderInTransaction(updatedOrder)) { // Prevent modification during active transaction
            System.out.println("Cannot update order, as it is currently in a transaction: " + updatedOrder);
            return;
        }

        // Check and update buy orders
        for (Order order : buyOrders) {
            if (order.company.equals(updatedOrder.company)) { // Match by company
                order.quantity = updatedOrder.quantity;
                order.price = updatedOrder.price;
                System.out.println("Updated Buy Order: " + order);
                return;
            }
        }

        // Check and update sell orders
        for (Order order : sellOrders) {
            if (order.company.equals(updatedOrder.company)) {
                order.quantity = updatedOrder.quantity;
                order.price = updatedOrder.price;
                System.out.println("Updated Sell Order: " + order);
                return;
            }
        }

        System.out.println("Order not found for update: " + updatedOrder); // If order doesn't exist
    }
}