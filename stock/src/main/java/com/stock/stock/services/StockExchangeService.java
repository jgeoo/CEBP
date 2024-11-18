package com.stock.stock.services;


import com.stock.stock.models.Order;
import com.stock.stock.models.Transaction;
import com.stock.stock.repositories.OrderRepository;
import com.stock.stock.repositories.TransactionRepository;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class StockExchangeService {
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;

    public StockExchangeService(OrderRepository orderRepository, TransactionRepository transactionRepository) {
        this.orderRepository = orderRepository;
        this.transactionRepository = transactionRepository;
    }

    public void placeBuyOrder(Order order) {
        order.setBuyOrder(true);
        orderRepository.save(order);
        matchOrders(order.getCompany());
    }

    public void placeSellOrder(Order order) {
        order.setBuyOrder(false);
        orderRepository.save(order);
        matchOrders(order.getCompany());
    }

    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    private void matchOrders(String company) {
        List<Order> buyOrders = orderRepository.findByCompanyAndBuyOrder(company, true);
        List<Order> sellOrders = orderRepository.findByCompanyAndBuyOrder(company, false);

        buyOrders.sort((o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice())); // Descending
        sellOrders.sort(Comparator.comparingDouble(Order::getPrice)); // Ascending

        for (Order buyOrder : buyOrders) {
            for (Order sellOrder : sellOrders) {
                if (buyOrder.getPrice() >= sellOrder.getPrice() && buyOrder.getQuantity() > 0 && sellOrder.getQuantity() > 0) {
                    int quantityTraded = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());

                    Transaction transaction = Transaction.builder()
                            .company(company)
                            .quantity(quantityTraded)
                            .price(sellOrder.getPrice())
                            .build();

                    transactionRepository.save(transaction);

                    buyOrder.setQuantity(buyOrder.getQuantity() - quantityTraded);
                    sellOrder.setQuantity(sellOrder.getQuantity() - quantityTraded);

                    if (buyOrder.getQuantity() == 0) orderRepository.delete(buyOrder);
                    if (sellOrder.getQuantity() == 0) orderRepository.delete(sellOrder);

                    break;
                }
            }
        }
    }
}