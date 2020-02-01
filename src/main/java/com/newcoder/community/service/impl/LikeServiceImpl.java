package com.newcoder.community.service.impl;

import com.newcoder.community.service.LikeService;
import com.newcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * 点赞业务
 */
@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 点赞/取消点赞
     * @param userId 点赞用户
     * @param entityType 实体类型(1贴2回复)
     * @param entityId 实体id
     * @param entityUserId 被点赞用户id
     */
    public void like(Integer userId, Integer entityType, Integer entityId, Integer entityUserId) {
        // 使用redis事务处理
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                // 获取entityLikeKey
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                // 获取userLikeKey
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                // 判断当前用户是否为该实体点赞
                Boolean isMember = redisOperations.opsForSet().isMember(entityLikeKey, userId);
                // 开启事务
                redisOperations.multi();
                if (isMember) {
                    // 已赞，取消点赞
                    redisOperations.opsForSet().remove(entityLikeKey, userId);
                    redisOperations.opsForValue().decrement(userLikeKey);
                } else {
                    // 未赞，执行点赞
                    redisOperations.opsForSet().add(entityLikeKey, userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }
                return redisOperations.exec();
            }
        });
    }

    /**
     * 获取实体的点赞数量
     * @param entityType
     * @param entityId
     * @return
     */
    public Integer getEntityLikeCount(Integer entityType, Integer entityId) {
        // 获取实体点赞key
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.boundSetOps(entityLikeKey).size().intValue();
    }

    /**
     * 查询某用户对某实体的点赞状态(1已赞0未赞)
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public Integer getEntityLikeStatus(Integer userId, Integer entityType, Integer entityId) {
        // 获取实体点赞key
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.boundSetOps(entityLikeKey).isMember(userId) ? 1 : 0;
    }

    /**
     * 查询某个用户含有的赞
     * @param userId
     * @return
     */
    public Integer getUserLikeCount(Integer userId) {
        // 获取用户赞key
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        return (Integer)redisTemplate.boundValueOps(userLikeKey).get() == null ? 0 : (Integer)redisTemplate.boundValueOps(userLikeKey).get();
    }
}
