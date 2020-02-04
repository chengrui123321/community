package com.newcoder.community.service.impl;

import com.newcoder.community.service.DataService;
import com.newcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Auther: r.cheng
 * @Date: 2020/2/4 22:01
 * @Description: 数据统计业务
 * @Version: 1.0
 */
@Service
public class DataServiceImpl implements DataService {

    @Autowired
    RedisTemplate redisTemplate;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    /**
     * 记录UV
     * @param ip
     */
    @Override
    public void recordUV(String ip) {
        // 获取UV key
        String uvKey = RedisKeyUtil.getUVKey(sdf.format(new Date()));
        // 保存,使用HyperLogLog数据类型,可以具有很好的去重功能
        redisTemplate.opsForHyperLogLog().add(uvKey, ip);
    }

    /**
     * 统计区间UV数量
     * @param start
     * @param end
     * @return
     */
    @Override
    public long calculateUV(Date start, Date end) {
        // 判空
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不合法!");
        }
        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        // 设置开始时间
        calendar.setTime(start);
        List<String> keyList = new ArrayList<>();
        // 遍历
        while (!calendar.getTime().after(end)) {
            String uvKey = RedisKeyUtil.getUVKey(sdf.format(calendar.getTime()));
            keyList.add(uvKey);
            calendar.add(Calendar.DATE, 1);
        }
        // 合并数据
        String redisKey = RedisKeyUtil.getUVKey(sdf.format(start), sdf.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray());
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    /**
     * 记录DAU
     * @param userId
     */
    @Override
    public void recordDAU(Integer userId) {
        // 获取日活跃用户key
        String dauKey = RedisKeyUtil.getDAUKey(sdf.format(new Date()));
        // 保存,使用BitMap数据类型
        redisTemplate.opsForValue().setBit(dauKey, userId, true);
    }

    /**
     * 统计区间DAU数量
     * @param start
     * @param end
     * @return
     */
    @Override
    public long calculateDAU(Date start, Date end) {
        // 判空
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不合法!");
        }
        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        // 设置开始时间
        calendar.setTime(start);
        List<String> keyList = new ArrayList<>();
        // 遍历
        while (!calendar.getTime().after(end)) {
            String dauKey = RedisKeyUtil.getDAUKey(sdf.format(calendar.getTime()));
            keyList.add(dauKey);
            calendar.add(Calendar.DATE, 1);
        }
        // 进行OR运算
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(sdf.format(start), sdf.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(), keyList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });
    }
}
