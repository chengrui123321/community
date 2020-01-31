package com.newcoder.community.dao;

import com.newcoder.community.domain.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论
 */
@Mapper
public interface CommentMapper {

    List<Comment> getCommentList(@Param("entityType") Integer entityType, @Param("entityId") Integer entityId);

    Integer selectCount(@Param("entityType") Integer entityType, @Param("entityId") Integer entityId);

    void insert(Comment comment);

}
