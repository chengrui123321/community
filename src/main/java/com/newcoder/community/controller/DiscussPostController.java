package com.newcoder.community.controller;

import com.github.pagehelper.PageHelper;
import com.newcoder.community.domain.*;
import com.newcoder.community.kafka.EventProducer;
import com.newcoder.community.service.CommentService;
import com.newcoder.community.service.DiscussPostService;
import com.newcoder.community.service.LikeService;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommunityConstant;
import com.newcoder.community.util.CommunityUtil;
import com.newcoder.community.util.HostHolder;
import com.newcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * DiscussPost
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 获取讨论贴详细信息
     *
     * @param id
     * @param current
     * @param model
     * @return
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Integer id,
                         Integer current, Model model) {
        //设置分页参数
        if (current == null) {
            current = 1;
        }
        // 获取讨论贴信息
        DiscussPost discussPost = discussPostService.getDiscussPostById(id);
        // 保存讨论贴信息
        model.addAttribute("post", discussPost);
        // 获取对应的用户名
        User user = userService.get(discussPost.getUserId());
        // 保存用户信息
        model.addAttribute("user", user);
        // 查询讨论贴的回复列表
        PageBean<Comment> page = commentService.findPostCommentList(CommunityConstant.ENTITY_TYPE_POST, discussPost.getId(), current);
        // 保存分页参数
        page.setCurrent(current);
        page.setPath("/discuss/detail/" + id);
        model.addAttribute("page", page);
        // 获取讨论贴点赞数量
        Integer likeCount = likeService.getEntityLikeCount(CommunityConstant.ENTITY_TYPE_POST, id);
        // 保存点赞数量
        model.addAttribute("likeCount", likeCount);
        // 获取点赞状态
        Integer likeStatus = user == null ? 0 : likeService.getEntityLikeStatus(user.getId(), CommunityConstant.ENTITY_TYPE_POST, id);
        model.addAttribute("likeStatus", likeStatus);
        // 获取贴子回复列表集合
        List<Comment> commentList = page.getContent();
        // 评论列表
        List<Map<String, Object>> commentVOList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(commentList)) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVO = new HashMap<>();
                // 保存评论对象
                commentVO.put("comment", comment);
                // 保存评论的用户
                commentVO.put("user", userService.get(comment.getUserId()));
                // 获取讨论贴点赞数量
                likeCount = likeService.getEntityLikeCount(CommunityConstant.ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("likeCount", likeCount);
                // 获取点赞状态
                likeStatus = user == null ? 0 : likeService.getEntityLikeStatus(user.getId(), CommunityConstant.ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("likeStatus", likeStatus);
                // 获取该评论所有的回复数
                List<Comment> replyCommentList = commentService.findReplyCommentList(CommunityConstant.ENTITY_TYPE_COMMENT, comment.getId());
                List<Map<String, Object>> replyVOList = new ArrayList<>();
                if (!ObjectUtils.isEmpty(replyCommentList)) {
                    for (Comment reply : replyCommentList) {
                        // 回复评论VO
                        Map<String, Object> replyVO = new HashMap<>();
                        // 保存回复评论对象
                        replyVO.put("reply", reply);
                        // 保存用户信息
                        replyVO.put("user", userService.get(reply.getUserId()));
                        // 获取讨论贴点赞数量
                        likeCount = likeService.getEntityLikeCount(CommunityConstant.ENTITY_TYPE_COMMENT, reply.getId());
                        replyVO.put("likeCount", likeCount);
                        // 获取点赞状态
                        likeStatus = user == null ? 0 : likeService.getEntityLikeStatus(user.getId(), CommunityConstant.ENTITY_TYPE_COMMENT, reply.getId());
                        replyVO.put("likeStatus", likeStatus);
                        // 回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.get(reply.getTargetId());
                        replyVO.put("target", target);
                        replyVOList.add(replyVO);
                    }
                }
                // 将回复列表保存在commentVO中
                commentVO.put("replys", replyVOList);
                // 查询该评论的回复数
                Integer count = commentService.selectCount(CommunityConstant.ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("relpyCount", count);
                // 保存评论信息
                commentVOList.add(commentVO);
            }
            // 保存评论列表
            model.addAttribute("comments", commentVOList);
        }
        return "site/discuss-detail";
    }

    /**
     * 发布新帖
     * @param
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        // 获取用户信息
        User user = hostHolder.get();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "您还没有登录!");
        }
        // 设置参数
        DiscussPost discussPost = new DiscussPost()
                        .setTitle(title)
                        .setContent(content)
                        .setUserId(user.getId())
                        .setCreateTime(new Date());
        // 添加
        discussPostService.addDiscussPost(discussPost);

        // 触发发帖事件
        Event event = new Event()
                .setUserId(user.getId())
                .setEntityId(discussPost.getId())
                .setEntityType(CommunityConstant.ENTITY_TYPE_POST)
                .setTopic(CommunityConstant.TOPIC_PUBLISH);
        // 发送事件
        eventProducer.fireEvent(event);
        // 将添加帖子事件加入redis中计算分数
        // 获取redis key
        String postKey = RedisKeyUtil.getPostKey();
        redisTemplate.boundSetOps(postKey).add(discussPost.getId());
        return CommunityUtil.getJSONString(0, "发布成功!");
    }

    /**
     * 置顶
     * @param id
     * @return
     */
    @PostMapping("/top")
    @ResponseBody
    public String setTop(Integer id) {
        // 修改帖子类型为置顶
        discussPostService.updateType(id, 1);
        // 创建发布事件
        Event event = new Event()
                .setTopic(CommunityConstant.TOPIC_PUBLISH)
                .setEntityId(id)
                .setEntityType(CommunityConstant.ENTITY_TYPE_POST)
                .setUserId(hostHolder.get().getId());
        // 发布事件
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0, "置顶成功!");
    }

    /**
     * 加精
     * @param id
     * @return
     */
    @PostMapping("/wonderful")
    @ResponseBody
    public String setWonderful(Integer id) {
        // 加精
        discussPostService.updateStatus(id, 1);
        // 创建发布事件
        Event event = new Event()
                .setUserId(hostHolder.get().getId())
                .setEntityType(CommunityConstant.ENTITY_TYPE_POST)
                .setEntityId(id)
                .setTopic(CommunityConstant.TOPIC_PUBLISH);
        // 发布事件
        eventProducer.fireEvent(event);
        // 计算帖子分数
        String postKey = RedisKeyUtil.getPostKey();
        redisTemplate.boundSetOps(postKey).add(id);
        return CommunityUtil.getJSONString(0, "加精成功!");
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    public String setDelete(Integer id) {
        // 删除
        discussPostService.updateStatus(id, 2);
        // 创建事件
        Event event = new Event()
                .setTopic(CommunityConstant.TOPIC_DELETE)
                .setEntityId(id)
                .setEntityType(CommunityConstant.ENTITY_TYPE_POST)
                .setUserId(hostHolder.get().getId());
        // 发布事件
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0, "删除成功!");
    }
}
