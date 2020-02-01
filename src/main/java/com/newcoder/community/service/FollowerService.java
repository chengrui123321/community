package com.newcoder.community.service;

import java.util.List;
import java.util.Map;

/**
 * 关注 业务
 */
public interface FollowerService {

    void follow(Integer userId, Integer entityType, Integer entityId);

    void unFollow(Integer userId, Integer entityType, Integer entityId);

    Integer getFolloweeCount(Integer userId, Integer entityType);

    Integer getFollowerCount(Integer entityType, Integer entityId);

    Boolean hasFollowed(Integer userId, Integer entityType, Integer entityId);

    List<Map<String, Object>> getFolloweeList(Integer userId, Integer offset, Integer limit);

    List<Map<String, Object>> getFollowerList(Integer userId, Integer offset, Integer limit);
}
