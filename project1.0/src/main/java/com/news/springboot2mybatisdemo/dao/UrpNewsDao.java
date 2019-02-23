package com.news.springboot2mybatisdemo.dao;

import com.news.springboot2mybatisdemo.model.UrpNews;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UrpNewsDao extends ElasticsearchRepository<UrpNews,Integer> {

    UrpNews queryUrpNewsById(Integer id);

    UrpNews queryUrpNewsByNewsurl(String newsurl);

    List<UrpNews> queryAllByTitleLike(String title);

    List<UrpNews> queryAllByIdExists();

    @Override
    Iterable<UrpNews> search(QueryBuilder queryBuilder);

    @Override
    Page<UrpNews> search(SearchQuery searchQuery);
}
