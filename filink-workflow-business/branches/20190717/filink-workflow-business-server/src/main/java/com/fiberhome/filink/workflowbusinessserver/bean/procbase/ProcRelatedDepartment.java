package com.fiberhome.filink.workflowbusinessserver.bean.procbase;

import java.util.ArrayList;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 工单关联单位表
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-03-11
 */
@TableName("proc_related_department")
@Data
public class ProcRelatedDepartment extends Model<ProcRelatedDepartment> {

    private static final long serialVersionUID = 1L;

    @TableField("proc_related_dept_id")
    private String procRelatedDeptId;

    /**
     * 工单编码
     */
    @TableField("proc_id")
    private String procId;

    /**
     * 责任单位编号
     */
    @TableField("accountability_dept")
    private String accountabilityDept;

    /**
     * 工单创建时间
     */
    @TableField("proc_create_time")
    private Date procCreateTime;

    @TableField("is_deleted")
    private String isDeleted;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改人
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

    @Override
    protected Serializable pkVal() {
        return this.procRelatedDeptId;
    }

    @Override
    public String toString() {
        return "ProcRelatedDepartment{" +
        "procRelatedDeptId=" + procRelatedDeptId +
        ", procId=" + procId +
        ", accountabilityDept=" + accountabilityDept +
        ", isDeleted=" + isDeleted +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        "}";
    }

    /**
     * 获取工单关联部门
     * @author hedongwei@wistronits.com
     * @date  2019/6/26 11:08
     * @param processInfoList 工单信息集合
     * @param procRelatedDepartments 工单关联部门信息
     * @return 工单关联部门信息
     */
    public static List<ProcRelatedDepartment> getProcRelatedDepartment(List<ProcessInfo> processInfoList, List<ProcRelatedDepartment> procRelatedDepartments) {
        List<ProcRelatedDepartment> inspectionRelatedDepartmentList = new ArrayList<>();
        ProcRelatedDepartment procRelatedDepartment;
        if (!ObjectUtils.isEmpty(processInfoList) ) {
            for (ProcessInfo processInfo : processInfoList) {
                if (!ObjectUtils.isEmpty(processInfo.getProcBase().getAccountabilityDept())) {
                    procRelatedDepartment = new ProcRelatedDepartment();
                    BeanUtils.copyProperties(processInfo.getProcBase(), procRelatedDepartment);
                    inspectionRelatedDepartmentList.add(procRelatedDepartment);
                }
            }

            if (!ObjectUtils.isEmpty(inspectionRelatedDepartmentList)) {

                if (!ObjectUtils.isEmpty(procRelatedDepartments)) {
                    procRelatedDepartments.addAll(inspectionRelatedDepartmentList);
                } else {
                    procRelatedDepartments = new ArrayList<>();
                    procRelatedDepartments.addAll(inspectionRelatedDepartmentList);
                }
            }
        }
        return procRelatedDepartments;

    }
}
