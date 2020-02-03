package com.newcoder.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.newcoder.community.domain.DiscussPost;
import com.newcoder.community.service.ElasticsearchService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 搜索业务
 */
@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    @Autowired
    ElasticsearchRepository elasticsearchRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 添加/修改
     *
     * @param discussPost
     */
    public void save(DiscussPost discussPost) {
        elasticsearchRepository.save(discussPost);
    }

    /**
     * 根据id删除
     *
     * @param id
     */
    public void deleteById(Integer id) {
        elasticsearchRepository.deleteById(id);
    }

    /**
     * 关键字查询
     *
     * @param keyword
     * @param current
     * @param limit
     * @return
     */
    public Page<DiscussPost> search(String keyword, Integer current, Integer limit) {
        // 构建查询条件
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current, limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        return elasticsearchTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                SearchHits hits = searchResponse.getHits();
                if (hits.getTotalHits() <= 0) {
                    return null;
                }

                List<DiscussPost> list = new ArrayList<>();
                // 遍历
                for (SearchHit hit : hits) {
                    // 将查询的结果转为map
                    Map<String, Object> map = hit.getSourceAsMap();
                    // 将map转为DiscussPost
                    String jsonMap = JSONObject.toJSONString(map);
                    DiscussPost discussPost = JSONObject.parseObject(jsonMap, DiscussPost.class);
                    // 获取处理高亮字段
                    HighlightField title = hit.getHighlightFields().get("title");
                    if (title != null) {
                        discussPost.setTitle(title.fragments()[0].toString());
                    }
                    HighlightField content = hit.getHighlightFields().get("content");
                    if (content != null) {
                        discussPost.setContent(content.fragments()[0].toString());
                    }
                    list.add(discussPost);
                }
                return new AggregatedPageImpl(list, pageable, hits.getTotalHits(), searchResponse.getAggregations(),
                        searchResponse.getScrollId(), hits.getMaxScore());
            }
        });
    }
}
