package com.newcoder.community.dao;

import com.newcoder.community.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User get(Integer id);

    void insert(User user);

    User getByName(String username);

    User getByEmail(String email);

}
