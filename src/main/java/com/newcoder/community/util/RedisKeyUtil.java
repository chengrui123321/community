package com.newcoder.community.util;

/**
 * Redis key 工具类
 */
public class RedisKeyUtil {

    /**
     * Redis key 分隔符
     */
    private static final String SPLIT = ":";

    /**
     * 贴赞和回复赞前缀
     */
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    /**
     * 某个用户的赞前缀
     */
    private static final String PREFIX_USER_LIKE = "like:user";

    /**
     * 用户关注的目标前缀
     */
    private static final String PREFIX_FOLLOWEE = "followee";

    /**
     * 某个实体的粉丝前缀
     */
    private static final String PREFIX_FOLLOWER = "follower";

    /**
     * 图形验证码
     */
    private static final String PREFIX_KAPTCHA = "kaptcha";

    /**
     * 登录票据
     */
    private static final String PREFIX_TICKET = "ticket";

    /**
     * 用户信息
     */
    private static final String PREFIX_USER = "user";

    /**
     * 获取用户key
     * @param userId
     * @return
     */
    public static String getUserKey(Integer userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    /**
     * 获取登录凭证key
     * @param ticket
     * @return
     */
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * 获取验证码key
     * @param owner
     * @return
     */
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * 获取粉丝key[follower:entityType:entityId -> zset(userId,now)]
     * @param entityId
     * @param entityType
     * @return
     */
    public static String getFollowerKey(Integer entityType, Integer entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 获取某个用户关注的目标key[followee:userId:entityType]
     * @param userId
     * @param entityType
     * @return
     */
    public static String getFolloweeKey(Integer userId, Integer entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 获取贴赞和回复赞Redis key[like:entity:entityType:entityId]
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getEntityLikeKey(Integer entityType, Integer entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 获取某个用户的赞[like:user:userId]
     * @param userId
     * @return
     */
    public static String getUserLikeKey(Integer userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

}
