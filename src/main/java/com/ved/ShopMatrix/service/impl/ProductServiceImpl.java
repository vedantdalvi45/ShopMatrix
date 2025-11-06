package com.ved.ShopMatrix.service.impl;

import com.ved.ShopMatrix.dto.ProductDto;

import com.ved.ShopMatrix.model.Product;
import com.ved.ShopMatrix.repository.ProductRepository;
import com.ved.ShopMatrix.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    // private final CategoryRepository categoryRepository; // For validation

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductById(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toDto(product);
    }

    @Override
    public List<ProductDto> getProductsByCategoryId(String categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        // categoryRepository.findById(productDto.getCategoryId())
        //         .orElseThrow(() -> new RuntimeException("Category not found"));
        Product product = toEntity(productDto);
        product = productRepository.save(product);
        return toDto(product);
    }

    @Override
    public ProductDto updateProduct(String productId, ProductDto productDto) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        BeanUtils.copyProperties(productDto, existingProduct, "id");
        existingProduct = productRepository.save(existingProduct);
        return toDto(existingProduct);
    }

    @Override
    public void deleteProduct(String productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(productId);
    }

    // --- Helper Methods ---
    private ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        BeanUtils.copyProperties(product, dto);
        return dto;
    }

    private Product toEntity(ProductDto dto) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        return product;
    }
}
