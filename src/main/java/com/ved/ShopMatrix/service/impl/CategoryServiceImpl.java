package com.ved.ShopMatrix.service.impl;

import com.ved.ShopMatrix.dto.CategoryDto;
import com.ved.ShopMatrix.model.Category;
import com.ved.ShopMatrix.repository.CategoryRepository;
import com.ved.ShopMatrix.service.CategoryService;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = toEntity(categoryDto);
        category = categoryRepository.save(category);
        return toDto(category);
    }

    @Override
    public CategoryDto updateCategory(String categoryId, CategoryDto categoryDto) {
        Category existing = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        BeanUtils.copyProperties(categoryDto, existing, "id");
        existing = categoryRepository.save(existing);
        return toDto(existing);
    }

    @Override
    public void deleteCategory(String categoryId) {
        // Add logic here to check if products are using this category
        categoryRepository.deleteById(categoryId);
    }

    private CategoryDto toDto(Category category) {
        CategoryDto dto = new CategoryDto();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }

    private Category toEntity(CategoryDto dto) {
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        return category;
    }
}
