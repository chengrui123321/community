package com.newcoder.community;

import com.github.pagehelper.PageHelper;
import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.dao.UserMapper;
import com.newcoder.community.domain.DiscussPost;
import com.newcoder.community.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommunityApplicationTests {

    @Autowired
    UserMapper userMapper;

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Test
    public void testPage() {
        PageHelper.startPage(1, 10);
        List<DiscussPost> discussPosts = discussPostMapper.list(null);
        discussPosts.forEach(System.out::println);
    }

    @Test
    public void contextLoads() {
        User user = userMapper.get(1);
        System.out.println(user);
    }

}
