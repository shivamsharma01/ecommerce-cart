package com.mcart.cart.repository;

import com.mcart.cart.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, UUID> {
    List<CartItemEntity> findByUserIdOrderByUpdatedAtDesc(UUID userId);
    Optional<CartItemEntity> findByUserIdAndProductId(UUID userId, String productId);
    long deleteByUserId(UUID userId);
    long deleteByUserIdAndProductId(UUID userId, String productId);
}

