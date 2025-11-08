package com.irris.yamo.service;

import com.irris.yamo.entities.Article;
import com.irris.yamo.entities.ArticleInstance;
import com.irris.yamo.entities.ProcessStep;

import java.util.List;

public interface ProcessingService {

    List<ArticleInstance> getArticleInstancesByProcessStep(Long processStepId);

    void markArticleInstanceAsProcessed(Long articleInstanceId);

    void markArticleInstancesAsProcessed(List<Long> articleInstanceIds);


}
