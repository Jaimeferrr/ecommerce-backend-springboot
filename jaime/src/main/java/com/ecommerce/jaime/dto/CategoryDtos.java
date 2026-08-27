package com.ecommerce.jaime.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


public class CategoryDtos {

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class CategoryRequest {
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 80, message = "El nombre no debe superar los 80 caracteres")
    private String name;

    private String description;
}

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public static class CategoryResponse {
        private Long id;
        private String name;
        private String description;
        private int productCount;

}
}