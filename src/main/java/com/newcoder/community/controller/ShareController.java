package com.newcoder.community.controller;

import com.newcoder.community.domain.Event;
import com.newcoder.community.kafka.EventProducer;
import com.newcoder.community.util.CommunityConstant;
import com.newcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: r.cheng
 * @Date: 2020/2/5 00:20
 * @Description: 分享长图/pdf
 * @Version: 1.0
 */
@Controller
public class ShareController {

    @Autowired
    EventProducer eventProducer;

    @Value("${oss.bucketName}")
    private String bucketName;

    @Value("${oss.endpoint}")
    private String endPoint;

    /**
     * 分享图片到阿里云oss
     * @param url
     * @param folder
     * @return
     */
    @GetMapping("/share")
    @ResponseBody
    public String share(String url, String folder) {
        // 创建文件名
        String fileName = folder + "/" + CommunityUtil.genUUID();
        // 创建分享事件
        Event event = new Event()
                .setTopic(CommunityConstant.TOPIC_SHARE)
                .setData("url", url)
                .setData("suffix", ".png")
                .setData("fileName", fileName);
        // 发布事件
        eventProducer.fireEvent(event);
        // 保存oss服务器url
        Map<String, Object> map = new HashMap<>();
        map.put("shareUrl", "http://"+bucketName+"."+ endPoint.replace("http://","") +"/"+fileName + ".png");
        return CommunityUtil.getJSONString(0, "分享成功", map);
    }

}
