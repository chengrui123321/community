package com.newcoder.community.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newcoder.community.dao.MessageMapper;
import com.newcoder.community.domain.Message;
import com.newcoder.community.domain.PageBean;
import com.newcoder.community.service.MessageService;
import com.newcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Message 业务
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    /**
     * 分页查询会话列表
     * @param current
     * @param userId
     * @return
     */
    public PageBean<Message> findConversions(Integer current, Integer userId) {
        // 设置分页参数
        PageHelper.startPage(current, 10);
        // 查询
        List<Message> conversions = messageMapper.findConversions(userId);
        // 封装分页参数
        PageInfo<Message> pageInfo = new PageInfo<>(conversions);
        PageBean<Message> page = new PageBean<>();
        page.setContent(conversions);
        page.setCurrent(current);
        page.setRows(Long.valueOf(pageInfo.getTotal()).intValue());
        return page;
    }

    /**
     * 分页查询某个会话私信列表
     * @param current
     * @param conversionId
     * @return
     */
    public PageBean<Message> findLetters(Integer current, String conversionId) {
        // 设置分页参数
        PageHelper.startPage(current, 10);
        // 查询
        List<Message> letters = messageMapper.findLetters(conversionId);
        // 封装分页参数
        PageInfo<Message> pageInfo = new PageInfo<>(letters);
        PageBean<Message> page = new PageBean<>();
        page.setContent(letters);
        page.setCurrent(current);
        page.setRows(Long.valueOf(pageInfo.getTotal()).intValue());
        return page;
    }

    /**
     * 查询会话私信数量
     * @param conversionId
     * @return
     */
    public Integer findLetterCount(String conversionId) {
        return messageMapper.findLetterCount(conversionId);
    }

    /**
     * 查询会话未读信息数量
     * @param userId
     * @param conversionId
     * @return
     */
    public Integer findUnReadLetterCount(Integer userId, String conversionId) {
        return messageMapper.findUnReadLetterCount(userId, conversionId);
    }

    /**
     * 修改信息状态
     * @param status
     * @param ids
     */
    public void updateMessageStatus(Integer status, List<Integer> ids) {
        messageMapper.updateMessageStatus(status, ids);
    }

    /**
     * 添加信息
     * @param message
     */
    public void insert(Message message) {
        // 过滤敏感词
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        // 添加
        messageMapper.insert(message);
    }
}
