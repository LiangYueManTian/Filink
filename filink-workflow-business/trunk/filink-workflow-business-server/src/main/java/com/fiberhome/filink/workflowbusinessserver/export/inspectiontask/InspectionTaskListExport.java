package com.fiberhome.filink.workflowbusinessserver.export.inspectiontask;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTask;
import com.fiberhome.filink.workflowbusinessserver.dao.inspectiontask.InspectionTaskDao;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskService;
import com.fiberhome.filink.workflowbusinessserver.utils.common.ValidateUtils;
import com.fiberhome.filink.workflowbusinessserver.vo.inspectiontask.QueryListInspectionTaskByPageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 巡检任务导出类
 *
 * @author hedongwei@wistronits.com
 */
@Component
public class InspectionTaskListExport extends AbstractExport {

    @Autowired
    private InspectionTaskService inspectionTaskService;

    @Autowired
    private InspectionTaskDao inspectionTaskDao;

    /**
     * 查询数据信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 16:37
     * @param queryCondition 查询对象
     * @return 查询数据信息
     */
    @Override
    protected List queryData(QueryCondition queryCondition) {
        //获取查询条件过滤之后的条件
        queryCondition = ValidateUtils.filterQueryCondition(queryCondition);
        //获得开始行数
        Integer begin = (queryCondition.getPageCondition().getPageNum() - 1) * queryCondition.getPageCondition().getPageSize();
        queryCondition.getPageCondition().setBeginNum(begin);
        List<QueryListInspectionTaskByPageVo> inspectionTaskVoList = new ArrayList<>();

        //是导出操作
        boolean isExport = true;

        //获取查询权限
        queryCondition = inspectionTaskService.getInspectionTaskCondition(queryCondition, isExport);

        //巡检任务列表数据
        //获取巡检编号
        List<String> inspectionTaskIds = inspectionTaskDao.queryListInspectionTaskByPage(queryCondition);

        if (!ObjectUtils.isEmpty(inspectionTaskIds)) {
            //查询数据信息
            List<InspectionTask> taskList = inspectionTaskDao.selectInspectionTaskForInspectionTaskIds(inspectionTaskIds);

            //将数据转化成有序的内容
            List<InspectionTask> showTaskList = inspectionTaskService.getShowInspectionTaskList(taskList, inspectionTaskIds);

            inspectionTaskVoList = inspectionTaskService.getInspectionTaskInfoList(showTaskList);
        }

        return inspectionTaskVoList;
    }

    /**
     * 查询对象的个数
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 16:38
     * @param queryCondition 查询对象
     */
    @Override
    protected Integer queryCount(QueryCondition queryCondition) {

        boolean isExport = false;

        //获取查询权限
        queryCondition = inspectionTaskService.getInspectionTaskCondition(queryCondition, isExport);

        //查询数据数量
        Integer count = inspectionTaskDao.queryListInspectionTaskCount(queryCondition);
        return count;
    }

}
