package com.example.item.service;

import com.example.item.dto.ItemRequest;
import com.example.item.dto.ItemResponse;
import com.example.item.exception.ItemNotFoundException;
import com.example.item.model.Item;
import com.example.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
    private final ItemRepository itemRepository;

    public ItemResponse createItem(ItemRequest request) {
        logger.info("Creating new item with name: {}", request.getName());
        try {
            Item item = Item.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .price(request.getPrice())
                    .stockQuantity(request.getStockQuantity())
                    .build();
            
            Item savedItem = itemRepository.save(item);
            logger.debug("Item created successfully with ID: {}", savedItem.getId());
            return mapToItemResponse(savedItem);
        } catch (Exception e) {
            logger.error("Error creating item: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create item: " + e.getMessage());
        }
    }

    @Cacheable(value = "items", key = "'all'")
    public List<ItemResponse> getAllItems() {
        logger.debug("Fetching all items");
        try {
            List<ItemResponse> items = itemRepository.findAll().stream()
                    .map(this::mapToItemResponse)
                    .collect(Collectors.toList());
            logger.debug("Found {} items", items.size());
            return items;
        } catch (Exception e) {
            logger.error("Error fetching all items: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch items: " + e.getMessage());
        }
    }

    @Cacheable(value = "items", key = "#id")
    public ItemResponse getItemById(String id) {
        logger.debug("Fetching item with ID: {}", id);
        try {
            Item item = itemRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Item not found with ID: {}", id);
                        return new ItemNotFoundException("Item not found with ID: " + id);
                    });
            logger.debug("Item found: {}", item.getName());
            return mapToItemResponse(item);
        } catch (ItemNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching item with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch item: " + e.getMessage());
        }
    }

    @CacheEvict(value = "items", allEntries = true)
    public ItemResponse updateItem(String id, ItemRequest request) {
        logger.info("Updating item with ID: {}", id);
        try {
            Item item = itemRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Item not found for update with ID: {}", id);
                        return new ItemNotFoundException("Item not found with ID: " + id);
                    });
            
            item.setName(request.getName());
            item.setDescription(request.getDescription());
            item.setPrice(request.getPrice());
            item.setStockQuantity(request.getStockQuantity());
            
            Item updatedItem = itemRepository.save(item);
            logger.debug("Item updated successfully: {}", updatedItem.getName());
            return mapToItemResponse(updatedItem);
        } catch (ItemNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating item with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update item: " + e.getMessage());
        }
    }

    @CacheEvict(value = "items", allEntries = true)
    public void deleteItem(String id) {
        logger.info("Deleting item with ID: {}", id);
        try {
            if (!itemRepository.existsById(id)) {
                logger.warn("Item not found for deletion with ID: {}", id);
                throw new ItemNotFoundException("Item not found with ID: " + id);
            }
            itemRepository.deleteById(id);
            logger.debug("Item deleted successfully with ID: {}", id);
        } catch (ItemNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting item with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete item: " + e.getMessage());
        }
    }

    @CacheEvict(value = "items", allEntries = true)
    public ItemResponse updateStock(String id, Integer quantity) {
        logger.info("Updating stock for item ID: {} with quantity: {}", id, quantity);
        try {
            Item item = itemRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Item not found for stock update with ID: {}", id);
                        return new ItemNotFoundException("Item not found with ID: " + id);
                    });
            
            if (item.getStockQuantity() + quantity < 0) {
                logger.warn("Insufficient stock for item ID: {}. Current stock: {}, Requested change: {}", 
                    id, item.getStockQuantity(), quantity);
                throw new RuntimeException("Insufficient stock");
            }
            
            item.setStockQuantity(item.getStockQuantity() + quantity);
            Item updatedItem = itemRepository.save(item);
            logger.debug("Stock updated successfully for item: {}. New stock: {}", 
                updatedItem.getName(), updatedItem.getStockQuantity());
            return mapToItemResponse(updatedItem);
        } catch (ItemNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating stock for item ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update stock: " + e.getMessage());
        }
    }

    private ItemResponse mapToItemResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .stockQuantity(item.getStockQuantity())
                .build();
    }
} 