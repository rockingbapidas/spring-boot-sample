package com.example.order.service;

import com.example.order.dto.OrderRequest;
import com.example.order.dto.OrderResponse;
import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import com.example.order.model.OrderStatus;
import com.example.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private OrderRequest orderRequest;
    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        orderRequest = new OrderRequest();
        orderRequest.setUserId("user123");
        OrderRequest.OrderItemRequest itemRequest = new OrderRequest.OrderItemRequest();
        itemRequest.setItemId("1");
        itemRequest.setQuantity(2);
        orderRequest.setItems(Arrays.asList(itemRequest));

        order = new Order();
        order.setId(1L);
        order.setUserId("user123");
        order.setStatus(OrderStatus.PENDING);

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setItemId("1");
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("10.00"));
        order.setItems(Arrays.asList(orderItem));
        order.setTotalAmount(new BigDecimal("20.00"));
    }

    @Test
    void createOrder_Success() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse response = orderService.createOrder(orderRequest);

        assertNotNull(response);
        assertEquals(order.getId(), response.getId());
        assertEquals(order.getUserId(), response.getUserId());
        assertEquals(order.getStatus(), response.getStatus());
        assertEquals(order.getTotalAmount(), response.getTotalAmount());
        assertEquals(1, response.getItems().size());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void getOrdersByUserId_Success() {
        when(orderRepository.findByUserId(anyString())).thenReturn(Arrays.asList(order));

        List<OrderResponse> responses = orderService.getOrdersByUserId("user123");

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(order.getId(), responses.get(0).getId());
        verify(orderRepository).findByUserId("user123");
    }

    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(1L);

        assertNotNull(response);
        assertEquals(order.getId(), response.getId());
        assertEquals(order.getUserId(), response.getUserId());
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_NotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void updateOrderStatus_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse response = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        assertNotNull(response);
        assertEquals(OrderStatus.CONFIRMED, response.getStatus());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_NotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED));
    }
} 