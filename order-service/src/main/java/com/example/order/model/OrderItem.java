package com.example.order.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String itemId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    
    @PrePersist
    @PreUpdate
    protected void calculateSubtotal() {
        if (price != null && quantity != null) {
            subtotal = price.multiply(BigDecimal.valueOf(quantity));
        }
    }
} 