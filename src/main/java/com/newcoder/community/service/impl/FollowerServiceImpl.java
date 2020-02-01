package com.newcoder.community.service.impl;

import com.newcoder.community.domain.User;
import com.newcoder.community.service.FollowerService;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommunityConstant;
import com.newcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 关注/取消关注
 */
@Service
public class FollowerServiceImpl implements FollowerService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserService userService;

    /**
     * 关注(用户/帖子/题目)
     *
     * @param userId
     * @param entityType
     * @param entityId
     */
    public void follow(Integer userId, Integer entityType, Integer entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                // 获取目标key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                //获取粉丝key
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                // 开启事务
                redisOperations.multi();
                redisOperations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                redisOperations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                return redisOperations.exec();
            }
        });
    }

    /**
     * 取消关注
     *
     * @param userId
     * @param entityType
     * @param entityId
     */
    public void unFollow(Integer userId, Integer entityType, Integer entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                // 获取目标key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                // 获取粉丝key
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                // 开启事务
                redisOperations.multi();
                redisOperations.opsForZSet().remove(followeeKey, entityId);
                redisOperations.opsForZSet().remove(followerKey, userId);
                return redisOperations.exec();
            }
        });
    }

    /**
     * 查询用户关注的目标数量
     *
     * @param userId
     * @param entityType
     * @return
     */
    public Integer getFolloweeCount(Integer userId, Integer entityType) {
        // 获取实体key
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.boundZSetOps(followeeKey).zCard().intValue();
    }

    /**
     * 查询用户的粉丝数量
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public Integer getFollowerCount(Integer entityType, Integer entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.boundZSetOps(followerKey).zCard().intValue();
    }

    /**
     * 查询用户是否已关注实体
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public Boolean hasFollowed(Integer userId, Integer entityType, Integer entityId) {
        // 获取实体key
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    /**
     * 获取用户所有关注的用户列表
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Map<String, Object>> getFolloweeList(Integer userId, Integer offset, Integer limit) {
        // 获取关注实体的key
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, CommunityConstant.ENTITY_TYPE_USER);
        // 分页查询
        Set<Integer> targetIds = redisTemplate.boundZSetOps(followeeKey).reverseRange(offset, offset + limit - 1);
        if (targetIds == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer id : targetIds) {
            Map<String, Object> map = new HashMap<>();
            // 查询用户
            User user = userService.get(id);
            map.put("user", user);
            // 获取关注时间
            Double score = redisTemplate.boundZSetOps(followeeKey).score(id);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    /**
     * 获取用户粉丝
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Map<String, Object>> getFollowerList(Integer userId, Integer offset, Integer limit) {
        // 获取用户粉丝的key
        String followerKey = RedisKeyUtil.getFollowerKey(CommunityConstant.ENTITY_TYPE_USER, userId);
        // 分页查询
        Set<Integer> targetIds = redisTemplate.boundZSetOps(followerKey).reverseRange(offset, offset + limit - 1);
        if (targetIds == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer id : targetIds) {
            Map<String, Object> map = new HashMap<>();
            // 查询用户
            User user = userService.get(id);
            map.put("user", user);
            // 获取关注时间
            Double score = redisTemplate.boundZSetOps(followerKey).score(id);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
}
