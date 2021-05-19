package com.fiberhome.filink.alarmhistoryserver.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@RestController
@RequestMapping("/test")
public class Test {
    @Autowired
    private AlarmService alarmService;

    @GetMapping("/current/{count}")
    public void current(@PathVariable int count) {
        alarmService.addAlarmCurrent(count);
    }

    @GetMapping("/history/{count}")
    public void history(@PathVariable int count) {
        alarmService.addAlarmHistory(count);
    }
}
