package com.example.order.controller;

import com.example.order.dto.OrderRequest;
import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import com.example.order.model.OrderStatus;
import com.example.order.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void createOrder_Success() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setUserId("1");
        List<OrderRequest.OrderItemRequest> items = new ArrayList<>();
        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setItemId("1");
        item.setQuantity(2);
        items.add(item);
        request.setItems(items);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("1"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].itemId").value("1"))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void getOrdersByUserId_Success() throws Exception {
        // Create a test order
        Order order = new Order();
        order.setUserId("1");
        order.setStatus(OrderStatus.PENDING);
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setItemId("1");
        item.setQuantity(2);
        item.setPrice(new BigDecimal("10.00"));
        items.add(item);
        order.setItems(items);
        orderRepository.save(order);

        mockMvc.perform(get("/api/orders/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value("1"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void getOrderById_Success() throws Exception {
        // Create a test order
        Order order = new Order();
        order.setUserId("1");
        order.setStatus(OrderStatus.PENDING);
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setItemId("1");
        item.setQuantity(2);
        item.setPrice(new BigDecimal("10.00"));
        items.add(item);
        order.setItems(items);
        Order savedOrder = orderRepository.save(order);

        mockMvc.perform(get("/api/orders/" + savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId()))
                .andExpect(jsonPath("$.userId").value("1"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getOrderById_NotFound() throws Exception {
        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOrderStatus_Success() throws Exception {
        // Create a test order
        Order order = new Order();
        order.setUserId("1");
        order.setStatus(OrderStatus.PENDING);
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setItemId("1");
        item.setQuantity(2);
        item.setPrice(new BigDecimal("10.00"));
        items.add(item);
        order.setItems(items);
        Order savedOrder = orderRepository.save(order);

        mockMvc.perform(put("/api/orders/" + savedOrder.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"CONFIRMED\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void updateOrderStatus_NotFound() throws Exception {
        mockMvc.perform(put("/api/orders/999/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"CONFIRMED\""))
                .andExpect(status().isNotFound());
    }
} 