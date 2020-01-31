package com.newcoder.community.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 评论
 */
@Data
@Accessors(chain = true)
public class Comment {

    private int id;
    private int userId;
    // 1帖子实体 2评论实体
    private int entityType;
    private int entityId;
    private int targetId;
    private String content;
    private int status;
    private Date createTime;

}
