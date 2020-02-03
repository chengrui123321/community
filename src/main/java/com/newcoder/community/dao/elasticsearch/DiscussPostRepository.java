package com.newcoder.community.dao.elasticsearch;

import com.newcoder.community.domain.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * DiscussPost 基于ElasticsearchRepository做crud
 */
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
