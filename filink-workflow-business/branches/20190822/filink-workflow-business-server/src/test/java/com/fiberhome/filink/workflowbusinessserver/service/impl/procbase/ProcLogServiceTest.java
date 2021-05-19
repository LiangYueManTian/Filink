package com.fiberhome.filink.workflowbusinessserver.service.impl.procbase;

import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.component.log.AddLogForApp;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hedongwei@wistronits.com
 * description
 * @date 2019/7/25 13:20
 */
@RunWith(JMockit.class)
public class ProcLogServiceTest {

    /**
     * 工单日志逻辑层
     */
    @Tested
    private ProcLogServiceImpl procLogService;

    /**
     * 日志处理类
     */
    @Injectable
    private LogProcess logProcess;

    /**
     * 新增日志处理类
     */
    @Injectable
    private AddLogForApp addLogForApp;

    /**
     * 系统语言
     */
    @Injectable
    private SystemLanguageUtil systemLanguage;

    /**
     * 新增工单操作日志参数
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 13:29
     */
    @Test
    public void getAddProcOperateLogParam() {
        new Expectations() {
            {
                logProcess.generateAddLogToCallParam(anyString);
                AddLogBean addLogBean = new AddLogBean();
                result = addLogBean;
            }
        };
        ProcBase procBase = new ProcBase();
        procBase.setProcId("1");
        procBase.setTitle("1");


        String functionCode = "1";

        String dataOptType = LogConstants.DATA_OPT_TYPE_UPDATE;
        procLogService.getAddProcOperateLogParam(procBase, functionCode, dataOptType);
    }

    /**
     * 获取app操作日志的参数
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 13:41
     */
    @Test
    public void getAddProcOperateLogParamForApp() {
        ProcBase procBase = new ProcBase();
        procBase.setProcId("1");
        procBase.setTitle("1");


        String functionCode = "1";

        String dataOptType = LogConstants.DATA_OPT_TYPE_UPDATE;
        procLogService.getAddProcOperateLogParamForApp(procBase, functionCode, dataOptType);
    }


    /**
     * app批量新增操作日志
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 13:44
     */
    @Test
    public void addOperateLogBatchForApp() {
        List<ProcBaseRespForApp> procBaseList = new ArrayList<>();
        procLogService.addOperateLogBatchForApp(procBaseList);
        ProcBaseRespForApp procBaseRespForApp = new ProcBaseRespForApp();
        procBaseRespForApp.setProcId("1");
        procBaseRespForApp.setTitle("1");
        procBaseRespForApp.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        procBaseList.add(procBaseRespForApp);
        procBaseRespForApp = new ProcBaseRespForApp();
        procBaseRespForApp.setProcId("2");
        procBaseRespForApp.setTitle("2");
        procBaseRespForApp.setProcType(ProcBaseConstants.PROC_INSPECTION);
        procBaseList.add(procBaseRespForApp);
        procLogService.addOperateLogBatchForApp(procBaseList);
    }

    /**
     * 删除工单操作日志
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 13:52
     */
    @Test
    public void insertDeleteProcLog() {
        List<AddLogBean> addLogBeanList = new ArrayList<>();
        AddLogBean addLogBean = new AddLogBean();
        addLogBean.setOptObjId("1");
        addLogBeanList.add(addLogBean);
        procLogService.insertDeleteProcLog(addLogBeanList);
    }

    /**
     * 列表导出记录日志
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 13:57
     */
    @Test
    public void addLogByExport() {
        ExportDto exportDto = new ExportDto();
        String functionCode = "1";
        procLogService.addLogByExport(exportDto, functionCode);
    }

}
