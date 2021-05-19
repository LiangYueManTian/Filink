package com.fiberhome.filink.logserver.service;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.logserver.req.AddOperateLogReq;
import com.fiberhome.filink.logserver.req.AddSecurityLogReq;
import com.fiberhome.filink.logserver.req.AddSystemLogReq;

/**
 * @author hedongwei@wistronits.com
 * description 日志逻辑层
 * date 2019/1/14 14:36
 */
public interface LogService {

    /**
     * 新增操作日志
     *
     * @param operateLog 操作日志
     * @author hedongwei@wistronits.com
     * @date 16:35 2019/1/14
     * @return 新增操作日志结果
     */
    boolean insertOperateLog(AddOperateLogReq operateLog);

    /**
     * 新增安全日志
     *
     * @param securityLog 安全日志
     * @author hedongwei@wistronits.com
     * @date 16:35 2019/1/14
     */
    void insertSecurityLog(AddSecurityLogReq securityLog);

    /**
     * 新增系统日志
     *
     * @param systemLog 系统日志
     * @author hedongwei@wistronits.com
     * @date 16:35 2019/1/14
     */
    void insertSystemLog(AddSystemLogReq systemLog);


    /**
     * 查询操作日志列表
     *
     * @param queryCondition 查询条件
     * @return Result 操作日志列表
     * @author hedongwei@wistronits.com
     * @date 16:35 2019/1/14
     */
    Result queryListOperateLogByPage(QueryCondition queryCondition);


    /**
     * 查询安全日志列表
     *
     * @param queryCondition 查询条件
     * @return Result 安全日志列表
     * @author hedongwei@wistronits.com
     * @date 16:35 2019/1/14
     */
    Result queryListSecurityLogByPage(QueryCondition queryCondition);

    /**
     * 查询系统日志列表
     *
     * @param queryCondition 查询条件
     * @return Result 系统日志列表
     * @author hedongwei@wistronits.com
     * @date 16:35 2019/1/14
     */
    Result queryListSystemLogByPage(QueryCondition queryCondition);


    /**
     * 根据日志编号查询操作日志详情
     *
     * @param logId 日志编号
     * @return Result 操作日志列表
     * @author hedongwei@wistronits.com
     * @date 16:35 2019/1/14
     */
    Result getOperateLogByLogId(String logId);

    /**
     * 根据日志编号查询安全日志详情
     *
     * @param logId 日志编号
     * @return Result 操作日志列表
     * @author hedongwei@wistronits.com
     * @date 16:35 2019/1/14
     */
    Result getSecurityLogByLogId(String logId);

    /**
     * 根据日志编号查询系统日志详情
     *
     * @param logId 日志编号
     * @return Result 操作日志列表
     * @author hedongwei@wistronits.com
     * @date 16:35 2019/1/14
     */
    Result getSystemLogByLogId(String logId);

    /**
     * 导出操作日志
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    Result exportOperateLog(ExportDto exportDto);
    /**
     * 导出系统日志
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    Result exportSysLog(ExportDto exportDto);
    /**
     * 导出安全日志
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    Result exportSecurityLog(ExportDto exportDto);
}
