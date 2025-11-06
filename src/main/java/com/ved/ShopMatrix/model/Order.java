package com.ved.ShopMatrix.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String userId; // Reference to the User
    private Instant createdAt = Instant.now();
    private String status; // e.g., "PENDING", "SHIPPED", "DELIVERED"
    private List<OrderItem> items;
    private BigDecimal total;
    // You can add shippingAddress here
}
