package com.newcoder.community.dao;

import com.newcoder.community.domain.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 讨论贴Mapper
 */
@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> list(@Param("userId") Integer userId);

}