package com.newcoder.community.service.impl;

import com.github.pagehelper.PageInfo;
import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.domain.DiscussPost;
import com.newcoder.community.domain.PageBean;
import com.newcoder.community.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public PageBean<DiscussPost> list(Integer userId) {
        //查询讨论贴
        List<DiscussPost> discussPosts = discussPostMapper.list(userId);
        PageInfo<DiscussPost> pageInfo = new PageInfo<>(discussPosts);
        //封装分页查询结果
        PageBean<DiscussPost> pageBean = new PageBean<>();
        pageBean.setContent(discussPosts);
        pageBean.setRows(Long.valueOf(pageInfo.getTotal()).intValue());
        return pageBean;
    }
}
