package com.fiberhome.filink.workflowbusinessserver.service.impl.procbase;

import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.component.log.AddLogForApp;
import com.fiberhome.filink.workflowbusinessserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hedongwei@wistronits.com
 * @date 2019/4/24 13:23
 */
@Service
public class ProcLogServiceImpl implements ProcLogService {


    @Autowired
    private LogProcess logProcess;

    @Autowired
    private AddLogForApp addLogForApp;

    /**
     * 新增工单操作日志参数
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 23:03
     * @param procBase 工单参数
     * @param functionCode 功能项
     * @param dataOptType 数据操作
     * @return 新增工单操作日志参数
     */
    @Override
    public AddLogBean getAddProcOperateLogParam(ProcBase procBase, String functionCode, String dataOptType) {
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("procId");
        addLogBean.setDataName("title");
        addLogBean.setOptObj(procBase.getTitle());
        addLogBean.setOptObjId(procBase.getProcId());
        addLogBean.setFunctionCode(functionCode);
        addLogBean.setDataOptType(dataOptType);
        return addLogBean;
        //新增操作日志
        //logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 获取app操作日志的参数
     * @author hedongwei@wistronits.com
     * @date  2019/4/16 8:51
     * @param procBase 工单主表
     * @param functionCode 功能项编码
     * @param dataOptType 数据操作类型
     * @return app操作日志的参数
     */
    @Override
    public AddLogBean getAddProcOperateLogParamForApp(ProcBase procBase, String functionCode, String dataOptType) {
        AddLogBean addLogBean = this.getAddProcOperateLogParam(procBase, functionCode, dataOptType);
        //设置日志类型为pda操作日志
        addLogBean.setOptType(LogConstants.OPT_TYPE_PDA);
        return addLogBean;
    }

    /**
     * app批量新增操作日志
     * @author hedongwei@wistronits.com
     * @date  2019/4/16 9:30
     * @param procBaseList 工单集合
     */
    @Override
    public void addOperateLogBatchForApp(List<ProcBaseRespForApp> procBaseList) {
        ProcBase procBase;
        AddLogBean addLogBean;
        AddLogBean addLogBeanOne;
        List<AddLogBean> addLogBeanList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(procBaseList)) {
            procBase = new ProcBase();
            BeanUtils.copyProperties(procBaseList.get(0), procBase);
            //新增工单下载日志信息
            String addOptType = LogConstants.DATA_OPT_TYPE_UPDATE;
            String functionCode = LogFunctionCodeConstant.DOWNLOAD_INSPECTION_FUNCTION_CODE;
            addLogBean = this.getAddProcOperateLogParam(procBase, functionCode, addOptType);
            //pda日志
            addLogBean.setOptType(LogConstants.OPT_TYPE_PDA);
        } else {
            return;
        }

        //组装新增日志的参数
        for (ProcBaseRespForApp procBaseRespForApp : procBaseList) {
            addLogBeanOne = new AddLogBean();
            BeanUtils.copyProperties(addLogBean, addLogBeanOne);
            addLogBeanOne.setLogId(NineteenUUIDUtils.uuid());
            if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBaseRespForApp.getProcType())) {
                addLogBeanOne.setFunctionCode(LogFunctionCodeConstant.DOWNLOAD_CLEAR_FAILURE_FUNCTION_CODE);
            } else if (ProcBaseConstants.PROC_INSPECTION.equals(procBaseRespForApp.getProcType())) {
                addLogBeanOne.setFunctionCode(LogFunctionCodeConstant.DOWNLOAD_INSPECTION_FUNCTION_CODE);
            }
            String title = procBaseRespForApp.getTitle();
            String procId = procBaseRespForApp.getProcId();
            addLogBeanOne.setOptObj(title);
            addLogBeanOne.setOptObjId(procId);
            addLogBeanList.add(addLogBeanOne);
        }

        //调用批量新增操作日志的异步方法
        addLogForApp.addOperateLogBatchForApp(addLogBeanList);

        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }


    /**
     * 删除工单操作日志
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 13:44
     * @param list 工单集合
     */
    @Async
    @Override
    public void insertDeleteProcLog(List list) {
        // 保存删除工单操作日志
        //此处通过feign方式调用log服务
        if (!ObjectUtils.isEmpty(list)) {
            logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }


    /**
     * 列表导出记录日志
     *
     * @param exportDto
     */
    @Override
    public void addLogByExport(ExportDto exportDto, String functionCode) {
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("export");
        addLogBean.setDataName("listName");
        //获得操作对象id
        addLogBean.setOptObjId("export");
        //操作为导出
        addLogBean.setDataOptType("export");
        addLogBean.setOptObj(exportDto.getListName());
        addLogBean.setFunctionCode(functionCode);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

}
