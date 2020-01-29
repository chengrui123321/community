package com.newcoder.community.service;

import com.newcoder.community.domain.User;

import java.util.Map;

/**
 * @Auther: wb_cheng
 * @Date: 2020/1/29 12:56
 * @Description:
 */
public interface UserService {

    User get(Integer id);

    Map<String, Object> register(User user);

}
