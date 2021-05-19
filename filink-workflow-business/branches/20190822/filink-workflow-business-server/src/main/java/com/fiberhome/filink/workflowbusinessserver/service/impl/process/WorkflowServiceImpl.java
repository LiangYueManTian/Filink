package com.fiberhome.filink.workflowbusinessserver.service.impl.process;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowapi.api.ProcessFeign;
import com.fiberhome.filink.workflowapi.req.CompleteTaskReq;
import com.fiberhome.filink.workflowapi.req.StartProcessReq;
import com.fiberhome.filink.workflowapi.constant.ProcessConstants;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkflowBusinessResultCode;
import com.fiberhome.filink.workflowbusinessserver.req.process.*;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcBaseService;
import com.fiberhome.filink.workflowbusinessserver.service.process.WorkflowService;
import com.fiberhome.filink.workflowbusinessserver.utils.common.GenerateProcessParam;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 流程服务实现类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-03-11
 */
@Service
public class WorkflowServiceImpl implements WorkflowService {


    @Autowired
    private ProcessFeign processFeign;

    @Autowired
    private ProcBaseService procBaseService;

    /**
     * 启动流程
     * @author hedongwei@wistronits.com
     * @date  2019/3/16 10:18
     * @param req 流程参数
     * @return 返回启动流程结果
     */
    @Override
    public Result startProcess(StartProcessInfoReq req) {
        //调用启动流程方法
        ProcessBaseInfoReq procReq = this.getProcessBaseInfoReq(req);
        //发起流程
        String operateStart = ProcessConstants.PROC_OPERATION_START;

        StartProcessReq processReq = this.getStartProcessReq(req, operateStart);

        //启动流程
        Result result = processFeign.startProcess(processReq);
        return result;
    }


    /**
     * 获取开启流程参数
     * @author hedongwei@wistronits.com
     * @date  2019/3/22 17:02
     * @param req
     * @return 返回开启流程的结果
     */
    private StartProcessReq getStartProcessReq(ProcessBaseInfoReq req, String operate) {
        //获得流程的参数信息
        Map<String, Object> varibleMap = GenerateProcessParam.generateProcessParam(req, operate);
        StartProcessReq processReq = new StartProcessReq();
        BeanUtils.copyProperties(req, processReq);
        processReq.setVariables(varibleMap);
        //流程编号
        processReq.setProcCode(req.getProcId());
        //流程类型
        processReq.setProcType(ProcBaseConstants.PROC_TEMPLATE_NAME);
        return processReq;
    }


    /**
     * 办结流程
     * @author hedongwei@wistronits.com
     * @date  2019/3/16 10:18
     * @param req 流程参数
     * @return 返回办结流程结果
     */
    @Override
    public Result completeProcess(CompleteProcessInfoReq req) {
        if (StringUtils.isEmpty(req.getOperation())) {
            return ResultUtils.warn(WorkflowBusinessResultCode.PROC_PARAM_ERROR, I18nUtils.getSystemString(ProcBaseI18n.PROC_PARAM_ERROR));
        }

        //操作
        String operation = req.getOperation();

        //办结流程
        return this.completeInfoProcess(req, operation);
    }

    /**
     * 退单流程
     * @author hedongwei@wistronits.com
     * @date  2019/3/16 10:18
     * @param req 退单参数
     * @return 返回退单结果
     */
    @Override
    public Result singleBackProcess(SingleBackInfoReq req) {
        //操作为退单
        String operation = ProcessConstants.PROC_OPERATION_SINGLE_BACK;
        //办结流程
        return this.completeInfoProcess(req, operation);
    }


    /**
     * 撤回流程
     * @author hedongwei@wistronits.com
     * @date  2019/3/16 10:18
     * @param req 撤回参数
     * @return 返回撤回结果
     */
    @Override
    public Result revokeProcess(RevokeInfoReq req) {
        //操作为撤销
        String operation = ProcessConstants.PROC_OPERATION_REVOKE;
        //办结流程
        return this.completeInfoProcess(req, operation);
    }


    /**
     * 转派流程
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 14:32
     * @param req 转派参数
     * @return 返回转派结果
     */
    @Override
    public Result turnProcess(TurnInfoReq req) {
        //操作为转派
        String operation = ProcessConstants.PROC_OPERATION_TURN;
        //办结流程
        return this.completeInfoProcess(req, operation);
    }

    /**
     * app批量下载工单
     * @author hedongwei@wistronits.com
     * @date  2019/4/14 20:18
     * @param procBaseRespForAppList
     */
    @Override
    @Async
    public void appDownloadProcBatch(List<ProcBaseRespForApp> procBaseRespForAppList) {
        ProcBase procBase;
        if (!ObjectUtils.isEmpty(procBaseRespForAppList)) {
            for (ProcBaseRespForApp procBaseRespForApp : procBaseRespForAppList) {
                procBase = new ProcBase();
                BeanUtils.copyProperties(procBaseRespForApp, procBase);
                //完结流程的参数
                CompleteProcessInfoReq completeReq = procBaseService.getCompleteProcessInfoReq(procBase);

                //流程操作办结
                completeReq.setOperation(ProcessConstants.PROC_OPERATION_COMPLETE);
                //下载用户
                completeReq.setAssign(procBase.getAssign());
                //下载工单
                Result result = this.completeProcess(completeReq);
            }
        }
    }

    /**
     * 办结流程
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 15:01
     * @param req 参数
     * @param operation 操作
     * @return 办结流程的结果
     */
    private Result completeInfoProcess(Object req, String operation) {
        ProcessBaseInfoReq procReq = this.getProcessBaseInfoReq(req);
        CompleteTaskReq completeReq = this.getCompleteTaskReq(req);

        //完成参数
        completeReq = this.setParamToCompleteTaskReq(completeReq, operation, procReq);

        //办理流程
        Result result = processFeign.completeTask(completeReq);
        return result;
    }

    /**
     * 获取流程信息参数
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 14:43
     * @param req 获取需要转换的参数
     * @return 获取流程
     */
    private ProcessBaseInfoReq getProcessBaseInfoReq(Object req) {
        ProcessBaseInfoReq procReq = new ProcessBaseInfoReq();
        BeanUtils.copyProperties(req, procReq);
        return procReq;
    }

    /**
     * 完成任务参数
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 14:50
     * @param req 参数
     * @return 完成任务参数
     */
    private CompleteTaskReq getCompleteTaskReq(Object req) {
        CompleteTaskReq completeReq = new CompleteTaskReq();
        BeanUtils.copyProperties(req, completeReq);
        return completeReq;
    }

    /**
     * 添加参数到完成任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/22 17:21
     * @param completeReq 完成参数
     * @param operation 操作
     * @param procReq 流程参数
     * @return 完成任务请求参数
     */
    private CompleteTaskReq setParamToCompleteTaskReq(CompleteTaskReq completeReq, String operation, ProcessBaseInfoReq procReq) {
        //获得流程的参数信息
        Map<String, Object> varibleMap = GenerateProcessParam.generateProcessParam(procReq, operation);

        //流程编号
        completeReq.setProcCode(procReq.getProcId());
        //参数
        completeReq.setVariables(varibleMap);

        return completeReq;
    }
}
