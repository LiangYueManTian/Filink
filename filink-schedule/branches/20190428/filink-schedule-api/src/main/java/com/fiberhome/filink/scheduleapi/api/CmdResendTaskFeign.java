package com.fiberhome.filink.scheduleapi.api;

import com.fiberhome.filink.scheduleapi.bean.CmdResendTaskBean;
import com.fiberhome.filink.scheduleapi.fallback.CmdResendTaskFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 指令重发定时任务远程调用
 * @author CongcaiYu
 */
@FeignClient(name = "filink-schedule-server", path = "/cmd",fallback = CmdResendTaskFallback.class)
public interface CmdResendTaskFeign {

    /**
     * 新增指令重发定时任务
     * @param resendTaskBean 任务实体
     */
    @PostMapping("/addResendTask")
    void addCmdResendTask(@RequestBody CmdResendTaskBean resendTaskBean);

    /**
     * 删除指令重发定时任务
     *
     * @param resendTaskBean 任务实体
     */
    @PostMapping("/deleteResendTask")
    void deleteCmdResendTask(@RequestBody CmdResendTaskBean resendTaskBean);
}
