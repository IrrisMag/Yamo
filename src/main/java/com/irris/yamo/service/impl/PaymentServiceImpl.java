package com.irris.yamo.service.impl;

import com.irris.yamo.entities.Order;
import com.irris.yamo.entities.Payment;
import com.irris.yamo.entities.Receipt;
import com.irris.yamo.entities.enums.PaymentStatus;
import com.irris.yamo.exception.InvalidOperationException;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.repositories.OrderRepository;
import com.irris.yamo.repositories.PaymentRepository;
import com.irris.yamo.repositories.ReceiptRepository;
import com.irris.yamo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ReceiptRepository receiptRepository;

    @Override
    @Transactional
    public void processPayment(Double amount) {
        // Méthode générique pour traiter un paiement
        // L'implémentation spécifique dépend du gateway de paiement
        if (amount == null || amount <= 0) {
            throw new InvalidOperationException("Le montant du paiement doit être positif");
        }
        // TODO: Intégrer avec le gateway de paiement (Orange Money, MTN MoMo, etc.)
    }

    @Override
    @Transactional
    public void registerOrderPayment(Long orderId, Double amount) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande non trouvée avec l'ID: " + orderId));

        if (amount == null || amount <= 0) {
            throw new InvalidOperationException("Le montant du paiement doit être positif");
        }

        // Créer un nouveau paiement
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(BigDecimal.valueOf(amount));
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PENDING);
        
        // Vérifier si c'est un paiement partiel
        BigDecimal totalAmount = order.getTotalWithDelivery();
        payment.setPartial(BigDecimal.valueOf(amount).compareTo(totalAmount) < 0);

        paymentRepository.save(payment);
        
        // Ajouter le paiement à la commande
        order.addPayment(payment);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void validateOrderPayment(Long orderId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paiement non trouvé avec l'ID: " + paymentId));

        if (!payment.getOrder().getId().equals(orderId)) {
            throw new InvalidOperationException(
                    "Le paiement n'appartient pas à cette commande");
        }

        // Valider le paiement
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        // Mettre à jour le statut de la commande
        Order order = payment.getOrder();
        order.updateStatus();
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public Receipt generateReceipt(Long orderId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paiement non trouvé avec l'ID: " + paymentId));

        if (!payment.getOrder().getId().equals(orderId)) {
            throw new InvalidOperationException(
                    "Le paiement n'appartient pas à cette commande");
        }

        // Vérifier que le paiement est complété
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new InvalidOperationException(
                    "Le paiement doit être validé avant de générer un reçu");
        }

        // Vérifier si un reçu existe déjà
        if (payment.getReceipt() != null) {
            return payment.getReceipt();
        }

        // Créer le reçu
        Receipt receipt = new Receipt();
        receipt.setPayment(payment);
        receipt.setAmount(payment.getAmount());
        receipt.setPaymentMethod(payment.getMethod() != null ? payment.getMethod().name() : "UNKNOWN");
        receipt.setTransactionReference(payment.getTransactionId());
        receipt.setIssueDate(LocalDateTime.now());

        receipt = receiptRepository.save(receipt);

        // Lier le reçu au paiement
        payment.setReceipt(receipt);
        paymentRepository.save(payment);

        return receipt;
    }

    @Override
    @Transactional
    public void refundPayment(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paiement non trouvé avec la transaction ID: " + transactionId));

        // Vérifier que le paiement peut être remboursé
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new InvalidOperationException(
                    "Seuls les paiements complétés peuvent être remboursés");
        }

        // Marquer comme remboursé
        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        // Mettre à jour le statut de la commande
        Order order = payment.getOrder();
        order.updateStatus();
        orderRepository.save(order);

        // TODO: Intégrer avec le gateway de paiement pour le remboursement effectif
    }
}
