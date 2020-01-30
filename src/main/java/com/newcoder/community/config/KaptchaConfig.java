package com.newcoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Kaptcha 验证码配置类
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public Producer producer() {
        DefaultKaptcha producer = new DefaultKaptcha();
        // 创建Properties对象放入验证码参数
        Properties properties = new Properties();
        // 宽度
        properties.setProperty("kaptcha.image.width", "100");
        // 高度
        properties.setProperty("kaptcha.image.height", "40");
        // 字号
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        // 颜色
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
        // 字符
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYAZ");
        // 字符数量
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 是否有干扰项
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        Config config = new Config(properties);
        producer.setConfig(config);
        return producer;
    }

}
