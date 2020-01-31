package com.newcoder.community.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 消息
 */
@Data
@Accessors(chain = true)
public class Message {

    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;

}
