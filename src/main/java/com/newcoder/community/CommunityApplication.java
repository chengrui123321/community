package com.newcoder.community;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class CommunityApplication implements InitializingBean {

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }
}
