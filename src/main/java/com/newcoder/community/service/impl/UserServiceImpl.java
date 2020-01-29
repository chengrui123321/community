package com.newcoder.community.service.impl;

import com.newcoder.community.dao.UserMapper;
import com.newcoder.community.domain.User;
import com.newcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: wb_cheng
 * @Date: 2020/1/29 12:57
 * @Description:
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User get(Integer id) {
        return userMapper.get(id);
    }
}
