package com.fiberhome.filink.alarmCurrent_api.fallback;


import com.fiberhome.filink.alarmCurrent_api.api.AlarmCurrentFeign;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 告警服务熔断
 *
 * @author wtao103@fiberhome.com
 * create on 2018/12/16 5:39 PM
 */
@Slf4j
@Component
public class AlarmFeignFallback implements AlarmCurrentFeign {

    /**
     * 查询设备信息是否存在
     *
     * @param alarmSources 设备id
     * @return 判断结果
     */
    @Override
    public List<String> queryAlarmSource(List<String> alarmSources) {
        log.info("queryAlarmSource 熔断》》》》》》》");
        return null;
    }

    /**
     * 查询区域信息是否存在
     *
     * @param areas 区域id
     * @return 判断结果
     */
    @Override
    public List<String> queryArea(List<String> areas) {
        log.info("queryArea 熔断》》》》》》》");
        return null;
    }
}
