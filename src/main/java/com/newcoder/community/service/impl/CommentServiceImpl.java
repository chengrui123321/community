package com.newcoder.community.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newcoder.community.dao.CommentMapper;
import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.domain.Comment;
import com.newcoder.community.domain.PageBean;
import com.newcoder.community.service.CommentService;
import com.newcoder.community.util.CommunityConstant;
import com.newcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * 评论业务处理
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    /**
     * 分页查询讨论贴评论
     * @param entityType
     * @param entityId
     * @param current
     * @return
     */
    public PageBean<Comment> findPostCommentList(Integer entityType, Integer entityId, Integer current) {
        // 设置分页参数
        PageHelper.startPage(current, 10);
        // 查询
        List<Comment> commentList = commentMapper.getCommentList(entityType, entityId);
        PageInfo<Comment> pageInfo = new PageInfo<>(commentList);
        PageBean<Comment> pageBean = new PageBean<>();
        pageBean.setContent(commentList);
        pageBean.setRows(Long.valueOf(pageInfo.getTotal()).intValue());
        return pageBean;
    }

    /**
     * 查询回复评论列表
     * @param entityType
     * @param entityId
     * @return
     */
    public List<Comment> findReplyCommentList(Integer entityType, Integer entityId) {
        return commentMapper.getCommentList(entityType, entityId);
    }

    /**
     * 查询回复数量
     * @param entityType
     * @param entityId
     * @return
     */
    public Integer selectCount(Integer entityType, Integer entityId) {
        return commentMapper.selectCount(entityType, entityId);
    }

    /**
     * 添加评论
     * @param comment
     */
    @Transactional
    public void addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        // 将评论筛选敏感词和特殊标签
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        // 添加评论
        commentMapper.insert(comment);

        // 修改帖子评论数量
        if (CommunityConstant.ENTITY_TYPE_POST == comment.getEntityType()) {
            // 查询原来评论数量
            Integer count = commentMapper.selectCount(comment.getEntityType(), comment.getEntityId());
            // 更新评论数量
            discussPostMapper.updateCommentCount(comment.getEntityId(), count);
        }
    }

    /**
     * 根据id查询回复
     * @param id
     * @return
     */
    public Comment findCommentById(Integer id) {
        return commentMapper.findCommentById(id);
    }
}
