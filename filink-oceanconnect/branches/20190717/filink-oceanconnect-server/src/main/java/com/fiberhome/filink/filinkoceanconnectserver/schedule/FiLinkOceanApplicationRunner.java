package com.fiberhome.filink.filinkoceanconnectserver.schedule;

import com.fiberhome.filink.commonstation.constant.Constant;
import com.fiberhome.filink.commonstation.entity.config.RetryConfig;
import com.fiberhome.filink.filinkoceanconnectserver.client.HttpsClient;
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
public class FiLinkOceanApplicationRunner implements ApplicationRunner {

    @Autowired
    private HttpsClient httpsClient;

    @Autowired
    private CmdResendTaskFeign cmdResendTaskFeign;

    @Resource(name = "oceanRetryConfig")
    private RetryConfig retryConfig;


    /**
     * 系统初始化方法
     * @param applicationArguments 参数信息
     * @throws Exception 异常信息
     */
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        try {
            httpsClient.initSSLConfigForTwoWay();
            //订阅平台消息
            //deviceClient.subscribe();
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
        cmdResendTaskBean.setJobName(Constant.OCEAN_CONNECT_RESEND_JOB_NAME);
        cmdResendTaskBean.setGroupName(Constant.OCEAN_CONNECT_RESEND_GROUP_NAME);
        cmdResendTaskBean.setType(Constant.OCEAN_CONNECT_RESEND_TYPE);
        //新增指令重发定时任务
        cmdResendTaskFeign.addCmdResendTask(cmdResendTaskBean);
    }

    /**
     * 删除指令重发定时任务
     */
    private void deleteCmdResendTask() {
        //构造定时任务对象
        CmdResendTaskBean cmdResendTaskBean = new CmdResendTaskBean();
        cmdResendTaskBean.setJobName(Constant.OCEAN_CONNECT_RESEND_JOB_NAME);
        //删除定时任务
        cmdResendTaskFeign.deleteCmdResendTask(cmdResendTaskBean);
    }
}
