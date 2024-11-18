package com.stock.stock.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.concurrent.atomic.AtomicLong;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String company;
    private int quantity;
    private double price;
    private boolean isBuyOrder;
}