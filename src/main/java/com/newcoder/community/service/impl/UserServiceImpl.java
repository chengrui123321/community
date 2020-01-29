package com.newcoder.community.service.impl;

import com.newcoder.community.dao.UserMapper;
import com.newcoder.community.domain.User;
import com.newcoder.community.service.UserService;
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
}
