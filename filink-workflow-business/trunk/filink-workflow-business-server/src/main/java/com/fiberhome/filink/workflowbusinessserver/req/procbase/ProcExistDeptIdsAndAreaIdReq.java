package com.fiberhome.filink.workflowbusinessserver.req.procbase;

import lombok.Data;

import java.util.List;

/**
 * 工单存在部门中有这个区域的值
 * @author hedongwei@wistronits.com
 * @date 2019/8/28 15:18
 */
@Data
public class ProcExistDeptIdsAndAreaIdReq {

    /**
     * 工单部门编号
     */
    private List<String> deptIdList;

    /**
     * 区域编号
     */
    private String areaId;
}
