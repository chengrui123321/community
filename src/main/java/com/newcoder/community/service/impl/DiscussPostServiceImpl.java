package com.newcoder.community.service.impl;

import com.github.pagehelper.PageInfo;
import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.domain.DiscussPost;
import com.newcoder.community.domain.PageBean;
import com.newcoder.community.service.DiscussPostService;
import com.newcoder.community.util.SensitiveFilter;
import com.sun.corba.se.impl.resolver.ORBDefaultInitRefResolverImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @Auther: wb_cheng
 * @Date: 2020/1/29 12:42
 * @Description:
 */
@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    /**
     * 展示所有帖子
     * @param userId
     * @return
     */
    public PageBean<DiscussPost> list(Integer userId, int orderMode) {
        //查询讨论贴
        List<DiscussPost> discussPosts = discussPostMapper.list(userId, orderMode);
        PageInfo<DiscussPost> pageInfo = new PageInfo<>(discussPosts);
        //封装分页查询结果
        PageBean<DiscussPost> pageBean = new PageBean<>();
        pageBean.setContent(discussPosts);
        pageBean.setRows(Long.valueOf(pageInfo.getTotal()).intValue());
        return pageBean;
    }

    /**
     * 根据id查询讨论贴
     * @param id
     * @return
     */
    public DiscussPost getDiscussPostById(Integer id) {
        return discussPostMapper.get(id);
    }

    /**
     * 发布新帖
     * @param discussPost
     */
    public void addDiscussPost(DiscussPost discussPost) {
        // 判空
        if (discussPost == null) {
            throw new IllegalArgumentException("参数非法!");
        }
        // 敏感词过滤、特殊标签处理
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));
        // 添加
        discussPostMapper.insert(discussPost);
    }

    /**
     *
     * @param postId
     * @param type
     */
    @Override
    public void updateType(Integer postId, Integer type) {
        discussPostMapper.updateType(postId, type);
    }

    @Override
    public void updateStatus(Integer postId, Integer status) {
        discussPostMapper.updateStatus(postId, status);
    }

    @Override
    public void updateScore(Integer postId, Double score) {
        discussPostMapper.updateScore(postId, score);
    }
}
