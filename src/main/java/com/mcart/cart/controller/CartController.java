package com.mcart.cart.controller;

import com.mcart.cart.dto.CartItemRequest;
import com.mcart.cart.dto.CartResponse;
import com.mcart.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.mcart.cart.config.OpenApiConfig.BEARER_JWT;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "Cart")
public class CartController {

    private static final String CLAIM_USER_ID = "userId";

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get current user's cart")
    @SecurityRequirement(name = BEARER_JWT)
    public ResponseEntity<CartResponse> get(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaimAsString(CLAIM_USER_ID));
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/items")
    @Operation(summary = "Add/update cart item")
    @SecurityRequirement(name = BEARER_JWT)
    public ResponseEntity<CartResponse> upsert(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CartItemRequest req) {
        UUID userId = UUID.fromString(jwt.getClaimAsString(CLAIM_USER_ID));
        return ResponseEntity.ok(cartService.upsertItem(userId, req));
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove item from cart")
    @SecurityRequirement(name = BEARER_JWT)
    public ResponseEntity<Void> remove(@AuthenticationPrincipal Jwt jwt, @PathVariable String productId) {
        UUID userId = UUID.fromString(jwt.getClaimAsString(CLAIM_USER_ID));
        cartService.removeItem(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/clear")
    @Operation(summary = "Clear cart")
    @SecurityRequirement(name = BEARER_JWT)
    public ResponseEntity<Void> clear(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaimAsString(CLAIM_USER_ID));
        cartService.clear(userId);
        return ResponseEntity.noContent().build();
    }
}

