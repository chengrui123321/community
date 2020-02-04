package com.newcoder.community.quartz;

import com.newcoder.community.domain.DiscussPost;
import com.newcoder.community.service.DiscussPostService;
import com.newcoder.community.service.ElasticsearchService;
import com.newcoder.community.service.LikeService;
import com.newcoder.community.util.CommunityConstant;
import com.newcoder.community.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auther: r.cheng
 * @Date: 2020/2/4 19:30
 * @Description: 帖子分数刷新Job
 * @Version: 1.0
 */
@Slf4j
public class PostScoreRefreshJob implements Job {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    LikeService likeService;

    @Autowired
    ElasticsearchService elasticsearchService;

    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-01-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化纪元时间失败!");
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 获取计算帖子分数 redis key
        String postKey = RedisKeyUtil.getPostKey();
        // 获取需要计算分数的帖子
        BoundSetOperations operations = redisTemplate.boundSetOps(postKey);
        if (operations.size() == 0) {
            // 没有需要刷新的帖子,跳过
            log.info("没有需要刷新的帖子，放行!");
            return;
        }
        log.info("正在刷新帖子分数: " + operations.size());
        while (operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }
        log.info("刷新帖子分数结束");
    }

    /**
     * 刷新某个帖子分数
     * @param postId
     */
    private void refresh(Integer postId) {
        // 查询帖子
        DiscussPost post = discussPostService.getDiscussPostById(postId);
        if (post == null) {
            log.error("帖子不存在: " + postId);
            return;
        }
        // 是否精华
        boolean isWonderful = post.getStatus() == 1 ? true : false;
        // 评论数量
        int commentCount = post.getCommentCount();
        // 点赞数量
        int likeCount = likeService.getEntityLikeCount(CommunityConstant.ENTITY_TYPE_POST, postId);
        // 计算权重
        double q = (isWonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 计算分数(帖子权重 + 日期)
        double score = Math.log10(Math.max(q, 1)) + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);
        // 更新帖子分数
        discussPostService.updateScore(postId, score);
        // 将更新后的帖子放入ES
        post.setScore(score);
        elasticsearchService.save(post);
    }
}
