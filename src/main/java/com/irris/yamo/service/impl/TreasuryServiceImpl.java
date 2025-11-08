package com.irris.yamo.service.impl;

import com.irris.yamo.dtos.TreasuryDto;
import com.irris.yamo.entities.Customer;
import com.irris.yamo.entities.Treasury;
import com.irris.yamo.entities.enums.PaymentMethod;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.repositories.CustomerRepository;
import com.irris.yamo.repositories.PaymentRepository;
import com.irris.yamo.repositories.TreasuryRepository;
import com.irris.yamo.service.TreasuryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TreasuryServiceImpl implements TreasuryService {

    private final TreasuryRepository treasuryRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public TreasuryDto getTreasuryOverview() {
        BigDecimal totalBalance = getTotalBalance();
        BigDecimal totalCustomerCredit = getTotalCustomerCredit();
        Map<String, BigDecimal> balanceByMethod = getBalanceByPaymentMethod();

        return TreasuryDto.builder()
                .totalBalance(totalBalance)
                .totalCustomerCredit(totalCustomerCredit)
                .balanceByPaymentMethod(balanceByMethod)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getBalanceByPaymentMethod() {
        Map<String, BigDecimal> balances = new HashMap<>();

        for (PaymentMethod method : PaymentMethod.values()) {
            BigDecimal balance = treasuryRepository.findByPaymentMethod(method)
                    .map(Treasury::getBalance)
                    .orElse(BigDecimal.ZERO);
            balances.put(method.name(), balance);
        }

        // Ajouter aussi les totaux calculés depuis les paiements
        Map<String, BigDecimal> paymentTotals = new HashMap<>();
        List<Object[]> paymentsByMethod = paymentRepository.sumPaymentsByMethod();
        
        for (Object[] row : paymentsByMethod) {
            PaymentMethod method = (PaymentMethod) row[0];
            BigDecimal total = (BigDecimal) row[1];
            paymentTotals.put(method.name(), total != null ? total : BigDecimal.ZERO);
        }

        return balances.isEmpty() ? paymentTotals : balances;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCustomerCredit() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(customer -> BigDecimal.valueOf(
                        customer.getCustomerCredit() != null ? customer.getCustomerCredit() : 0.0))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalBalance() {
        List<Treasury> treasuries = treasuryRepository.findAll();
        return treasuries.stream()
                .map(Treasury::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional
    public void updateTreasuryBalance(PaymentMethod method, BigDecimal amount, boolean isCredit) {
        Treasury treasury = treasuryRepository.findByPaymentMethod(method)
                .orElseGet(() -> {
                    Treasury newTreasury = new Treasury();
                    newTreasury.setPaymentMethod(method);
                    newTreasury.setName(method.name());
                    newTreasury.setBalance(BigDecimal.ZERO);
                    return newTreasury;
                });

        if (isCredit) {
            treasury.credit(amount);
        } else {
            treasury.debit(amount);
        }

        treasuryRepository.save(treasury);
    }

    @Override
    @Transactional(readOnly = true)
    public TreasuryDto getTreasuryByPaymentMethod(PaymentMethod method) {
        Treasury treasury = treasuryRepository.findByPaymentMethod(method)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trésorerie non trouvée pour la méthode: " + method));

        return TreasuryDto.builder()
                .id(treasury.getId())
                .paymentMethod(treasury.getPaymentMethod() != null ? treasury.getPaymentMethod().name() : null)
                .name(treasury.getName())
                .balance(treasury.getBalance())
                .pendingPayments(treasury.getPendingPayments())
                .expectedIncome(treasury.getExpectedIncome())
                .projectedBalance(treasury.getProjectedBalance())
                .lastUpdated(treasury.getLastUpdated())
                .build();
    }
}
