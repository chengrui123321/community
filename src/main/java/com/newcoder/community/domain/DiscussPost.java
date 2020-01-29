package com.newcoder.community.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 讨论贴
 */
@Data
@Accessors(chain = true)
public class DiscussPost {

    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;

}
