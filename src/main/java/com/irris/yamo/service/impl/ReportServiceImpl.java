package com.irris.yamo.service.impl;

import com.irris.yamo.entities.*;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.repositories.*;
import com.irris.yamo.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final ReceiptRepository receiptRepository;
    private final DriverRepository driverRepository;
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;

    // ========== Génération PDF ==========

    @Override
    @Transactional(readOnly = true)
    public byte[] generateOrderReportPdf(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        String html = generateOrderHtml(order);
        return generatePdfFromHtml(html);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateInvoicePdf(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée"));

        String html = generateInvoiceHtml(invoice);
        return generatePdfFromHtml(html);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateReceiptPdf(Long receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Reçu non trouvé"));

        String html = generateReceiptHtml(receipt);
        return generatePdfFromHtml(html);
    }

    // ========== Rapports de Statistiques ==========

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDailySalesReport(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getCreatedAt() != null &&
                        o.getCreatedAt().isAfter(startOfDay) &&
                        o.getCreatedAt().isBefore(endOfDay))
                .collect(Collectors.toList());

        return buildSalesReport(orders, date.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMonthlySalesReport(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atStartOfDay();

        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getCreatedAt() != null &&
                        o.getCreatedAt().isAfter(start) &&
                        o.getCreatedAt().isBefore(end))
                .collect(Collectors.toList());

        return buildSalesReport(orders, year + "-" + String.format("%02d", month));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDriverPerformanceReport(Long driverId, LocalDate startDate, LocalDate endDate) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Chauffeur non trouvé"));

        Map<String, Object> report = new HashMap<>();
        report.put("driverId", driverId);
        report.put("driverName", driver.getFullName());
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("totalDeliveries", driver.getTotalDeliveries());
        report.put("totalPickups", driver.getTotalPickups());
        report.put("totalTasks", (driver.getTotalDeliveries() != null ? driver.getTotalDeliveries() : 0) +
                (driver.getTotalPickups() != null ? driver.getTotalPickups() : 0));

        return report;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getVipCustomersReport() {
        List<Customer> vipCustomers = customerRepository.findAll().stream()
                .filter(c -> com.irris.yamo.entities.enums.CustomerSegment.VIP.equals(c.getCustomerSegment()))
                .collect(Collectors.toList());

        Map<String, Object> report = new HashMap<>();
        report.put("totalVipCustomers", vipCustomers.size());
        report.put("customers", vipCustomers.stream()
                .map(c -> {
                    Map<String, Object> customerData = new HashMap<>();
                    customerData.put("id", c.getId());
                    customerData.put("name", c.getFullName());
                    customerData.put("email", c.getEmail());
                    customerData.put("balance", c.getCustomerBalance());
                    return customerData;
                })
                .collect(Collectors.toList()));

        return report;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getLateOrdersReport() {
        LocalDateTime now = LocalDateTime.now();
        List<Order> lateOrders = orderRepository.findAll().stream()
                .filter(o -> o.getRequiredCompletionDate() != null &&
                        o.getRequiredCompletionDate().isBefore(now) &&
                        !o.isFullyProcessed())
                .collect(Collectors.toList());

        Map<String, Object> report = new HashMap<>();
        report.put("totalLateOrders", lateOrders.size());
        report.put("orders", lateOrders.stream()
                .map(o -> {
                    Map<String, Object> orderData = new HashMap<>();
                    orderData.put("id", o.getId());
                    orderData.put("reference", o.getReference());
                    orderData.put("customer", o.getCustomer().getFullName());
                    orderData.put("dueDate", o.getRequiredCompletionDate());
                    orderData.put("hoursLate", java.time.Duration.between(o.getRequiredCompletionDate(), now).toHours());
                    return orderData;
                })
                .collect(Collectors.toList()));

        return report;
    }

    // ========== Export PDF Rapports ==========

    @Override
    public byte[] exportDailyReportPdf(LocalDate date) {
        Map<String, Object> report = getDailySalesReport(date);
        String html = generateReportHtml("Rapport Journalier", report);
        return generatePdfFromHtml(html);
    }

    @Override
    public byte[] exportMonthlyReportPdf(int year, int month) {
        Map<String, Object> report = getMonthlySalesReport(year, month);
        String html = generateReportHtml("Rapport Mensuel", report);
        return generatePdfFromHtml(html);
    }

    @Override
    public byte[] exportDriverReportPdf(Long driverId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = getDriverPerformanceReport(driverId, startDate, endDate);
        String html = generateReportHtml("Rapport Chauffeur", report);
        return generatePdfFromHtml(html);
    }

    @Override
    public byte[] generateOrderListPdf(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getCreatedAt() != null &&
                        o.getCreatedAt().isAfter(start) &&
                        o.getCreatedAt().isBefore(end))
                .collect(Collectors.toList());

        String html = generateOrderListHtml(orders, startDate, endDate);
        return generatePdfFromHtml(html);
    }

    // ========== Méthodes Privées de Génération HTML ==========

    private Map<String, Object> buildSalesReport(List<Order> orders, String period) {
        Map<String, Object> report = new HashMap<>();

        BigDecimal totalRevenue = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscount = orders.stream()
                .map(o -> o.getDiscountAmount() != null ? o.getDiscountAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        report.put("period", period);
        report.put("totalOrders", orders.size());
        report.put("totalRevenue", totalRevenue);
        report.put("totalDiscount", totalDiscount);
        report.put("averageOrderValue", orders.isEmpty() ? BigDecimal.ZERO :
                totalRevenue.divide(BigDecimal.valueOf(orders.size()), 2, java.math.RoundingMode.HALF_UP));

        return report;
    }

    private String generateOrderHtml(Order order) {
        return String.format("""
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; }
                        .header { text-align: center; margin-bottom: 30px; }
                        .info { margin: 20px 0; }
                        table { width: 100%%; border-collapse: collapse; }
                        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                        th { background-color: #4CAF50; color: white; }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1>Rapport de Commande</h1>
                        <p>Référence: %s</p>
                    </div>
                    <div class="info">
                        <p><strong>Client:</strong> %s</p>
                        <p><strong>Date:</strong> %s</p>
                        <p><strong>Montant Total:</strong> %s €</p>
                        <p><strong>Statut:</strong> %s</p>
                    </div>
                </body>
                </html>
                """,
                order.getReference(),
                order.getCustomer().getFullName(),
                order.getCreatedAt(),
                order.getTotalAmount(),
                order.getStatus());
    }

    private String generateInvoiceHtml(Invoice invoice) {
        return String.format("""
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; }
                        .header { text-align: center; margin-bottom: 30px; }
                        .total { font-size: 20px; font-weight: bold; margin-top: 20px; }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1>FACTURE</h1>
                        <p>N° %s</p>
                    </div>
                    <p><strong>Date d'émission:</strong> %s</p>
                    <p><strong>Montant HT:</strong> %s €</p>
                    <p><strong>TVA:</strong> %s €</p>
                    <p><strong>Remise:</strong> %s €</p>
                    <div class="total">
                        <p>TOTAL TTC: %s €</p>
                    </div>
                </body>
                </html>
                """,
                invoice.getInvoiceNumber(),
                invoice.getIssueDate(),
                invoice.getSubtotal(),
                invoice.getTaxAmount(),
                invoice.getDiscountAmount(),
                invoice.getTotalAmount());
    }

    private String generateReceiptHtml(Receipt receipt) {
        return String.format("""
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; }
                        .header { text-align: center; margin-bottom: 20px; }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h2>REÇU</h2>
                        <p>N° %s</p>
                    </div>
                    <p><strong>Date:</strong> %s</p>
                    <p><strong>Montant:</strong> %s €</p>
                    <p><strong>Méthode:</strong> %s</p>
                </body>
                </html>
                """,
                receipt.getReceiptNumber(),
                receipt.getIssueDate(),
                receipt.getAmount(),
                receipt.getPaymentMethod());
    }

    private String generateReportHtml(String title, Map<String, Object> data) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("body { font-family: Arial, sans-serif; }");
        html.append(".header { text-align: center; margin-bottom: 30px; }");
        html.append("table { width: 100%; border-collapse: collapse; }");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; }");
        html.append("</style></head><body>");
        html.append("<div class='header'><h1>").append(title).append("</h1></div>");
        html.append("<table>");

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!(entry.getValue() instanceof List)) {
                html.append("<tr><th>").append(entry.getKey()).append("</th>");
                html.append("<td>").append(entry.getValue()).append("</td></tr>");
            }
        }

        html.append("</table></body></html>");
        return html.toString();
    }

    private String generateOrderListHtml(List<Order> orders, LocalDate startDate, LocalDate endDate) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("body { font-family: Arial, sans-serif; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        html.append("th { background-color: #4CAF50; color: white; }");
        html.append("</style></head><body>");
        html.append("<h1>Liste des Commandes</h1>");
        html.append("<p>Période: ").append(startDate).append(" - ").append(endDate).append("</p>");
        html.append("<table><tr><th>Référence</th><th>Client</th><th>Date</th><th>Montant</th><th>Statut</th></tr>");

        for (Order order : orders) {
            html.append("<tr>");
            html.append("<td>").append(order.getReference()).append("</td>");
            html.append("<td>").append(order.getCustomer().getFullName()).append("</td>");
            html.append("<td>").append(order.getCreatedAt()).append("</td>");
            html.append("<td>").append(order.getTotalAmount()).append(" €</td>");
            html.append("<td>").append(order.getStatus()).append("</td>");
            html.append("</tr>");
        }

        html.append("</table></body></html>");
        return html.toString();
    }

    @Override
    public byte[] generatePdfFromHtml(String html) {
        // TODO: Implémenter avec une bibliothèque PDF (iText, Flying Saucer, etc.)
        // Pour l'instant, retourne le HTML en bytes comme placeholder
        // En production, utiliser: iText 7, OpenPDF, ou Flying Saucer
        
        /* Exemple avec Flying Saucer:
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF", e);
        }
        */
        
        return html.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}
