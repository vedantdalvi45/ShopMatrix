package com.ved.ShopMatrix.dto;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDto {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private String categoryId;
    private String imageUrl;
}
