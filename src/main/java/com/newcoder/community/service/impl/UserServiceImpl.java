package com.newcoder.community.service.impl;

import com.newcoder.community.dao.LoginTicketMapper;
import com.newcoder.community.dao.UserMapper;
import com.newcoder.community.domain.LoginTicket;
import com.newcoder.community.domain.User;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommunityConstant;
import com.newcoder.community.util.CommunityUtil;
import com.newcoder.community.util.MailClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 用户业务
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    MailClient mailClient;

    @Autowired
    LoginTicketMapper loginTicketMapper;

    @Value("${community.path.mail}")
    private String mailPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 根据用户id查询用户
     * @param id
     * @return
     */
    public User get(Integer id) {
        return userMapper.get(id);
    }

    /**
     * 注册用户
     * @param user
     * @return
     */
    @Transactional
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if (user == null) {
            throw new RuntimeException("参数不能为空!");
        }
        if (StringUtils.isEmpty(user.getUsername())) {
            map.put("usernameMsg", "用户名不能为空!");
            return map;
        }
        if (StringUtils.isEmpty(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isEmpty(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }
        // 验证用户名是否注册
        if (userMapper.getByName(user.getUsername()) != null) {
            map.put("usernameMsg", "用户名已存在!");
            return map;
        }
        // 验证邮箱是否注册
        if (userMapper.getByEmail(user.getEmail()) != null) {
            map.put("emailMsg", "邮箱已存在!");
            return map;
        }

        // 设置用户信息
        user.setSalt(CommunityUtil.genUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setActivationCode(CommunityUtil.genUUID());
        user.setStatus(0);
        user.setType(0);
        user.setCreateTime(new Date());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        // 添加用户
        userMapper.insert(user);

        // 发送邮件激活码
        Context context = new Context();
        // 设置变量值
        Map<String, Object> params = new HashMap<>();
        params.put("email", user.getEmail());
        params.put("url", mailPath + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode());
        context.setVariables(params);
        // 使用模板引擎解析
        String content = templateEngine.process("mail/activation", context);
        // 发送邮件
        mailClient.sendMail(user.getEmail(), "账号激活", content);
        return map;
    }

    /**
     * 激活账号
     * @param userId
     * @param code
     * @return
     */
    public Integer activation(Integer userId, String code) {
        // 根据id查询用户
        User user = userMapper.get(userId);
        if (user.getStatus() == 1) {
            return CommunityConstant.ACTIVATION_REPEAT;
        }
        if (user.getActivationCode().equals(code)) {
            // 更新激活状态
            userMapper.updateStatus(userId, 1);
            return CommunityConstant.ACTIVATION_SUCCESS;
        }

        return CommunityConstant.ACTIVATION_FAILURE;
    }

    /**
     * 登录
     * @param username
     * @param password
     * @param expire
     * @return
     */
    public Map<String, Object> login(String username, String password, Integer expire) {
        Map<String, Object> map = new HashMap<>();
        // 判空
        if (StringUtils.isEmpty(username)) {
            map.put("usernameMsg", "用户名不能为空!");
            return map;
        }
        if (StringUtils.isEmpty(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        // 验证账号
        User user = userMapper.getByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }
        if (0 == user.getStatus()) {
            map.put("usernameMsg", "该账号还未激活!");
            return map;
        }
        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!password.equals(user.getPassword())) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 生成登录凭证
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(user.getId())
                .setStatus(0)
                .setTicket(CommunityUtil.genUUID())
                .setExpired(new Date(System.currentTimeMillis() + expire * 1000));
        loginTicketMapper.insert(ticket);
        // 保存票据
        map.put("ticket", ticket.getTicket());
        return map;
    }

    /**
     * 根据票据查询LoginTicket
     * @return
     */
    public LoginTicket getLoginTicketByTicket(String ticket) {
        return loginTicketMapper.getByTicket(ticket);
    }

    /**
     * 修改登录票据状态
     * @param ticket
     * @param status
     */
    public void updateTicketStatus(String ticket, Integer status) {
        loginTicketMapper.updateStatus(ticket, status);
    }

    /**
     * 修改用户头像
     * @param userId
     * @param headerUrl
     */
    public void updateHeader(Integer userId, String headerUrl) {
        userMapper.updateHeaderUrl(userId, headerUrl);
    }

    /**
     * 修改密码
     * @param oldPwd
     * @param newPwd
     */
    public Map<String, Object> updatePassword(String oldPwd, String newPwd, Integer userId) {
        Map<String, Object> map = new HashMap<>();
        // 判空
        if (StringUtils.isEmpty(oldPwd)) {
            map.put("oldMsg", "旧密码不能为空!");
            return map;
        }
        if (StringUtils.isEmpty(newPwd)) {
            map.put("newMsg", "新密码不能为空!");
            return map;
        }
        // 查询当前用户
        User user = userMapper.get(userId);
        // 加密旧密码
        oldPwd = CommunityUtil.md5(oldPwd + user.getSalt());
        // 加密新密码
        newPwd = CommunityUtil.md5(newPwd + user.getSalt());
        if (!oldPwd.equals(user.getPassword())) {
            map.put("oldMsg", "请输入正确的旧密码!");
            return map;
        }
        if (oldPwd.equals(newPwd)) {
            map.put("newMsg", "新密码不能和旧密码一致!");
            return map;
        }
        // 修改密码
        userMapper.updatePassword(newPwd, userId);
        return map;
    }

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    public User findByUsername(String username) {
        return userMapper.getByName(username);
    }
}
