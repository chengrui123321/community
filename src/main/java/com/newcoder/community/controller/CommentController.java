package com.newcoder.community.controller;

import com.newcoder.community.domain.Comment;
import com.newcoder.community.domain.DiscussPost;
import com.newcoder.community.domain.Event;
import com.newcoder.community.domain.User;
import com.newcoder.community.kafka.EventProducer;
import com.newcoder.community.service.CommentService;
import com.newcoder.community.service.DiscussPostService;
import com.newcoder.community.util.CommunityConstant;
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

    @Autowired
    EventProducer eventProducer;

    @Autowired
    DiscussPostService discussPostService;

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

        // 创建事件对象
        Event event = new Event()
                .setUserId(user.getId())
                .setEntityId(comment.getEntityId())
                .setEntityType(comment.getEntityType())
                .setTopic(CommunityConstant.TOPIC_COMMENT)
                .setData("postId", postId);
        // 判断评论的是帖子还是回复
        if (comment.getEntityType() == CommunityConstant.ENTITY_TYPE_POST) {
            // 查询帖子
            DiscussPost post = discussPostService.getDiscussPostById(comment.getEntityId());
            event.setEntityUserId(post.getUserId());
        } else if (comment.getEntityType() == CommunityConstant.ENTITY_TYPE_COMMENT) {
            // 查询回复
            Comment c = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(c.getUserId());
        }
        // 发送消息
        eventProducer.fireEvent(event);
        // 如果是评论帖子，则触发发布事件
        if (comment.getEntityType() == CommunityConstant.ENTITY_TYPE_POST) {
            event = new Event()
                    .setUserId(user.getId())
                    .setTopic(CommunityConstant.TOPIC_PUBLISH)
                    .setEntityType(CommunityConstant.ENTITY_TYPE_POST)
                    .setEntityId(postId);
            // 发布事件
            eventProducer.fireEvent(event);
        }
        return "redirect:/discuss/detail/" + postId;
    }

}
