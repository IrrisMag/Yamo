package com.irris.yamo.service;

import com.irris.yamo.dtos.LaundryServiceDto;
import com.irris.yamo.dtos.ProcessStepDto;
import com.irris.yamo.dtos.creation.LaundryServiceCreationDto;
import com.irris.yamo.dtos.creation.ProcessStepCreationDto;
import com.irris.yamo.entities.Article;
import com.irris.yamo.entities.LaundryService;
import com.irris.yamo.entities.ProcessStep;

import java.util.List;

public interface laundryServiceManagement {

    LaundryServiceDto createLaundryService(LaundryServiceCreationDto laundryService);

    LaundryServiceDto getLaundryServiceById(Long id);

    LaundryServiceDto updateLaundryService(LaundryServiceDto laundryService, Long id);

    void removeProcessStepFromLaundryService(Long laundryServiceId, Long processStepId);

    List<LaundryServiceDto> getAllLaundryServices();


    void deleteLaundryService(Long id);

    ProcessStepDto getProcessStepById(Long processStepId);

    ProcessStepDto createProcessStep(ProcessStepCreationDto processStep);

    void deleteProcessStep(Long processStepId);

    ProcessStep addProcessStepToLaundryService(Long laundryServiceId, Long processStepId);

    ProcessStep updateProcessStep(ProcessStepDto processStep, Long processStepId);

    List<ProcessStepDto> getAllProcessSteps();

    void removeProcessStep(Long processStepId);


}
