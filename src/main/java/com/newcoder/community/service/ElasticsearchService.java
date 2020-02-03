package com.newcoder.community.service;

import com.newcoder.community.domain.DiscussPost;
import org.springframework.data.domain.Page;

/**
 * 搜索业务
 */
public interface ElasticsearchService {

    void save(DiscussPost discussPost);

    void deleteById(Integer id);

    Page<DiscussPost> search(String keyword, Integer current, Integer limit);

}
