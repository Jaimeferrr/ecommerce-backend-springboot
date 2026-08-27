package com.ecommerce.jaime.controller;

import com.ecommerce.jaime.dto.AuthDtos;
import com.ecommerce.jaime.exceptions.BadRequestException;
import com.ecommerce.jaime.model.Role;
import com.ecommerce.jaime.model.User;
import com.ecommerce.jaime.repository.UserRepository;
import com.ecommerce.jaime.security.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para Login y Registro de Usuarios")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT válido por 24 horas.")
    @PostMapping("/login")
    public ResponseEntity<AuthDtos.AuthResponse> authenticaUser(@Valid @RequestBody AuthDtos.LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
        return ResponseEntity.ok(new AuthDtos.AuthResponse(
                jwt,
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        ));
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario en el sistema con contraseña cifrada en BCrypt.")
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        return ResponseEntity.ok("Registro de usuario exitoso");
    }

}