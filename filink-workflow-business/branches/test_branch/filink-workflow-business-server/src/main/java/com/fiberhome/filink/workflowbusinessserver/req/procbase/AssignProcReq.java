package com.fiberhome.filink.workflowbusinessserver.req.procbase;

import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import lombok.Data;

import java.util.List;

/**
 * 指派工单信息
 * @author hedongwei@wistronits.com
 * @date 2019/3/27 20:21
 */
@Data
public class AssignProcReq {

    /**
     * 流程编号
     */
    private String procId;

    /**
     * 部门集合
     */
    private List<ProcRelatedDepartment> departmentList;
}
