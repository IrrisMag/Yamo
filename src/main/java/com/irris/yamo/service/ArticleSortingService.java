package com.irris.yamo.service;

import com.irris.yamo.dtos.ArticleDto;
import com.irris.yamo.entities.Article;
import com.irris.yamo.entities.Order;

import java.util.List;

public interface ArticleSortingService {

    Article sortArticle(Long articleId);

    List<Article> sortArticles(List<Long> articleIds);

    boolean verifyArticlesSorted(Long articleId);

    List<ArticleDto> getReceptionnedArticles();

    List<ArticleDto> getReceptionnedArticlesForOrder(Long orderId);

    List<ArticleDto> getReceptionnedArticlesForOrders(List<Long> orderIds);



}
