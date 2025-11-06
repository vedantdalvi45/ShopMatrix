package com.ved.ShopMatrix.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private String productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
}
