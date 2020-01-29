package com.newcoder.community.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 用户
 */
@Data
@Accessors(chain = true)
public class User {

    private Integer id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private String activationCode;
    private String headerUrl;
    private Integer type;
    private Integer status;
    private Date createTime;

}
