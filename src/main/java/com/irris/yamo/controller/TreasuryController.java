package com.irris.yamo.controller;

import com.irris.yamo.dtos.TreasuryDto;
import com.irris.yamo.entities.enums.PaymentMethod;
import com.irris.yamo.service.TreasuryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/treasury")
@RequiredArgsConstructor
public class TreasuryController {

    private final TreasuryService treasuryService;

    @GetMapping("/overview")
    public ResponseEntity<TreasuryDto> getTreasuryOverview() {
        TreasuryDto overview = treasuryService.getTreasuryOverview();
        return ResponseEntity.ok(overview);
    }

    @GetMapping("/balance/by-method")
    public ResponseEntity<Map<String, BigDecimal>> getBalanceByPaymentMethod() {
        Map<String, BigDecimal> balances = treasuryService.getBalanceByPaymentMethod();
        return ResponseEntity.ok(balances);
    }

    @GetMapping("/customer-credit/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotalCustomerCredit() {
        BigDecimal total = treasuryService.getTotalCustomerCredit();
        
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("totalCustomerCredit", total);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/balance/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotalBalance() {
        BigDecimal total = treasuryService.getTotalBalance();
        
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("totalBalance", total);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/method/{method}")
    public ResponseEntity<TreasuryDto> getTreasuryByMethod(@PathVariable PaymentMethod method) {
        TreasuryDto treasury = treasuryService.getTreasuryByPaymentMethod(method);
        return ResponseEntity.ok(treasury);
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> updateBalance(
            @RequestParam PaymentMethod method,
            @RequestParam BigDecimal amount,
            @RequestParam boolean isCredit) {
        treasuryService.updateTreasuryBalance(method, amount, isCredit);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Trésorerie mise à jour");
        
        return ResponseEntity.ok(response);
    }
}
