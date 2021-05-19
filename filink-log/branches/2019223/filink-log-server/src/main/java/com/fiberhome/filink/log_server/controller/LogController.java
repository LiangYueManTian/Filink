package com.fiberhome.filink.log_server.controller;


import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.log_server.req.AddOperateLogReq;
import com.fiberhome.filink.log_server.req.AddSecurityLogReq;
import com.fiberhome.filink.log_server.req.AddSystemLogReq;
import com.fiberhome.filink.log_server.service.LogService;
import com.fiberhome.filink.log_server.utils.I18nConstants;
import com.fiberhome.filink.log_server.utils.LogResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @Author hedongwei@wistronits.com
 * description 日志管理控制层
 * date 10:45 2019/1/14
 */
@RestController("logController")
@RequestMapping("/log")
public class LogController {

    @Autowired
    private LogService logService;

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
     * @author hedongwei@wistronits.com
     * description 查询操作日志信息
     * date 16:47 2019/1/14
     * param [queryCondition]
     */
    @PostMapping("findOperateLog")
    public @ResponseBody
    Result findOperateLog(@RequestBody QueryCondition queryCondition) {
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
     * 检查日志编号
     * @author hedongwei@wistronits.com
     * @date  2019/3/25 10:08
     * @param logId 日志编号
     * @return 检查日志编号结果
     */
    private Result checkLogId(String logId) {
        if (null == logId || StringUtils.isEmpty(logId)) {
            //获取日志编号空的错误编码
            Integer logIdNull = LogResultCode.LOG_ID_NULL;
            //获取日志编号空的错误提示信息
            String logIdNull1Message = I18nConstants.LOG_ID_NULL;
            return ResultUtils.warn(logIdNull, I18nUtils.getString(logIdNull1Message));
        } else {
            return null;
        }
    }
}
