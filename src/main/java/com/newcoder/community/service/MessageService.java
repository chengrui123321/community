package com.newcoder.community.service;

import com.newcoder.community.domain.Message;
import com.newcoder.community.domain.PageBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Message 业务
 */
public interface MessageService {

    PageBean<Message> findConversions(Integer current, Integer userId);

    PageBean<Message> findLetters(Integer current, String conversionId);

    Integer findLetterCount(String conversionId);

    Integer findUnReadLetterCount(Integer userId, String conversionId);

    void updateMessageStatus(Integer status, List<Integer> ids);

    void insert(Message message);

}
