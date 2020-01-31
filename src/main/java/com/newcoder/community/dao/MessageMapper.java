package com.newcoder.community.dao;

import com.newcoder.community.domain.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息
 */
@Mapper
public interface MessageMapper {

    // 查询用户的会话列表
    List<Message> findConversions(Integer userId);

    // 查询某个会话的私信列表
    List<Message> findLetters(String conversionId);

    // 查询某个会话私信数量
    Integer findLetterCount(String conversionId);

    // 查询维度私信数量
    Integer findUnReadLetterCount(@Param("userId") Integer userId, @Param("conversionId") String conversionId);

    //批量更新未读信息状态
    void updateMessageStatus(@Param("status") Integer status, @Param("ids") List<Integer> ids);

    // 添加信息
    void insert(Message message);
}
