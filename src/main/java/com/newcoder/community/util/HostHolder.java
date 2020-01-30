package com.newcoder.community.util;

import com.newcoder.community.domain.User;
import org.springframework.stereotype.Component;

/**
 * 使用ThreadLocal保存用户信息
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void set(User user) {
        users.set(user);
    }

    public User get() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

}
