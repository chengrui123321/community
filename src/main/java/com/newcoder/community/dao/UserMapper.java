package com.newcoder.community.dao;

import com.newcoder.community.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User get(Integer id);

    void insert(User user);

    User getByName(String username);

    User getByEmail(String email);

    void updateStatus(@Param("userId") Integer userId, @Param("status") Integer status);

    void updateHeaderUrl(@Param("userId") Integer userId, @Param("headerUrl") String headUrl);

    void updatePassword(@Param("password")String password, @Param("userId")Integer userId);
}
