package com.fiberhome.filink.logserver.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.logserver.bean.LogTypeExportBean;
import com.fiberhome.filink.logserver.bean.OperateTypeExportBean;
import com.fiberhome.filink.logserver.bean.SecurityLevelExportBean;
import com.fiberhome.filink.logserver.constant.I18nConstants;
import com.fiberhome.filink.logserver.req.AddOperateLogReq;
import com.fiberhome.filink.logserver.req.AddSecurityLogReq;
import com.fiberhome.filink.logserver.req.AddSystemLogReq;
import com.fiberhome.filink.logserver.service.LogService;
import com.fiberhome.filink.logserver.utils.LogResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author hedongwei@wistronits.com
 * description 日志管理控制层
 * date 10:45 2019/1/14
 */
@RestController("logController")
@RequestMapping("/log")
public class LogController {

    @Autowired
    private LogService logService;

    @Autowired
    private UserFeign userFeign;

    /**
     * @author hedongwei@wistronits.com
     * description 新增操作日志信息
     * date 15:03 2019/1/14
     * param [operateLog]
     */
    @PostMapping("/addOperateLog")
    public Result addOperateLog(@RequestBody AddOperateLogReq operateLogReq) {
        logService.insertOperateLog(operateLogReq);
        JSONObject data = new JSONObject();
        return ResultUtils.success(data);
    }

    /**
     * @author hedongwei@wistronits.com
     * description 新增安全日志信息
     * date 15:03 2019/1/14
     * param [securitylog]
     */
    @PostMapping("/addSecurityLog")
    public Result addSecurityLog(@RequestBody AddSecurityLogReq securityLogReq) {
        logService.insertSecurityLog(securityLogReq);
        JSONObject data = new JSONObject();
        return ResultUtils.success(data);
    }

    /**
     * @author hedongwei@wistronits.com
     * description 新增系统日志信息
     * date 15:03 2019/1/14
     * param [systemLog]
     */
    @PostMapping("/addSystemLog")
    public Result addSystemLog(@RequestBody AddSystemLogReq systemLogReq) {
        logService.insertSystemLog(systemLogReq);
        JSONObject data = new JSONObject();
        return ResultUtils.success(data);
    }

    /**
     * app新增操作日志
     *
     * @param operateLogReq 操作日志请求参数
     * @return app新增操作日志结果
     * @author hedongwei@wistronits.com
     * @date 2019/4/8 9:02
     */
    @PostMapping("/addOperateLogForApp")
    public Result addOperateLogForApp(@RequestBody AddOperateLogReq operateLogReq) {
        //校验参数是否正确
        Result result = AddOperateLogReq.checkAppOperateLogReq(operateLogReq);
        if (null != result) {
            //校验结果
            return result;
        }

        //根据用户编号查询用户的详细信息
        Result userResult = userFeign.queryUserInfoById(operateLogReq.getOptUserCode());
        User loginUser = null;
        if (!ObjectUtils.isEmpty(userResult)) {
            if (!ObjectUtils.isEmpty(userResult.getData())) {
                loginUser = JSONArray.toJavaObject((JSON) JSONArray.toJSON(userResult.getData()), User.class);
            }
        }

        //获得新增操作日志的集合
        operateLogReq = AddOperateLogReq.generateAppOperateLogReq(operateLogReq, loginUser);
        boolean operateInfo = logService.insertOperateLog(operateLogReq);
        if (!operateInfo) {
            return ResultUtils.warn(LogResultCode.ADD_OPERATE_FAIL, I18nUtils.getSystemString(I18nConstants.ADD_OPERATE_FAIL));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(I18nConstants.ADD_OPERATE_SUCCESS));
    }


    /**
     * @author hedongwei@wistronits.com
     * description 查询操作日志信息
     * date 16:47 2019/1/14
     * param [queryCondition]
     */
    @PostMapping("findOperateLog")
    public Result findOperateLog(@RequestBody QueryCondition queryCondition) {
        //查询操作日志信息
        Result result = logService.queryListOperateLogByPage(queryCondition);
        return result;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 查询安全日志信息
     * date 16:47 2019/1/14
     * param [queryCondition]
     */
    @PostMapping("findSecurityLog")
    public Result findSecurityLog(@RequestBody QueryCondition queryCondition) {
        //查询安全日志信息
        return logService.queryListSecurityLogByPage(queryCondition);
    }

    /**
     * @author hedongwei@wistronits.com
     * description 查询系统日志信息
     * date 16:47 2019/1/14
     * param [queryCondition]
     */
    @PostMapping("findSystemLog")
    public Result findSystemLog(@RequestBody QueryCondition queryCondition) {
        //查询系统日志信息
        return logService.queryListSystemLogByPage(queryCondition);
    }

    /**
     * @author wanzhaozhang@wistronits.com
     * description 按日志类型统计日志
     * date 9:57 2019/6/14
     * param [queryCondition]
     */
    @PostMapping("/countByLogType")
    public Result countByLogType(@RequestBody QueryCondition queryCondition) {
        return logService.countByLogType(queryCondition);
    }

    /**
     * @author wanzhaozhang@wistronits.com
     * description 按操作类型统计日志
     * date 9:57 2019/6/14
     * param [queryCondition]
     */
    @PostMapping("/countByOperateType")
    public Result countByOperateType(@RequestBody QueryCondition queryCondition) {
        return logService.countByOperateType(queryCondition);
    }

    /**
     * @author wanzhaozhang@wistronits.com
     * description 按安全级别统计日志
     * date 9:57 2019/6/14
     * param [queryCondition]
     */
    @PostMapping("/countBySecurityType")
    public Result countBySecurityType(@RequestBody QueryCondition queryCondition) {
        return logService.countBySecurityType(queryCondition);
    }

    /**
     * @author hedongwei@wistronits.com
     * description 查询操作日志详情
     * date 12:55 2019/1/23
     * param [logId]
     */
    @GetMapping("getOperateLogByLogId/{logId}")
    public Result getOperateLogByLogId(@PathVariable("logId") String logId) {
        //检查日志编号
        Result result = this.checkLogId(logId);
        if (null != result) {
            return result;
        }
        //查询操作日志详情
        return logService.getOperateLogByLogId(logId);
    }

    /**
     * @author hedongwei@wistronits.com
     * description 查询安全日志详情
     * date 12:55 2019/1/23
     * param [logId]
     */
    @GetMapping("getSecurityLogByLogId/{logId}")
    public Result getSecurityLogByLogId(@PathVariable("logId") String logId) {
        //检查日志编号
        Result result = this.checkLogId(logId);
        if (null != result) {
            return result;
        }
        //查询安全日志详情
        return logService.getSecurityLogByLogId(logId);
    }

    /**
     * @author hedongwei@wistronits.com
     * description 查询系统日志详情
     * date 12:55 2019/1/23
     * param [logId]
     */
    @GetMapping("getSystemLogByLogId/{logId}")
    public Result getSystemLogByLogId(@PathVariable("logId") String logId) {
        //检查日志编号
        Result result = this.checkLogId(logId);
        if (null != result) {
            return result;
        }
        //查询系统日志详情
        return logService.getSystemLogByLogId(logId);
    }

    /**
     * 导出操作日志
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @PostMapping("exportOperateLog")
    public Result exportOperateLog(@RequestBody ExportDto exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }
        return logService.exportOperateLog(exportDto);
    }

    /**
     * 导出系统日志
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @PostMapping("exportSysLog")
    public Result exportSysLog(@RequestBody ExportDto exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }
        return logService.exportSysLog(exportDto);
    }

    /**
     * 导出安全日志
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @PostMapping("exportSecurityLog")
    public Result exportSecurityLog(@RequestBody ExportDto exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }
        return logService.exportSecurityLog(exportDto);
    }

    /**
     * 日志类型统计导出
     *
     * @param exportDto 导出条件
     * @return Result
     */
    @PostMapping("exportLogType")
    public Result exportLogType(@RequestBody ExportDto<LogTypeExportBean> exportDto) {
        return logService.exportLogType(exportDto);
    }

    /**
     * 操作类型统计导出
     *
     * @param exportDto 导出条件
     * @return Result
     */
    @PostMapping("exportOperateType")
    public Result exportOperateType(@RequestBody ExportDto<OperateTypeExportBean> exportDto) {
     return logService.exportOperateType(exportDto);
    }

    /**
     * 安全级别统计导出
     *
     * @param exportDto 导出条件
     * @return Result
     */
    @PostMapping("exportSecurityLevel")
    public Result exportSecurityLevel(@RequestBody ExportDto<SecurityLevelExportBean> exportDto) {
      return logService.exportSecurityLevel(exportDto);
    }


    /**
     * 检查日志编号
     *
     * @param logId 日志编号
     * @return 检查日志编号结果
     * @author hedongwei@wistronits.com
     * @date 2019/3/25 10:08
     */
    private Result checkLogId(String logId) {
        if (null == logId || StringUtils.isEmpty(logId)) {
            //获取日志编号空的错误编码
            Integer logIdNull = LogResultCode.LOG_ID_NULL;
            //获取日志编号空的错误提示信息
            String logIdNull1Message = I18nConstants.LOG_ID_NULL;
            return ResultUtils.warn(logIdNull, I18nUtils.getSystemString(logIdNull1Message));
        } else {
            return null;
        }
    }

    /**
     * 自动新增操作日志
     * @author hedongwei@wistronits.com
     * @date  2019/7/17 13:32
     */
    @PostMapping("/autoInsertOperateLog")
    public void autoInsertOperateLog() {
        logService.autoInsertOperateLog();
    }

    /**
     * 自动新增系统日志
     * @author hedongwei@wistronits.com
     * @date  2019/7/17 13:32
     */
    @PostMapping("/autoInsertSystemLog")
    public void autoInsertSystemLog() {
        logService.autoInsertSystemLog();
    }

    /**
     * 自动新增安全日志
     * @author hedongwei@wistronits.com
     * @date  2019/7/17 13:32
     */
    @PostMapping("/autoInsertSecurityLog")
    public void autoInsertSecurityLog() {
        logService.autoInsertSecurityLog();
    }
}
