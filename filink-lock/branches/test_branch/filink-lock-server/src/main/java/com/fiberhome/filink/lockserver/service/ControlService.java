package com.fiberhome.filink.lockserver.service;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.lockserver.bean.ControlParam;
import com.fiberhome.filink.lockserver.bean.ControlParamForControl;
import com.fiberhome.filink.lockserver.bean.ControlReq;
import com.fiberhome.filink.lockserver.bean.RemoveAlarm;
import com.fiberhome.filink.lockserver.bean.SetConfigBean;

import java.util.List;

/**
 * 主控信息接口
 *
 * @author CongcaiYu
 */
public interface ControlService {

    /**
     * 根据设施id查询主控信息(不需要权限)
     *
     * @param deviceId 设施id
     * @return 主控信息
     */
    List<ControlParam> getControlInfoByDeviceId(String deviceId);

    /**
     * 根据设施id查询主控信息
     *
     * @param deviceId 设施id
     * @return 主控信息
     */
    List<ControlParam> getControlInfoByDeviceIdToCall(String deviceId);

    /**
     * 根据设施id查询主控信息和门禁信息
     *
     * @param deviceId 设施id
     * @return 主控信息
     */
    //todo build3 查询设施信息时把主控及门禁全部拿到
//    List<ControlParam> getControlAndDoorByDeviceId(String deviceId);

    /**
     * 根据设施id和主控名称查询主控信息
     *
     * @param controlParam 主控信息参数
     * @return 主控信息
     */
    ControlParam getControlByDeviceIdAndControlName(ControlParam controlParam);


    /**
     * 根据设施id查询主控信息
     *
     * @param deviceId 设施id
     * @return 主控信息
     */
    List<ControlParamForControl> getControlParamForControlByDeviceId(String deviceId);

    /**
     * 根据主控id获取设施id
     *
     * @param controlId 主控id
     * @return 设施id
     */
    String getDeviceIdByControlId(String controlId);


    /**
     * 根据主控id查询电子锁主控信息
     *
     * @param controlReq 主控信息请求对象
     * @return 查询结果
     */
    Result queryControlByControlId(ControlReq controlReq);


    /**
     * 配置设施策略
     *
     * @param setConfigBean 参数信息
     * @return 操作结果
     */
    Result setConfig(SetConfigBean setConfigBean);

    /**
     * 根据id查询主控信息
     *
     * @param controlId
     * @return 主控信息
     */
    ControlParam getControlParamById(String controlId);

    /**
     * 新增主控信息
     *
     * @param controlParam 主控信息
     */
    void addControlParam(ControlParam controlParam);

    /**
     * 更新主控信息
     *
     * @param controlParam 主控信息
     * @param type         操作类型
     */
    void updateControlParam(ControlParam controlParam, String type);


    /**
     * 根据主控id更新主控的部署状态
     *
     * @param controlParam 主控信息
     */
    void updateDeployStatusById(ControlParam controlParam);


    /**
     * 根据主控id更新主控的设施状态
     *
     * @param controlParam 主控信息
     */
    void updateDeviceStatusById(ControlParam controlParam);

    /**
     * 根据主控id更新主控的设施状态及部署状态
     *
     * @param controlParam 主控信息
     */
    void updateControlStatusById(ControlParam controlParam);

    /**
     * 根据设施id更新主控的部署状态
     *
     * @param deviceIds    设施id集合
     * @param deployStatus 部署状态
     */
    void updateDeployStatusByDeviceId(List<String> deviceIds, String deployStatus);

    /**
     * 根据设施id删除主控
     *
     * @param deviceIds 设施id集合
     */
    void deleteControlByDeviceIds(List<String> deviceIds);

    /**
     * 根据主控id删除电子锁主控
     *
     * @param controlReq
     */
    void deleteLockAndControlByControlId(ControlReq controlReq);

    /**
     * 根据分页条件查询出有主控信息及门锁信息的设施的id集合
     *
     * @param pageCondition 分页条件
     * @param deviceIds     设施id集合
     * @return 查询结果
     */
    List<String> deviceIdListByPage(PageCondition pageCondition, List<String> deviceIds);

    /**
     * 查询出有主控信息及门锁信息的设施的id集合
     *
     * @return 查询结果
     */
    List<String> deviceIdList();

    /**
     * 检查主控id是否重复
     *
     * @param controlId 主控id
     */
    void checkControlIdReused(String controlId);

    /**
     * 删除OneNet平台注册记录
     *
     * @param controlParamList 主控参数
     */
    void deleteFromOneNet(List<ControlParam> controlParamList);

    /**
     * 删除OceanConnect平台注册记录
     *
     * @param controlParamList 主控参数
     */
    void deleteFromOceanConnect(List<ControlParam> controlParamList);

    /**
     * 新增传感值
     *
     * @param controlParam 主控信息
     * @return 结果
     */
    void addSensor(ControlParam controlParam);

    /**
     * 根据主控id及告警类型清除告警显示状态
     *
     * @param removeAlarmList 告警清除集合
     */
    void removeAlarm(List<RemoveAlarm> removeAlarmList);
}
