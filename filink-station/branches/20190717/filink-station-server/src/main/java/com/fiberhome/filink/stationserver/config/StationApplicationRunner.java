package com.fiberhome.filink.stationserver.config;

import com.fiberhome.filink.commonstation.constant.Constant;
import com.fiberhome.filink.commonstation.entity.config.RetryConfig;
import com.fiberhome.filink.scheduleapi.api.CmdResendTaskFeign;
import com.fiberhome.filink.scheduleapi.bean.CmdResendTaskBean;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * station启动运行类
 * @author CongcaiYu
 */
@Log4j
@Component
public class StationApplicationRunner implements ApplicationRunner {

    @Autowired
    private CmdResendTaskFeign cmdResendTaskFeign;

    @Resource(name = "stationRetryConfig")
    private RetryConfig retryConfig;


    /**
     * 服务启动加载类
     * @param applicationArguments 参数对象
     * @throws Exception 异常
     */
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        try {
            //先删除定时任务
            deleteCmdResendTask();
            //新增定时任务
            addCmdResendTask();
        }catch (Exception e){
            log.error("station init cmd resend task failed>>>>>>>>>>>>>>>>>>>");
        }
    }

    /**
     * 新增指令重发定时任务
     */
    private void addCmdResendTask() {
        //获取重试时间
        Integer retryCycle = retryConfig.getRetryCycle();
        //构造定时任务对象
        CmdResendTaskBean cmdResendTaskBean = new CmdResendTaskBean();
        cmdResendTaskBean.setRetryCycle(retryCycle);
        cmdResendTaskBean.setJobName(Constant.UDP_RESEND_JOB_NAME);
        cmdResendTaskBean.setGroupName(Constant.UDP_RESEND_GROUP_NAME);
        cmdResendTaskBean.setType(Constant.UDP_RESEND_TYPE);
        //新增指令重发定时任务
        cmdResendTaskFeign.addCmdResendTask(cmdResendTaskBean);
    }

    /**
     * 删除指令重发定时任务
     */
    private void deleteCmdResendTask() {
        //构造定时任务对象
        CmdResendTaskBean cmdResendTaskBean = new CmdResendTaskBean();
        cmdResendTaskBean.setJobName(Constant.UDP_RESEND_JOB_NAME);
        //删除定时任务
        cmdResendTaskFeign.deleteCmdResendTask(cmdResendTaskBean);
    }
}
