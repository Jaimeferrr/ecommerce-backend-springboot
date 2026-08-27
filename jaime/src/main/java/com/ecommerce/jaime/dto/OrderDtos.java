package com.ecommerce.jaime.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDtos {
    @Getter @Setter
    public static class OrderItemRequest {
        @NotNull(message = "El ID del producto es obligatorio")
        private Long productId;
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad mínima debe de ser 1")
        private Integer quantity;
    }
    @Getter @Setter
    public static class CreateOrderRequest {
        @NotEmpty(message = "El pedido debe incluir al menos un producto")
        private List<OrderItemRequest> items;
    }
    @Getter @Setter
    @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class OrderItemResponse {
        private Long productId;
        private Integer quantity;
        private String productName;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
    @Getter @Setter
    @Builder
    @AllArgsConstructor @NoArgsConstructor
    public static class OrderResponse {
        private Long id;
        private String userName;
        private String userEmail;
        private BigDecimal totalAmount;
        private String status;
        private LocalDateTime createdAt;
        private List<OrderItemResponse> items;
    }
}
