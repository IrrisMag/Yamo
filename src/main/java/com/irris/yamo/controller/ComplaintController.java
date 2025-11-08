package com.irris.yamo.controller;

import com.irris.yamo.dtos.ComplaintDto;
import com.irris.yamo.dtos.creation.ComplaintCreationDto;
import com.irris.yamo.entities.enums.ComplaintStatus;
import com.irris.yamo.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<ComplaintDto> createComplaint(@RequestBody ComplaintCreationDto dto) {
        ComplaintDto complaint = complaintService.createComplaint(dto);
        return new ResponseEntity<>(complaint, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ComplaintDto>> getAllComplaints() {
        List<ComplaintDto> complaints = complaintService.getAllComplaints();
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/open")
    public ResponseEntity<List<ComplaintDto>> getOpenComplaints() {
        List<ComplaintDto> complaints = complaintService.getOpenComplaints();
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ComplaintDto>> getComplaintsByStatus(@PathVariable ComplaintStatus status) {
        List<ComplaintDto> complaints = complaintService.getComplaintsByStatus(status);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintDto> getComplaint(@PathVariable Long id) {
        ComplaintDto complaint = complaintService.getComplaintById(id);
        return ResponseEntity.ok(complaint);
    }

    @PutMapping("/{id}/assign/{userId}")
    public ResponseEntity<Map<String, String>> assignComplaint(
            @PathVariable Long id,
            @PathVariable Long userId) {
        complaintService.assignComplaint(id, userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Réclamation assignée");
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> updateStatus(
            @PathVariable Long id,
            @RequestParam ComplaintStatus status) {
        complaintService.updateComplaintStatus(id, status);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Statut mis à jour");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<Map<String, String>> resolveComplaint(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String resolutionNotes = body.get("resolutionNotes");
        complaintService.resolveComplaint(id, resolutionNotes);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Réclamation résolue");
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.noContent().build();
    }
}
