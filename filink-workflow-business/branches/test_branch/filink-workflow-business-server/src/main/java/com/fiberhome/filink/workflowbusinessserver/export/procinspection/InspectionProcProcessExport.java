package com.fiberhome.filink.workflowbusinessserver.export.procinspection;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.workflowbusinessserver.dao.procinspection.ProcInspectionDao;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
import com.fiberhome.filink.workflowbusinessserver.service.impl.procbase.ProcBaseServiceImpl;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionService;
import com.fiberhome.filink.workflowbusinessserver.vo.procinspection.ProcInspectionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 未完工巡检工单导出
 * @author hedongwei@wistronits.com
 * @date 2019/4/11 16:34
 */
@Component
public class InspectionProcProcessExport extends AbstractExport {

    @Autowired
    private ProcInspectionService procInspectionService;

    @Autowired
    private ProcInspectionDao procInspectionDao;

    @Autowired
    private ProcBaseServiceImpl procBaseService;

    /**
     * 查询数据信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 16:37
     * @param queryCondition 查询对象
     * @return 查询数据信息
     */
    @Override
    protected List queryData(QueryCondition queryCondition) {
        //设施工单类型为巡检工单
        procInspectionService.setProcTypeToInspection(queryCondition);

        //获取拥有权限信息
        procBaseService.getPermissionsInfoForExport(queryCondition);

        List<ProcBaseResp> procBaseRespList = procInspectionDao.queryListProcInspectionUnfinishedProcByPage(queryCondition);
        List<ProcBaseResp> resultList = procBaseService.convertProcInfoToProcResp(procBaseRespList);
        List<ProcInspectionVo> procInspectionVoList = procInspectionService.getInspectionVoInfo(resultList);
        return procInspectionVoList;
    }

    /**
     * 查询对象的个数
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 16:38
     * @param queryCondition 查询对象
     */
    @Override
    protected Integer queryCount(QueryCondition queryCondition) {
        //设施工单类型为巡检工单
        procInspectionService.setProcTypeToInspection(queryCondition);

        //获取拥有权限信息
        procBaseService.getPermissionsInfo(queryCondition);

        Integer count = procInspectionDao.queryCountListProcInspectionUnfinishedProc(queryCondition);
        return count;
    }
}
