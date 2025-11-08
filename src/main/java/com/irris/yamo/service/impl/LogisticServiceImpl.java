package com.irris.yamo.service.impl;

import com.irris.yamo.dtos.LogisticTaskDto;
import com.irris.yamo.dtos.creation.LogisticTaskCreationDto;
import com.irris.yamo.entities.*;
import com.irris.yamo.entities.enums.TaskStatus;
import com.irris.yamo.entities.enums.TaskType;
import com.irris.yamo.exception.InvalidOperationException;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.mapper.LogisticTaskMapper;
import com.irris.yamo.repositories.*;
import com.irris.yamo.service.LogisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogisticServiceImpl implements LogisticService {

    private final LogisticTaskRepository logisticTaskRepository;
    private final OrderRepository orderRepository;
    private final DriverRepository driverRepository;
    private final AddressRepository addressRepository;
    private final ArticleRepository articleRepository;
    private final ArticleInstanceRepository articleInstanceRepository;
    private final LogisticTaskMapper logisticTaskMapper;
    private final com.irris.yamo.service.GoogleMapsService googleMapsService;

    @Override
    @Transactional
    public LogisticTask createLogisticTask(Long orderId, LogisticTaskCreationDto request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande non trouvée avec l'ID: " + orderId));

        LogisticTask task = LogisticTask.builder()
                .order(order)
                .type(TaskType.valueOf(request.getTaskType().toUpperCase()))
                .status(TaskStatus.PENDING)
                .scheduledDate(request.getScheduledDate())
                .availableFrom(request.getTimeFrom())
                .availableTo(request.getTimeTo())
                .contactName(request.getContactName())
                .contactPhone(request.getContactPhone())
                .createdAt(LocalDateTime.now())
                .build();

        // Gérer l'adresse
        if (request.getExistingAddress() != null && request.getExistingAddress().getId() != null) {
            Adresse address = addressRepository.findById(request.getExistingAddress().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Adresse non trouvée avec l'ID: " + request.getExistingAddress().getId()));
            task.setAddress(address);
        }

        return logisticTaskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public LogisticTask getLogisticTaskById(Long taskId) {
        return logisticTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tâche logistique non trouvée avec l'ID: " + taskId));
    }

    @Override
    @Transactional
    public LogisticTask scheduleLogisticTask(Long taskId, LocalDateTime scheduledDateTime) {
        LogisticTask task = getLogisticTaskById(taskId);
        
        task.setScheduledDate(scheduledDateTime.toLocalDate());
        task.setDueAt(scheduledDateTime);
        
        return logisticTaskRepository.save(task);
    }

    @Override
    @Transactional
    public void updateLogisticStatus(Long taskId, TaskStatus status) {
        LogisticTask task = getLogisticTaskById(taskId);
        task.setStatus(status);
        logisticTaskRepository.save(task);
    }

    @Override
    @Transactional
    public void assignTaskToDriver(Long taskId, Long driverId) {
        LogisticTask task = getLogisticTaskById(taskId);
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chauffeur non trouvé avec l'ID: " + driverId));

        task.setDriver(driver);
        logisticTaskRepository.save(task);
        
        // Réoptimisation automatique de la tournée du chauffeur
        LocalDate taskDate = task.getScheduledDate() != null 
            ? task.getScheduledDate() 
            : LocalDate.now();
        
        List<LogisticTask> optimizedRoute = optimizeDriverRoute(driverId, taskDate);
        updateTaskSequence(optimizedRoute);
        
        // Notifier le chauffeur de la mise à jour
        notifyDriverRouteUpdate(driverId, optimizedRoute);
    }

    @Override
    @Transactional
    public void startTask(Long taskId) {
        LogisticTask task = getLogisticTaskById(taskId);
        
        if (task.getStatus() != TaskStatus.PENDING) {
            throw new InvalidOperationException(
                    "Seules les tâches en attente peuvent être démarrées");
        }
        
        task.start();
        logisticTaskRepository.save(task);
    }

    @Override
    @Transactional
    public void completeTask(Long taskId) {
        LogisticTask task = getLogisticTaskById(taskId);
        
        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new InvalidOperationException(
                    "Seules les tâches en cours peuvent être complétées");
        }
        
        task.complete();
        logisticTaskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogisticTaskDto> getTasksByDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chauffeur non trouvé avec l'ID: " + driverId));
        
        List<LogisticTask> tasks = logisticTaskRepository.findByDriver(driver);
        return tasks.stream()
                .map(logisticTaskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogisticTaskDto> getTodayTasksForDriver(Long driverId) {
        LocalDate today = LocalDate.now();
        List<LogisticTask> tasks = logisticTaskRepository.findDriverTasksForDay(driverId, today);
        
        return tasks.stream()
                .map(logisticTaskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogisticTaskDto> getPendingTasks() {
        List<LogisticTask> tasks = logisticTaskRepository.findAllPendingTasks();
        return tasks.stream()
                .map(logisticTaskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Article> getArticlesToWeight(Long taskId) {
        LogisticTask task = getLogisticTaskById(taskId);
        
        if (task.getOrder() == null) {
            return List.of();
        }
        
        // Récupérer les articles de la commande qui nécessitent une pesée
        return articleRepository.findByOrderId(task.getOrder().getId()).stream()
                .filter(article -> article.getActualWeight() == null || 
                                 article.getActualWeight().compareTo(BigDecimal.ZERO) == 0)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ArticleInstance recordWeighedArticle(Long articleId, double weight) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Article non trouvé avec l'ID: " + articleId));
        
        // Enregistrer le poids réel
        article.setActualWeight(BigDecimal.valueOf(weight));
        articleRepository.save(article);
        
        // Retourner la première instance (ou null)
        return article.getInstances().stream().findFirst().orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyAllArticlesWeighed(Long taskId) {
        List<Article> articlesToWeigh = getArticlesToWeight(taskId);
        return articlesToWeigh.isEmpty();
    }

    // ========== Gestion Signatures et Photos ==========

    @Transactional
    public void recordSignature(Long taskId, String signaturePath) {
        LogisticTask task = getLogisticTaskById(taskId);
        task.setSignaturePath(signaturePath);
        logisticTaskRepository.save(task);
    }

    @Transactional
    public void addPhoto(Long taskId, String photoPath) {
        LogisticTask task = getLogisticTaskById(taskId);
        if (task.getPhotoPaths() == null) {
            task.setPhotoPaths(new java.util.ArrayList<>());
        }
        task.getPhotoPaths().add(photoPath);
        logisticTaskRepository.save(task);
    }

    @Transactional
    public void addPhotos(Long taskId, List<String> photoPaths) {
        LogisticTask task = getLogisticTaskById(taskId);
        if (task.getPhotoPaths() == null) {
            task.setPhotoPaths(new java.util.ArrayList<>());
        }
        task.getPhotoPaths().addAll(photoPaths);
        logisticTaskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public String getSignature(Long taskId) {
        LogisticTask task = getLogisticTaskById(taskId);
        return task.getSignaturePath();
    }

    @Transactional(readOnly = true)
    public List<String> getPhotos(Long taskId) {
        LogisticTask task = getLogisticTaskById(taskId);
        return task.getPhotoPaths() != null ? task.getPhotoPaths() : List.of();
    }

    @Transactional
    public void removePhoto(Long taskId, String photoPath) {
        LogisticTask task = getLogisticTaskById(taskId);
        if (task.getPhotoPaths() != null) {
            task.getPhotoPaths().remove(photoPath);
            logisticTaskRepository.save(task);
        }
    }

    @Transactional(readOnly = true)
    public boolean hasSignature(Long taskId) {
        LogisticTask task = getLogisticTaskById(taskId);
        return task.getSignaturePath() != null && !task.getSignaturePath().isEmpty();
    }

    @Transactional(readOnly = true)
    public boolean hasPhotos(Long taskId) {
        LogisticTask task = getLogisticTaskById(taskId);
        return task.getPhotoPaths() != null && !task.getPhotoPaths().isEmpty();
    }

    // ========== Intégration Google Maps ==========

    /**
     * Calculer l'itinéraire réel vers une tâche (avec Google Maps)
     */
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> calculateRouteToTask(Long taskId, Double driverLat, Double driverLon) {
        LogisticTask task = getLogisticTaskById(taskId);
        
        if (task.getAddress() == null || 
            task.getAddress().getLatitude() == null || 
            task.getAddress().getLongitude() == null) {
            throw new InvalidOperationException("L'adresse de la tâche n'a pas de coordonnées GPS");
        }

        return googleMapsService.getDirections(
            driverLat, driverLon,
            task.getAddress().getLatitude(), 
            task.getAddress().getLongitude()
        );
    }

    /**
     * Trouver le chauffeur le plus proche d'une tâche
     */
    @Transactional(readOnly = true)
    public Driver findNearestDriver(Long taskId) {
        LogisticTask task = getLogisticTaskById(taskId);
        List<Driver> availableDrivers = driverRepository.findAll().stream()
                .filter(d -> d.getIsAvailable())
                .collect(Collectors.toList());

        if (availableDrivers.isEmpty()) {
            throw new InvalidOperationException("Aucun chauffeur disponible");
        }

        Driver nearest = null;
        Double minDistance = Double.MAX_VALUE;

        for (Driver driver : availableDrivers) {
            // Utiliser position actuelle ou dernière position connue
            // TODO: Implémenter tracking position temps réel
            if (task.getAddress() != null && 
                task.getAddress().getLatitude() != null && 
                task.getAddress().getLongitude() != null) {
                
                // Distance à vol d'oiseau pour commencer
                Double distance = task.getDistanceFromDriver(
                    task.getAddress().getLatitude(), 
                    task.getAddress().getLongitude()
                );
                
                if (distance != null && distance < minDistance) {
                    minDistance = distance;
                    nearest = driver;
                }
            }
        }

        return nearest;
    }

    /**
     * Optimiser l'ordre des tâches pour un chauffeur (tournée)
     * Utilise l'algorithme Nearest Neighbor avec contraintes horaires
     */
    @Transactional(readOnly = true)
    public List<LogisticTask> optimizeDriverRoute(Long driverId, LocalDate date) {
        List<LogisticTask> tasks = logisticTaskRepository.findDriverTasksForDay(driverId, date);
        
        if (tasks.isEmpty()) {
            return tasks;
        }

        // Filtrer les tâches avec coordonnées GPS valides
        List<LogisticTask> validTasks = tasks.stream()
                .filter(t -> t.getAddress() != null && 
                           t.getAddress().getLatitude() != null && 
                           t.getAddress().getLongitude() != null)
                .collect(Collectors.toList());

        if (validTasks.size() <= 1) {
            return validTasks;
        }

        // Séparer par type et plages horaires strictes
        List<LogisticTask> morningPickups = new java.util.ArrayList<>();
        List<LogisticTask> afternoonDeliveries = new java.util.ArrayList<>();
        List<LogisticTask> flexibleTasks = new java.util.ArrayList<>();

        for (LogisticTask task : validTasks) {
            if (task.getAvailableFrom() != null && task.getAvailableTo() != null) {
                if (task.getType() == TaskType.PICKUP && 
                    task.getAvailableFrom().isBefore(java.time.LocalTime.of(12, 0))) {
                    morningPickups.add(task);
                } else if (task.getType() == TaskType.DELIVERY && 
                          task.getAvailableFrom().isAfter(java.time.LocalTime.of(12, 0))) {
                    afternoonDeliveries.add(task);
                } else {
                    flexibleTasks.add(task);
                }
            } else {
                flexibleTasks.add(task);
            }
        }

        // Optimiser chaque groupe séparément
        List<LogisticTask> optimizedRoute = new java.util.ArrayList<>();
        
        if (!morningPickups.isEmpty()) {
            optimizedRoute.addAll(optimizeTaskGroup(morningPickups));
        }
        
        if (!flexibleTasks.isEmpty()) {
            optimizedRoute.addAll(optimizeTaskGroup(flexibleTasks));
        }
        
        if (!afternoonDeliveries.isEmpty()) {
            optimizedRoute.addAll(optimizeTaskGroup(afternoonDeliveries));
        }

        return optimizedRoute;
    }

    /**
     * Optimise un groupe de tâches avec l'algorithme Nearest Neighbor
     */
    private List<LogisticTask> optimizeTaskGroup(List<LogisticTask> tasks) {
        if (tasks.size() <= 1) {
            return new java.util.ArrayList<>(tasks);
        }

        List<LogisticTask> remaining = new java.util.ArrayList<>(tasks);
        List<LogisticTask> optimized = new java.util.ArrayList<>();

        // Commencer par la tâche la plus proche du pressing (ou première tâche)
        // Pour simplifier, on commence par la première tâche par ordre horaire
        LogisticTask current = remaining.stream()
                .min((t1, t2) -> {
                    if (t1.getAvailableFrom() == null) return 1;
                    if (t2.getAvailableFrom() == null) return -1;
                    return t1.getAvailableFrom().compareTo(t2.getAvailableFrom());
                })
                .orElse(remaining.get(0));

        optimized.add(current);
        remaining.remove(current);

        // Algorithme Nearest Neighbor
        while (!remaining.isEmpty()) {
            LogisticTask finalCurrent = current;
            
            // Trouver la tâche la plus proche de la tâche actuelle
            LogisticTask nearest = null;
            double minDistance = Double.MAX_VALUE;

            for (LogisticTask task : remaining) {
                // Vérifier contraintes horaires
                if (!canVisitAfter(finalCurrent, task)) {
                    continue;
                }

                double distance = calculateDistance(
                    finalCurrent.getAddress().getLatitude(),
                    finalCurrent.getAddress().getLongitude(),
                    task.getAddress().getLatitude(),
                    task.getAddress().getLongitude()
                );

                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = task;
                }
            }

            // Si aucune tâche compatible trouvée, prendre la première restante
            if (nearest == null && !remaining.isEmpty()) {
                nearest = remaining.get(0);
            }

            if (nearest != null) {
                optimized.add(nearest);
                remaining.remove(nearest);
                current = nearest;
            } else {
                break;
            }
        }

        // Ajouter les tâches restantes (si contraintes non satisfaites)
        optimized.addAll(remaining);

        return optimized;
    }

    /**
     * Vérifie si on peut visiter task2 après task1 (contraintes horaires)
     */
    private boolean canVisitAfter(LogisticTask task1, LogisticTask task2) {
        if (task1.getAvailableTo() == null || task2.getAvailableFrom() == null) {
            return true; // Pas de contrainte
        }

        // La fenêtre de task2 doit commencer après ou pendant la fin de task1
        // + temps de trajet estimé
        return task2.getAvailableFrom().isAfter(task1.getAvailableTo().minusMinutes(30)) ||
               task2.getAvailableFrom().equals(task1.getAvailableTo());
    }

    /**
     * Calcule la distance entre deux points (Haversine)
     */
    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return Double.MAX_VALUE;
        }

        final int EARTH_RADIUS = 6371; // km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * Géocoder une adresse lors de la création de tâche
     */
    @Transactional
    public void geocodeTaskAddress(Long taskId) {
        LogisticTask task = getLogisticTaskById(taskId);
        
        if (task.getAddress() != null && 
            (task.getAddress().getLatitude() == null || task.getAddress().getLongitude() == null)) {
            
            String fullAddress = task.getAddress().getFullAddress();
            java.util.Map<String, Double> coords = googleMapsService.geocodeAddress(fullAddress);
            
            if (coords != null) {
                task.getAddress().setLatitude(coords.get("latitude"));
                task.getAddress().setLongitude(coords.get("longitude"));
                addressRepository.save(task.getAddress());
            }
        }
    }

    /**
     * Calculer le temps de trajet estimé vers une tâche
     */
    @Transactional(readOnly = true)
    public Integer calculateEstimatedTravelTime(Long taskId, Double driverLat, Double driverLon) {
        java.util.Map<String, Object> route = calculateRouteToTask(taskId, driverLat, driverLon);
        
        if (route != null && route.containsKey("durationMinutes")) {
            return (Integer) route.get("durationMinutes");
        }
        
        return null;
    }

    // ========== Réoptimisation Dynamique ==========

    /**
     * Met à jour la séquence d'exécution des tâches
     */
    @Transactional
    public void updateTaskSequence(List<LogisticTask> orderedTasks) {
        for (int i = 0; i < orderedTasks.size(); i++) {
            LogisticTask task = orderedTasks.get(i);
            task.setSequenceOrder(i + 1);
            logisticTaskRepository.save(task);
        }
    }

    /**
     * Réoptimise la tournée d'un chauffeur pour une date donnée
     * (peut être appelé manuellement)
     */
    @Transactional
    public List<LogisticTask> reoptimizeDriverRoute(Long driverId, LocalDate date) {
        List<LogisticTask> optimizedRoute = optimizeDriverRoute(driverId, date);
        updateTaskSequence(optimizedRoute);
        return optimizedRoute;
    }

    /**
     * Trouve le meilleur chauffeur pour une tâche urgente
     * et l'assigne automatiquement avec réoptimisation
     */
    @Transactional
    public java.util.Map<String, Object> autoAssignUrgentTask(Long taskId) {
        LogisticTask task = getLogisticTaskById(taskId);
        
        // Trouver le chauffeur optimal
        Driver bestDriver = findBestDriverForTask(taskId);
        
        if (bestDriver == null) {
            throw new InvalidOperationException("Aucun chauffeur disponible pour cette tâche");
        }
        
        // Assigner (réoptimisation automatique dans assignTaskToDriver)
        assignTaskToDriver(taskId, bestDriver.getId());
        
        // Récupérer la tournée mise à jour
        LocalDate taskDate = task.getScheduledDate() != null 
            ? task.getScheduledDate() 
            : LocalDate.now();
        List<LogisticTask> newRoute = getCurrentDriverRoute(bestDriver.getId(), taskDate);
        
        // Préparer la réponse
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("taskId", taskId);
        result.put("assignedDriverId", bestDriver.getId());
        result.put("driverName", bestDriver.getFullName());
        result.put("optimizedRouteSize", newRoute.size());
        result.put("message", "Tâche assignée et tournée réoptimisée automatiquement");
        
        return result;
    }

    /**
     * Trouve le meilleur chauffeur pour une tâche en tenant compte de :
     * - Disponibilité
     * - Proximité
     * - Charge de travail actuelle
     */
    @Transactional(readOnly = true)
    public Driver findBestDriverForTask(Long taskId) {
        LogisticTask task = getLogisticTaskById(taskId);
        
        if (task.getAddress() == null || 
            task.getAddress().getLatitude() == null || 
            task.getAddress().getLongitude() == null) {
            throw new InvalidOperationException("La tâche n'a pas d'adresse géolocalisée");
        }
        
        List<Driver> availableDrivers = driverRepository.findAll().stream()
                .filter(Driver::getIsAvailable)
                .collect(Collectors.toList());
        
        if (availableDrivers.isEmpty()) {
            return null;
        }
        
        LocalDate taskDate = task.getScheduledDate() != null 
            ? task.getScheduledDate() 
            : LocalDate.now();
        
        Driver bestDriver = null;
        double bestScore = Double.MAX_VALUE;
        
        for (Driver driver : availableDrivers) {
            // Calculer le nombre de tâches déjà assignées
            List<LogisticTask> driverTasks = logisticTaskRepository.findDriverTasksForDay(driver.getId(), taskDate);
            int taskCount = driverTasks.size();
            
            // Si le chauffeur a déjà trop de tâches (>10), passer
            if (taskCount > 10) {
                continue;
            }
            
            // Calculer distance moyenne vers la nouvelle tâche
            double distance = Double.MAX_VALUE;
            
            if (!driverTasks.isEmpty()) {
                // Distance depuis la dernière tâche du chauffeur
                LogisticTask lastTask = driverTasks.get(driverTasks.size() - 1);
                if (lastTask.getAddress() != null && 
                    lastTask.getAddress().getLatitude() != null) {
                    distance = calculateDistance(
                        lastTask.getAddress().getLatitude(),
                        lastTask.getAddress().getLongitude(),
                        task.getAddress().getLatitude(),
                        task.getAddress().getLongitude()
                    );
                }
            } else {
                // Distance depuis la position actuelle (ou pressing)
                Double distanceFromDriver = task.getDistanceFromDriver(
                    task.getAddress().getLatitude(),
                    task.getAddress().getLongitude()
                );
                if (distanceFromDriver != null) {
                    distance = distanceFromDriver;
                } else {
                    distance = 10.0; // Distance par défaut
                }
            }
            
            // Score = distance + pénalité pour charge de travail
            double score = distance + (taskCount * 2.0); // 2km par tâche déjà assignée
            
            if (score < bestScore) {
                bestScore = score;
                bestDriver = driver;
            }
        }
        
        return bestDriver;
    }

    /**
     * Obtient la tournée actuelle d'un chauffeur avec ordre de séquence
     */
    @Transactional(readOnly = true)
    public List<LogisticTask> getCurrentDriverRoute(Long driverId, LocalDate date) {
        List<LogisticTask> tasks = logisticTaskRepository.findDriverTasksForDay(driverId, date);
        
        // Trier par sequenceOrder si disponible, sinon par heure
        return tasks.stream()
                .sorted((t1, t2) -> {
                    if (t1.getSequenceOrder() != null && t2.getSequenceOrder() != null) {
                        return t1.getSequenceOrder().compareTo(t2.getSequenceOrder());
                    }
                    if (t1.getAvailableFrom() == null) return 1;
                    if (t2.getAvailableFrom() == null) return -1;
                    return t1.getAvailableFrom().compareTo(t2.getAvailableFrom());
                })
                .collect(Collectors.toList());
    }

    /**
     * Calcule les métriques d'une tournée (distance totale, durée estimée)
     */
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> calculateRouteMetrics(Long driverId, LocalDate date) {
        List<LogisticTask> route = getCurrentDriverRoute(driverId, date);
        
        if (route.isEmpty()) {
            java.util.Map<String, Object> empty = new java.util.HashMap<>();
            empty.put("totalTasks", 0);
            empty.put("totalDistanceKm", 0.0);
            empty.put("estimatedDurationMinutes", 0);
            return empty;
        }
        
        double totalDistance = 0.0;
        int totalDuration = 0;
        
        // Calculer distance et durée entre chaque tâche consécutive
        for (int i = 0; i < route.size() - 1; i++) {
            LogisticTask current = route.get(i);
            LogisticTask next = route.get(i + 1);
            
            if (current.getAddress() != null && next.getAddress() != null &&
                current.getAddress().getLatitude() != null && next.getAddress().getLatitude() != null) {
                
                double distance = calculateDistance(
                    current.getAddress().getLatitude(),
                    current.getAddress().getLongitude(),
                    next.getAddress().getLatitude(),
                    next.getAddress().getLongitude()
                );
                
                totalDistance += distance;
                
                // Estimer durée (vitesse moyenne 30 km/h en ville)
                totalDuration += (int) (distance / 30.0 * 60); // minutes
            }
        }
        
        // Ajouter temps d'arrêt moyen par tâche (15 min)
        totalDuration += route.size() * 15;
        
        java.util.Map<String, Object> metrics = new java.util.HashMap<>();
        metrics.put("driverId", driverId);
        metrics.put("date", date);
        metrics.put("totalTasks", route.size());
        metrics.put("totalDistanceKm", Math.round(totalDistance * 100.0) / 100.0);
        metrics.put("estimatedDurationMinutes", totalDuration);
        metrics.put("estimatedDurationHours", Math.round(totalDuration / 60.0 * 10.0) / 10.0);
        
        return metrics;
    }

    /**
     * Notifie un chauffeur de sa tournée mise à jour
     * (à connecter avec NotificationService)
     */
    @Transactional
    public void notifyDriverRouteUpdate(Long driverId, List<LogisticTask> newRoute) {
        // TODO: Intégrer avec NotificationService
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Chauffeur non trouvé"));
        
        String message = String.format(
            "Votre tournée a été mise à jour. Vous avez maintenant %d tâches.",
            newRoute.size()
        );
        
        // Log pour l'instant
        System.out.println("NOTIFICATION CHAUFFEUR " + driver.getFullName() + ": " + message);
        
        // En production, utiliser:
        // notificationService.sendDriverNotification(driverId, message);
    }
}
