package com.irris.yamo.service;

import com.irris.yamo.entities.Order;

import java.time.LocalDate;
import java.util.Map;

public interface ReportService {

    // ========== Rapports de Commandes ==========

    /**
     * Génère un rapport PDF pour une commande
     */
    byte[] generateOrderReportPdf(Long orderId);

    /**
     * Génère un rapport PDF pour une facture
     */
    byte[] generateInvoicePdf(Long invoiceId);

    /**
     * Génère un reçu PDF
     */
    byte[] generateReceiptPdf(Long receiptId);

    // ========== Rapports de Statistiques ==========

    /**
     * Rapport des ventes journalières
     */
    Map<String, Object> getDailySalesReport(LocalDate date);

    /**
     * Rapport des ventes mensuelles
     */
    Map<String, Object> getMonthlySalesReport(int year, int month);

    /**
     * Rapport des performances chauffeurs
     */
    Map<String, Object> getDriverPerformanceReport(Long driverId, LocalDate startDate, LocalDate endDate);

    /**
     * Rapport des clients VIP
     */
    Map<String, Object> getVipCustomersReport();

    /**
     * Rapport des commandes en retard
     */
    Map<String, Object> getLateOrdersReport();

    // ========== Export PDF Rapports ==========

    /**
     * Export rapport journalier en PDF
     */
    byte[] exportDailyReportPdf(LocalDate date);

    /**
     * Export rapport mensuel en PDF
     */
    byte[] exportMonthlyReportPdf(int year, int month);

    /**
     * Export rapport chauffeur en PDF
     */
    byte[] exportDriverReportPdf(Long driverId, LocalDate startDate, LocalDate endDate);

    // ========== Utilitaires PDF ==========

    /**
     * Génère un PDF générique à partir de HTML
     */
    byte[] generatePdfFromHtml(String html);

    /**
     * Génère un PDF de liste de commandes
     */
    byte[] generateOrderListPdf(LocalDate startDate, LocalDate endDate);
}
