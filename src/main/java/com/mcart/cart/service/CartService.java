package com.mcart.cart.service;

import com.mcart.cart.dto.CartItemRequest;
import com.mcart.cart.dto.CartItemResponse;
import com.mcart.cart.dto.CartResponse;
import com.mcart.cart.dto.InventoryResponse;
import com.mcart.cart.entity.CartItemEntity;
import com.mcart.cart.exception.CartValidationException;
import com.mcart.cart.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final InventoryClient inventoryClient;

    @Value("${cart.inventory.check-on-write:true}")
    private boolean checkInventoryOnWrite;

    @Transactional(readOnly = true)
    public CartResponse getCart(UUID userId) {
        List<CartItemResponse> items = cartItemRepository.findByUserIdOrderByUpdatedAtDesc(userId)
                .stream()
                .map(e -> new CartItemResponse(e.getProductId(), e.getQuantity()))
                .toList();
        log.debug("Cart loaded userId={} lineCount={}", userId, items.size());
        return new CartResponse(userId.toString(), items);
    }

    @Transactional
    public CartResponse upsertItem(UUID userId, CartItemRequest req, String authorizationHeader) {
        String productId = req.productId().trim();
        if (productId.isEmpty()) throw new CartValidationException("productId is required");
        if (req.quantity() <= 0) throw new CartValidationException("quantity must be >= 1");

        if (checkInventoryOnWrite) {
            InventoryResponse inv = inventoryClient.getInventory(productId, authorizationHeader);
            if (inv == null) {
                throw new CartValidationException("Inventory unavailable for productId=" + productId);
            }
            if (inv.availableQty() < req.quantity()) {
                throw new CartValidationException("Out of stock (available " + inv.availableQty() + ")");
            }
        }

        CartItemEntity entity = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseGet(CartItemEntity::new);
        entity.setUserId(userId);
        entity.setProductId(productId);
        entity.setQuantity(req.quantity());
        entity.setUpdatedAt(Instant.now());
        cartItemRepository.save(entity);

        log.info("Cart line upserted userId={} productId={} quantity={}", userId, productId, req.quantity());
        return getCart(userId);
    }

    @Transactional
    public void removeItem(UUID userId, String productId) {
        if (productId == null || productId.isBlank()) return;
        cartItemRepository.deleteByUserIdAndProductId(userId, productId.trim());
        log.info("Cart line removed userId={} productId={}", userId, productId.trim());
    }

    @Transactional
    public void clear(UUID userId) {
        cartItemRepository.deleteByUserId(userId);
        log.info("Cart cleared userId={}", userId);
    }
}

