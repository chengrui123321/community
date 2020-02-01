package com.newcoder.community.service;

/**
 * 点赞业务
 */
public interface LikeService {

    void like(Integer userId, Integer entityType, Integer entityId, Integer entityUserId);

    Integer getEntityLikeCount(Integer entityType, Integer entityId);

    Integer getEntityLikeStatus(Integer userId, Integer entityType, Integer entityId);

    Integer getUserLikeCount(Integer userId);
}
