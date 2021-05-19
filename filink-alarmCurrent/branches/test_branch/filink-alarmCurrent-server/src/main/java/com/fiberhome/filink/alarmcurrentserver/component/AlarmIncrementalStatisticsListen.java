package com.fiberhome.filink.alarmcurrentserver.component;

import com.fiberhome.filink.alarmcurrentserver.alarmrecive.AlarmChannel;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmSourceHomeParameter;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * 告警增量统计定时任务(日)
 *
 * @author wk
 */
@Component
@Slf4j
public class AlarmIncrementalStatisticsListen {


    @Autowired
    private AlarmStatisticsService alarmStatisticsTempService;

    /**
     * 监听消息
     *
     * @param code 消息码
     */
    @StreamListener(AlarmChannel.INCREMENTAL_STATISTICSLISTEN)
    public void incrementalStatisticsListen(String code) {

        if (AppConstant.INCREMENTAL_STATISTICSLISTEN.equals(code)) {
            AlarmSourceHomeParameter alarmSourceHomeParameter = new AlarmSourceHomeParameter();
            alarmSourceHomeParameter.setType(AppConstant.DAY);
            alarmStatisticsTempService.queryStatisticsData(alarmSourceHomeParameter);
        }
    }

}
