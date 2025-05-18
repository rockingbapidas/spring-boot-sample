package com.example.item.controller;

import com.example.item.dto.ItemRequest;
import com.example.item.dto.ItemResponse;
import com.example.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody ItemRequest request) {
        logger.info("Received request to create item: {}", request.getName());
        ItemResponse response = itemService.createItem(request);
        logger.debug("Item created successfully with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        logger.info("Received request to get all items");
        List<ItemResponse> items = itemService.getAllItems();
        logger.debug("Retrieved {} items", items.size());
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable String id) {
        logger.info("Received request to get item with ID: {}", id);
        ItemResponse item = itemService.getItemById(id);
        logger.debug("Retrieved item: {}", item.getName());
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponse> updateItem(
            @PathVariable String id,
            @Valid @RequestBody ItemRequest request) {
        logger.info("Received request to update item with ID: {}", id);
        ItemResponse response = itemService.updateItem(id, request);
        logger.debug("Item updated successfully: {}", response.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable String id) {
        logger.info("Received request to delete item with ID: {}", id);
        itemService.deleteItem(id);
        logger.debug("Item deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ItemResponse> updateStock(
            @PathVariable String id,
            @RequestParam Integer quantity) {
        logger.info("Received request to update stock for item ID: {} with quantity: {}", id, quantity);
        ItemResponse response = itemService.updateStock(id, quantity);
        logger.debug("Stock updated successfully for item: {}. New stock: {}", 
            response.getName(), response.getStockQuantity());
        return ResponseEntity.ok(response);
    }
} 