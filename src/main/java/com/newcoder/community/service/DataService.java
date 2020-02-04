package com.newcoder.community.service;

import java.util.Date;

/**
 * @Auther: r.cheng
 * @Date: 2020/2/4 21:58
 * @Description: 数据统计业务
 * @Version: 1.0
 */
public interface DataService {

    void recordUV(String ip);

    long calculateUV(Date start, Date end);

    void recordDAU(Integer userId);

    long calculateDAU(Date start, Date end);
}
