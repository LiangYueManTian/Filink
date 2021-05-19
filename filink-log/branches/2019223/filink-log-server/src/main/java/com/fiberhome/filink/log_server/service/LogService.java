package com.fiberhome.filink.log_server.service;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.log_server.req.AddOperateLogReq;
import com.fiberhome.filink.log_server.req.AddSecurityLogReq;
import com.fiberhome.filink.log_server.req.AddSystemLogReq;

/**
 * @author hedongwei@wistronits.com
 * description 日志逻辑层
 * date 2019/1/14 14:36
 */
public interface LogService {

   /**
    * 新增操作日志
    * @author hedongwei@wistronits.com
    * @date 16:35 2019/1/14
    * @param operateLog 操作日志
    */
    void insertOperateLog(AddOperateLogReq operateLog);
   /**
    * 新增安全日志
    * @author hedongwei@wistronits.com
    * @date 16:35 2019/1/14
    * @param securityLog 安全日志
    */
    void insertSecurityLog(AddSecurityLogReq securityLog);

   /**
    * 新增系统日志
    * @author hedongwei@wistronits.com
    * @date 16:35 2019/1/14
    * @param systemLog 系统日志
    */
    void insertSystemLog(AddSystemLogReq systemLog);


   /**
    * 查询操作日志列表
    * @author hedongwei@wistronits.com
    * @date 16:35 2019/1/14
    * @param queryCondition 查询条件
    * @return Result 操作日志列表
    */
    Result queryListOperateLogByPage(QueryCondition queryCondition);


   /**
    * 查询安全日志列表
    * @author hedongwei@wistronits.com
    * @date 16:35 2019/1/14
    * @param queryCondition 查询条件
    * @return Result 安全日志列表
    */
    Result queryListSecurityLogByPage(QueryCondition queryCondition);

   /**
    * 查询系统日志列表
    * @author hedongwei@wistronits.com
    * @date 16:35 2019/1/14
    * @param queryCondition 查询条件
    * @return Result 系统日志列表
    */
    Result queryListSystemLogByPage(QueryCondition queryCondition);


   /**
    * 根据日志编号查询操作日志详情
    * @author hedongwei@wistronits.com
    * @date 16:35 2019/1/14
    * @param logId 日志编号
    * @return Result 操作日志列表
    */
    Result getOperateLogByLogId(String logId);

   /**
    * 根据日志编号查询安全日志详情
    * @author hedongwei@wistronits.com
    * @date 16:35 2019/1/14
    * @param logId 日志编号
    * @return Result 操作日志列表
    */
    Result getSecurityLogByLogId(String logId);

   /**
    * 根据日志编号查询系统日志详情
    * @author hedongwei@wistronits.com
    * @date 16:35 2019/1/14
    * @param logId 日志编号
    * @return Result 操作日志列表
    */
    Result getSystemLogByLogId(String logId);
}
