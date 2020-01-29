package com.newcoder.community.util;

import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * 工具类
 */
public class CommunityUtil {

    /**
     * 使用UUID生成随机字符串
     * @return
     */
    public static String genUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 使用MD5加密字符串
     * @param key
     * @return
     */
    public static String md5(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

}
