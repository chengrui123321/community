package com.newcoder.community.controller;

import com.aliyun.oss.OSSClient;
import com.newcoder.community.domain.User;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

}
