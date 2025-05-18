package com.example.item.controller;

import com.example.item.dto.ItemRequest;
import com.example.item.model.Item;
import com.example.item.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
    }

    @Test
    void createItem_Success() {
        ItemRequest request = new ItemRequest();
        request.setName("Test Item");
        request.setDescription("Test Description");
        request.setPrice(new BigDecimal("10.00"));
        request.setStockQuantity(100);

        ResponseEntity<Map> response = restTemplate.postForEntity("/api/items", request, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("name")).isEqualTo("Test Item");
        assertThat(response.getBody().get("description")).isEqualTo("Test Description");
        assertThat(response.getBody().get("price")).isEqualTo(10.00);
        assertThat(response.getBody().get("stockQuantity")).isEqualTo(100);
    }

    @Test
    void getAllItems_Success() {
        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setPrice(new BigDecimal("10.00"));
        item1.setStockQuantity(100);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(new BigDecimal("20.00"));
        item2.setStockQuantity(200);
        itemRepository.save(item2);

        ResponseEntity<Map[]> response = restTemplate.getForEntity("/api/items", Map[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void getItemById_Success() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setPrice(new BigDecimal("10.00"));
        item.setStockQuantity(100);
        Item savedItem = itemRepository.save(item);

        ResponseEntity<Map> response = restTemplate.getForEntity("/api/items/" + savedItem.getId(), Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("id")).isEqualTo(savedItem.getId());
        assertThat(response.getBody().get("name")).isEqualTo("Test Item");
        assertThat(response.getBody().get("description")).isEqualTo("Test Description");
        assertThat(response.getBody().get("price")).isEqualTo(10.00);
        assertThat(response.getBody().get("stockQuantity")).isEqualTo(100);
    }

    @Test
    void getItemById_NotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/items/999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateItem_Success() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setPrice(new BigDecimal("10.00"));
        item.setStockQuantity(100);
        Item savedItem = itemRepository.save(item);

        ItemRequest updateRequest = new ItemRequest();
        updateRequest.setName("Updated Item");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPrice(new BigDecimal("20.00"));
        updateRequest.setStockQuantity(200);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ItemRequest> entity = new HttpEntity<>(updateRequest, headers);
        ResponseEntity<Map> response = restTemplate.exchange("/api/items/" + savedItem.getId(), HttpMethod.PUT, entity, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("name")).isEqualTo("Updated Item");
        assertThat(response.getBody().get("description")).isEqualTo("Updated Description");
        assertThat(response.getBody().get("price")).isEqualTo(20.00);
        assertThat(response.getBody().get("stockQuantity")).isEqualTo(200);
    }

    @Test
    void updateItem_NotFound() {
        ItemRequest updateRequest = new ItemRequest();
        updateRequest.setName("Updated Item");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPrice(new BigDecimal("20.00"));
        updateRequest.setStockQuantity(200);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ItemRequest> entity = new HttpEntity<>(updateRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/items/999", HttpMethod.PUT, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteItem_Success() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setPrice(new BigDecimal("10.00"));
        item.setStockQuantity(100);
        Item savedItem = itemRepository.save(item);

        ResponseEntity<Void> response = restTemplate.exchange("/api/items/" + savedItem.getId(), HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/api/items/" + savedItem.getId(), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteItem_NotFound() {
        ResponseEntity<String> response = restTemplate.exchange("/api/items/999", HttpMethod.DELETE, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateStock_Success() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setPrice(new BigDecimal("10.00"));
        item.setStockQuantity(100);
        Item savedItem = itemRepository.save(item);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("50", headers);
        ResponseEntity<Map> response = restTemplate.exchange("/api/items/" + savedItem.getId() + "/stock", HttpMethod.PUT, entity, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("stockQuantity")).isEqualTo(50);
    }

    @Test
    void updateStock_NotFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("50", headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/items/999/stock", HttpMethod.PUT, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
} 