package com.irris.yamo.service.impl;

import com.irris.yamo.entities.*;
import com.irris.yamo.exception.InvalidOperationException;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.repositories.*;
import com.irris.yamo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final LogisticTaskRepository logisticTaskRepository;
    private final UserRepository userRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");

    // ========== Notifications Email ==========

    @Override
    public void sendEmail(String to, String subject, String body) {
        if (!isValidEmail(to)) {
            throw new InvalidOperationException("Email invalide: " + to);
        }

        log.info("=== ENVOI EMAIL ===");
        log.info("À: {}", to);
        log.info("Sujet: {}", subject);
        log.info("Message: {}", body);
        log.info("==================");

        // TODO: Intégrer service d'email (SendGrid, AWS SES, etc.)
        /*
        SendGridEmail email = new SendGridEmail();
        email.setTo(to);
        email.setSubject(subject);
        email.setContent(body);
        sendGridClient.send(email);
        */
    }

    @Override
    public void sendTemplateEmail(String to, String templateName, Map<String, Object> variables) {
        String body = renderTemplate(templateName, variables);
        sendEmail(to, "Notification Yamo", body);
    }

    @Override
    @Transactional(readOnly = true)
    public void sendOrderConfirmationEmail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        String customerEmail = order.getCustomer().getEmail();
        String subject = "Confirmation de votre commande " + order.getReference();
        String body = String.format("""
                Bonjour %s,
                
                Votre commande %s a bien été enregistrée.
                
                Montant total: %s €
                Date estimée de livraison: %s
                
                Merci de votre confiance !
                
                L'équipe Yamo
                """,
                order.getCustomer().getFullName(),
                order.getReference(),
                order.getTotalAmount(),
                order.getRequiredCompletionDate());

        sendEmail(customerEmail, subject, body);
    }

    @Override
    @Transactional(readOnly = true)
    public void sendDeliveryNotificationEmail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        String subject = "Votre commande " + order.getReference() + " est en livraison";
        String body = String.format("""
                Bonjour %s,
                
                Votre commande %s est en cours de livraison.
                Notre chauffeur arrivera bientôt à votre adresse.
                
                Montant à payer: %s €
                
                Merci !
                """,
                order.getCustomer().getFullName(),
                order.getReference(),
                order.getRemainingAmount());

        sendEmail(order.getCustomer().getEmail(), subject, body);
    }

    @Override
    @Transactional(readOnly = true)
    public void sendInvoiceEmail(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée"));

        String subject = "Votre facture " + invoice.getInvoiceNumber();
        String body = String.format("""
                Bonjour,
                
                Veuillez trouver ci-joint votre facture N° %s.
                
                Montant total: %s €
                Date d'échéance: %s
                
                Cordialement,
                L'équipe Yamo
                """,
                invoice.getInvoiceNumber(),
                invoice.getTotalAmount(),
                invoice.getDueDate());

        sendEmail(invoice.getCustomerEmail(), subject, body);
    }

    // ========== Notifications SMS ==========

    @Override
    public void sendSms(String phoneNumber, String message) {
        if (!isValidPhoneNumber(phoneNumber)) {
            throw new InvalidOperationException("Numéro de téléphone invalide: " + phoneNumber);
        }

        log.info("=== ENVOI SMS ===");
        log.info("À: {}", phoneNumber);
        log.info("Message: {}", message);
        log.info("=================");

        // TODO: Intégrer service SMS (Twilio, Vonage, etc.)
        /*
        twilioClient.messages.create(
            new Message.Builder()
                .to(phoneNumber)
                .from(twilioPhoneNumber)
                .body(message)
                .build()
        );
        */
    }

    @Override
    @Transactional(readOnly = true)
    public void sendOrderConfirmationSms(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        String message = String.format(
                "Yamo: Votre commande %s est confirmée. Montant: %s €. Merci !",
                order.getReference(),
                order.getTotalAmount()
        );

        sendSms(order.getCustomer().getPhoneNumber(), message);
    }

    @Override
    @Transactional(readOnly = true)
    public void sendPickupScheduledSms(Long taskId) {
        LogisticTask task = logisticTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche non trouvée"));

        String message = String.format(
                "Yamo: Ramassage programmé pour le %s entre %s et %s. À bientôt !",
                task.getScheduledDate(),
                task.getAvailableFrom(),
                task.getAvailableTo()
        );

        sendSms(task.getContactPhone(), message);
    }

    @Override
    @Transactional(readOnly = true)
    public void sendDeliveryInProgressSms(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        String message = String.format(
                "Yamo: Votre commande %s est en livraison. Notre chauffeur arrive bientôt !",
                order.getReference()
        );

        sendSms(order.getCustomer().getPhoneNumber(), message);
    }

    @Override
    @Transactional(readOnly = true)
    public void sendDeliveryCompletedSms(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        String message = String.format(
                "Yamo: Votre commande %s a été livrée. Merci et à bientôt !",
                order.getReference()
        );

        sendSms(order.getCustomer().getPhoneNumber(), message);
    }

    // ========== Notifications Push ==========

    @Override
    public void sendPushNotification(Long userId, String title, String message) {
        log.info("=== NOTIFICATION PUSH ===");
        log.info("Utilisateur ID: {}", userId);
        log.info("Titre: {}", title);
        log.info("Message: {}", message);
        log.info("========================");

        // TODO: Intégrer Firebase Cloud Messaging ou autre service push
        /*
        Message pushMessage = Message.builder()
            .setToken(userDeviceToken)
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(message)
                .build())
            .build();
        
        FirebaseMessaging.getInstance().send(pushMessage);
        */
    }

    // ========== Notifications Multi-canal ==========

    @Override
    @Transactional(readOnly = true)
    public void sendMultiChannelNotification(Long userId, String subject, String message) {
        UserYamo user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Map<String, Boolean> preferences = getUserNotificationPreferences(userId);

        if (preferences.getOrDefault("email", true)) {
            sendEmail(user.getEmail(), subject, message);
        }

        if (preferences.getOrDefault("sms", true)) {
            sendSms(user.getPhoneNumber(), message);
        }

        if (preferences.getOrDefault("push", false)) {
            sendPushNotification(userId, subject, message);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void notifyOrderCreated(Long orderId) {
        sendOrderConfirmationEmail(orderId);
        sendOrderConfirmationSms(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public void notifyOrderReady(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        String message = "Votre commande " + order.getReference() + " est prête ! Vous pouvez venir la récupérer.";

        sendEmail(order.getCustomer().getEmail(), "Commande prête", message);
        sendSms(order.getCustomer().getPhoneNumber(), "Yamo: " + message);
    }

    @Override
    @Transactional(readOnly = true)
    public void notifyPaymentReceived(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));

        Order order = payment.getOrder();
        String message = String.format(
                "Paiement de %s € reçu pour votre commande %s. Merci !",
                payment.getAmount(),
                order.getReference()
        );

        sendEmail(order.getCustomer().getEmail(), "Paiement confirmé", message);
        sendSms(order.getCustomer().getPhoneNumber(), "Yamo: " + message);
    }

    // ========== Utilitaires ==========

    @Override
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && PHONE_PATTERN.matcher(phoneNumber).matches();
    }

    @Override
    public Map<String, Boolean> getUserNotificationPreferences(Long userId) {
        // TODO: Récupérer les vraies préférences depuis la base de données
        Map<String, Boolean> preferences = new HashMap<>();
        preferences.put("email", true);
        preferences.put("sms", true);
        preferences.put("push", false);
        return preferences;
    }

    // ========== Méthodes Privées ==========

    private String renderTemplate(String templateName, Map<String, Object> variables) {
        // TODO: Utiliser un moteur de template (Thymeleaf, Freemarker)
        // Pour l'instant, retour simple
        return "Template: " + templateName + " - Variables: " + variables;
    }
}
