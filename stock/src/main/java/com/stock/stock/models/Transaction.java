package com.stock.stock.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "transactions")

public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String company;
    private int quantity;
    private double price;

    public Transaction(String company, int quantity, double price) {
        this.company = company;
        this.quantity = quantity;
        this.price = price;
    }
    @ManyToOne
    @JsonBackReference( value = "transactions-user")
    private User user;
}