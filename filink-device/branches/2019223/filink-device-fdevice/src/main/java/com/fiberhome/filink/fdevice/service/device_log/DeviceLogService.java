package com.fiberhome.filink.fdevice.service.device_log;

import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.fdevice.bean.device_log.DeviceLog;

/**
 * 设施日志service
 * @author CongcaiYu@wistronits.com
 */
public interface DeviceLogService {

    /**
     * 分页查询设施日志
     *
     * @param queryCondition 查询条件
     * @return 分页数据
     */
    PageBean deviceLogListByPage(QueryCondition queryCondition);

    /**
     * 保存设施日志
     * @param deviceLog 设施日志对象
     * @return 操作结果
     * @throws Exception
     */
    Result saveDeviceLog(DeviceLog deviceLog) throws Exception;
}
