package com.newcoder.community.service;

import com.newcoder.community.domain.LoginTicket;
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

    Integer activation(Integer userId, String code);

    Map<String, Object> login(String username, String password, Integer expire);

    LoginTicket getLoginTicketByTicket(String ticket);

    void updateTicketStatus(String ticket, Integer status);

    void updateHeader(Integer userId, String headerUrl);

    Map<String, Object> updatePassword(String oldPwd, String newPwd, Integer userId);

    User findByUsername(String username);

}
