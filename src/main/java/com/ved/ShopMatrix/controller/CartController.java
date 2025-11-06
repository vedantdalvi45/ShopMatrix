package com.ved.ShopMatrix.controller;


import com.ved.ShopMatrix.dto.CartDto;
import com.ved.ShopMatrix.dao.request.CartAddRequest;
import com.ved.ShopMatrix.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    private final CartService cartService;

    private String getUsername(Principal principal) {
        return principal.getName();
    }

    @GetMapping
    public ResponseEntity<CartDto> getMyCart(Principal principal) {
        return ResponseEntity.ok(cartService.getCart(getUsername(principal)));
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<CartDto> addProductToCart(
            @PathVariable String productId,
            @RequestBody CartAddRequest request,
            Principal principal) {
        return ResponseEntity.ok(cartService.addProductToCart(getUsername(principal), productId, request));
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<CartDto> updateQuantity(
            @PathVariable String productId,
            @RequestParam int quantity, // As per your API design
            Principal principal) {
        return ResponseEntity.ok(cartService.updateCartItemQuantity(getUsername(principal), productId, quantity));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartDto> removeProduct(
            @PathVariable String productId,
            Principal principal) {
        return ResponseEntity.ok(cartService.removeProductFromCart(getUsername(principal), productId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<CartDto> clearCart(Principal principal) {
        return ResponseEntity.ok(cartService.clearCart(getUsername(principal)));
    }
}
