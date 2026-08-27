package com.ecommerce.jaime.controller;

import com.ecommerce.jaime.dto.OrderDtos;
import com.ecommerce.jaime.service.OrderService;
import com.ecommerce.jaime.service.PdfReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Pedidos y Ventas", description = "Endpoints para procesar ventas y consultar historial de pedidos")
public class OrderController {

    private final OrderService orderService;
    private final PdfReportService pdfReportService;

    @Operation(summary = "Descargar factura en PDF", description = "Genera y descarga un comprobante de pago en formato PDF para el pedido especificado.")
    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<InputStreamResource> downloadInvoicePdf(@PathVariable Long id) {
        OrderDtos.OrderResponse order = orderService.getOrderById(id);
        ByteArrayInputStream pdfStream = pdfReportService.generateInvoicePdf(order);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=factura_pedido_" + id + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }

    @Operation(summary = "Obtener historial de pedidos (ADMIN)", description = "Devuelve la lista de todos los pedidos realizados en la tienda.")
    @ApiResponse(responseCode = "200", description = "Historial recuperado con éxito")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDtos.OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @Operation(summary = "Obtener detalle de un pedido", description = "Busca un pedido por su ID e incluye sus líneas de detalle con productos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderDtos.OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @Operation(summary = "Crear nuevo pedido", description = "Procesa una venta, valida stock, descuenta inventario automáticamente y registra la transacción.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido procesado y creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente o producto inexistente")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderDtos.OrderResponse> createOrder(
            @Valid @RequestBody OrderDtos.CreateOrderRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        OrderDtos.OrderResponse created = orderService.createOrder(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}