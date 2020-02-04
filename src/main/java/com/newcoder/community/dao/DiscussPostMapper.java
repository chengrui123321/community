package com.newcoder.community.dao;

import com.newcoder.community.domain.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 讨论贴Mapper
 */
@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> list(@Param("userId") Integer userId, @Param("orderMode") int orderMode);

    DiscussPost get(Integer id);

    void updateCommentCount(@Param("id") Integer id, @Param("count") Integer count);

    void insert(DiscussPost discussPost);

    void updateType(@Param("postId") Integer postId, @Param("type") Integer type);

    void updateStatus(@Param("postId") Integer postId, @Param("status") Integer status);

    void updateScore(@Param("postId") Integer postId, @Param("score") Double score);
}
