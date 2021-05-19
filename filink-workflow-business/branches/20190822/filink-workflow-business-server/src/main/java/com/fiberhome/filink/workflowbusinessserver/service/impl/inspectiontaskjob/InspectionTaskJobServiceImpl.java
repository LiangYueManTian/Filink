package com.fiberhome.filink.workflowbusinessserver.service.impl.inspectiontaskjob;

import com.fiberhome.filink.scheduleapi.api.InspectionTaskFeign;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskRelatedJob;
import com.fiberhome.filink.workflowbusinessserver.dao.inspectiontask.InspectionTaskRelatedJobDao;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontaskjob.InspectionTaskRelatedJobReq;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontaskjob.InspectionTaskJobService;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 删除巡检任务定时任务
 * @author hedongwei@wistronits.com
 * @date 2019/4/23 10:49
 */
@Service
public class InspectionTaskJobServiceImpl implements InspectionTaskJobService {

    @Autowired
    private InspectionTaskRelatedJobDao inspectionTaskRelatedJobDao;

    @Autowired
    private InspectionTaskFeign inspectionTaskFeign;

    /**
     * 删除巡检任务定时任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 10:55
     * @param inspectionTaskIds 巡检任务编号集合
     */
    @Override
    @Async
    public void deleteInspectionTaskJob(List<String> inspectionTaskIds, String isDeleted) {
        if (!ObjectUtils.isEmpty(inspectionTaskIds)) {
            //查询需要删除的定时任务的信息
            InspectionTaskRelatedJobReq relatedJobParam = new InspectionTaskRelatedJobReq();
            relatedJobParam.setInspectionTaskIds(inspectionTaskIds);
            relatedJobParam.setIsDeleted(WorkFlowBusinessConstants.IS_NOT_DELETED);
            List<InspectionTaskRelatedJob> relatedJobs = inspectionTaskRelatedJobDao.selectInspectionTaskRelatedInfo(relatedJobParam);
            if (!ObjectUtils.isEmpty(relatedJobs)) {

                List<String> inspectionTaskList = new ArrayList<>();
                for (InspectionTaskRelatedJob relatedJobOne : relatedJobs) {
                    inspectionTaskList.add(relatedJobOne.getInspectionTaskJobName());
                }
                //删除定时任务
                inspectionTaskFeign.deleteTaskJobList(inspectionTaskList);

                //删除定时任务关联记录信息
                relatedJobParam.setIsDeleted(isDeleted);
                inspectionTaskRelatedJobDao.logicDeleteInspectionTaskRelatedJob(relatedJobParam);

            }
        }
    }
}
