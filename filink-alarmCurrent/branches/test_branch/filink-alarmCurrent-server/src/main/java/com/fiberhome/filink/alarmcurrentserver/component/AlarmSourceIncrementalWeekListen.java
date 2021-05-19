package com.fiberhome.filink.alarmcurrentserver.component;

import com.fiberhome.filink.alarmcurrentserver.alarmrecive.AlarmChannel;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmSourceHomeParameter;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.service.impl.AlarmStatisticsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 设施告警增量按周统计定时任务
 * </p>
 *
 * @author weikaun@fiberhome.com
 * @since 2019-06-20
 */
@Slf4j
@Component
public class AlarmSourceIncrementalWeekListen {

    private static final String INCREMENTAL_EXPIRE_WEEK = "INCREMENTAL_EXPIRE_WEEK";

    @Autowired
    private AlarmStatisticsServiceImpl alarmStatisticsTempService;

    @StreamListener(AlarmChannel.INCREMENTAL_EXPIRE_WEEK)
    public void incrementalSourceListen(String code) {
        if (INCREMENTAL_EXPIRE_WEEK.equals(code)) {
            AlarmSourceHomeParameter alarmSourceHomeParameter = new AlarmSourceHomeParameter();
            alarmSourceHomeParameter.setSource(2);
            alarmStatisticsTempService.querySourceIncremental(alarmSourceHomeParameter);
            System.out.println("-----------------aaaaaaaaaaaa告警周");
            AlarmSourceHomeParameter sourceHomeParameter = new AlarmSourceHomeParameter();
            sourceHomeParameter.setType(AppConstant.WEEK);
            alarmStatisticsTempService.queryStatisticsData(sourceHomeParameter);
            System.out.println("-----------------aaaaaaaaaaaa告警周");
        }
    }
}
