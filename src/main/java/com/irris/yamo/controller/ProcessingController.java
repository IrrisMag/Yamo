package com.irris.yamo.controller;

import com.irris.yamo.dtos.ArticleInstanceDto;
import com.irris.yamo.entities.ArticleInstance;
import com.irris.yamo.mapper.ArticleInstanceMapper;
import com.irris.yamo.service.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/processing")
@RequiredArgsConstructor
public class ProcessingController {

    private final ProcessingService processingService;
    private final ArticleInstanceMapper articleInstanceMapper;

    /**
     * Obtenir les instances d'articles pour une étape de process
     */
    @GetMapping("/step/{processStepId}/instances")
    public ResponseEntity<List<ArticleInstanceDto>> getArticleInstancesByProcessStep(
            @PathVariable Long processStepId) {
        List<ArticleInstance> instances = processingService.getArticleInstancesByProcessStep(processStepId);
        List<ArticleInstanceDto> dtos = instances.stream()
                .map(articleInstanceMapper::toDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Marquer une instance comme traitée
     */
    @PostMapping("/instance/{instanceId}/complete")
    public ResponseEntity<Map<String, Object>> markInstanceAsProcessed(@PathVariable Long instanceId) {
        processingService.markArticleInstanceAsProcessed(instanceId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Instance marquée comme traitée");
        response.put("instanceId", instanceId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Marquer plusieurs instances comme traitées
     */
    @PostMapping("/instances/complete")
    public ResponseEntity<Map<String, String>> markInstancesAsProcessed(
            @RequestBody List<Long> instanceIds) {
        processingService.markArticleInstancesAsProcessed(instanceIds);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Instances marquées comme traitées");
        response.put("count", String.valueOf(instanceIds.size()));
        
        return ResponseEntity.ok(response);
    }
}
