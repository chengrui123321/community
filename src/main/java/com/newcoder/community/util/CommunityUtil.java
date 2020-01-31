package com.newcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
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

    /**
     * 将指定参数转为json
     * @param code
     * @param msg
     * @param map
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    /**
     * 将指定参数转为json，没有结果集
     * @param code
     * @param msg
     * @return
     */
    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    /**
     * 将指定参数转为json，没有结果集和消息
     * @param code
     * @return
     */
    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

}
