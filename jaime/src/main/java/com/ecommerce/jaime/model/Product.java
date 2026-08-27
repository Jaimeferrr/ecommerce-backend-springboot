package com.ecommerce.jaime.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Fetch;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String name;

    @Column(unique = true, length = 50)
    private String sku;
    private String description;

    @NotNull
    @Min(0)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer stock;

    @NotNull
    @Min(0)
    @Column(name = "min_stock", nullable = false)
    private Integer minStock;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
    }

}
