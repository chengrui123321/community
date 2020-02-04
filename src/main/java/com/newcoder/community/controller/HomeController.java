package com.newcoder.community.controller;

import com.github.pagehelper.PageHelper;
import com.newcoder.community.domain.DiscussPost;
import com.newcoder.community.domain.PageBean;
import com.newcoder.community.domain.User;
import com.newcoder.community.service.DiscussPostService;
import com.newcoder.community.service.LikeService;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页
 */
@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    LikeService likeService;

    /**
     * 访问首页
     * @param current
     * @param model
     * @return
     */
    @GetMapping("/index")
    public String index(Integer current, @RequestParam(value = "orderMode", defaultValue = "0") int orderMode, Model model) {
        //设置分页参数
        if (current == null) {
            current = 1;
        }
        PageHelper.startPage(current, 10);
        PageBean<DiscussPost> page = discussPostService.list(0, orderMode);
        List<DiscussPost> discussPosts = page.getContent();
        //保存讨论贴和用户集合
        List<Map<String, Object>> list = new ArrayList<>();
        if (!ObjectUtils.isEmpty(discussPosts)) {
            discussPosts.stream().forEach(discussPost -> {
                //获取讨论贴对应的用户
                User user = userService.get(discussPost.getUserId());
                Map<String, Object> map = new HashMap<>();
                map.put("user", user);
                map.put("discussPost", discussPost);
                // 获取讨论贴点赞数量
                Integer likeCount = likeService.getEntityLikeCount(CommunityConstant.ENTITY_TYPE_POST, discussPost.getId());
                // 保存点赞数量
                map.put("likeCount", likeCount);
                list.add(map);
            });
        }
        model.addAttribute("list", list);
        //保存分页参数
        page.setCurrent(current);
        page.setPath("/index");
        model.addAttribute("page", page);
        model.addAttribute("orderMode", orderMode);
        return "index";
    }

    /**
     * 统一异常处理请求
     * @return
     */
    @GetMapping("/error")
    public String error() {
        return "error/500";
    }

    /**
     * 没有权限
     * @return
     */
    @GetMapping("/denied")
    public String deny() {
        return "error/404";
    }

}
