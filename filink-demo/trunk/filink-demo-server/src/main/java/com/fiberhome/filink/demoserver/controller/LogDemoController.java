package com.fiberhome.filink.demoserver.controller;

import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目示例---记录日志
 *
 * 示例内容：
 *      注解方式记录日志
 *      调用接口方式记录日志
 *
 * @author 姚远
 */
@Slf4j
@RestController
@RequestMapping("/log")
public class LogDemoController {


    @Autowired
    private LogProcess logProcess;

    /**
     * 注解方式记录日志
     *      value： 记录日志类型
     *      logType： 1 是记录失败写成文件 ， 2是记录日志失败时不写成文件
     *      functionCode： 对应 resources/xml/FunctionConfig.xml 中的functionCode,可以做国际化
     *
     */
    @AddLogAnnotation(
            value ="add",
            logType = "1",
            functionCode = "1301101")
    @GetMapping("/anno")
    public void annoLog() {
        log.info("this is a log");
    }


    /**
     * 使用调用接口的方式记录日志
     * 需要构造AddLogBean实例
     * 调用logProcess的addOperateLogBatchInfoToCall方法记录日志
     */
    @GetMapping("/rpc")
    public void rpcLog() {
        List list = new ArrayList();
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("demoid");
        addLogBean.setDataName("demoName");
        addLogBean.setOptObj("demo");
        addLogBean.setOptObjId("demoid");
        addLogBean.setDataOptType("demotype");
        addLogBean.setFunctionCode("1301101");
        addLogBean.setOptType(LogConstants.OPT_TYPE_PDA);
        list.add(addLogBean);
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
        log.info("this is a log");
    }
}
