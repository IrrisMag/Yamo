package com.irris.yamo.controller;

import com.irris.yamo.dtos.LaundryServiceDto;
import com.irris.yamo.dtos.ProcessStepDto;
import com.irris.yamo.dtos.creation.LaundryServiceCreationDto;
import com.irris.yamo.entities.ProcessStep;
import com.irris.yamo.service.laundryServiceManagement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laundry-services")
@RequiredArgsConstructor
public class LaundryServiceController {

    private final laundryServiceManagement laundryService;

    @PostMapping
    public ResponseEntity<LaundryServiceDto> createService(@RequestBody LaundryServiceCreationDto dto) {
        LaundryServiceDto service = laundryService.createLaundryService(dto);
        return new ResponseEntity<>(service, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<LaundryServiceDto>> getAllServices() {
        List<LaundryServiceDto> services = laundryService.getAllLaundryServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LaundryServiceDto> getService(@PathVariable Long id) {
        LaundryServiceDto service = laundryService.getLaundryServiceById(id);
        return ResponseEntity.ok(service);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LaundryServiceDto> updateService(
            @PathVariable Long id, 
            @RequestBody LaundryServiceDto dto) {
        LaundryServiceDto service = laundryService.updateLaundryService(dto, id);
        return ResponseEntity.ok(service);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        laundryService.deleteLaundryService(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{serviceId}/steps/{stepId}")
    public ResponseEntity<ProcessStep> addProcessStep(
            @PathVariable Long serviceId,
            @PathVariable Long stepId) {
        ProcessStep step = laundryService.addProcessStepToLaundryService(serviceId, stepId);
        return ResponseEntity.ok(step);
    }

    @DeleteMapping("/{serviceId}/steps/{stepId}")
    public ResponseEntity<Void> removeProcessStep(
            @PathVariable Long serviceId,
            @PathVariable Long stepId) {
        laundryService.removeProcessStepFromLaundryService(serviceId, stepId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/steps")
    public ResponseEntity<List<ProcessStepDto>> getAllSteps() {
        List<ProcessStepDto> steps = laundryService.getAllProcessSteps();
        return ResponseEntity.ok(steps);
    }
}
