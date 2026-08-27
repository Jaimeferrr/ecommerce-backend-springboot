package com.ecommerce.jaime.service;

import com.ecommerce.jaime.dto.OrderDtos;
import com.ecommerce.jaime.exceptions.BadRequestException;
import com.ecommerce.jaime.exceptions.ResourceNotFoundException;
import com.ecommerce.jaime.model.*;
import com.ecommerce.jaime.repository.OrderRepository;
import com.ecommerce.jaime.repository.ProductRepository;
import com.ecommerce.jaime.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private final PdfReportService pdfService;
    private final EmailService emailService;

    @Transactional
    public OrderDtos.OrderResponse createOrder(OrderDtos.CreateOrderRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.COMPLETED)
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderDtos.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new BadRequestException("Stock insuficiente para el producto: " + product.getName() +
                        "'. Disponible: " + product.getStock() + ", Solicitado: " + itemRequest.getQuantity());
            }
            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();
            order.addItem(orderItem);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        OrderDtos.OrderResponse response = mapToResponse(savedOrder);

        try {
            ByteArrayInputStream pdfStream = pdfService.generateInvoicePdf(response);

            byte[] pdfBytes = pdfStream.readAllBytes();

            emailService.sendInvoiceEmail(user.getEmail(), savedOrder.getId(), pdfBytes);
        } catch (Exception e) {
            System.err.println("No se pudo enviar el correo de confirmación: " + e.getMessage());
        }

        return response;
    }

    @Transactional
    public List<OrderDtos.OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDtos.OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con el id: " + id));
        return mapToResponse(order);
    }

    private OrderDtos.OrderResponse mapToResponse(Order order) {
        List<OrderDtos.OrderItemResponse> itemResponse = order.getItems().stream()
                .map(item -> OrderDtos.OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        return OrderDtos.OrderResponse.builder()
                .id(order.getId())
                .userName(order.getUser().getName())
                .userEmail(order.getUser().getEmail())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .items(itemResponse)
                .build();
    }
}