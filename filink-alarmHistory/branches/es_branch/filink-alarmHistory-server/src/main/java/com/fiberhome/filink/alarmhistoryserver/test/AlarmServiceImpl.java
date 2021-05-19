package com.fiberhome.filink.alarmhistoryserver.test;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Service
@Slf4j
public class AlarmServiceImpl implements AlarmService {

    @Autowired
    private DtoEnd dtoEnd;
    /**
     * test
     * @param count 1
     */
    @Override
    public void addAlarmHistory(int count) {

        for (int i = 1; i <= count; i++) {
            dtoEnd.addAlarmHistory();
            log.info("完成addAlarmHistory：第{}任务,下一个任务", i);
        }

    }

    /**
     * test
     * @param count 1
     */
    @Override
    public void addAlarmCurrent(int count) {
        for (int i = 1; i <= count; i++) {
            dtoEnd.addAlarmCurrent();
            log.info("完成addAlarmCurrent：第{}任务,下一个任务", i);
        }
    }
}
