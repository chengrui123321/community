package com.newcoder.community.controller;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.PutObjectResult;
import com.newcoder.community.domain.User;
import com.newcoder.community.service.FollowerService;
import com.newcoder.community.service.LikeService;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommunityConstant;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * 用户
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    OSSClient ossClient;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Value("${oss.bucketName}")
    private String bucketName;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowerService followerService;

    /**
     * 跳转到setting页面
     * @return
     */
    @GetMapping("/setting")
    public String getSettingPage() {
        return "site/setting";
    }

    /**
     * 阿里oss上传
     * @param file
     * @param folder
     * @return
     */
    @PostMapping("/oss")
    public String ossUpload(@RequestParam("file") MultipartFile file, String folder){
        String fileName= folder+"/"+ UUID.randomUUID()+"_"+file.getOriginalFilename();
        try {
            ossClient.putObject(bucketName, fileName, file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String headerUrl = "http://"+bucketName+"."+ ossClient.getEndpoint().toString().replace("http://","") +"/"+fileName;
        // 获取用户对象
        User user = hostHolder.get();
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }

    /**
     * 修改密码
     * @param oldPwd
     * @param newPwd
     * @param model
     * @return
     */
    @PostMapping("/updatePwd")
    public String updatePassword(String oldPwd, String newPwd, Model model) {
        // 获取当前用户
        User user = hostHolder.get();
        // 修改密码
        Map<String, Object> map = userService.updatePassword(oldPwd, newPwd, user.getId());
        if (ObjectUtils.isEmpty(map)) {
            return "redirect:/logout";
        } else {
            model.addAttribute("newMsg", map.get("newMsg"));
            model.addAttribute("oldMsg", map.get("oldMsg"));
            return "site/setting";
        }
    }

    /**
     * 跳转到个人主页
     * @param userId
     * @param model
     * @return
     */
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") Integer userId, Model model) {

        // 查询用户
        User user = userService.get(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        // 保存用户信息
        model.addAttribute("user", user);
        // 获取用户的赞数量
        Integer likeCount = likeService.getUserLikeCount(userId);
        // 保存用户赞
        model.addAttribute("likeCount", likeCount);
        // 获取粉丝数量
        Integer followerCount = followerService.getFollowerCount(CommunityConstant.ENTITY_TYPE_USER, userId);
        // 保存粉丝数量
        model.addAttribute("followerCount", followerCount);
        // 获取关注人数数量
        Integer followeeCount = followerService.getFolloweeCount(userId, CommunityConstant.ENTITY_TYPE_USER);
        // 保存关注人数数量
        model.addAttribute("followeeCount", followeeCount);
        // 查询当前用户是否已关注该用户
        Boolean hasFollowed = false;
        if (hostHolder.get() != null) {
            hasFollowed = followerService.hasFollowed(hostHolder.get().getId(), CommunityConstant.ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);
        return "site/profile";
    }

}
