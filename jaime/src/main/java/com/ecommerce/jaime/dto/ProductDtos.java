package com.ecommerce.jaime.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDtos {

    @Getter @Setter
    public static class ProductRequest {
        @NotBlank(message = "El nombre del producto es obligatorio")
        private String name;

        @NotBlank(message = "El código SKU es obligatorio")
        private String sku;

        private String description;

        @NotNull(message = "El precio es obligatorio")
        @Min(value = 0, message = "El precio debe ser positivo")
        private BigDecimal price;

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock debe ser 0 o superior")
        private Integer stock;

        @NotNull(message = "El stock mínimo es obligatorio")
        @Min(value = 0, message = "El stock mínimo debe ser 0 o superior")
        private Integer minStock;

        @NotNull(message = "La categoría es obligatoria")
        private Long categoryId;
    }

    @Getter @Setter
    @Builder
    @AllArgsConstructor @NoArgsConstructor
    public static class ProductResponse {
        private Long id;
        private String name;
        private String sku;
        private String description;
        private BigDecimal price;
        private Integer stock;
        private Integer minStock;
        private Long categoryId;
        private String categoryName;
        private boolean lowStock;
        private LocalDateTime updatedAt;
    }
}