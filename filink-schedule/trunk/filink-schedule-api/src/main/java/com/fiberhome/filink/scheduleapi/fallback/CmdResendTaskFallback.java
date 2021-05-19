package com.fiberhome.filink.scheduleapi.fallback;

import com.fiberhome.filink.scheduleapi.api.CmdResendTaskFeign;
import com.fiberhome.filink.scheduleapi.bean.CmdResendTaskBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 指令重发定时任务远程调用熔断
 * @author CongcaiYu
 */
@Slf4j
@Component
public class CmdResendTaskFallback implements CmdResendTaskFeign {

    /**
     * 新增重发指令任务熔断处理
     * @param resendTaskBean 任务对象
     */
    @Override
    public void addCmdResendTask(CmdResendTaskBean resendTaskBean) {
        log.error("add cmd resend failed>>>>>>>>>>>>>>");
    }

    /**
     * 删除指令重发任务熔断处理
     * @param resendTaskBean 任务实体
     */
    @Override
    public void deleteCmdResendTask(CmdResendTaskBean resendTaskBean) {
        log.error("delete cmd resend failed>>>>>>>>>>>>>");
    }
}
