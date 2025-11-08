package com.irris.yamo.mapper;

import com.irris.yamo.dtos.InvoiceDto;
import com.irris.yamo.entities.Invoice;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {

    public InvoiceDto toDto(Invoice invoice) {
        if (invoice == null) {
            return null;
        }

        return InvoiceDto.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .orderId(invoice.getOrder() != null ? invoice.getOrder().getId() : null)
                .orderReference(invoice.getOrder() != null ? invoice.getOrder().getReference() : null)
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .subtotal(invoice.getSubtotal())
                .taxAmount(invoice.getTaxAmount())
                .discountAmount(invoice.getDiscountAmount())
                .totalAmount(invoice.getTotalAmount())
                .pdfUrl(invoice.getPdfUrl())
                .sentToCustomer(invoice.isSentToCustomer())
                .sentDate(invoice.getSentDate())
                .customerEmail(invoice.getCustomerEmail())
                .customerPhone(invoice.getCustomerPhone())
                .customerName(invoice.getOrder() != null && invoice.getOrder().getCustomer() != null 
                        ? invoice.getOrder().getCustomer().getFullName() : null)
                .build();
    }
}
