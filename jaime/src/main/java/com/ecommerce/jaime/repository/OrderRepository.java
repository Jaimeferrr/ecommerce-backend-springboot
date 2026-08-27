package com.ecommerce.jaime.repository;

import com.ecommerce.jaime.model.Order;
import com.ecommerce.jaime.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(OrderStatus status);
    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.user " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.product " +
            "WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);

}
