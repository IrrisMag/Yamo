package com.irris.yamo.controller;

import com.irris.yamo.dtos.InvoiceDto;
import com.irris.yamo.entities.Invoice;
import com.irris.yamo.entities.Order;
import com.irris.yamo.mapper.InvoiceMapper;
import com.irris.yamo.repositories.InvoiceRepository;
import com.irris.yamo.repositories.OrderRepository;
import com.irris.yamo.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;
    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;
    private final InvoiceMapper invoiceMapper;

    /**
     * Générer une facture pour une commande
     */
    @PostMapping("/invoice/{orderId}")
    public ResponseEntity<Map<String, String>> generateInvoice(@PathVariable Long orderId) {
        billingService.generateInvoice(orderId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Facture générée avec succès");
        response.put("orderId", orderId.toString());
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Récupérer une facture par ID
     */
    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<InvoiceDto> getInvoice(@PathVariable Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));
        
        return ResponseEntity.ok(invoiceMapper.toDto(invoice));
    }

    /**
     * Récupérer la facture d'une commande
     */
    @GetMapping("/invoice/order/{orderId}")
    public ResponseEntity<InvoiceDto> getInvoiceByOrder(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
        
        Invoice invoice = invoiceRepository.findByOrder(order)
                .orElseThrow(() -> new RuntimeException("Aucune facture pour cette commande"));
        
        return ResponseEntity.ok(invoiceMapper.toDto(invoice));
    }

    /**
     * Récupérer toutes les factures d'un client
     */
    @GetMapping("/invoice/customer/{customerId}")
    public ResponseEntity<List<InvoiceDto>> getCustomerInvoices(@PathVariable Long customerId) {
        List<Invoice> invoices = invoiceRepository.findByCustomerId(customerId);
        List<InvoiceDto> invoiceDtos = invoices.stream()
                .map(invoiceMapper::toDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(invoiceDtos);
    }

    /**
     * Calculer le montant total d'une commande
     */
    @GetMapping("/calculate/order/{orderId}")
    public ResponseEntity<Map<String, Object>> calculateOrderTotal(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
        
        BigDecimal total = billingService.calculateOrderTotalAmount(order);
        
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("totalAmount", total);
        response.put("currency", "XAF"); // ou votre devise
        
        return ResponseEntity.ok(response);
    }

    /**
     * Récupérer toutes les factures
     */
    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceDto>> getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        List<InvoiceDto> invoiceDtos = invoices.stream()
                .map(invoiceMapper::toDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(invoiceDtos);
    }
}
