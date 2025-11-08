package com.irris.yamo.controller;

import com.irris.yamo.dtos.LogisticTaskDto;
import com.irris.yamo.dtos.creation.LogisticTaskCreationDto;
import com.irris.yamo.entities.Article;
import com.irris.yamo.entities.ArticleInstance;
import com.irris.yamo.entities.LogisticTask;
import com.irris.yamo.entities.enums.TaskStatus;
import com.irris.yamo.service.LogisticService;
import com.irris.yamo.service.impl.LogisticServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logistics")
@RequiredArgsConstructor
public class LogisticController {

    private final LogisticService logisticService;
    // ✅ SUPPRIMÉ : LogisticServiceImpl - utilise uniquement l'interface

    @PostMapping("/tasks/order/{orderId}")
    public ResponseEntity<LogisticTask> createTask(
            @PathVariable Long orderId,
            @RequestBody LogisticTaskCreationDto request) {
        LogisticTask task = logisticService.createLogisticTask(orderId, request);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<LogisticTask> getTask(@PathVariable Long taskId) {
        LogisticTask task = logisticService.getLogisticTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/tasks/{taskId}/schedule")
    public ResponseEntity<LogisticTask> scheduleTask(
            @PathVariable Long taskId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledDateTime) {
        LogisticTask task = logisticService.scheduleLogisticTask(taskId, scheduledDateTime);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<Map<String, String>> updateStatus(
            @PathVariable Long taskId,
            @RequestParam TaskStatus status) {
        logisticService.updateLogisticStatus(taskId, status);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Statut mis à jour");
        response.put("status", status.name());
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/tasks/{taskId}/assign/{driverId}")
    public ResponseEntity<Map<String, String>> assignDriver(
            @PathVariable Long taskId,
            @PathVariable Long driverId) {
        logisticService.assignTaskToDriver(taskId, driverId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Chauffeur assigné");
        response.put("taskId", taskId.toString());
        response.put("driverId", driverId.toString());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tasks/{taskId}/start")
    public ResponseEntity<Map<String, String>> startTask(@PathVariable Long taskId) {
        logisticService.startTask(taskId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Tâche démarrée");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tasks/{taskId}/complete")
    public ResponseEntity<Map<String, String>> completeTask(@PathVariable Long taskId) {
        logisticService.completeTask(taskId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Tâche complétée");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tasks/driver/{driverId}")
    public ResponseEntity<List<LogisticTaskDto>> getDriverTasks(@PathVariable Long driverId) {
        List<LogisticTaskDto> tasks = logisticService.getTasksByDriver(driverId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/driver/{driverId}/today")
    public ResponseEntity<List<LogisticTaskDto>> getTodayTasks(@PathVariable Long driverId) {
        List<LogisticTaskDto> tasks = logisticService.getTodayTasksForDriver(driverId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/pending")
    public ResponseEntity<List<LogisticTaskDto>> getPendingTasks() {
        List<LogisticTaskDto> tasks = logisticService.getPendingTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/{taskId}/articles/to-weigh")
    public ResponseEntity<List<Article>> getArticlesToWeigh(@PathVariable Long taskId) {
        List<Article> articles = logisticService.getArticlesToWeight(taskId);
        return ResponseEntity.ok(articles);
    }

    @PostMapping("/articles/{articleId}/weigh")
    public ResponseEntity<ArticleInstance> recordWeight(
            @PathVariable Long articleId,
            @RequestParam double weight) {
        ArticleInstance instance = logisticService.recordWeighedArticle(articleId, weight);
        return ResponseEntity.ok(instance);
    }

    @GetMapping("/tasks/{taskId}/verify-weighing")
    public ResponseEntity<Map<String, Boolean>> verifyWeighing(@PathVariable Long taskId) {
        boolean allWeighed = logisticService.verifyAllArticlesWeighed(taskId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("allArticlesWeighed", allWeighed);
        
        return ResponseEntity.ok(response);
    }

    // ========== Signatures et Photos ==========

    @PostMapping("/tasks/{taskId}/signature")
    public ResponseEntity<Map<String, String>> recordSignature(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> request) {
        String signaturePath = request.get("signaturePath");
        logisticService.recordSignature(taskId, signaturePath);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Signature enregistrée");
        response.put("signaturePath", signaturePath);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tasks/{taskId}/photos")
    public ResponseEntity<Map<String, String>> addPhoto(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> request) {
        String photoPath = request.get("photoPath");
        logisticService.addPhoto(taskId, photoPath);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Photo ajoutée");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tasks/{taskId}/photos/batch")
    public ResponseEntity<Map<String, String>> addPhotos(
            @PathVariable Long taskId,
            @RequestBody Map<String, List<String>> request) {
        List<String> photoPaths = request.get("photoPaths");
        logisticService.addPhotos(taskId, photoPaths);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", photoPaths.size() + " photos ajoutées");
        
        return ResponseEntity.ok(response);
    }

    // ❌ SUPPRIMÉ : GET /tasks/{taskId}/signature - inclus dans le DTO
    // ❌ SUPPRIMÉ : GET /tasks/{taskId}/photos - inclus dans le DTO

    @DeleteMapping("/tasks/{taskId}/photos")
    public ResponseEntity<Map<String, String>> removePhoto(
            @PathVariable Long taskId,
            @RequestParam String photoPath) {
        logisticService.removePhoto(taskId, photoPath);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Photo supprimée");
        
        return ResponseEntity.ok(response);
    }

    // ❌ SUPPRIMÉ : GET /tasks/{taskId}/has-signature - vérifier via signaturePath != null dans DTO
    // ❌ SUPPRIMÉ : GET /tasks/{taskId}/has-photos - vérifier via photoPaths.size() > 0 dans DTO

    // ========== Intégration Google Maps ==========

    @GetMapping("/tasks/{taskId}/route")
    public ResponseEntity<Map<String, Object>> getRouteToTask(
            @PathVariable Long taskId,
            @RequestParam Double driverLat,
            @RequestParam Double driverLon) {
        Map<String, Object> route = logisticService.calculateRouteToTask(taskId, driverLat, driverLon);
        return ResponseEntity.ok(route);
    }

    @GetMapping("/tasks/{taskId}/nearest-driver")
    public ResponseEntity<Map<String, Object>> findNearestDriver(@PathVariable Long taskId) {
        com.irris.yamo.entities.Driver driver = logisticService.findNearestDriver(taskId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("driverId", driver.getId());
        response.put("driverName", driver.getFullName());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/drivers/{driverId}/optimized-route")
    public ResponseEntity<List<LogisticTaskDto>> getOptimizedRoute(
            @PathVariable Long driverId,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) 
            java.time.LocalDate date) {
        List<com.irris.yamo.entities.LogisticTask> tasks = logisticService.optimizeDriverRoute(driverId, date);
        List<LogisticTaskDto> dtos = tasks.stream()
                .map(this::toDto)
                .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/tasks/{taskId}/geocode-address")
    public ResponseEntity<Map<String, String>> geocodeTaskAddress(@PathVariable Long taskId) {
        logisticService.geocodeTaskAddress(taskId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Adresse géocodée avec succès");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tasks/{taskId}/travel-time")
    public ResponseEntity<Map<String, Object>> getEstimatedTravelTime(
            @PathVariable Long taskId,
            @RequestParam Double driverLat,
            @RequestParam Double driverLon) {
        Integer travelTime = logisticService.calculateEstimatedTravelTime(taskId, driverLat, driverLon);
        
        Map<String, Object> response = new HashMap<>();
        response.put("travelTimeMinutes", travelTime);
        
        return ResponseEntity.ok(response);
    }

    // Méthode helper pour conversion DTO
    private LogisticTaskDto toDto(com.irris.yamo.entities.LogisticTask task) {
        // Utilise le mapper existant via logisticService
        return LogisticTaskDto.builder()
                .id(task.getId())
                .taskType(task.getType() != null ? task.getType().name() : null)
                .status(task.getStatus() != null ? task.getStatus().name() : null)
                .scheduledDate(task.getScheduledDate())
                .build();
    }

    // ========== Réoptimisation Dynamique ==========

    @PostMapping("/tasks/{taskId}/auto-assign-urgent")
    public ResponseEntity<Map<String, Object>> autoAssignUrgentTask(@PathVariable Long taskId) {
        Map<String, Object> result = logisticService.autoAssignUrgentTask(taskId);
        return ResponseEntity.ok(result);
    }

    // ❌ SUPPRIMÉ : POST /drivers/{driverId}/reoptimize-route - doublon de optimizeDriverRoute()
    // L'optimisation se fait automatiquement dans assignTaskToDriver()

    @GetMapping("/drivers/{driverId}/current-route")
    public ResponseEntity<List<LogisticTaskDto>> getCurrentDriverRoute(
            @PathVariable Long driverId,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) 
            java.time.LocalDate date) {
        List<com.irris.yamo.entities.LogisticTask> route = 
            logisticService.getCurrentDriverRoute(driverId, date);
        
        List<LogisticTaskDto> dtos = route.stream()
                .map(this::toDto)
                .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/drivers/{driverId}/route-metrics")
    public ResponseEntity<Map<String, Object>> getRouteMetrics(
            @PathVariable Long driverId,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) 
            java.time.LocalDate date) {
        Map<String, Object> metrics = logisticService.calculateRouteMetrics(driverId, date);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/tasks/{taskId}/best-driver")
    public ResponseEntity<Map<String, Object>> findBestDriverForTask(@PathVariable Long taskId) {
        com.irris.yamo.entities.Driver driver = logisticService.findBestDriverForTask(taskId);
        
        if (driver == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Aucun chauffeur disponible");
            return ResponseEntity.ok(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("driverId", driver.getId());
        response.put("driverName", driver.getFullName());
        response.put("email", driver.getEmail());
        response.put("phoneNumber", driver.getPhoneNumber());
        
        return ResponseEntity.ok(response);
    }

    // ❌ SUPPRIMÉ : POST /drivers/{driverId}/notify-route-update
    // Méthode interne appelée automatiquement par assignTaskToDriver()
}
