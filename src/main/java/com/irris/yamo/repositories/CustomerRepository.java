package com.irris.yamo.repositories;

import com.irris.yamo.entities.Customer;
import com.irris.yamo.entities.enums.CustomerSegment;
import com.irris.yamo.entities.enums.CustomerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    // Recherche par identifiants uniques
    Optional<Customer> findByNui(String nui);
    Optional<Customer> findByRccm(String rccm);
    
    // Recherche par type
    List<Customer> findByCustomerType(CustomerType customerType);
    List<Customer> findByCustomerSegment(CustomerSegment customerSegment);
    
    // Recherche clients VIP
    @Query("SELECT c FROM Customer c WHERE c.customerSegment = 'VIP' AND c.isActive = true")
    List<Customer> findVipCustomers();
    
    // Recherche clients entreprises actives
    @Query("SELECT c FROM Customer c WHERE c.customerType = 'BUSINESS' AND c.isActive = true")
    List<Customer> findActiveBusinessCustomers();
    
    // Recherche par nom entreprise
    Optional<Customer> findByCompanyName(String companyName);
    
    // Clients avec crédit disponible
    @Query("SELECT c FROM Customer c WHERE c.customerCredit > 0 AND c.isActive = true")
    List<Customer> findCustomersWithCredit();
    
    // Clients avec dette
    @Query("SELECT c FROM Customer c WHERE c.customerDebt > 0")
    List<Customer> findCustomersWithDebt();
}
