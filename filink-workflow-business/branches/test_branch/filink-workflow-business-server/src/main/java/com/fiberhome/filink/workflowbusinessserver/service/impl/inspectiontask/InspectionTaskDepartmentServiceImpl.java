package com.fiberhome.filink.workflowbusinessserver.service.impl.inspectiontask;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDepartment;
import com.fiberhome.filink.workflowbusinessserver.dao.inspectiontask.InspectionTaskDepartmentDao;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskDepartmentService;
import com.fiberhome.filink.workflowbusinessserver.constant.RequestHeaderConstants;
import com.fiberhome.filink.workflowbusinessserver.utils.request.RequestHeaderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 巡检任务关联单位表 服务实现类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-27
 */
@Service
public class InspectionTaskDepartmentServiceImpl extends ServiceImpl<InspectionTaskDepartmentDao, InspectionTaskDepartment> implements InspectionTaskDepartmentService {

    @Autowired
    private InspectionTaskDepartmentDao inspectionTaskDepartmentDao;

    /**
     * 查询巡检任务关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/5 13:37
     * @param inspectionTaskIds 巡检任务编号集合
     * @return List<InspectionTaskDepartment> 巡检任务关联部门信息
     */
    @Override
    public List<InspectionTaskDepartment> queryInspectionTaskDepartmentsByTaskIds(List<String> inspectionTaskIds) {
        return inspectionTaskDepartmentDao.queryInspectionTaskDepartmentsByTaskIds(inspectionTaskIds);
    }


    /**
     * 查询巡检任务关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/5 13:37
     * @param inspectionTaskId 巡检任务编号
     * @return List<InspectionTaskDepartment> 巡检任务关联部门信息
     */
    @Override
    public List<InspectionTaskDepartment> queryInspectionTaskDepartmentsByTaskId(String inspectionTaskId) {
        return inspectionTaskDepartmentDao.queryInspectionTaskDepartmentsByTaskId(inspectionTaskId);
    }

    /**
     * 删除巡检任务关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/2/27 15:44
     * @param inspectionTaskDepartment 巡检任务关联部门参数
     * @return int 返回删除关联部门更改的行数
     */
    @Override
    public int deleteInspectionTaskDepartment(InspectionTaskDepartment inspectionTaskDepartment) {
        //删除结果
        int resultDeleteDepartment = 0;
        if (null != inspectionTaskDepartment && !StringUtils.isEmpty(inspectionTaskDepartment.getInspectionTaskId())) {
            //巡检任务编号不为空时删除巡检关联单位
            resultDeleteDepartment = inspectionTaskDepartmentDao.deleteInspectionTaskDepartment(inspectionTaskDepartment);
        } else {
            //巡检任务编号为空时不删除巡检关联单位
            resultDeleteDepartment = 0;
        }
        //返回删除结果
        return resultDeleteDepartment;
    }

    /**
     * 批量新增巡检任务关联单位
     * @author hedongwei@wistronits.com
     * @date  2019/2/27 15:52
     * @param departmentList 新增巡检任务关联部门的参数
     * @param inspectionTaskId 巡检任务编号
     */
    @Override
    public int insertInspectionTaskDepartmentBatch(List<InspectionTaskDepartment> departmentList, String inspectionTaskId) {
        int resultInsertDepartment = 0;
        Date nowDate = new Date();
        if (null != departmentList && 0 < departmentList.size()) {
            //批量新增关联设施
            for (InspectionTaskDepartment departmentOne: departmentList) {
                //巡检任务单位编号
                departmentOne.setInspectionTaskDeptId(NineteenUUIDUtils.uuid());
                //创建时间
                departmentOne.setCreateTime(nowDate);
                //新增用户信息
                departmentOne.setCreateUser(RequestHeaderUtils.getHeadParam(RequestHeaderConstants.PARAM_USER_ID));
                //巡检任务编号
                departmentOne.setInspectionTaskId(inspectionTaskId);
            }
            //新增集合的结果
            resultInsertDepartment = inspectionTaskDepartmentDao.insertInspectionTaskDepartmentBatch(departmentList);
        }
        return resultInsertDepartment;
    }

    /**
     * 批量删除巡检任务关联单位
     * @author hedongwei@wistronits.com
     * @date  2019/3/2 23:47
     * @param inspectionTaskIds 巡检任务编号
     * @param isDeleted 是否删除
     * @return int 批量删除巡检任务关联单位修改的行数
     */
    @Override
    public int deleteInspectionTaskDepartmentBatch(List<String> inspectionTaskIds, String isDeleted) {
        int resultDeleteDepartment = 0;
        if (null != inspectionTaskIds && 0 < inspectionTaskIds.size()) {
            resultDeleteDepartment = inspectionTaskDepartmentDao.deleteInspectionTaskDepartmentBatch(inspectionTaskIds, isDeleted);
        } else {
            resultDeleteDepartment = 0;
        }
        return resultDeleteDepartment;
    }


    /**
     * 根据部门编号查询巡检部门数据
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 20:14
     * @param deptIds 部门编号
     * @return 部门编号查询巡检部门数据
     */
    @Override
    public List<InspectionTaskDepartment> queryTaskListByDeptIds(List<String> deptIds) {
        return inspectionTaskDepartmentDao.queryTaskListByDeptIds(deptIds);
    }
}
