package com.fiberhome.filink.onenetserver.schedule;

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
 * 平台启动运行方法
 * @author CongcaiYu
 */
@Log4j
@Component
public class OceanApplicationRunner implements ApplicationRunner {

    @Autowired
    private CmdResendTaskFeign cmdResendTaskFeign;

    @Resource(name = "oneNetRetryConfig")
    private RetryConfig retryConfig;

    /**
     * 系统初始化方法
     * @param applicationArguments 参数信息
     */
    @Override
    public void run(ApplicationArguments applicationArguments) {
        try {
            //先删除定时任务
            deleteCmdResendTask();
            //新增定时任务
            addCmdResendTask();
        }catch (Exception e){
            log.error("oceanConnect init failed>>>>>>>>>>>>>>>>>>>");
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
        cmdResendTaskBean.setJobName(Constant.ONE_NET_RESEND_JOB_NAME);
        cmdResendTaskBean.setGroupName(Constant.ONE_NET_RESEND_GROUP_NAME);
        cmdResendTaskBean.setType(Constant.ONE_NET_RESEND_TYPE);
        //新增指令重发定时任务
        cmdResendTaskFeign.addCmdResendTask(cmdResendTaskBean);
    }

    /**
     * 删除指令重发定时任务
     */
    private void deleteCmdResendTask() {
        //构造定时任务对象
        CmdResendTaskBean cmdResendTaskBean = new CmdResendTaskBean();
        cmdResendTaskBean.setJobName(Constant.ONE_NET_RESEND_JOB_NAME);
        //删除定时任务
        cmdResendTaskFeign.deleteCmdResendTask(cmdResendTaskBean);
    }
}
