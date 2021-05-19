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
 * @author yuanyao@wistronits.com
 * create on 2019-01-23 20:00
 */
@Slf4j
public class DeleteExportJob extends QuartzJobBean {
    /**
     * kafka通道
     */
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
     * 任务码 当前只有一个
     */
    private static final Integer DELETE_OVERDUE_EXPORT_TASK = 001;

    /**
     * 执行方法
     *
     * @param context
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Message msg = MessageBuilder.withPayload(DELETE_OVERDUE_EXPORT_TASK).build();
        log.info("Wake-up export service to delete expired task data");
        scheduleStreams.output().send(msg);
        addLogByExport();
    }
    /**
     * 删除过期任务记录日志
     *
     */
    private void addLogByExport() {
        systemLanguageUtil.querySystemLanguage();
        String logType = LogConstants.LOG_TYPE_SYSTEM;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("export");
        addLogBean.setDataName("taskName");
        //获得操作对象id
        addLogBean.setOptObjId("export");
        //操作为新增
        addLogBean.setDataOptType("export");
        addLogBean.setOptObj(systemLanguageUtil.getI18nString(ScheduleI18nConstant.EXPIRED_EXPORT_TASK));
        addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_EXPORT_FUNCTION_CODE);
        //新增系统日志
        logProcess.addSystemLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }
}
