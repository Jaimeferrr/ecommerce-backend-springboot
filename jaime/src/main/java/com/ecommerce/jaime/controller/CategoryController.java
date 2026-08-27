package com.ecommerce.jaime.controller;

import com.ecommerce.jaime.dto.CategoryDtos;
import com.ecommerce.jaime.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Endpoints para la gestión del catálogo de categorías")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Obtener todas las categorías", description = "Devuelve el listado completo de categorías disponibles.")
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<CategoryDtos.CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @Operation(summary = "Obtener categoría por ID", description = "Devuelve el detalle de una categoría específica.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CategoryDtos.CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Operation(summary = "Crear categoría (ADMIN)", description = "Permite a un usuario con rol ADMIN registrar una nueva categoría.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDtos.CategoryResponse> createCategory(@Valid @RequestBody CategoryDtos.CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @Operation(summary = "Actualizar categoría (ADMIN)", description = "Permite a un usuario con rol ADMIN modificar una categoría existente.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDtos.CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDtos.CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @Operation(summary = "Eliminar categoría (ADMIN)", description = "Permite a un usuario con rol ADMIN eliminar una categoría si no tiene productos asociados.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}