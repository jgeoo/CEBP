package com.stock.stock.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String company;
    private int quantity;
    private double price;
    private boolean isBuyOrder;

    @ManyToOne
    @JsonBackReference(value = "user-orders")  // Changed to match User class
    private User user;
}