package com.ved.ShopMatrix.service;

import com.ved.ShopMatrix.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> getAllProducts();
    ProductDto getProductById(String productId);
    List<ProductDto> getProductsByCategoryId(String categoryId);
    ProductDto createProduct(ProductDto productDto);
    ProductDto updateProduct(String productId, ProductDto productDto);
    void deleteProduct(String productId);
}
