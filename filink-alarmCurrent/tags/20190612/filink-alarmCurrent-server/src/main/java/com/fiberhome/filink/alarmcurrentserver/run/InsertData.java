package com.fiberhome.filink.alarmcurrentserver.run;

import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/6/14
 */
@Component
public class InsertData implements CommandLineRunner {
    @Autowired
    private AlarmCurrentService alarmCurrentService;


    @Override
    public void run(String... strings) throws Exception {
        System.out.println("造数据开始-------");
        alarmCurrentService.addAlarmCurrentData();
        System.out.println("造数据结束-------");
    }
}
