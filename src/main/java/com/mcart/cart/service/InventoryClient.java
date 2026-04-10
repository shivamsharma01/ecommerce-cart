package com.mcart.cart.service;

import com.mcart.cart.dto.InventoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class InventoryClient {

    private final RestClient restClient = RestClient.create();

    @Value("${cart.inventory.base-url}")
    private String inventoryBaseUrl;

    /**
     * Inventory is a JWT-protected resource server; forward the caller's bearer token (same issuer as cart).
     */
    public InventoryResponse getInventory(String productId, String authorizationHeader) {
        var req = restClient
                .get()
                .uri(inventoryBaseUrl + "/inventory/" + productId);
        if (StringUtils.hasText(authorizationHeader)) {
            req = req.header(HttpHeaders.AUTHORIZATION, authorizationHeader);
        }
        ResponseEntity<InventoryResponse> r = req.retrieve().toEntity(InventoryResponse.class);
        return r.getBody();
    }
}

