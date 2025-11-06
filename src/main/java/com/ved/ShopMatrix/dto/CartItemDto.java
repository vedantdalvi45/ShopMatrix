package com.ved.ShopMatrix.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDto {
    private String productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
    private String imageUrl;
}
