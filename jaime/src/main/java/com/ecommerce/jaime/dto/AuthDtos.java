package com.ecommerce.jaime.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public class AuthDtos {

    @Getter @Setter
    public static class LoginRequest {
        @Email @NotBlank
        private String email;
        @NotBlank
        private String password;
    }
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class AuthResponse {
        private String token;
        private String email;
        private String name;
        private String role;

    }
    @Data
    public static class RegisterRequest {
        @NotBlank(message = "El nombre es obligatorio")
        private String name;

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato de email no es válido")
        private String email;

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        private String password;
    }

}
