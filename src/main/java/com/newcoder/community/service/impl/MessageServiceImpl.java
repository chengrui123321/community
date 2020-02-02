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

    /**
     * 查询某个主题最新的通知
     * @param topic
     * @param userId
     * @return
     */
    public Message findLatestNotice(String topic, Integer userId) {
        return messageMapper.findLatestNotice(topic, userId);
    }

    /**
     * 查询某个主题的通知个数
     * @param topic
     * @param userId
     * @return
     */
    public Integer findNoticeCount(String topic, Integer userId) {
        return messageMapper.findNoticeCount(topic, userId);
    }

    /**
     * 查询未读通知个数
     * @param topic
     * @param userId
     * @return
     */
    public Integer findUnreadNoticeCount(String topic, Integer userId) {
        return messageMapper.findUnreadNoticeCount(topic, userId);
    }

    /**
     * 查询某个主题通知列表
     * @param userId
     * @param topic
     * @return
     */
    public PageBean<Message> findNotices(Integer userId, String topic) {
        // 查询
        List<Message> notices = messageMapper.findNotices(userId, topic);
        // 构建分页信息
        PageInfo<Message> pageInfo = new PageInfo<>(notices);
        PageBean<Message> page = new PageBean<>();
        page.setRows(Long.valueOf(pageInfo.getTotal()).intValue());
        page.setContent(notices);
        return page;
    }
}
