package com.fiberhome.filink.filinklockapi.hystrix;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.bean.RemoveAlarm;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 主控熔断类
 *
 * @author CongcaiYu
 */
@Log4j
@Component
public class ControlHystrix implements ControlFeign {

    /**
     * 判断服务是否畅通
     *
     * @return true:畅通
     */
    @Override
    public boolean ping() {
        return false;
    }

    /**
     * 根据设施id查询主控信息
     *
     * @param deviceId 设施id
     * @return 主控信息
     */
    @Override
    public List<ControlParam> getControlParams(String deviceId) {
        log.info("query control params failed>>>>>>>>>>>>");
        return null;
    }

    /**
     * 根据主控id获取设施id
     *
     * @param controlId 主控id
     * @return 设施id
     * @throws Exception
     */
    @Override
    public String getDeviceIdByControlId(String controlId) throws Exception {
        log.info("get deviceId  failed>>>>>>>>>>>>");
        throw new Exception();
    }


    /**
     * 根据id查询主控信息
     *
     * @param controlId
     * @return 主控信息
     */
    @Override
    public ControlParam getControlParamById(String controlId) {
        log.info("query control params by controlId failed>>>>>>>>>>>>");
        return null;
    }

    /**
     * 根据主控id更新主控的部署状态
     *
     * @param controlParam 主控信息
     * @return 结果
     */
    @Override
    public Result updateDeployStatusById(ControlParam controlParam) {
        log.info("update deployStatus by controlId failed>>>>>>>>>>>>");
        return null;
    }

    /**
     * 根据主控id更新主控的设施状态
     *
     * @param controlParam 主控信息
     * @return 查询结果
     */
    @Override
    public Result updateDeviceStatusById(ControlParam controlParam) {
        log.info("update deviceStatus failed >>>>>>>>>>>>");
        return null;
    }

    /**
     * 根据主控id更新主控的设施状态及部署状态
     *
     * @param controlParam 主控信息
     * @return 查询结果
     */
    @Override
    public Result updateControlStatusById(ControlParam controlParam) {
        log.info("update deviceStatus and deployStatus failed >>>>>>>>>>>>");
        return null;
    }

    /**
     * 根据设施id更新主控的部署状态
     *
     * @param controlParam 主控信息
     */
    @Override
    public Result updateDeployStatusByDeviceId(ControlParam controlParam) {
        log.info("update deviceStatus  by deviceId failed>>>>>>>>>>>>");
        return null;
    }

    /**
     * 更新主控信息
     *
     * @param controlParam 主控信息
     * @return 结果
     */
    @Override
    public Result updateControlParam(ControlParam controlParam) {
        log.info("update control param failed>>>>>>>>>>>>");
        return null;
    }

    /**
     * 根据设施id删除主控
     *
     * @param deviceIdList 设施id集合
     * @return 操作结果
     */
    @Override
    public Result deleteControlByDeviceIds(List<String> deviceIdList) {
        log.info("delete control param failed>>>>>>>>>>>>");
        return null;
    }

    /**
     * 根据主控id及告警类型清除告警显示状态
     *
     * @param removeAlarmList 告警清除集合
     * @return 操作结果
     */
    @Override
    public Result removeAlarm(List<RemoveAlarm> removeAlarmList) {
        log.info("remove alarm display failed>>>>>>>>>>>>");
        return null;
    }
}
