package com.newcoder.community.util;

/**
 * 常量
 */
public class CommunityConstant {

    /**
     * 激活成功
     */
    public static final Integer ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    public static final Integer ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    public static final Integer ACTIVATION_FAILURE = 2;

    /**
     * 默认状态的登录凭证的超时时间
     */
    public static final Integer DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 记住状态的登录凭证超时时间
     */
    public static final Integer REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     * 实体类型: 帖子
     */
    public static final Integer ENTITY_TYPE_POST = 1;

    /**
     * 实体类型: 评论
     */
    public static final Integer ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型: 用户
     */
    public static final Integer ENTITY_TYPE_USER = 3;

    /**
     * 评论主题
     */
    public static final String TOPIC_COMMENT = "comment";

    /**
     * 点赞主题
     */
    public static final String TOPIC_LIKE = "like";

    /**
     * 关注主题
     */
    public static final String TOPIC_FOLLOW = "follow";

    /**
     * 发帖主题
     */
    public static final String TOPIC_PUBLISH = "publish";

    /**
     * 删帖主题
     */
    public static final String TOPIC_DELETE = "delete";

    /**
     * 分享主题
     */
    public static final String TOPIC_SHARE = "share";

    /**
     * 系统用户
     */
    public static final Integer SYSTEM_USER_ID = 1;

    /**
     * 普通用户
     */
    public static final String AUTHORITY_USER = "user";

    /**
     * 版主
     */
    public static final String AUTHORITY_MODERATOR = "moderator";

    /**
     * 管理员
     */
    public static final String AUTHORITY_ADMIN = "admin";

}
