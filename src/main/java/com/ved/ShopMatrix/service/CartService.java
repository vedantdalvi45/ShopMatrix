package com.ved.ShopMatrix.service;


import com.ved.ShopMatrix.dao.request.CartAddRequest;
import com.ved.ShopMatrix.dto.CartDto;


public interface CartService {
    CartDto getCart(String username);
    CartDto addProductToCart(String username, String productId, CartAddRequest request);
    CartDto updateCartItemQuantity(String username, String productId, int quantity);
    CartDto removeProductFromCart(String username, String productId);
    CartDto clearCart(String username);
}
