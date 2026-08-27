package com.ecommerce.jaime.service;

import com.ecommerce.jaime.dto.CategoryDtos;
import com.ecommerce.jaime.exceptions.BadRequestException;
import com.ecommerce.jaime.exceptions.ResourceNotFoundException;
import com.ecommerce.jaime.model.Category;
import com.ecommerce.jaime.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDtos.CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDtos.CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el id: " + id));
        return mapToResponse(category);
    }

    @Transactional
    public CategoryDtos.CategoryResponse createCategory(CategoryDtos.CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException("Ya existe una categoría con el nombre: " + request.getName());
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    @Transactional
    public CategoryDtos.CategoryResponse updateCategory(Long id, CategoryDtos.CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el id: " + id));

        if (!category.getName().equalsIgnoreCase(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException("Ya existe una categoría con el nombre: " + request.getName());
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        return mapToResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el id: " + id));

        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new BadRequestException("No se puede eliminar la categoría porque contiene productos asociados");
        }

        categoryRepository.delete(category);
    }

    private CategoryDtos.CategoryResponse mapToResponse(Category category) {
        return CategoryDtos.CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .productCount(category.getProducts() != null ? category.getProducts().size() : 0)
                .build();
    }
}
