package com.stock.stock.repositories;

import com.stock.stock.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Use 'IsBuyOrder' to match the field in the entity class
    List<Order> findByCompanyAndIsBuyOrder(String company, boolean isBuyOrder);
    List<Order> findByIsBuyOrder(boolean isBuyOrder);
}
