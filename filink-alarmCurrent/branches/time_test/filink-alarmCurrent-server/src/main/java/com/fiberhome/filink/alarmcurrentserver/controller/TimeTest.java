package com.fiberhome.filink.alarmcurrentserver.controller;

import com.fiberhome.filink.alarmcurrentserver.utils.DateUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * todo 告警时间测试类
 */
@RestController
@RequestMapping("/test")
public class TimeTest {

    @GetMapping("/a/{beginTime}/{endTime}")
    public List<String> getTimeDayBetween(@PathVariable Long beginTime, @PathVariable Long endTime) {
        return DateUtil.getTimeDayBetween(beginTime, endTime);
    }

    @GetMapping("/b")
    public List<String> getTimeWeekBetween() {
        return DateUtil.getTimeWeekBetween();
    }
    @GetMapping("/c/{beginTime}/{endTime}")
    public List<String> getTimeWeekBetween(@PathVariable Long beginTime, @PathVariable Long endTime) {
        return DateUtil.getTimeWeekBetween(beginTime, endTime);
    }

    @GetMapping("/d")
    public List<String> getTimeMonthBetween() {
        return DateUtil.getTimeMonthBetween();
    }
    @GetMapping("/e/{num}")
    public Long getAdvanceNumberDay(@PathVariable int num) {
        return DateUtil.getAdvanceNumberDay(num);
    }
    @GetMapping("/f/{time}")
    public Long getTimeZeroTime(@PathVariable Long time) {
        return DateUtil.getTimeZeroTime(time);
    }
    @GetMapping("/g")
    public Long getAdvanceNumberEndDay() {
        return DateUtil.getAdvanceNumberEndDay();
    }
    @GetMapping("/h/{num}")
    public Long getAdvanceNumberWeek(@PathVariable int num) {
        return DateUtil.getAdvanceNumberWeek(num);
    }
    @GetMapping("/i")
    public Long getAdvanceNumberEndWeek() {
        return DateUtil.getAdvanceNumberEndWeek();
    }
    @GetMapping("/j/{num}")
    public Long getAdvanceNumberMonth(@PathVariable int num) {
        return DateUtil.getAdvanceNumberMonth(num);
    }
    @GetMapping("/k")
    public Long getAdvanceNumberEndMonth() {
        Date date = new Date();
        return DateUtil.getAdvanceNumberEndMonth();
    }
}
