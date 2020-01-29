package com.newcoder.community.controller;

import com.newcoder.community.domain.User;
import com.newcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * 注册、登录、激活
 */
@Controller
public class LoginController {

    @Autowired
    UserService userService;

    /**
     * 跳转到注册页面
     * @return
     */
    @GetMapping("/register")
    public String getRegistryPage() {
        return "site/register";
    }

    /**
     * 注册用户
     * @param user
     * @return
     */
    @PostMapping("/register")
    public String register(User user, Model model) {
        Map<String, Object> map = userService.register(user);
        if (ObjectUtils.isEmpty(map)) {
            model.addAttribute("msg", "注册成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活!");
            model.addAttribute("target", "/index");
            return "site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "site/register";
        }
    }

}
