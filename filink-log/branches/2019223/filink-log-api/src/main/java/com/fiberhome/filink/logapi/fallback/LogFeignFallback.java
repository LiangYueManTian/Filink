package com.fiberhome.filink.logapi.fallback;

import com.fiberhome.filink.clientcommon.utils.Result;
import com.fiberhome.filink.clientcommon.utils.ResultUtils;
import com.fiberhome.filink.logapi.api.LogFeign;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.req.AddOperateLogReq;
import com.fiberhome.filink.logapi.req.AddSecurityLogReq;
import com.fiberhome.filink.logapi.req.AddSystemLogReq;
import com.fiberhome.filink.logapi.utils.LogConstants;
import com.fiberhome.filink.logapi.utils.LogResultCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @author hedongwei@wistronits.com
 * ferign 熔断
 * 13:45 2019/1/19
 */
@Slf4j
@Component
public class LogFeignFallback implements LogFeign {

    @Value("${readLogFilePath}")
    private String logFilePath;

    @Autowired
    private LogProcess logProcess;

    private final Logger logger = LoggerFactory.getLogger(LogFeignFallback.class);

    /**
     * @author hedongwei@wistronits.com
     * 新增操作日志异常后执行的方法
     * 12:56 2019/1/21
     * param [operateLogReq]
     */
    @Override
    public Result addOperateLog(AddOperateLogReq operateLogReq) {
        //当logid为空的时候，并且需要添加文件的时候调用新增文件的接口
        try {
            operateLogReq.setTableName(LogConstants.OPERATE_LOG_TABLE_NAME);
            //获得日志编号
            String logId = operateLogReq.getLogId();
            //获得文件类型
            String fileType = LogConstants.LOG_LOCAL_FILE_TYPE;
            logProcess.addLocalLogFile(logId, operateLogReq.getAddLocalFile(), operateLogReq, fileType);
        } catch (Exception e) {
            logger.warn("新增操作日志异常后执行的方法", e);
        }
        String addOperateLogError = "新增操作日志失败";
        return ResultUtils.warn(LogResultCode.ADD_OPERATE_LOG_ERROR, addOperateLogError);
    }

    /**
     * @author hedongwei@wistronits.com
     * 新增安全日志异常后执行的方法
     * 14:11 2019/1/21
     * param [securityLogReq]
     */
    @Override
    public Result addSecurityLog(AddSecurityLogReq securityLogReq) {
        try {
            securityLogReq.setTableName(LogConstants.SECURITY_LOG_TABLE_NAME);
            //获得日志编号
            String logId = securityLogReq.getLogId();
            //获得文件类型
            String fileType = LogConstants.LOG_LOCAL_FILE_TYPE;
            //添加本地文件信息
            logProcess.addLocalLogFile(logId, securityLogReq.getAddLocalFile(), securityLogReq, fileType);
        } catch (Exception e) {
            logger.warn("新增安全日志异常后执行的方法", e);
        }
        String addSecurityLogError = "新增安全日志失败";
        return ResultUtils.warn(LogResultCode.ADD_SECURITY_LOG_ERROR, addSecurityLogError);
    }

    /**
     * @author hedongwei@wistronits.com
     * 新增系统日志异常后执行的方法
     * 14:11 2019/1/21
     * param [systemLogReq]
     */
    @Override
    public Result addSystemLog(AddSystemLogReq systemLogReq) {
        try {
            systemLogReq.setTableName(LogConstants.SYSTEM_LOG_TABLE_NAME);
            //日志编号
            String logId = systemLogReq.getLogId();
            //获得文件类型
            String fileType = LogConstants.LOG_LOCAL_FILE_TYPE;
            //添加本地文件信息
            logProcess.addLocalLogFile(logId , systemLogReq.getAddLocalFile(), systemLogReq, fileType);
        } catch (Exception e) {
            logger.warn("新增系统日志异常后执行的方法", e);
        }
        String addSystemLogInfo = "新增系统日志失败";
        return ResultUtils.warn(LogResultCode.ADD_SYSTEM_LOG_ERROR, addSystemLogInfo);
    }


}
