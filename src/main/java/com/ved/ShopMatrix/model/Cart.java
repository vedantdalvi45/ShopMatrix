package com.ved.ShopMatrix.model;


import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class Cart {
    private List<CartItem> items = new ArrayList<>();
}
