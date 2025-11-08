package com.irris.yamo.controller;

import com.irris.yamo.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // ========== Rapports PDF ==========

    @GetMapping("/orders/{orderId}/pdf")
    public ResponseEntity<byte[]> downloadOrderReport(@PathVariable Long orderId) {
        byte[] pdf = reportService.generateOrderReportPdf(orderId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "order_" + orderId + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    @GetMapping("/invoices/{invoiceId}/pdf")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long invoiceId) {
        byte[] pdf = reportService.generateInvoicePdf(invoiceId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice_" + invoiceId + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    @GetMapping("/receipts/{receiptId}/pdf")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long receiptId) {
        byte[] pdf = reportService.generateReceiptPdf(receiptId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "receipt_" + receiptId + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    // ========== Rapports de Statistiques (JSON) ==========

    @GetMapping("/sales/daily")
    public ResponseEntity<Map<String, Object>> getDailySalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Map<String, Object> report = reportService.getDailySalesReport(date);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/sales/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlySalesReport(
            @RequestParam int year,
            @RequestParam int month) {
        Map<String, Object> report = reportService.getMonthlySalesReport(year, month);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/drivers/{driverId}/performance")
    public ResponseEntity<Map<String, Object>> getDriverPerformance(
            @PathVariable Long driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> report = reportService.getDriverPerformanceReport(driverId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/customers/vip")
    public ResponseEntity<Map<String, Object>> getVipCustomersReport() {
        Map<String, Object> report = reportService.getVipCustomersReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/orders/late")
    public ResponseEntity<Map<String, Object>> getLateOrdersReport() {
        Map<String, Object> report = reportService.getLateOrdersReport();
        return ResponseEntity.ok(report);
    }

    // ========== Export PDF Rapports ==========

    @GetMapping("/sales/daily/pdf")
    public ResponseEntity<byte[]> exportDailyReportPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        byte[] pdf = reportService.exportDailyReportPdf(date);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "daily_report_" + date + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    @GetMapping("/sales/monthly/pdf")
    public ResponseEntity<byte[]> exportMonthlyReportPdf(
            @RequestParam int year,
            @RequestParam int month) {
        byte[] pdf = reportService.exportMonthlyReportPdf(year, month);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", 
                "monthly_report_" + year + "-" + String.format("%02d", month) + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    @GetMapping("/drivers/{driverId}/performance/pdf")
    public ResponseEntity<byte[]> exportDriverReportPdf(
            @PathVariable Long driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        byte[] pdf = reportService.exportDriverReportPdf(driverId, startDate, endDate);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", 
                "driver_" + driverId + "_report.pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    @GetMapping("/orders/list/pdf")
    public ResponseEntity<byte[]> generateOrderListPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        byte[] pdf = reportService.generateOrderListPdf(startDate, endDate);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", 
                "orders_" + startDate + "_to_" + endDate + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}
