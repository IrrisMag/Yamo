package com.irris.yamo.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDto {
    private Long id;
    private String invoiceNumber;
    private Long orderId;
    private String orderReference;
    
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    
    private String pdfUrl;
    private boolean sentToCustomer;
    private LocalDateTime sentDate;
    
    private String customerEmail;
    private String customerPhone;
    private String customerName;
}
