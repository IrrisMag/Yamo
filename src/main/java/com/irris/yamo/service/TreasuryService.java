package com.irris.yamo.service;

import com.irris.yamo.dtos.TreasuryDto;
import com.irris.yamo.entities.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.Map;

public interface TreasuryService {
    
    TreasuryDto getTreasuryOverview();
    
    Map<String, BigDecimal> getBalanceByPaymentMethod();
    
    BigDecimal getTotalCustomerCredit();
    
    BigDecimal getTotalBalance();
    
    void updateTreasuryBalance(PaymentMethod method, BigDecimal amount, boolean isCredit);
    
    TreasuryDto getTreasuryByPaymentMethod(PaymentMethod method);
}
