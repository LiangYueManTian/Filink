package com.fiberhome.filink.userserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 部门表
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */
@Data
public class Department extends Model<Department> {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    private String id;

    /**
     * 部门名称
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * 责任人
     */
    @TableField("dept_chargeuser")
    private String deptChargeUser;

    /**
     * 联系电话
     */
    @TableField("dept_phonenum")
    private String deptPhoneNum;

    /**
     * 地址
     */
    private String address;

    /**
     * 类型
     */
    @TableField("dept_type")
    private String deptType;

    /**
     * 父级部门ID
     */
    @TableField("dept_fatherId")
    private String deptFatherId;

    /**
     * 部门级别
     */
    @TableField("dept_level")
    private String deptLevel;

    /**
     * 是否被删除,1没有，0已删除
     */
    @TableField("is_deleted")
    private String deleted;

    /**
     * 备注
     */
    private String remark;

    /**
     * 当前部门的下级部门
     */
    @TableField(exist = false)
    private List<Department> childDepartmentList;

    /**
     * 上级部门
     */
    @TableField(exist = false)
    private Department parentDepartment;

    /**
     * 上级部门名称
     */
    @TableField(exist = false)
    private String parentDepartmentName;

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

    /**
     * 区域id列表信息
     */
    @TableField(exist = false)
    private List<String> areaIdList;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Department{"
                + "id='" + id + '\''
                + ", deptName='" + deptName + '\''
                + ", deptChargeUser='" + deptChargeUser + '\''
                + ", deptPhoneNum='" + deptPhoneNum + '\''
                + ", address='" + address + '\''
                + ", deptType='" + deptType + '\''
                + ", deptFatherId='" + deptFatherId + '\''
                + ", deptLevel='" + deptLevel + '\''
                + ", deleted='" + deleted + '\''
                + ", remark='" + remark + '\''
                + ", createUser='" + createUser + '\''
                + ", createTime=" + createTime
                + ", updateUser='" + updateUser + '\''
                + ", updateTime=" + updateTime
                + '}';
    }
}
