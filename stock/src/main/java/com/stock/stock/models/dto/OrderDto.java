package com.stock.stock.models.dto;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class OrderDto {
    private String company;
    private int quantity;
    private double price;

}