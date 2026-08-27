package com.ecommerce.jaime.controller;

import com.ecommerce.jaime.dto.ProductDtos;
import com.ecommerce.jaime.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Endpoints para la gestión de productos")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Obtener todos los productos", description = "Devuelve catálogo completo de productos registrados")
    @ApiResponse(responseCode = "200", description = "Lista de objetos recuperada con éxito")
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ProductDtos.ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Operation(summary = "Obtener producto por ID", description = "Busca un producto específico mediante su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ProductDtos.ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Obtener productos con stock bajo (ADMIN)", description = "Filtra y devuelve aquellos productos cuyo stock actual es menor o igual al stock mínimo configurado.")
    @ApiResponse(responseCode = "200", description = "Lista de alertas de stock recuperada con éxito")
    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductDtos.ProductResponse>> getLowStockProducts() {
        return ResponseEntity.ok(productService.getLowStockProducts());
    }

    @Operation(summary = "Crear nuevo producto (ADMIN)", description = "Registra un producto en el catálogo. Requiere SKU único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o SKU duplicado")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDtos.ProductResponse> createProduct(@Valid @RequestBody ProductDtos.ProductRequest request) {
        ProductDtos.ProductResponse created = productService.createProduct(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un producto (ADMIN)", description = "Modifica los datos o el stock de un producto existente por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado con éxito"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDtos.ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDtos.ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(request, id));
    }

    @Operation(summary = "Eliminar un producto (ADMIN)", description = "Elimina un producto del inventario mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}