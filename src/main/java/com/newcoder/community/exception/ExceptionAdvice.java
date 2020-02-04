package com.newcoder.community.exception;

import com.newcoder.community.util.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 全局异常处理
 */
@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    /**
     * 异常拦截方法
     * @param ex
     * @param request
     * @param response
     */
    @ExceptionHandler(Exception.class)
    public void handleException(Exception ex, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("服务器发生异常: " + ex.getMessage());
        for (StackTraceElement element : ex.getStackTrace()) {
            log.error(element.toString());
        }
        ex.printStackTrace();
        // 判断是否是异步请求
        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            // 异步请求
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(CommunityUtil.getJSONString(1, "服务器发生异常!"));
        } else {
            // 普通请求由Spring Boot默认处理方式处理
            response.sendRedirect(request.getContextPath() + "/error");
        }

    }

}
