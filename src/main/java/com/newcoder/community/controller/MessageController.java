package com.newcoder.community.controller;

import com.newcoder.community.domain.Message;
import com.newcoder.community.domain.PageBean;
import com.newcoder.community.domain.User;
import com.newcoder.community.service.MessageService;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommunityUtil;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Message 控制层
 */
@Controller
@RequestMapping("/message")
public class MessageController {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    /**
     * 获取会话列表
     * @param current
     * @param model
     * @return
     */
    @GetMapping("/letter/list")
    public String getLetterList(Integer current, Model model) {
        if (current == null) {
            current = 1;
        }
        // 获取用户对象
        User user = hostHolder.get();
        // 查询会话列表
        PageBean<Message> page = messageService.findConversions(current, user.getId());
        // 获取会话列表
        List<Message> conversionList = page.getContent();
        List<Map<String, Object>> conversions = new ArrayList<>();
        if (!ObjectUtils.isEmpty(conversionList)) {
            for (Message message : conversionList) {
                Map<String, Object> map = new HashMap<>();
                // 保存会话信息
                map.put("conversion", message);
                // 保存会话数量
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                // 保存会话的未读消息数量
                map.put("unreadCount", messageService.findUnReadLetterCount(user.getId(), message.getConversationId()));
                // 保存目标id
                Integer targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                // 查询目标用户
                User target = userService.get(targetId);
                map.put("target", target);
                conversions.add(map);
            }
        }
        // 保存结果集
        model.addAttribute("conversions", conversions);
        // 设置分页参数
        page.setPath("/message/letter/list");
        // 保存分页参数
        model.addAttribute("page", page);
        // 保存所有未读信息数量
        Integer allUnreadCount = messageService.findUnReadLetterCount(user.getId(), null);
        model.addAttribute("allUnreadCount", allUnreadCount);
        return "site/letter";
    }

    /**
     * 获取私信列表
     * @param current
     * @param model
     * @return
     */
    @GetMapping("/letter/detail/{conversionId}")
    public String letterDetail(@PathVariable("conversionId") String conversionId, Integer current, Model model) {
        if (current == null) {
            current = 1;
        }
        // 获取用户对象
        User user = hostHolder.get();
        // 查询私信列表
        PageBean<Message> page = messageService.findLetters(current, conversionId);
        // 设置分页信息
        page.setPath("/message/letter/detail/" + conversionId);
        // 保存分页信息
        model.addAttribute("page", page);
        // 获取私信列表
        List<Message> letterList = page.getContent();
        List<Map<String, Object>> letters = new ArrayList<>();
        if (!ObjectUtils.isEmpty(letterList)) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                // 保存私信信息
                map.put("letter", message);
                // 保存消息发送者
                map.put("fromUser", userService.get(message.getFromId()));
                letters.add(map);
            }
        }
        // 保存私信列表
        model.addAttribute("letters", letters);
        //保存私信目标
        model.addAttribute("target", getLetterTarget(conversionId));
        // 过滤获取未读id集合
        if (!ObjectUtils.isEmpty(letterList)) {
            List<Integer> ids = letterList.stream()
                    .filter(letter -> user.getId() == letter.getToId() && letter.getStatus() == 0)
                    .map(Message::getId).collect(Collectors.toList());
            // 更新未读信息为已读
            if (!ObjectUtils.isEmpty(ids)) {
                messageService.updateMessageStatus(1, ids);
            }
        }
        return "site/letter-detail";
    }

    /**
     * 发送私信
     * @param username
     * @param content
     * @return
     */
    @PostMapping("/letter/send")
    @ResponseBody
    public String addMessage(String username, String content) {
        // 查询目标用户
        User target = userService.findByUsername(username);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在!");
        }
        // 设置参数
        Message message = new Message()
                .setContent(content)
                .setToId(target.getId())
                .setCreateTime(new Date())
                .setFromId(hostHolder.get().getId())
                .setStatus(0);
        // 创建会话id
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        // 添加
        messageService.insert(message);
        return CommunityUtil.getJSONString(0);
    }

    private User getLetterTarget(String conversionId) {
        if (!StringUtils.isEmpty(conversionId)) {
            String[] ids = conversionId.split("_");
            Integer id0 = Integer.parseInt(ids[0]);
            Integer id1 = Integer.parseInt(ids[1]);
            if (hostHolder.get().getId() == id0) {
                return userService.get(id1);
            } else {
                return userService.get(id0);
            }
        }
        return null;
    }

}
