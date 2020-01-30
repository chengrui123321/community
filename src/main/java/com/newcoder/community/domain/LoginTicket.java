package com.newcoder.community.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 登录凭证
 */
@Data
@Accessors(chain = true)
public class LoginTicket {

    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;

}
