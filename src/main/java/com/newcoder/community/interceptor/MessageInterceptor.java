package com.newcoder.community.interceptor;

import com.newcoder.community.domain.User;
import com.newcoder.community.service.MessageService;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 消息数量拦截器
 */
@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.get();
        if (user != null && modelAndView != null) {
            Integer unreadLetterCount = messageService.findUnReadLetterCount(user.getId(), null);
            Integer unreadNoticeCount = messageService.findUnreadNoticeCount(null, user.getId());
            modelAndView.addObject("allUnreadCount", unreadLetterCount + unreadNoticeCount);
        }
    }
}
