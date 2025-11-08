package com.irris.yamo.repositories;

import com.irris.yamo.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    

    
    List<Expense> findByIsPaid(boolean isPaid);
    
    @Query("SELECT e FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate")
    List<Expense> findExpensesBetweenDates(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.isPaid = true AND e.expenseDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal sumPaidExpensesBetweenDates(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.isPaid = true GROUP BY e.category")
    List<Object[]> sumExpensesByCategory();
    
    @Query("SELECT e FROM Expense e WHERE e.isRecurring = true")
    List<Expense> findRecurringExpenses();
    
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.isPaid = true")
    java.math.BigDecimal sumAllPaidExpenses();
}
