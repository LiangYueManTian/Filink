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
 * 设施告警增量按日统计定时任务
 * </p>
 *
 * @author weikaun@fiberhome.com
 * @since 2019-06-20
 */
@Slf4j
@Component
public class AlarmSourceIncrementalListen {

    private static final String INCREMENTAL_EXPIRE = "INCREMENTAL_EXPIRE";

    @Autowired
    private AlarmStatisticsServiceImpl alarmStatisticsTempService;

    @StreamListener(AlarmChannel.INCREMENTAL_EXPIRE)
    public void incrementalSourceListen(String code) {
        if (INCREMENTAL_EXPIRE.equals(code)) {
            AlarmSourceHomeParameter alarmSourceHomeParameter = new AlarmSourceHomeParameter();
            alarmSourceHomeParameter.setSource(1);
            //告警增量按设施统计
            alarmStatisticsTempService.querySourceIncremental(alarmSourceHomeParameter);
            log.info("alarm incremental statistics by day: for device success");
            AlarmSourceHomeParameter sourceHomeParameter = new AlarmSourceHomeParameter();
            sourceHomeParameter.setType(AppConstant.DAY);
            //告警增量按区域、设施类型统计
            alarmStatisticsTempService.queryStatisticsData(sourceHomeParameter);
            log.info("alarm incremental statistics by day: for area and deviceType success");
        }
    }
}
