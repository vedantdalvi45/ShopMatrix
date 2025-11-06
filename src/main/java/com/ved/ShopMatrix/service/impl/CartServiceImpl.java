package com.ved.ShopMatrix.service.impl;

// The import for CartRepository has been removed
import com.ved.ShopMatrix.dto.CartDto;
import com.ved.ShopMatrix.dto.CartItemDto;
import com.ved.ShopMatrix.dao.request.CartAddRequest; // Using your package path
import com.ved.ShopMatrix.model.Cart;
import com.ved.ShopMatrix.model.CartItem;
import com.ved.ShopMatrix.model.Product;
import com.ved.ShopMatrix.model.User;
import com.ved.ShopMatrix.repository.ProductRepository;
import com.ved.ShopMatrix.repository.UserRepository;
import com.ved.ShopMatrix.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    // private final CartRepository cartRepository; // <-- This is removed

    @Override
    public CartDto getCart(String username) {
        User user = findUser(username);
        return toDto(user.getCart());
    }

    @Override
    public CartDto addProductToCart(String username, String productId, CartAddRequest request) {
        User user = findUser(username);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Not enough stock");
        }

        Cart cart = user.getCart();
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem(
                    product.getId(),
                    product.getName(),
                    request.getQuantity(),
                    product.getPrice(),
                    product.getImageUrl()
            );
            cart.getItems().add(newItem);
        }

        // --- FIX ---
        // Save the User, which contains the updated cart
        userRepository.save(user);
        return toDto(cart);
    }

    @Override
    public CartDto updateCartItemQuantity(String username, String productId, int quantity) {
        if (quantity <= 0) {
            return removeProductFromCart(username, productId);
        }

        User user = findUser(username);
        Cart cart = user.getCart();

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not in cart"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getStock() < quantity) {
            throw new RuntimeException("Not enough stock");
        }

        item.setQuantity(quantity);

        // --- FIX ---
        userRepository.save(user); // Save the User
        return toDto(cart);
    }

    @Override
    public CartDto removeProductFromCart(String username, String productId) {
        User user = findUser(username);
        Cart cart = user.getCart();
        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        // --- FIX ---
        if (removed) {
            userRepository.save(user); // Save the User
        }
        return toDto(cart);
    }

    @Override
    public CartDto clearCart(String username) {
        User user = findUser(username);
        Cart cart = user.getCart();
        cart.getItems().clear(); // Clear items from the cart

        // --- FIX ---
        userRepository.save(user); // Save the User
        return toDto(cart);
    }

    // --- Helper Methods (Unchanged) ---
    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private CartDto toDto(Cart cart) {
        CartDto dto = new CartDto();
        dto.setItems(cart.getItems().stream().map(this::toDto).collect(Collectors.toList()));
        dto.setSubtotal(calculateSubtotal(cart));
        return dto;
    }

    private CartItemDto toDto(CartItem item) {
        CartItemDto dto = new CartItemDto();
        BeanUtils.copyProperties(item, dto);
        return dto;
    }

    private BigDecimal calculateSubtotal(Cart cart) {
        return cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}