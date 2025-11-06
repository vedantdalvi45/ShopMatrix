package com.ved.ShopMatrix.controller;

import com.ved.ShopMatrix.dto.OrderDto;
import com.ved.ShopMatrix.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class OrderController {

    private final OrderService orderService;

    private String getUsername(Principal principal) {
        return principal.getName();
    }

    @PostMapping("/place")
    public ResponseEntity<OrderDto> placeOrder(Principal principal) {
        return new ResponseEntity<>(orderService.placeOrder(getUsername(principal)), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getMyOrders(Principal principal) {
        return ResponseEntity.ok(orderService.getUserOrders(getUsername(principal)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getMyOrderById(@PathVariable String orderId, Principal principal) {
        return ResponseEntity.ok(orderService.getOrderById(getUsername(principal), orderId));
    }
}
