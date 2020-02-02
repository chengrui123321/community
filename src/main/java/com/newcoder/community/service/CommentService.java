package com.newcoder.community.service;

import com.newcoder.community.domain.Comment;
import com.newcoder.community.domain.PageBean;

import java.util.List;

/**
 * Comment
 */
public interface CommentService {

    PageBean<Comment> findPostCommentList(Integer entityType, Integer entityId, Integer current);

    List<Comment> findReplyCommentList(Integer entityType, Integer entityId);

    Integer selectCount(Integer entityType, Integer entityId);

    void addComment(Comment comment);

    Comment findCommentById(Integer id);

}
