package com.mcart.cart.dto;

public record CartItemResponse(
        String productId,
        int quantity
) {
}

