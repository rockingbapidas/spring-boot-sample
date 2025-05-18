package com.example.order.service;

import com.example.order.dto.OrderRequest;
import com.example.order.dto.OrderResponse;
import com.example.order.model.OrderStatus;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderResponse getOrderById(Long id);
    List<OrderResponse> getOrdersByUserId(String userId);
    OrderResponse updateOrderStatus(Long id, OrderStatus status);
} 