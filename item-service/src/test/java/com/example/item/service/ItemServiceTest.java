package com.example.item.service;

import com.example.item.dto.ItemRequest;
import com.example.item.dto.ItemResponse;
import com.example.item.model.Item;
import com.example.item.repository.ItemRepository;
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
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    void setUp() {
        itemRequest = new ItemRequest();
        itemRequest.setName("Test Item");
        itemRequest.setDescription("Test Description");
        itemRequest.setPrice(new BigDecimal("10.00"));
        itemRequest.setStockQuantity(100);

        item = new Item();
        item.setId("1");
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setPrice(new BigDecimal("10.00"));
        item.setStockQuantity(100);
    }

    @Test
    void createItem_Success() {
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemResponse response = itemService.createItem(itemRequest);

        assertNotNull(response);
        assertEquals(item.getId(), response.getId());
        assertEquals(item.getName(), response.getName());
        assertEquals(item.getDescription(), response.getDescription());
        assertEquals(item.getPrice(), response.getPrice());
        assertEquals(item.getStockQuantity(), response.getStockQuantity());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void getAllItems_Success() {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item));

        List<ItemResponse> responses = itemService.getAllItems();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(item.getId(), responses.get(0).getId());
        verify(itemRepository).findAll();
    }

    @Test
    void getItemById_Success() {
        when(itemRepository.findById(anyString())).thenReturn(Optional.of(item));

        ItemResponse response = itemService.getItemById("1");

        assertNotNull(response);
        assertEquals(item.getId(), response.getId());
        assertEquals(item.getName(), response.getName());
        verify(itemRepository).findById("1");
    }

    @Test
    void getItemById_NotFound() {
        when(itemRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> itemService.getItemById("1"));
    }

    @Test
    void updateItem_Success() {
        when(itemRepository.findById(anyString())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemResponse response = itemService.updateItem("1", itemRequest);

        assertNotNull(response);
        assertEquals(item.getId(), response.getId());
        assertEquals(item.getName(), response.getName());
        verify(itemRepository).findById("1");
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_NotFound() {
        when(itemRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> itemService.updateItem("1", itemRequest));
    }

    @Test
    void deleteItem_Success() {
        when(itemRepository.existsById(anyString())).thenReturn(true);
        doNothing().when(itemRepository).deleteById(anyString());

        itemService.deleteItem("1");

        verify(itemRepository).existsById("1");
        verify(itemRepository).deleteById("1");
    }

    @Test
    void deleteItem_NotFound() {
        when(itemRepository.existsById(anyString())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> itemService.deleteItem("1"));
        verify(itemRepository, never()).deleteById(anyString());
    }

    @Test
    void updateStock_Success() {
        when(itemRepository.findById(anyString())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemResponse response = itemService.updateStock("1", 50);

        assertNotNull(response);
        assertEquals(150, response.getStockQuantity());
        verify(itemRepository).findById("1");
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateStock_NotFound() {
        when(itemRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> itemService.updateStock("1", 50));
    }
} 