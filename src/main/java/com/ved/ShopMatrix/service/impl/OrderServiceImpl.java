package com.ved.ShopMatrix.service.impl;


import com.ved.ShopMatrix.dto.OrderDto;
import com.ved.ShopMatrix.dto.OrderItemDto;
import com.ved.ShopMatrix.model.*;
import com.ved.ShopMatrix.repository.OrderRepository;
import com.ved.ShopMatrix.repository.ProductRepository;
import com.ved.ShopMatrix.repository.UserRepository;
import com.ved.ShopMatrix.service.OrderService;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional // This is critical. If stock update fails, the order should not be placed.
    public OrderDto placeOrder(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = user.getCart();

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUserId(user.getId());
        order.setStatus("PENDING");

        BigDecimal total = BigDecimal.ZERO;

        // Convert CartItems to OrderItems and check stock
        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProductName()));

            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for: " + product.getName());
            }

            // Decrease stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            return new OrderItem(
                    cartItem.getProductId(),
                    cartItem.getProductName(),
                    cartItem.getQuantity(),
                    cartItem.getPrice()
            );
        }).collect(Collectors.toList());

        order.setItems(orderItems);

        // Calculate total
        order.setTotal(orderItems.stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        // Save the order
        Order savedOrder = orderRepository.save(order);

        // Clear the user's cart
        user.setCart(new Cart());
        userRepository.save(user);

        return toDto(savedOrder);
    }

    @Override
    public List<OrderDto> getUserOrders(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return orderRepository.findByUserId(user.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderById(String username, String orderId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied"); // Not this user's order
        }
        return toDto(order);
    }

    // --- Admin Methods ---

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto updateOrderStatus(String orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status); // Add validation for status value
        Order updatedOrder = orderRepository.save(order);
        return toDto(updatedOrder);
    }

    // --- Helper Methods ---
    private OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        BeanUtils.copyProperties(order, dto, "items");
        dto.setItems(order.getItems().stream().map(this::toDto).collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDto toDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        BeanUtils.copyProperties(item, dto);
        return dto;
    }
}