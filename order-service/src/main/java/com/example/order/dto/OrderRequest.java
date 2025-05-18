package com.example.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    @NotNull(message = "User ID is required")
    private String userId;
    
    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemRequest> items;
    
    @Data
    public static class OrderItemRequest {
        @NotNull(message = "Item ID is required")
        private String itemId;
        
        @NotNull(message = "Quantity is required")
        private Integer quantity;
    }
} 