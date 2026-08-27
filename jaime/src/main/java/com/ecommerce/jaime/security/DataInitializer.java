package com.ecommerce.jaime.security;

import com.ecommerce.jaime.model.Category;
import com.ecommerce.jaime.model.Product;
import com.ecommerce.jaime.model.Role;
import com.ecommerce.jaime.model.User;
import com.ecommerce.jaime.repository.CategoryRepository;
import com.ecommerce.jaime.repository.ProductRepository;
import com.ecommerce.jaime.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .name("Administrador Jaime")
                    .email("admin@ecommerce.com")
                    .password(passwordEncoder.encode("Admin1234"))
                    .role(Role.ADMIN)
                    .build();

            User client = User.builder()
                    .name("Cliente de prueba")
                    .email("cliente@ecommerce.com")
                    .password(passwordEncoder.encode("User1234"))
                    .role(Role.USER)
                    .build();

            userRepository.saveAll(List.of(admin, client));
            System.out.println("✅ DataInitializer: Usuarios iniciales creados.");
        }

        if (categoryRepository.count() == 0 && productRepository.count() == 0) {

            Category electronics = Category.builder()
                    .name("Electronics")
                    .description("Dispositivos y gadgets electrónicos")
                    .build();
            Category clothing = Category.builder()
                    .name("Ropa")
                    .description("Moda y accesorios")
                    .build();
            Category home = Category.builder()
                    .name("Hogar")
                    .description("Artículos para el hogar y cocina")
                    .build();

            List<Category> savedCategories = categoryRepository.saveAll(List.of(electronics, clothing, home));

            Category savedElectronics = savedCategories.get(0);
            Category savedClothing = savedCategories.get(1);
            Category savedHome = savedCategories.get(2);

            Product p1 = Product.builder()
                    .name("Portátil Gaming Intel i7")
                    .sku("LAP-INTEL-01")
                    .description("Portátil de alto rendimiento con 16GB RAM y 512GB SSD")
                    .price(new BigDecimal("1199.99"))
                    .stock(15)
                    .minStock(3)
                    .category(savedElectronics)
                    .build();

            Product p2 = Product.builder()
                    .name("Auriculares Inalámbricos Noise Cancelling")
                    .sku("AUD-WIRELESS-02")
                    .description("Auriculares con cancelación activa de ruido y 30h de batería")
                    .price(new BigDecimal("149.50"))
                    .stock(2)
                    .minStock(5)
                    .category(savedElectronics)
                    .build();

            Product p3 = Product.builder()
                    .name("Camiseta Algodón Orgánico")
                    .sku("TSHIRT-ORG-01")
                    .description("Camiseta transpirable 100% algodón orgánico")
                    .price(new BigDecimal("24.99"))
                    .stock(50)
                    .minStock(10)
                    .category(savedClothing)
                    .build();

            Product p4 = Product.builder()
                    .name("Cafetera Express Automática")
                    .sku("COFFEE-EXP-01")
                    .description("Cafetera con molinillo integrado y espumador de leche")
                    .price(new BigDecimal("299.00"))
                    .stock(8)
                    .minStock(2)
                    .category(savedHome)
                    .build();

            productRepository.saveAll(List.of(p1, p2, p3, p4));

            System.out.println("✅ DataInitializer: Categorías y Productos de prueba insertados.");
        }
    }
}