package com.irris.yamo.controller;

import com.irris.yamo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ========== Notifications Email ==========

    @PostMapping("/email")
    public ResponseEntity<Map<String, String>> sendEmail(@RequestBody Map<String, String> request) {
        String to = request.get("to");
        String subject = request.get("subject");
        String body = request.get("body");
        
        notificationService.sendEmail(to, subject, body);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Email envoyé");
        response.put("to", to);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/email/template")
    public ResponseEntity<Map<String, String>> sendTemplateEmail(@RequestBody Map<String, Object> request) {
        String to = (String) request.get("to");
        String templateName = (String) request.get("templateName");
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>) request.get("variables");
        
        notificationService.sendTemplateEmail(to, templateName, variables);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Email template envoyé");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/orders/{orderId}/confirmation-email")
    public ResponseEntity<Map<String, String>> sendOrderConfirmationEmail(@PathVariable Long orderId) {
        notificationService.sendOrderConfirmationEmail(orderId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Email de confirmation envoyé");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/orders/{orderId}/delivery-email")
    public ResponseEntity<Map<String, String>> sendDeliveryNotificationEmail(@PathVariable Long orderId) {
        notificationService.sendDeliveryNotificationEmail(orderId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Email de livraison envoyé");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/invoices/{invoiceId}/email")
    public ResponseEntity<Map<String, String>> sendInvoiceEmail(@PathVariable Long invoiceId) {
        notificationService.sendInvoiceEmail(invoiceId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Facture envoyée par email");
        
        return ResponseEntity.ok(response);
    }

    // ========== Notifications SMS ==========

    @PostMapping("/sms")
    public ResponseEntity<Map<String, String>> sendSms(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String message = request.get("message");
        
        notificationService.sendSms(phoneNumber, message);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "SMS envoyé");
        response.put("to", phoneNumber);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/orders/{orderId}/confirmation-sms")
    public ResponseEntity<Map<String, String>> sendOrderConfirmationSms(@PathVariable Long orderId) {
        notificationService.sendOrderConfirmationSms(orderId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "SMS de confirmation envoyé");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tasks/{taskId}/pickup-sms")
    public ResponseEntity<Map<String, String>> sendPickupScheduledSms(@PathVariable Long taskId) {
        notificationService.sendPickupScheduledSms(taskId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "SMS de ramassage envoyé");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/orders/{orderId}/delivery-progress-sms")
    public ResponseEntity<Map<String, String>> sendDeliveryInProgressSms(@PathVariable Long orderId) {
        notificationService.sendDeliveryInProgressSms(orderId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "SMS de livraison en cours envoyé");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/orders/{orderId}/delivery-completed-sms")
    public ResponseEntity<Map<String, String>> sendDeliveryCompletedSms(@PathVariable Long orderId) {
        notificationService.sendDeliveryCompletedSms(orderId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "SMS de livraison terminée envoyé");
        
        return ResponseEntity.ok(response);
    }

    // ========== Notifications Push ==========

    @PostMapping("/push")
    public ResponseEntity<Map<String, String>> sendPushNotification(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        String title = (String) request.get("title");
        String message = (String) request.get("message");
        
        notificationService.sendPushNotification(userId, title, message);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification push envoyée");
        
        return ResponseEntity.ok(response);
    }

    // ========== Notifications Multi-canal ==========

    @PostMapping("/multi-channel")
    public ResponseEntity<Map<String, String>> sendMultiChannelNotification(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        String subject = (String) request.get("subject");
        String message = (String) request.get("message");
        
        notificationService.sendMultiChannelNotification(userId, subject, message);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notifications multi-canal envoyées");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/orders/{orderId}/created")
    public ResponseEntity<Map<String, String>> notifyOrderCreated(@PathVariable Long orderId) {
        notificationService.notifyOrderCreated(orderId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notifications de création envoyées");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/orders/{orderId}/ready")
    public ResponseEntity<Map<String, String>> notifyOrderReady(@PathVariable Long orderId) {
        notificationService.notifyOrderReady(orderId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notifications de commande prête envoyées");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payments/{paymentId}/received")
    public ResponseEntity<Map<String, String>> notifyPaymentReceived(@PathVariable Long paymentId) {
        notificationService.notifyPaymentReceived(paymentId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notifications de paiement envoyées");
        
        return ResponseEntity.ok(response);
    }

    // ========== Utilitaires ==========

    @PostMapping("/validate-email")
    public ResponseEntity<Map<String, Boolean>> validateEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean isValid = notificationService.isValidEmail(email);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("isValid", isValid);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate-phone")
    public ResponseEntity<Map<String, Boolean>> validatePhoneNumber(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        boolean isValid = notificationService.isValidPhoneNumber(phoneNumber);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("isValid", isValid);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/preferences")
    public ResponseEntity<Map<String, Boolean>> getUserPreferences(@PathVariable Long userId) {
        Map<String, Boolean> preferences = notificationService.getUserNotificationPreferences(userId);
        return ResponseEntity.ok(preferences);
    }
}
