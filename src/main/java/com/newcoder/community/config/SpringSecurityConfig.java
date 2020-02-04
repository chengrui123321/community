package com.newcoder.community.config;

import com.newcoder.community.util.CommunityConstant;
import com.newcoder.community.util.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Auther: r.cheng
 * @Date: 2020/2/4 13:47
 * @Description: Spring Security 配置类
 * @Version: 1.0
 */
@Configuration
@Slf4j
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 过滤静态资源
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 忽略静态资源
        web.ignoring().antMatchers("/resources/**");
    }

    /**
     * 处理登录、登出、授权
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 权限配置
        http.authorizeRequests()
                .antMatchers(
                    "/user/**",
                        "/like",
                        "/message/**",
                        "/follow",
                        "/unFollow",
                        "/followee/**",
                        "/follower/**",
                        "/comment/**",
                        "/discuss/add"
                )
                .hasAnyAuthority(
                        CommunityConstant.AUTHORITY_ADMIN,
                        CommunityConstant.AUTHORITY_MODERATOR,
                        CommunityConstant.AUTHORITY_USER
                )
                .antMatchers(
                        "/discuss/top",
                        "/discuss/wonderful"
                )
                .hasAnyAuthority(CommunityConstant.AUTHORITY_MODERATOR)
                .antMatchers(
                        "/discuss/delete",
                        "/data/**"
                )
                .hasAnyAuthority(CommunityConstant.AUTHORITY_ADMIN)
                .anyRequest().permitAll()
                .and()
                .csrf().disable(); // 禁用csrf

        // 没有权限处理配置
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    // 没有登录
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        log.error("没有登录!" + request.getServletPath());
                        // 判断是否是异步请求
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            // 异步请求
                            response.setContentType("application/plain;charset=utf-8");
                            response.getWriter().write(CommunityUtil.getJSONString(403, "您还没有登录!"));
                        } else {
                            // 跳转到登录页面
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    // 已经登录，没有权限访问
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        log.error("没有权限!" + request.getServletPath());
                        // 判断是否是异步请求
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            // 异步请求
                            response.setContentType("application/plain;charset=utf-8");
                            response.getWriter().write(CommunityUtil.getJSONString(403, "您没有权限访问该路径!"));
                        } else {
                            // 跳转到拒绝访问页面
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });
        // 退出登录
        http.logout()
                .logoutUrl("/securityLogout");
    }
}
