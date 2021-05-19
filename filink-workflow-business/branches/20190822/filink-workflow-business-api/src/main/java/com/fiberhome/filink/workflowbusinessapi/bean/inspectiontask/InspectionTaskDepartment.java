package com.fiberhome.filink.workflowbusinessapi.bean.inspectiontask;

import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 巡检任务关联单位表
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-27
 */
@Data
public class InspectionTaskDepartment {

    private static final long serialVersionUID = 1L;


    private String inspectionTaskDeptId;

    /**
     * 巡检任务名称
     */
    private String inspectionTaskId;

    /**
     * 责任单位编号
     */
    private String accountabilityDept;

    /**
     * 是否删除
     */
    private String isDeleted;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 修改时间
     */
    private Date updateTime;
}
