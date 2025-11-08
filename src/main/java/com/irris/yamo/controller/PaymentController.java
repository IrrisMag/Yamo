package com.irris.yamo.controller;

import com.irris.yamo.dtos.creation.PaymentRegistrationDto;
import com.irris.yamo.entities.Receipt;
import com.irris.yamo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Enregistrer un paiement pour une commande
     */
    @PostMapping("/order/{orderId}")
    public ResponseEntity<Map<String, String>> registerPayment(
            @PathVariable Long orderId,
            @RequestBody PaymentRegistrationDto paymentDto) {
        paymentService.registerOrderPayment(orderId, paymentDto.getAmount().doubleValue());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Paiement enregistré avec succès");
        response.put("orderId", orderId.toString());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Valider un paiement
     */
    @PostMapping("/order/{orderId}/payment/{paymentId}/validate")
    public ResponseEntity<Map<String, String>> validatePayment(
            @PathVariable Long orderId,
            @PathVariable Long paymentId) {
        paymentService.validateOrderPayment(orderId, paymentId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Paiement validé avec succès");
        response.put("paymentId", paymentId.toString());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Générer un reçu pour un paiement
     */
    @PostMapping("/order/{orderId}/payment/{paymentId}/receipt")
    public ResponseEntity<Receipt> generateReceipt(
            @PathVariable Long orderId,
            @PathVariable Long paymentId) {
        Receipt receipt = paymentService.generateReceipt(orderId, paymentId);
        return ResponseEntity.ok(receipt);
    }

    /**
     * Rembourser un paiement
     */
    @PostMapping("/refund/{transactionId}")
    public ResponseEntity<Map<String, String>> refundPayment(@PathVariable String transactionId) {
        paymentService.refundPayment(transactionId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Paiement remboursé avec succès");
        response.put("transactionId", transactionId);
        
        return ResponseEntity.ok(response);
    }
}
