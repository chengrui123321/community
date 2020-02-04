package com.newcoder.community.actuator;

import com.newcoder.community.util.CommunityUtil;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: r.cheng
 * @Date: 2020/2/5 01:46
 * @Description: 自定义监控信息
 * @Version: 1.0
 */
@Endpoint(id = "company")
@Component
public class CompanyInfo {

    @ReadOperation
    public String readCompanyInfo() {
        Map<String, Object> map = new HashMap<>();
        map.put("companyName", "宇宙vip互联网学校");
        map.put("companyAddress", "合肥市");
        map.put("companyTel", "18688886666");
        return CommunityUtil.getJSONString(0, "获取公司信息成功!", map);
    }

}
