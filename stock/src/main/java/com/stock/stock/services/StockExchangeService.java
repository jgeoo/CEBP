package com.stock.stock.services;

import com.stock.stock.models.Order;
import com.stock.stock.models.Transaction;
import com.stock.stock.repositories.OrderRepository;
import com.stock.stock.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.stock.stock.utils.OrderLockManager;

import java.util.*;
import java.util.concurrent.*;

@Service
public class StockExchangeService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // Thread-safe maps for buy and sell orders
    private final Map<String, TreeMap<Double, ConcurrentLinkedQueue<Order>>> buyOrders = new ConcurrentHashMap<>();
    private final Map<String, TreeMap<Double, ConcurrentLinkedQueue<Order>>> sellOrders = new ConcurrentHashMap<>();

    private final ExecutorService matcherExecutor = Executors.newSingleThreadExecutor();
    private volatile boolean running = true;

    public StockExchangeService() {
        // Start a background thread to process matches
        matcherExecutor.submit(this::runMatcher);
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
        // Just signal the matcher to check for new orders
    }

    private void runMatcher() {
        while (running) {
            matchOrders();
        }
    }

    private void matchOrders() {
        boolean matchFound;
        do {
            matchFound = false;

            for (String company : buyOrders.keySet()) {
                TreeMap<Double, ConcurrentLinkedQueue<Order>> companyBuyOrders = buyOrders.get(company);
                TreeMap<Double, ConcurrentLinkedQueue<Order>> companySellOrders = sellOrders.get(company);

                if (companyBuyOrders == null || companySellOrders == null) continue;

                Map.Entry<Double, ConcurrentLinkedQueue<Order>> highestBuyEntry = companyBuyOrders.firstEntry();
                Map.Entry<Double, ConcurrentLinkedQueue<Order>> lowestSellEntry = companySellOrders.firstEntry();

                if (highestBuyEntry == null || lowestSellEntry == null) continue;

                double buyPrice = highestBuyEntry.getKey();
                double sellPrice = lowestSellEntry.getKey();

                if (buyPrice >= sellPrice) {
                    Order buyOrder = highestBuyEntry.getValue().peek();
                    Order sellOrder = lowestSellEntry.getValue().peek();

                    if (buyOrder != null && sellOrder != null) {
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
                    }
                }
            }
        } while (matchFound);
    }

    public void stopMatcher() {
        running = false;
        matcherExecutor.shutdownNow(); // Shut down the executor gracefully
    }
}
