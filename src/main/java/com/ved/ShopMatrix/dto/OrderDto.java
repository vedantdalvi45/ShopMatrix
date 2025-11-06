package com.ved.ShopMatrix.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


@Data
public class OrderDto {
    private String id;
    private String userId;
    private Instant createdAt;
    private String status;
    private List<OrderItemDto> items;
    private BigDecimal total;
}
