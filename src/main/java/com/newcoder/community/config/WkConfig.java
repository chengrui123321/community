package com.newcoder.community.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @Auther: r.cheng
 * @Date: 2020/2/5 00:31
 * @Description: 分享插件配置
 * @Version: 1.0
 */
@Configuration
@Slf4j
public class WkConfig {

    @Value("${wk.image.storage}")
    private String storage;

    /**
     * 初始化文件目录
     */
    @PostConstruct
    public void init() {
        // 创建服务器本地存放目录
        File file = new File(storage);
        if (!file.exists()) {
            file.mkdirs();
            log.info("创建本地文件目录: " + storage);
        }
    }

}
