package com.newcoder.community.service;

import com.newcoder.community.domain.DiscussPost;
import com.newcoder.community.domain.PageBean;


/**
 * DiscussPost 业务逻辑层
 */
public interface DiscussPostService {

    PageBean<DiscussPost> list(Integer userId);

    DiscussPost getDiscussPostById(Integer id);

    void addDiscussPost(DiscussPost discussPost);

}
