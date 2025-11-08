package com.irris.yamo.service;

import java.util.Map;

public interface NotificationService {

    // ========== Notifications Email ==========

    /**
     * Envoie un email simple
     */
    void sendEmail(String to, String subject, String body);

    /**
     * Envoie un email avec template
     */
    void sendTemplateEmail(String to, String templateName, Map<String, Object> variables);

    /**
     * Envoie une confirmation de commande par email
     */
    void sendOrderConfirmationEmail(Long orderId);

    /**
     * Envoie une notification de livraison par email
     */
    void sendDeliveryNotificationEmail(Long orderId);

    /**
     * Envoie une facture par email
     */
    void sendInvoiceEmail(Long invoiceId);

    // ========== Notifications SMS ==========

    /**
     * Envoie un SMS simple
     */
    void sendSms(String phoneNumber, String message);

    /**
     * Envoie un SMS de confirmation de commande
     */
    void sendOrderConfirmationSms(Long orderId);

    /**
     * Envoie un SMS de ramassage programmé
     */
    void sendPickupScheduledSms(Long taskId);

    /**
     * Envoie un SMS de livraison en cours
     */
    void sendDeliveryInProgressSms(Long orderId);

    /**
     * Envoie un SMS de livraison effectuée
     */
    void sendDeliveryCompletedSms(Long orderId);

    // ========== Notifications Push (Optionnel) ==========

    /**
     * Envoie une notification push
     */
    void sendPushNotification(Long userId, String title, String message);

    // ========== Notifications Multi-canal ==========

    /**
     * Envoie une notification par tous les canaux disponibles
     */
    void sendMultiChannelNotification(Long userId, String subject, String message);

    /**
     * Notification de commande créée (Email + SMS)
     */
    void notifyOrderCreated(Long orderId);

    /**
     * Notification de commande prête (Email + SMS)
     */
    void notifyOrderReady(Long orderId);

    /**
     * Notification de paiement reçu (Email + SMS)
     */
    void notifyPaymentReceived(Long paymentId);

    // ========== Utilitaires ==========

    /**
     * Vérifie si un email est valide
     */
    boolean isValidEmail(String email);

    /**
     * Vérifie si un numéro de téléphone est valide
     */
    boolean isValidPhoneNumber(String phoneNumber);

    /**
     * Récupère les préférences de notification d'un utilisateur
     */
    Map<String, Boolean> getUserNotificationPreferences(Long userId);
}
