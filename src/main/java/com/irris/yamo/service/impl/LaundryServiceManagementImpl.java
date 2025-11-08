package com.irris.yamo.service.impl;

import com.irris.yamo.dtos.LaundryServiceDto;
import com.irris.yamo.dtos.ProcessStepDto;
import com.irris.yamo.dtos.creation.LaundryServiceCreationDto;
import com.irris.yamo.dtos.creation.ProcessStepCreationDto;
import com.irris.yamo.entities.LaundryService;
import com.irris.yamo.entities.ProcessStep;
import com.irris.yamo.exception.ResourceNotFoundException;
import com.irris.yamo.mapper.LaundryServiceMapper;
import com.irris.yamo.mapper.ProcessStepMapper;
import com.irris.yamo.repositories.LaundryServiceRepository;
import com.irris.yamo.repositories.ProcessStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaundryServiceManagementImpl implements com.irris.yamo.service.laundryServiceManagement {

    private final LaundryServiceRepository laundryServiceRepository;
    private final ProcessStepRepository processStepRepository;
    private final LaundryServiceMapper laundryServiceMapper;
    private final ProcessStepMapper processStepMapper;

    @Override
    @Transactional
    public LaundryServiceDto createLaundryService(LaundryServiceCreationDto serviceDto) {
        LaundryService service = new LaundryService();
        service.setName(serviceDto.getName());
        service.setDescription(serviceDto.getDescription());
        service.setPricePerKg(serviceDto.getPricePerKg() != null ? 
                BigDecimal.valueOf(serviceDto.getPricePerKg()) : null);
        service.setPricePerPiece(serviceDto.getPricePerItem() != null ? 
                BigDecimal.valueOf(serviceDto.getPricePerItem()) : null);
        service.setIsAvailable(true);

        // Associer les process steps si présents
        if (serviceDto.getProcessStepIds() != null && !serviceDto.getProcessStepIds().isEmpty()) {
            Set<ProcessStep> steps = new HashSet<>();
            for (Long stepId : serviceDto.getProcessStepIds()) {
                ProcessStep step = processStepRepository.findById(stepId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Étape de process non trouvée avec l'ID: " + stepId));
                steps.add(step);
            }
            service.setSteps(steps);
        }

        service = laundryServiceRepository.save(service);
        return laundryServiceMapper.toDto(service);
    }

    @Override
    @Transactional(readOnly = true)
    public LaundryServiceDto getLaundryServiceById(Long id) {
        LaundryService service = laundryServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service de blanchisserie non trouvé avec l'ID: " + id));
        return laundryServiceMapper.toDto(service);
    }

    @Override
    @Transactional
    public LaundryServiceDto updateLaundryService(LaundryServiceDto serviceDto, Long id) {
        LaundryService service = laundryServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service de blanchisserie non trouvé avec l'ID: " + id));

        if (serviceDto.getName() != null) {
            service.setName(serviceDto.getName());
        }
        if (serviceDto.getDescription() != null) {
            service.setDescription(serviceDto.getDescription());
        }
        if (serviceDto.getPricePerKg() != null) {
            service.setPricePerKg(serviceDto.getPricePerKg());
        }
        if (serviceDto.getPricePerItem() != null) {
            service.setPricePerPiece(serviceDto.getPricePerItem());
        }

        service = laundryServiceRepository.save(service);
        return laundryServiceMapper.toDto(service);
    }

    @Override
    @Transactional
    public void removeProcessStepFromLaundryService(Long laundryServiceId, Long processStepId) {
        LaundryService service = laundryServiceRepository.findById(laundryServiceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service de blanchisserie non trouvé avec l'ID: " + laundryServiceId));

        ProcessStep step = processStepRepository.findById(processStepId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Étape de process non trouvée avec l'ID: " + processStepId));

        service.removeProcessStep(step);
        laundryServiceRepository.save(service);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LaundryServiceDto> getAllLaundryServices() {
        List<LaundryService> services = laundryServiceRepository.findAll();
        return services.stream()
                .map(laundryServiceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteLaundryService(Long id) {
        if (!laundryServiceRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Service de blanchisserie non trouvé avec l'ID: " + id);
        }
        laundryServiceRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessStepDto getProcessStepById(Long processStepId) {
        ProcessStep step = processStepRepository.findById(processStepId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Étape de process non trouvée avec l'ID: " + processStepId));
        return processStepMapper.toDto(step);
    }

    @Override
    @Transactional
    public ProcessStepDto createProcessStep(ProcessStepCreationDto processStep) {
        ProcessStep step = ProcessStep.builder()
                .name("New Process Step")
                .description("Description")
                .isActive(true)
                .build();
        
        step = processStepRepository.save(step);
        return processStepMapper.toDto(step);
    }

    @Override
    @Transactional
    public void deleteProcessStep(Long processStepId) {
        if (!processStepRepository.existsById(processStepId)) {
            throw new ResourceNotFoundException(
                    "Étape de process non trouvée avec l'ID: " + processStepId);
        }
        processStepRepository.deleteById(processStepId);
    }

    @Override
    @Transactional
    public ProcessStep addProcessStepToLaundryService(Long laundryServiceId, Long processStepId) {
        LaundryService service = laundryServiceRepository.findById(laundryServiceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service de blanchisserie non trouvé avec l'ID: " + laundryServiceId));

        ProcessStep step = processStepRepository.findById(processStepId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Étape de process non trouvée avec l'ID: " + processStepId));

        service.addProcessStep(step);
        laundryServiceRepository.save(service);
        
        return step;
    }

    @Override
    @Transactional
    public ProcessStep updateProcessStep(ProcessStepDto processStepDto, Long processStepId) {
        ProcessStep step = processStepRepository.findById(processStepId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Étape de process non trouvée avec l'ID: " + processStepId));

        if (processStepDto.getName() != null) {
            step.setName(processStepDto.getName());
        }
        if (processStepDto.getDescription() != null) {
            step.setDescription(processStepDto.getDescription());
        }

        return processStepRepository.save(step);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessStepDto> getAllProcessSteps() {
        List<ProcessStep> steps = processStepRepository.findAll();
        return steps.stream()
                .map(processStepMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeProcessStep(Long processStepId) {
        deleteProcessStep(processStepId);
    }
}
