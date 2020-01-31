package com.newcoder.community.controller;

import com.newcoder.community.domain.Comment;
import com.newcoder.community.domain.User;
import com.newcoder.community.service.CommentService;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * 评论
 */
@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    /**
     * 添加评论
     * @param postId
     * @param comment
     * @return
     */
    @PostMapping("/add/{postId}")
    public String addComment(@PathVariable("postId") Integer postId, Comment comment) {
        // 获取当前登录用户
        User user = hostHolder.get();
        if (user == null) {
            return "redirect:/login";
        }
        // 设置参数
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        // 添加
        commentService.addComment(comment);
        return "redirect:/discuss/detail/" + postId;
    }

}
