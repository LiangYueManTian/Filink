package com.fiberhome.filink.workflowbusinessserver.export.procclear;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.workflowbusinessserver.dao.procbase.ProcBaseDao;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
import com.fiberhome.filink.workflowbusinessserver.service.impl.procbase.ProcBaseServiceImpl;
import com.fiberhome.filink.workflowbusinessserver.service.impl.procclear.ProcClearFailureServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 销障工单未完工列表导出类
 *
 * @author chaofanrong@wistronits.com
 */
@Component
public class ClearFailureProcUnfinishedExport extends AbstractExport {

    @Autowired
    private ProcClearFailureServiceImpl procClearFailureService;

    @Autowired
    private ProcBaseServiceImpl procBaseService;

    @Autowired
    private ProcBaseDao procBaseDao;

    @Override
    protected List queryData(QueryCondition queryCondition) {
        //设施工单类型为销障工单
        procClearFailureService.setProcTypeToClearFailure(queryCondition);

        //获取拥有权限信息
        procBaseService.getPermissionsInfoForExport(queryCondition);

        List<ProcBaseResp> procBaseRespList = procBaseDao.queryListProcUnfinishedProcByPage(queryCondition);
        // 构造页面需要数据
        List<ProcBaseResp> resultList = procBaseService.convertProcInfoToProcResp(procBaseRespList);
        return resultList;
    }

    @Override
    protected Integer queryCount(QueryCondition queryCondition) {
        //设施工单类型为销障工单
        procClearFailureService.setProcTypeToClearFailure(queryCondition);

        //获取拥有权限信息
        procBaseService.getPermissionsInfo(queryCondition);

        Integer count = procBaseDao.queryCountListProcUnfinishedProc(queryCondition);
        return count;
    }

}
