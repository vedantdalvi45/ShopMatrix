package com.ved.ShopMatrix.service;

import com.ved.ShopMatrix.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories();
    CategoryDto createCategory(CategoryDto categoryDto);
    CategoryDto updateCategory(String categoryId, CategoryDto categoryDto);
    void deleteCategory(String categoryId);
}
