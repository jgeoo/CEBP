package com.stock.stock.repositories;

import com.stock.stock.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCompanyAndBuyOrder(String company, boolean isBuyOrder);
}