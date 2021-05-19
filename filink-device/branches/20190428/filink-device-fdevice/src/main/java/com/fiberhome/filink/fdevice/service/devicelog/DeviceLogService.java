package com.fiberhome.filink.fdevice.service.devicelog;

import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.fdevice.bean.devicelog.DeviceLog;
import com.fiberhome.filink.userapi.bean.User;

/**
 * 设施日志service
 *
 * @author CongcaiYu@wistronits.com
 */
public interface DeviceLogService {

    /**
     * 分页查询设施日志
     *
     * @param queryCondition 查询条件
     * @param user           查询用户
     * @param needsAuth      是否需要控制权限
     * @return
     */
    PageBean deviceLogListByPage(QueryCondition queryCondition, User user, boolean needsAuth);

    /**
     * 查询设施日志数量
     *
     * @param queryCondition 查询条件
     * @param user
     * @return 返回设施日志数量
     */
    Integer deviceLogCount(QueryCondition queryCondition, User user);

    /**
     * 分页查询设施日志 for pda
     *
     * @param queryCondition 查询条件
     * @return 分页数据
     */
    PageBean deviceLogListByPageForPda(QueryCondition queryCondition);

    /**
     * 保存设施日志
     *
     * @param deviceLog 设施日志对象
     * @return 操作结果
     * @throws Exception
     */
    Result saveDeviceLog(DeviceLog deviceLog) throws Exception;

    /**
     * 导出设施日志列表
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    Result exportDeviceLogList(ExportDto exportDto);

    /**
     * 查询最近一次操作日志时间
     *
     * @param deviceId
     * @return
     */
    Result queryRecentDeviceLogTime(String deviceId);
}
