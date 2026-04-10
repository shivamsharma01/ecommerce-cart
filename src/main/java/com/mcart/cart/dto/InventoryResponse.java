package com.mcart.cart.dto;

public record InventoryResponse(
        String productId,
        int availableQty
) {
}

