package com.newcoder.community.controller;

import com.newcoder.community.domain.DiscussPost;
import com.newcoder.community.domain.PageBean;
import com.newcoder.community.service.ElasticsearchService;
import com.newcoder.community.service.LikeService;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索
 */
@Controller
public class SearchController {

    @Autowired
    ElasticsearchService elasticsearchService;

    @Autowired
    LikeService likeService;

    @Autowired
    UserService userService;

    /**
     * 搜索帖子
     * @param current
     * @param keyword
     * @param model
     * @return
     */
    @GetMapping("/search")
    public String search(Integer current, String keyword, Model model) {
        if (current == null) {
            current = 1;
        }
        // 创建分页对象
        PageBean<DiscussPost> page = new PageBean<>();
        page.setCurrent(current);
        page.setSize(10);
        page.setPath("/search?keyword=" + keyword);
        // 查询
        Page<DiscussPost> searchResult = elasticsearchService.search(keyword, current - 1, page.getSize());
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());
        // 封装结果
        List<Map<String, Object>> list = new ArrayList<>();
        if (!ObjectUtils.isEmpty(searchResult)) {
            for (DiscussPost post : searchResult) {
                Map<String, Object> map = new HashMap<>();
                // 保存帖子信息
                map.put("post", post);
                // 作者
                map.put("user", userService.get(post.getUserId()));
                //保存点赞数量
                map.put("likeCount", likeService.getEntityLikeCount(CommunityConstant.ENTITY_TYPE_POST, post.getId()));
                list.add(map);
            }
        }
        // 保存信息
        model.addAttribute("list", list);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        return "site/search";
    }

}
