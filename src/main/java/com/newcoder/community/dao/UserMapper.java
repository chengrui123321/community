package com.newcoder.community.dao;

import com.newcoder.community.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User get(Integer id);

}
