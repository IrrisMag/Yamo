package com.irris.yamo.service.impl;

import com.irris.yamo.dtos.ComplaintDto;
import com.irris.yamo.dtos.creation.ComplaintCreationDto;
import com.irris.yamo.entities.Complaint;
import com.irris.yamo.entities.Order;
import com.irris.yamo.entities.UserYamo;
import com.irris.yamo.entities.enums.ComplaintStatus;
import com.irris.yamo.entities.enums.ComplaintType;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.repositories.ComplaintRepository;
import com.irris.yamo.repositories.OrderRepository;
import com.irris.yamo.repositories.UserRepository;
import com.irris.yamo.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public ComplaintDto createComplaint(ComplaintCreationDto dto) {
        UserYamo customer = userRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client non trouvé avec l'ID: " + dto.getCustomerId()));

        Complaint complaint = new Complaint();
        complaint.setCustomer(customer);
        complaint.setDescription(dto.getDescription());
        complaint.setType(ComplaintType.valueOf(dto.getType()));

        if (dto.getOrderId() != null) {
            Order order = orderRepository.findById(dto.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Commande non trouvée avec l'ID: " + dto.getOrderId()));
            complaint.setOrder(order);
        }

        complaint = complaintRepository.save(complaint);
        return toDto(complaint);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDto> getAllComplaints() {
        return complaintRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDto> getComplaintsByStatus(ComplaintStatus status) {
        return complaintRepository.findByStatus(status).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDto> getOpenComplaints() {
        return complaintRepository.findOpenComplaints().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintDto getComplaintById(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Réclamation non trouvée avec l'ID: " + id));
        return toDto(complaint);
    }

    @Override
    @Transactional
    public void assignComplaint(Long complaintId, Long userId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Réclamation non trouvée avec l'ID: " + complaintId));

        UserYamo user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur non trouvé avec l'ID: " + userId));

        complaint.setAssignedTo(user);
        complaint.setStatus(ComplaintStatus.IN_PROGRESS);
        complaintRepository.save(complaint);
    }

    @Override
    @Transactional
    public void updateComplaintStatus(Long complaintId, ComplaintStatus status) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Réclamation non trouvée avec l'ID: " + complaintId));

        complaint.setStatus(status);
        complaintRepository.save(complaint);
    }

    @Override
    @Transactional
    public void resolveComplaint(Long complaintId, String resolutionNotes) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Réclamation non trouvée avec l'ID: " + complaintId));

        complaint.setStatus(ComplaintStatus.RESOLVED);
        complaint.setResolutionNotes(resolutionNotes);
        complaint.setResolvedAt(LocalDateTime.now());
        complaintRepository.save(complaint);
    }

    @Override
    @Transactional
    public void deleteComplaint(Long complaintId) {
        if (!complaintRepository.existsById(complaintId)) {
            throw new ResourceNotFoundException(
                    "Réclamation non trouvée avec l'ID: " + complaintId);
        }
        complaintRepository.deleteById(complaintId);
    }

    private ComplaintDto toDto(Complaint complaint) {
        return ComplaintDto.builder()
                .id(complaint.getId())
                .customerId(complaint.getCustomer() != null ? complaint.getCustomer().getId() : null)
                .customerName(complaint.getCustomer() != null ? complaint.getCustomer().getFullName() : null)
                .orderId(complaint.getOrder() != null ? complaint.getOrder().getId() : null)
                .orderReference(complaint.getOrder() != null ? complaint.getOrder().getReference() : null)
                .type(complaint.getType() != null ? complaint.getType().name() : null)
                .description(complaint.getDescription())
                .status(complaint.getStatus() != null ? complaint.getStatus().name() : null)
                .createdAt(complaint.getCreatedAt())
                .resolvedAt(complaint.getResolvedAt())
                .assignedToId(complaint.getAssignedTo() != null ? complaint.getAssignedTo().getId() : null)
                .assignedToName(complaint.getAssignedTo() != null ? complaint.getAssignedTo().getFullName() : null)
                .resolutionNotes(complaint.getResolutionNotes())
                .build();
    }
}
