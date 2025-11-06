package com.ved.ShopMatrix.repository;


import com.ved.ShopMatrix.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    // Finds all orders placed by a specific user
    List<Order> findByUserId(String userId);
}