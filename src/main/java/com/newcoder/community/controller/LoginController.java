package com.newcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.newcoder.community.domain.User;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommunityUtil;
import com.newcoder.community.util.RedisKeyUtil;
import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.newcoder.community.util.CommunityConstant.*;

/**
 * 注册、登录、激活
 */
@Controller
@Slf4j
public class LoginController {

    @Autowired
    UserService userService;

    @Autowired
    Producer producer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 跳转到注册页面
     * @return
     */
    @GetMapping("/register")
    public String getRegistryPage() {
        return "site/register";
    }

    /**
     * 跳转到登陆页面
     * @return
     */
    @GetMapping("/login")
    public String getLoginPage() {
        return "site/login";
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

    /**
     * 激活账号
     * @param userId
     * @param code
     * @param model
     * @return
     */
    @GetMapping("/activation/{userId}/{code}")
    public String activation(@PathVariable("userId") Integer userId,
                             @PathVariable("code") String code,
                             Model model) {
        // 激活
        Integer result = userService.activation(userId, code);
        if (ACTIVATION_SUCCESS == result) {
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        } else if (ACTIVATION_REPEAT == result) {
            model.addAttribute("msg", "您的账号已经激活过了!");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败!");
            model.addAttribute("target", "/index");
        }
        return "site/operate-result";
    }

    /**
     * 生成验证码，放入redis中
     * @param response
     */
    @GetMapping("/kaptcha")
    public void kaptcha(HttpServletResponse response) {
        // 创建文本
        String text = producer.createText();
        // 创建图片
        BufferedImage image = producer.createImage(text);
        try {
            // 获取输出流
            OutputStream os = response.getOutputStream();
            // 创建验证码归属
            String kaptchaOwner = CommunityUtil.genUUID();
            // 将owner写入cookie
            Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
            cookie.setPath(contextPath);
            cookie.setMaxAge(60);
            response.addCookie(cookie);
            // 获取验证码 key
            String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            // 将验证码信息放入redis中
            redisTemplate.boundValueOps(kaptchaKey).set(text, 60, TimeUnit.SECONDS);
            // 设置响应头
            response.setContentType("image/png");
            // 写出图片到浏览器
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            log.error("获取验证码失败: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 登录
     * @param username
     * @param password
     * @param code
     * @param response
     * @param model
     * @return
     */
    @PostMapping("/login")
    public String login(String username, String password, String code, boolean remember,
                        HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner,
                        Model model) {
        // 从redis中获取验证码
        String kaptcha = null;
        if (!StringUtils.isEmpty(kaptchaOwner)) {
            // 获取验证码key
            String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.boundValueOps(kaptchaKey).get();
        }
        // 检验验证码
        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(kaptcha) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确!");
            return "site/login";
        }
        Integer expire = remember ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        // 登录
        Map<String, Object> map = userService.login(username, password, expire);
        if (map.containsKey("ticket")) {
            // 登录成功,将票据写入cookie
            String ticket = (String) map.get("ticket");
            Cookie cookie = new Cookie("ticket", ticket);
            cookie.setPath(contextPath);
            cookie.setMaxAge(expire);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    /**
     * 退出登录
     * @param ticket
     * @return
     */
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.updateTicketStatus(ticket, 1);
        return "redirect:/login";
    }

}
