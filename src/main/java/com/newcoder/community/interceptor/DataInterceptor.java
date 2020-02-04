package com.newcoder.community.interceptor;

import com.newcoder.community.domain.User;
import com.newcoder.community.service.DataService;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: r.cheng
 * @Date: 2020/2/4 22:18
 * @Description: 数据统计拦截器
 * @Version: 1.0
 */
@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    DataService dataService;

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取登录ip
        String ip = request.getRemoteHost();
        // 记录UV
        dataService.recordUV(ip);
        // 如果用户登录，则记录DAU
        User user = hostHolder.get();
        if (user != null) {
            dataService.recordDAU(user.getId());
        }
        return true;
    }
}
