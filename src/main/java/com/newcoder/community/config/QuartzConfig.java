package com.newcoder.community.config;

import com.newcoder.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * @Auther: r.cheng
 * @Date: 2020/2/4 19:59
 * @Description: Quartz 定时器配置
 * @Version: 1.0
 */
@Configuration
public class QuartzConfig {

    /**
     * 注入JobDetail
     * @return
     */
    @Bean
    public JobDetailFactoryBean jobDetailFactoryBean() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        // 设置需要执行的Job
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        // 设置任务名称
        factoryBean.setBeanName("postScoreRefreshJob");
        // 设置任务组
        factoryBean.setGroup("communityJobGroup");
        // 设置持久化
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    /**
     * 注入SimpleTriggerFactoryBean 触发器
     * @return
     */
    @Bean
    public SimpleTriggerFactoryBean simpleTriggerFactoryBean(JobDetail jobDetail) {
        // 创建SimpleTriggerFactoryBean
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        // 设置JobDetail
        factoryBean.setJobDetail(jobDetail);
        // 设置触发器名称
        factoryBean.setBeanName("postScoreRefreshTrigger");
        // 设置触发器群组
        factoryBean.setGroup("communityTriggerGroup");
        // 设置刷新时间间隔
        factoryBean.setRepeatInterval(1000 * 60);
        // 设置参数
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
