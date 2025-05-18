package com.example.order.dto;

import com.example.order.model.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String userId;
    private OrderStatus status;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    public static class OrderItemResponse {
        private Long id;
        private String itemId;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subtotal;
    }
} 