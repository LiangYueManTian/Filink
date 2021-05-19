package com.fiberhome.filink.alarmhistoryserver.run;


import com.fiberhome.filink.alarmhistoryserver.service.AlarmHistoryService;
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
    private AlarmHistoryService alarmHistoryService;


    @Override
    public void run(String... strings) throws Exception {
        System.out.println("造数据开始-------");
        alarmHistoryService.addAlarmHistoryData();
        System.out.println("造数据结束-------");
    }
}
