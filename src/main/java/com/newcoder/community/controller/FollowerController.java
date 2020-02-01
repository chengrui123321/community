package com.newcoder.community.controller;

import com.newcoder.community.domain.PageBean;
import com.newcoder.community.domain.User;
import com.newcoder.community.service.FollowerService;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommunityConstant;
import com.newcoder.community.util.CommunityUtil;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 关注/取消关注/粉丝列表/关注用户列表
 */
@Controller
public class FollowerController {

    @Autowired
    FollowerService followerService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    /**
     * 关注
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/follow")
    @ResponseBody
    public String follow(Integer entityType, Integer entityId) {
        // 获取当前用户
        User user = hostHolder.get();
        // 关注
        followerService.follow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0, "已关注");
    }

    /**
     * 取消关注
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/unFollow")
    @ResponseBody
    public String unFollow(Integer entityType, Integer entityId) {
        // 获取当前用户
        User user = hostHolder.get();
        // 关注
        followerService.unFollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0, "已取消关注");
    }

    /**
     * 查询用户关注的所有实体
     * @param userId
     * @param model
     * @return
     */
    @GetMapping("/followee/{userId}")
    public String getFolloweeList(@PathVariable("userId") Integer userId, Integer current,  Model model) {
        if (current == null) {
            current = 1;
        }
        // 查询用户
        User user = userService.get(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在!");
        }
        // 保存用户
        model.addAttribute("user", user);
        // 创建分页对象
        PageBean<Object> page = new PageBean<>();
        page.setCurrent(current);
        page.setSize(5);
        page.setRows(followerService.getFolloweeCount(userId, CommunityConstant.ENTITY_TYPE_USER));
        page.setPath("/followee/" + userId);
        // 保存分页信息
        model.addAttribute("page", page);
        // 查询用户关注的实体
        List<Map<String, Object>> followeeList = followerService.getFolloweeList(userId, (current - 1) * page.getSize(), page.getSize());
        if (!ObjectUtils.isEmpty(followeeList)) {
            for (Map<String, Object> map : followeeList) {
                User u = (User) map.get("user");
                // 保存关注状态
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", followeeList);
        return "site/followee";
    }

    /**
     * 查询用户的粉丝
     * @param userId
     * @param model
     * @return
     */
    @GetMapping("/follower/{userId}")
    public String getFollowerList(@PathVariable("userId") Integer userId, Integer current,  Model model) {
        if (current == null) {
            current = 1;
        }
        // 查询用户
        User user = userService.get(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在!");
        }
        // 保存用户
        model.addAttribute("user", user);
        // 创建分页对象
        PageBean<Object> page = new PageBean<>();
        page.setCurrent(current);
        page.setSize(5);
        page.setRows(followerService.getFollowerCount(CommunityConstant.ENTITY_TYPE_USER, userId));
        page.setPath("/follower/" + userId);
        // 保存分页信息
        model.addAttribute("page", page);
        // 查询用户关注的实体
        List<Map<String, Object>> followeeList = followerService.getFollowerList(userId, (current - 1) * page.getSize(), page.getSize());
        if (!ObjectUtils.isEmpty(followeeList)) {
            for (Map<String, Object> map : followeeList) {
                User u = (User) map.get("user");
                // 保存关注状态
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", followeeList);
        return "site/follower";
    }

    private boolean hasFollowed(int userId) {
        if (hostHolder.get() == null) {
            return false;
        }
        return followerService.hasFollowed(hostHolder.get().getId(), CommunityConstant.ENTITY_TYPE_USER, userId);
    }

}
