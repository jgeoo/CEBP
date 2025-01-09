package com.stock.stock.services;

import com.stock.stock.models.Order;
import com.stock.stock.models.Transaction;
import com.stock.stock.models.User;
import com.stock.stock.models.dto.OrderDto;
import com.stock.stock.repositories.OrderRepository;
import com.stock.stock.repositories.TransactionRepository;
import com.stock.stock.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
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

    @Autowired
    private UserRepository userRepository;
    private final Map<String, TreeMap<Double, Queue<Order>>> buyOrders = new ConcurrentHashMap<>();
    private final Map<String, TreeMap<Double, Queue<Order>>> sellOrders = new ConcurrentHashMap<>();

    private final ReentrantLock matchLock = new ReentrantLock();
    private final Condition newOrderCondition = matchLock.newCondition();
    private volatile boolean running = true;

    private final OrderLockManager lockManager = new OrderLockManager();

    @PostConstruct
    public void initMatcherThread() {
        // Start a background thread to process matches
        Thread matcherThread = new Thread(this::runMatcher);
        matcherThread.start();
    }


    public void placeBuyOrder(OrderDto orderDto, Long userId) {
        // Convert OrderDto to Order entity
        Order order = new Order();
        order.setCompany(orderDto.getCompany());
        order.setQuantity(orderDto.getQuantity());
        order.setPrice(orderDto.getPrice());
        order.setBuyOrder(true);


        // Fetch the user by userId
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            order.setUser(user.get()); // Set the user in the Order entity
        } else {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        // Save the order to the database
        orderRepository.save(order);

        // Add the order to the in-memory buy orders
        buyOrders
                .computeIfAbsent(order.getCompany(), k -> new TreeMap<>(Collections.reverseOrder())) // Descending order
                .computeIfAbsent(order.getPrice(), k -> new ConcurrentLinkedQueue<>())
                .offer(order);

        System.out.println("Placed Buy Order: " + order);

        // Notify matcher to match orders
        notifyMatcher();
    }


    public void placeSellOrder(OrderDto orderDto, Long userId) {
        // Convert OrderDto to Order entity
        Order order = new Order();
        order.setCompany(orderDto.getCompany());
        order.setQuantity(orderDto.getQuantity());
        order.setPrice(orderDto.getPrice());
        order.setBuyOrder(false); // It's a sell order

        // Fetch the user by userId
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            order.setUser(user.get()); // Set the user in the Order entity
        } else {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        // Save the order to the database
        orderRepository.save(order);

        // Add the order to the in-memory sell orders (ascending order by price)
        sellOrders
                .computeIfAbsent(order.getCompany(), k -> new TreeMap<>()) // Ascending order
                .computeIfAbsent(order.getPrice(), k -> new ConcurrentLinkedQueue<>())
                .offer(order);

        System.out.println("Placed Sell Order: " + order);

        // Notify matcher to match orders
        notifyMatcher();
    }

    public List<Order> getBuyOrders() {
        return orderRepository.findByIsBuyOrder(Boolean.TRUE);
    }

    public List<Order> getSellOrders() {
        return orderRepository.findByIsBuyOrder(Boolean.FALSE);
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

            // Fetch all buy and sell orders from the database
            List<Order> buyOrdersFromDB = getBuyOrders();
            List<Order> sellOrdersFromDB = getSellOrders();

            // Group and sort orders for matching
            Map<String, List<Order>> groupedBuyOrders = new HashMap<>();
            Map<String, List<Order>> groupedSellOrders = new HashMap<>();

            buyOrdersFromDB.forEach(order ->
                    groupedBuyOrders.computeIfAbsent(order.getCompany(), k -> new ArrayList<>()).add(order)
            );
            sellOrdersFromDB.forEach(order ->
                    groupedSellOrders.computeIfAbsent(order.getCompany(), k -> new ArrayList<>()).add(order)
            );

            for (String company : groupedBuyOrders.keySet()) {
                List<Order> companyBuyOrders = groupedBuyOrders.get(company);
                List<Order> companySellOrders = groupedSellOrders.getOrDefault(company, new ArrayList<>());

                // Sort buy orders in descending price order
                companyBuyOrders.sort((o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice()));

                // Sort sell orders in ascending price order
                companySellOrders.sort(Comparator.comparingDouble(Order::getPrice));

                if (companyBuyOrders.isEmpty() || companySellOrders.isEmpty()) continue;

                // Try matching orders
                while (!companyBuyOrders.isEmpty() && !companySellOrders.isEmpty()) {
                    Order buyOrder = companyBuyOrders.get(0);
                    Order sellOrder = companySellOrders.get(0);

                    if (buyOrder.getPrice() >= sellOrder.getPrice()) {
                        // Lock both orders
                        ReentrantLock sellLock = lockManager.getLock(sellOrder.getId());
                        ReentrantLock buyLock = lockManager.getLock(buyOrder.getId());

                        sellLock.lock();
                        buyLock.lock();
                        try {
                            int quantityTraded = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());
                            if (quantityTraded > 0) {
                                // Save the transaction
                                Transaction transaction = new Transaction(sellOrder.getCompany(), quantityTraded, sellOrder.getPrice());
                                transactionRepository.save(transaction);

                                // Update order quantities
                                sellOrder.setQuantity(sellOrder.getQuantity() - quantityTraded);
                                buyOrder.setQuantity(buyOrder.getQuantity() - quantityTraded);

                                if (sellOrder.getQuantity() <= 0) {
                                    companySellOrders.remove(sellOrder);
                                    orderRepository.delete(sellOrder);
                                } else {
                                    orderRepository.save(sellOrder);
                                }

                                if (buyOrder.getQuantity() <= 0) {
                                    companyBuyOrders.remove(buyOrder);
                                    orderRepository.delete(buyOrder);
                                } else {
                                    orderRepository.save(buyOrder);
                                }

                                matchFound = true;
                            }
                        } finally {
                            sellLock.unlock();
                            buyLock.unlock();
                            lockManager.releaseLock(sellOrder.getId());
                            lockManager.releaseLock(buyOrder.getId());
                        }
                    } else {
                        break; // No match is possible
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
