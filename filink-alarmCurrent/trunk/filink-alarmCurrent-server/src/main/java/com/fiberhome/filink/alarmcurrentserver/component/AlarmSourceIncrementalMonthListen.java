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
 * 设施告警增量按月统计定时任务
 * </p>
 *
 * @author weikaun@fiberhome.com
 * @since 2019-06-20
 */
@Slf4j
@Component
public class AlarmSourceIncrementalMonthListen {

    private static final String INCREMENTAL_EXPIRE_MONTH = "INCREMENTAL_EXPIRE_MONTH";

    @Autowired
    private AlarmStatisticsServiceImpl alarmStatisticsTempService;

    @StreamListener(AlarmChannel.INCREMENTAL_EXPIRE_MONTH)
    public void incrementalSourceListen(String code) {
        if (INCREMENTAL_EXPIRE_MONTH.equals(code)) {
            AlarmSourceHomeParameter alarmSourceHomeParameter = new AlarmSourceHomeParameter();
            alarmSourceHomeParameter.setType(AppConstant.MONTH);
            //告警增量按区域、设施类型统计
            alarmStatisticsTempService.queryStatisticsData(alarmSourceHomeParameter);
            log.info("alarm incremental statistics by month: for area and deviceType success");
        }
    }
}
