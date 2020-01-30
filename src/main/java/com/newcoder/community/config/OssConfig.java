package com.newcoder.community.config;

import com.aliyun.oss.OSSClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * OSS服务配置
 */
@ConfigurationProperties(prefix = "oss")
@PropertySource("classpath:ali-oss.properties")
@Configuration
@Data
public class OssConfig {

    private String endpoint;
    private String accessKeyId;
    private String secretAccessKey;

    @Bean
    public OSSClient ossClient() {
        return new OSSClient(endpoint, accessKeyId, secretAccessKey);
    }

}
