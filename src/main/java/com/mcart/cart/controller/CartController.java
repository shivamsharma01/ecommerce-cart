package com.mcart.cart.controller;

import com.mcart.cart.dto.CartItemRequest;
import com.mcart.cart.dto.CartResponse;
import com.mcart.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private static final String CLAIM_USER_ID = "userId";

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> get(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaimAsString(CLAIM_USER_ID));
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> upsert(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Valid @RequestBody CartItemRequest req
    ) {
        UUID userId = UUID.fromString(jwt.getClaimAsString(CLAIM_USER_ID));
        return ResponseEntity.ok(cartService.upsertItem(userId, req, authorization));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal Jwt jwt, @PathVariable String productId) {
        UUID userId = UUID.fromString(jwt.getClaimAsString(CLAIM_USER_ID));
        cartService.removeItem(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/clear")
    public ResponseEntity<Void> clear(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaimAsString(CLAIM_USER_ID));
        cartService.clear(userId);
        return ResponseEntity.noContent().build();
    }
}
