package com.fiberhome.filink.workflowbusinessapi.bean.procbase;

import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 工单关联单位表
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-03-11
 */
@Data
public class ProcRelatedDepartment {

    private static final long serialVersionUID = 1L;

    private String procRelatedDeptId;

    /**
     * 工单编码
     */
    private String procId;

    /**
     * 责任单位编号
     */
    private String accountabilityDept;

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
