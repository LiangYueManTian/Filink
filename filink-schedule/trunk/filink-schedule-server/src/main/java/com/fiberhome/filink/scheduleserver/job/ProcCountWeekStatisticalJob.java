package com.fiberhome.filink.scheduleserver.job;

import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.scheduleserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.scheduleserver.constant.ScheduleI18nConstant;
import com.fiberhome.filink.scheduleserver.stream.ScheduleStreams;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 任务逻辑实现类demo
 * 需要实现Job接口
 *
 * @author hedongwei@wistronits.com
 * create on 2019-01-23 20:00
 */
@Slf4j
public class ProcCountWeekStatisticalJob extends QuartzJobBean {

    @Autowired
    private ScheduleStreams scheduleStreams;

    /**
     * 自动注入日志服务
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * 系统语言环境
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 执行方法
     *
     * @param context
     * @see #execute
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String info = "工单周增量统计";
        log.info("工单周增量统计!");
        Message msg = MessageBuilder.withPayload(info).build();
        //新增工单数量统计调用生成工单记录的方法 (使用消息推送)
        scheduleStreams.weekProcCountOutput().send(msg);
        addLogByProcAdd();
    }


    /**
     * 记录工单增量日志
     *
     */
    private void addLogByProcAdd() {
        systemLanguageUtil.querySystemLanguage();
        String logType = LogConstants.LOG_TYPE_SYSTEM;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("procAddStatistical");
        addLogBean.setDataName("statisticalName");
        //获得操作对象id
        addLogBean.setOptObjId("procAddStatistical");
        //操作为新增
        addLogBean.setDataOptType("statistical");
        addLogBean.setOptObj(systemLanguageUtil.getI18nString(ScheduleI18nConstant.PROC_ADD_MONTH_STATISTICAL));
        addLogBean.setFunctionCode(LogFunctionCodeConstant.PROC_WEEK_ADD_FUNCTION_CODE);
        //新增系统日志
        logProcess.addSystemLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }
}
