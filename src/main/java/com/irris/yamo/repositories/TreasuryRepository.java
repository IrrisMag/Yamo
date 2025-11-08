package com.irris.yamo.repositories;

import com.irris.yamo.entities.Treasury;
import com.irris.yamo.entities.enums.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TreasuryRepository extends JpaRepository<Treasury, Long> {
    
    /**
     * Trouve une trésorerie par méthode de paiement
     */
    Optional<Treasury> findByPaymentMethod(PaymentMethod paymentMethod);
    
    /**
     * Trouve la trésorerie générale (sans méthode de paiement spécifique)
     */
    Optional<Treasury> findByPaymentMethodIsNull();
}
