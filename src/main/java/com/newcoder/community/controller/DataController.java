package com.newcoder.community.controller;

import com.newcoder.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @Auther: r.cheng
 * @Date: 2020/2/4 22:20
 * @Description: 统计数据
 * @Version: 1.0
 */
@Controller
@RequestMapping("/data")
public class DataController {

    @Autowired
    DataService dataService;

    /**
     * 跳转到数据统计页面
     * @return
     */
    @RequestMapping(value = "/page", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDataPage() {
        return "site/admin/data";
    }

    /**
     * 统计区间UV数量
     * @param start
     * @param end
     * @param model
     * @return
     */
    @PostMapping("/uv")
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd")Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                        Model model) {
        // 统计UV
        long result = dataService.calculateUV(start, end);
        // 保存结果
        model.addAttribute("uvCount", result);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        return "forward:/data/page";
    }

    /**
     * 统计区间DAU
     * @param start
     * @param end
     * @param model
     * @return
     */
    @PostMapping("/dau")
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd")Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                        Model model) {
        // 统计UV
        long result = dataService.calculateDAU(start, end);
        // 保存结果
        model.addAttribute("dauCount", result);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        return "forward:/data/page";
    }

}
