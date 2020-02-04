package com.newcoder.community.service;

import com.newcoder.community.domain.DiscussPost;
import com.newcoder.community.domain.PageBean;


/**
 * DiscussPost 业务逻辑层
 */
public interface DiscussPostService {

    PageBean<DiscussPost> list(Integer userId, int orderMode);

    DiscussPost getDiscussPostById(Integer id);

    void addDiscussPost(DiscussPost discussPost);

    void updateType(Integer postId, Integer type);

    void updateStatus(Integer postId, Integer status);

    void updateScore(Integer postId, Double score);

}
