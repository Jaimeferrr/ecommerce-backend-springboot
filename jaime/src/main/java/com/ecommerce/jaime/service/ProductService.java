package com.ecommerce.jaime.service;

import com.ecommerce.jaime.dto.ProductDtos;
import com.ecommerce.jaime.exceptions.BadRequestException;
import com.ecommerce.jaime.exceptions.ResourceNotFoundException;
import com.ecommerce.jaime.model.Category;
import com.ecommerce.jaime.model.Product;
import com.ecommerce.jaime.repository.CategoryRepository;
import com.ecommerce.jaime.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductDtos.ProductResponse createProduct(ProductDtos.ProductRequest request) {
        if(productRepository.existsBySku(request.getSku())) {
            throw new BadRequestException("El SKU" + request.getSku() + " ya existe");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(()-> new ResourceNotFoundException("Categoría no encontranda con el id " + request.getCategoryId()));
        Product product = Product.builder()
                .name(request.getName())
                .sku(request.getSku())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .minStock(request.getMinStock())
                .category(category)
                .build();
        Product saved = productRepository.save(product);
        return mapToResponse(saved);
}
       @Transactional
public List<ProductDtos.ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
           .collect(Collectors.toList());
}

@Transactional
public ProductDtos.ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product no encontrado"));
        return mapToResponse(product);
}

@Transactional
public List<ProductDtos.ProductResponse> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream().map(this::mapToResponse).collect(Collectors.toList());
}

@Transactional
public ProductDtos.ProductResponse updateProduct(ProductDtos.ProductRequest request, Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrado"));
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setMinStock(request.getMinStock());
        product.setCategory(category);

        return mapToResponse(productRepository.save(product));
}

@Transactional
public void deleteProduct(Long id) {
        if(!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product no encontrado con el ID: " + id);
        }
    productRepository.deleteById(id);

}

    private ProductDtos.ProductResponse mapToResponse(Product product) {
        return ProductDtos.ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .minStock(product.getMinStock())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .lowStock(product.getStock() <= product.getMinStock())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}