package com.ecommerce.jaime.service;

import com.ecommerce.jaime.dto.OrderDtos;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
@Service
public class PdfReportService {

    public ByteArrayInputStream generateInvoicePdf(OrderDtos.OrderResponse order) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
        PdfWriter.getInstance(document, out);
        document.open();
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.DARK_GRAY);
        Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
        Paragraph title = new Paragraph("FACTURA DE COMPRA", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph companyName = new Paragraph("Jaime E-Commerce Inc.", subTitleFont);
        companyName.setAlignment(Element.ALIGN_CENTER);
        document.add(companyName);

        document.add(Chunk.NEWLINE);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedDate = order.getCreatedAt() != null ? order.getCreatedAt().format(formatter) : "N/A";

        Paragraph orderInfo = new Paragraph(
                "Número de Pedido: #" + order.getId() + "\n" +
                        "Fecha: " + formattedDate + "\n" +
                        "Cliente: " + order.getUserName() + "\n" +
                        "Email: " + order.getUserEmail() + "\n" +
                        "Estado: " + order.getStatus(), bodyFont);
        document.add(orderInfo);

        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 1, 2, 2});

        addCellToHeader(table, "Producto", headerFont);
        addCellToHeader(table, "Cant.", headerFont);
        addCellToHeader(table, "Precio Unit.", headerFont);
        addCellToHeader(table, "Subtotal", headerFont);

        for (OrderDtos.OrderItemResponse item : order.getItems()) {
            table.addCell(new PdfPCell(new Phrase(item.getProductName(), bodyFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), bodyFont)));
            table.addCell(new PdfPCell(new Phrase(item.getUnitPrice().toString() + " €", bodyFont)));
            table.addCell(new PdfPCell(new Phrase(item.getSubtotal().toString() + " €", bodyFont)));
        }

        document.add(table);

        document.add(Chunk.NEWLINE);

        Paragraph total = new Paragraph("TOTAL APAGAR: " + order.getTotalAmount().toString() + " €", totalFont);
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);

        document.close();

    } catch(DocumentException e){
        throw new RuntimeException("Error al generar la factura en PDF: " + e.getMessage());
    }

        return new ByteArrayInputStream(out.toByteArray());
}

private void addCellToHeader(PdfPTable table, String text, Font font) {
    PdfPCell header = new PdfPCell(new Phrase(text, font));
    header.setBackgroundColor(new Color(41, 128, 185));
    header.setHorizontalAlignment(Element.ALIGN_CENTER);
    header.setPadding(5);
    table.addCell(header);
}
}
