package com.fiberhome.filink.logapi.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fiberhome.filink.clientcommon.utils.Result;
import com.fiberhome.filink.clientcommon.utils.ResultCode;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogAspectBean;
import com.fiberhome.filink.logapi.log.LogCastProcess;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.constant.LogConstants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;


/**
 * @author hedongwei@wistronits.com
 * description 日志切面
 * date 2019/1/21 19:35
 */
@Aspect
@Component
@Slf4j
public class AddLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(AddLogAspect.class);

    @Value("${readLogFilePath}")
    private String logFilePath;

    @Autowired
    private LogCastProcess logCastProcess;

    @Autowired
    private LogProcess logProcess;

    @Autowired
    WebApplicationContext webApplicationConnect;

    @Pointcut("@annotation(com.fiberhome.filink.logapi.annotation.AddLogAnnotation)")
    public void webLogs() {
    }

    /**
     * @author hedongwei@wistronits.com
     * description 在weblog方法执行之前
     * date 9:22 2019/1/22
     * param [joinPoint]
     */
    @Before("webLogs()")
    public void debefore(JoinPoint joinPoint) throws Throwable {
        System.out.println("进入到before方法");
    }

    /**
     * @author hedongwei@wistronits.com
     * description 在weblog方法执行之后
     * date 9:22 2019/1/22
     * param [joinPoint]
     */
    @AfterReturning(returning = "rvt", pointcut = "@annotation(com.fiberhome.filink.logapi.annotation.AddLogAnnotation)")
    public void afterReturning(JoinPoint jp, Object rvt) {
        //转换方法的返回值为json
        String rvtString = JSON.toJSONString(rvt);
        JSONObject rvtInfo = JSON.parseObject(rvtString);
        if (null != rvtInfo) {
            if (null != rvtInfo.get(LogConstants.RESULT_CODE_ATTR_NAME)) {
                //当方法的执行结果不为0的时候不需要执行方法
                if (!ResultCode.SUCCESS.toString().equals(rvtInfo.get(LogConstants.RESULT_CODE_ATTR_NAME).toString())) {
                    return;
                }
            } else {
                return;
            }
        } else {
            return;
        }

        //获取新增日志的参数信息
        AddLogAnnotation addLogAnnotation = ((MethodSignature) jp.getSignature()).getMethod().getAnnotation(AddLogAnnotation.class);
        //调用对应的方法，获取应用的参数
        Object[] args = jp.getArgs();
        if (null != args && 0 < args.length) {
            //生成新增日志的内容
            AddLogAspectBean addLogAspectBean = LogProcess.generateAddLogAspectBean();
            //数据操作类型
            addLogAspectBean.setDataOptType(addLogAnnotation.value());
            //操作类型

            //日志类型
            String tableName = "";
            tableName = logProcess.generateTableNameForLogType(addLogAnnotation.logType());

            addLogAspectBean.setTableName(tableName);
            //功能编码用来查询日志的类型
            addLogAspectBean.setFunctionCode(addLogAnnotation.functionCode());

            //第一个参数
            Object argsOne = args[0];
            //转换参数为json
            String addLogParamString = JSON.toJSONString(argsOne);
            JSONObject addLogParamInfo = JSON.parseObject(addLogParamString);
            //数据的名称
            String dataName = addLogAnnotation.dataGetColumnName();
            //数据的编号
            String dataId = addLogAnnotation.dataGetColumnId();
            try {
                addLogAspectBean = logCastProcess.castBizToLog(addLogAspectBean, dataId, dataName, addLogParamInfo);
                if (null == addLogAspectBean) {
                    return;
                }
            } catch (Exception e) {
                logger.error("日志文件转换失败", e);
                return;
            }
            //新增日志信息
            String addLogInfo = JSON.toJSONString(addLogAspectBean, SerializerFeature.WRITE_MAP_NULL_FEATURES);
            JSONObject addLogObject = JSONObject.parseObject(addLogInfo);
            //调用新增日志的方法
            Result result = logProcess.addLogInfoForLogType(addLogObject, tableName);
            return;
        }
    }

    /**
     * @author hedongwei@wistronits.com
     * description 在weblog方法环绕
     * date 9:22 2019/1/22
     * param [joinPoint]
     */
    @Around("webLogs()")
    public Object arround(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("方法环绕start......");
        ProceedingJoinPoint tempJoinPoint = (ProceedingJoinPoint) pjp;
        Object object = tempJoinPoint.proceed();
        return object;
    }

}
