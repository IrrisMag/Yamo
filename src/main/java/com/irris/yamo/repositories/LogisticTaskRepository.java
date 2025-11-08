package com.irris.yamo.repositories;

import com.irris.yamo.entities.LogisticTask;
import com.irris.yamo.entities.Order;
import com.irris.yamo.entities.UserYamo;
import com.irris.yamo.entities.enums.TaskStatus;
import com.irris.yamo.entities.enums.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LogisticTaskRepository extends JpaRepository<LogisticTask, Long> {
    
    List<LogisticTask> findByDriver(UserYamo driver);
    
    List<LogisticTask> findByType(TaskType type);
    
    List<LogisticTask> findByOrder(Order order);
    
    @Query("SELECT lt FROM LogisticTask lt WHERE lt.driver.id = :driverId AND lt.status = :status")
    List<LogisticTask> findByDriverIdAndStatus(@Param("driverId") Long driverId, @Param("status") TaskStatus status);
    
    @Query("SELECT lt FROM LogisticTask lt WHERE lt.type = :type AND lt.status = 'PENDING'")
    List<LogisticTask> findPendingTasksByType(@Param("type") TaskType type);
    
    @Query("SELECT lt FROM LogisticTask lt WHERE lt.type = :type AND lt.status = 'PENDING' AND CAST(lt.scheduledDate AS date) = :date")
    List<LogisticTask> findPendingTasksByTypeAndDate(@Param("type") TaskType type, @Param("date") LocalDate date);

    @Query("SELECT lt FROM LogisticTask lt WHERE lt.driver.id = :driverId AND CAST(lt.scheduledDate AS date) = :date")
    List<LogisticTask> findByDriverAndDate(@Param("driverId") Long driverId, @Param("date") LocalDate date);

    @Query("SELECT lt FROM LogisticTask lt WHERE lt.driver.id = :driverId AND CAST(lt.scheduledDate AS date) = :date ORDER BY lt.availableFrom ASC")
    List<LogisticTask> findDriverTasksForDay(@Param("driverId") Long driverId, @Param("date") LocalDate date);

    @Query("SELECT lt FROM LogisticTask lt WHERE lt.driver.id = :driverId AND CAST(lt.scheduledDate AS date) = :date AND lt.status IN ('PENDING', 'IN_PROGRESS') ORDER BY lt.availableFrom ASC")
    List<LogisticTask> findDriverActiveTasksForDay(@Param("driverId") Long driverId, @Param("date") LocalDate date);

    @Query("SELECT lt FROM LogisticTask lt WHERE lt.status = 'PENDING' ORDER BY lt.scheduledDate ASC")
    List<LogisticTask> findAllPendingTasks();


    @Query("SELECT COUNT(lt) FROM LogisticTask lt WHERE lt.driver.id = :driverId AND lt.status = 'IN_PROGRESS' AND lt.type = :taskType")
    long countCurrentTasksByDriverAndType(@Param("driverId") Long driverId, @Param("taskType") TaskType taskType);

    @Query("SELECT COUNT(lt) FROM LogisticTask lt WHERE lt.driver.id = :driverId AND CAST(lt.scheduledDate AS date) = :date AND lt.type = :taskType")
    long countTasksByDriverAndDateAndType(@Param("driverId") Long driverId, @Param("date") LocalDate date, @Param("taskType") TaskType taskType);
    
    // Nouvelles méthodes pour la gestion des livraisons
    boolean existsByOrderAndType(Order order, TaskType type);
    
    List<LogisticTask> findByTypeAndScheduledDate(TaskType type, LocalDate date);
    
    List<LogisticTask> findByTypeAndDriver(TaskType type, UserYamo driver);
    
    List<LogisticTask> findByTypeAndScheduledDateAndDriver(TaskType type, LocalDate date, UserYamo driver);
}
