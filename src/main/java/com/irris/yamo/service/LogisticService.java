package com.irris.yamo.service;

import com.irris.yamo.dtos.LogisticTaskDto;
import com.irris.yamo.dtos.creation.LogisticTaskCreationDto;
import com.irris.yamo.entities.Article;
import com.irris.yamo.entities.ArticleInstance;
import com.irris.yamo.entities.Driver;
import com.irris.yamo.entities.LogisticTask;
import com.irris.yamo.entities.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface LogisticService {

    // ========== Gestion Tâches ==========
    LogisticTask createLogisticTask(Long orderId, LogisticTaskCreationDto request);

    LogisticTask getLogisticTaskById(Long taskId);

    LogisticTask scheduleLogisticTask(Long taskId, LocalDateTime scheduledDateTime);

    void updateLogisticStatus(Long taskId, TaskStatus status);

    void assignTaskToDriver(Long taskId, Long driverId);

    void startTask(Long taskId);

    void completeTask(Long taskId);

    List<LogisticTaskDto> getTasksByDriver(Long driverId);

    List<LogisticTaskDto> getTodayTasksForDriver(Long driverId);

    List<LogisticTaskDto> getPendingTasks();

    // ========== Pesée ==========
    List<Article> getArticlesToWeight(Long taskId);
    
    ArticleInstance recordWeighedArticle(Long articleId, double weight);

    boolean verifyAllArticlesWeighed(Long taskId);

    // ========== Signatures & Photos (POST/DELETE uniquement) ==========
    void recordSignature(Long taskId, String signaturePath);
    
    void addPhoto(Long taskId, String photoPath);
    
    void addPhotos(Long taskId, List<String> photoPaths);
    
    void removePhoto(Long taskId, String photoPath);

    // ========== Google Maps & Optimisation ==========
    Map<String, Object> calculateRouteToTask(Long taskId, Double driverLat, Double driverLon);
    
    Driver findNearestDriver(Long taskId);
    
    List<LogisticTask> optimizeDriverRoute(Long driverId, LocalDate date);
    
    void geocodeTaskAddress(Long taskId);
    
    Integer calculateEstimatedTravelTime(Long taskId, Double driverLat, Double driverLon);
    
    List<LogisticTask> getCurrentDriverRoute(Long driverId, LocalDate date);
    
    Map<String, Object> calculateRouteMetrics(Long driverId, LocalDate date);
    
    Driver findBestDriverForTask(Long taskId);
    
    Map<String, Object> autoAssignUrgentTask(Long taskId);
}
