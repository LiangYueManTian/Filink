package com.fiberhome.filink.scheduleserver.controller;

import com.fiberhome.filink.scheduleserver.bean.CmdResendTaskBean;
import com.fiberhome.filink.scheduleserver.bean.TaskInfoForAny;
import com.fiberhome.filink.scheduleserver.enums.JobGroupEnum;
import com.fiberhome.filink.scheduleserver.job.CmdResendJob;
import com.fiberhome.filink.scheduleserver.job.OceanConnectCmdResendJob;
import com.fiberhome.filink.scheduleserver.job.OneNetCmdResendJob;
import com.fiberhome.filink.scheduleserver.service.JobService;
import com.fiberhome.filink.scheduleserver.utils.PlatFormType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 重发指令控制层
 *
 * @author CongcaiYu
 */
@RestController
@RequestMapping("/cmd")
@Slf4j
public class CmdResendController {

    @Autowired
    private JobService jobService;

    /**
     * 新增udp指令重发定时任务
     *
     * @param resendTaskBean 任务实体
     */
    @PostMapping("/addResendTask")
    public void addCmdResendTask(@RequestBody CmdResendTaskBean resendTaskBean) {
        if (resendTaskBean == null) {
            log.error("param is null>>>>>>>>>>>>");
            return;
        }
        //构造定时任务对象
        TaskInfoForAny taskInfoForAny = new TaskInfoForAny();
        //设置job类
        String type = resendTaskBean.getType();
        if (PlatFormType.UDP.equals(type)) {
            //udp
            taskInfoForAny.setTClass(CmdResendJob.class);
        } else if (PlatFormType.OCEAN_CONNECT.equals(type)) {
            //oceanConnect
            taskInfoForAny.setTClass(OceanConnectCmdResendJob.class);
        }else if (PlatFormType.ONE_NET.equals(type)){
            //oneNet
            taskInfoForAny.setTClass(OneNetCmdResendJob.class);
        }else {
            log.error("platform type is error");
            return;
        }
        taskInfoForAny.setIntervalSecond(resendTaskBean.getRetryCycle());
        taskInfoForAny.setJobName(resendTaskBean.getJobName());
        taskInfoForAny.setJobGroup(JobGroupEnum.CMD_RESEND.getGroupName());
        //新增定时任务
        try {
            jobService.addJob(taskInfoForAny);
        } catch (Exception e) {
            log.error("add udp cmd resend task failed>>>>>>>>>>>");
        }
    }


    /**
     * 删除指令重发定时任务
     *
     * @param resendTaskBean 任务实体
     */
    @PostMapping("/deleteResendTask")
    public void deleteCmdResendTask(@RequestBody CmdResendTaskBean resendTaskBean) {
        if (resendTaskBean == null
                || StringUtils.isEmpty(resendTaskBean.getJobName())) {
            log.error("delete resend task param is error>>>>>>>>>>>>>>");
            return;
        }
        jobService.deleteJob(resendTaskBean.getJobName(), JobGroupEnum.CMD_RESEND);
    }
}
