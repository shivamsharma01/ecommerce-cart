package com.mcart.cart.service;

import com.mcart.cart.dto.InventoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class InventoryClient {

    private final RestClient restClient = RestClient.create();

    @Value("${cart.inventory.base-url}")
    private String inventoryBaseUrl;

    public InventoryResponse getInventory(String productId) {
        ResponseEntity<InventoryResponse> r = restClient
                .get()
                .uri(inventoryBaseUrl + "/inventory/" + productId)
                .retrieve()
                .toEntity(InventoryResponse.class);
        return r.getBody();
    }
}

