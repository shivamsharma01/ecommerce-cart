package com.mcart.cart.dto;

import java.util.List;

public record CartResponse(
        String userId,
        List<CartItemResponse> items
) {
}

