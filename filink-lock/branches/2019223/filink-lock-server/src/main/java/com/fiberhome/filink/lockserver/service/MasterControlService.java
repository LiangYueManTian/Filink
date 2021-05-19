package com.fiberhome.filink.lockserver.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.lockserver.bean.Control;
import com.fiberhome.filink.lockserver.bean.SetConfigBean;

/**
 * 主控信息接口
 * @author CongcaiYu
 */
public interface MasterControlService {

    /**
     * 根据设施id查询主控信息
     * @param deviceId 设施id
     * @return 主控信息
     */
    Control getControlInfoByDeviceId(String deviceId);

    /**
     * 保存主控参数信息
     * @param control 主控信息
     * @return 操作结果
     */
    Result saveControlParams(Control control);

    /**
     * 根据设施id修改主控信息
     * @param control 主控信息
     * @return 操作结果
     */
    Result updateControlParamsByDeviceId(Control control);


    /**
     * 配置设施策略
     * @param setConfigBean 参数信息
     * @return 操作结果
     */
    Result setConfig(SetConfigBean setConfigBean);
}
