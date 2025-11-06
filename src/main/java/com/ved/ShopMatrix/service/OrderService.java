package com.ved.ShopMatrix.service;

import com.ved.ShopMatrix.dto.OrderDto;

import java.util.List;

public interface OrderService {
    // Customer
    OrderDto placeOrder(String username);
    List<OrderDto> getUserOrders(String username);
    OrderDto getOrderById(String username, String orderId);

    // Admin
    List<OrderDto> getAllOrders();
    OrderDto updateOrderStatus(String orderId, String status);
}
