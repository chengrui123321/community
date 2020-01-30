package com.newcoder.community.util;

import org.springframework.util.ObjectUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 获取cookie值
 */
public class CookieUtil {

    public static String getValue(HttpServletRequest request, String name) {
        // 获取所有的Cookie
        Cookie[] cookies = request.getCookies();
        if (!ObjectUtils.isEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
