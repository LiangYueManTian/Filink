package com.fiberhome.filink.alarmcurrentserver.component;

import com.fiberhome.filink.alarmcurrentserver.alarmrecive.AlarmChannel;
import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrent18n;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * 当前转历史
 * @author weikaun@fiberhome.com
 */
@Component
@Slf4j
public class AlarmCleanStatusListen {

    @Autowired
    private AlarmCurrentExportService alarmCurrentExportService;


    @StreamListener(AlarmChannel.ALARM_CLEAN_STATUS)
    public void incrementalStatisticsListen(String code) {
        if (AlarmCurrent18n.ALARM_CLEAN_STATUS.equals(code)) {
            log.info("schedule alarm current to history: code: {}", code);
            alarmCurrentExportService.updateAlarmCleanStatusCompensation();
        }
    }

}
