package com.irris.yamo.service;

import com.irris.yamo.dtos.ComplaintDto;
import com.irris.yamo.dtos.creation.ComplaintCreationDto;
import com.irris.yamo.entities.Complaint;
import com.irris.yamo.entities.enums.ComplaintStatus;

import java.util.List;

public interface ComplaintService {
    
    ComplaintDto createComplaint(ComplaintCreationDto complaintDto);
    
    List<ComplaintDto> getAllComplaints();
    
    List<ComplaintDto> getComplaintsByStatus(ComplaintStatus status);
    
    List<ComplaintDto> getOpenComplaints();
    
    ComplaintDto getComplaintById(Long id);
    
    void assignComplaint(Long complaintId, Long userId);
    
    void updateComplaintStatus(Long complaintId, ComplaintStatus status);
    
    void resolveComplaint(Long complaintId, String resolutionNotes);
    
    void deleteComplaint(Long complaintId);
}
