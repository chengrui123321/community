package com.newcoder.community.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * 业务逻辑层处理日志记录
 */
@Component
@Aspect
@Slf4j
public class ServiceLogAspect {

    @Pointcut("execution(* com.newcoder.community.service.*.*(..))")
    public void pointCut() {}

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        // 获取请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        // 获取ip
        String ip = request.getRemoteHost();
        // 获取请求参数
        String param = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + Arrays.toString(joinPoint.getArgs());
        // 访问时间
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        // 记录日志
        log.info(String.format("用户[%s]:在[%s]访问了[%s]", ip, time, param));
    }

}
