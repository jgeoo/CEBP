package com.stock.stock.services;


import com.stock.stock.models.Order;
import com.stock.stock.repositories.OrderRepository;
import jakarta.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockExchangeService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TransactionRepository transactionRepository;

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
        List<Order> buyOrders = orderRepository.findByCompanyAndIsBuyOrder(company, true);
        List<Order> sellOrders = orderRepository.findByCompanyAndIsBuyOrder(company, false);

        buyOrders.sort((o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice())); // Descending
        sellOrders.sort((o1, o2) -> Double.compare(o1.getPrice(), o2.getPrice())); // Ascending

        for (Order buyOrder : buyOrders) {
            for (Order sellOrder : sellOrders) {
                if (buyOrder.getPrice() >= sellOrder.getPrice() && buyOrder.getQuantity() > 0 && sellOrder.getQuantity() > 0) {
                    int quantityTraded = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());

                    Transaction transaction = new Transaction(company, quantityTraded, sellOrder.getPrice());
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