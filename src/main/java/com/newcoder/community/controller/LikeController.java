package com.newcoder.community.controller;

import com.newcoder.community.domain.User;
import com.newcoder.community.service.LikeService;
import com.newcoder.community.util.CommunityUtil;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 点赞/取消点赞
 */
@RestController
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    /**
     * 点赞/取消点赞
     * @param entityType
     * @param entityId
     * @param entityUserId
     * @return
     */
    @PostMapping("/like")
    public String like(Integer entityType, Integer entityId, Integer entityUserId) {
        // 获取用户
        User user = hostHolder.get();
        // 点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        // 获取贴点赞数量
        Integer likeCount = likeService.getEntityLikeCount(entityType, entityId);
        // 获取用户点赞状态
        Integer likeStatus = likeService.getUserLikeCount(user.getId());
        // 保存信息
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        return CommunityUtil.getJSONString(0, null, map);
    }

}
