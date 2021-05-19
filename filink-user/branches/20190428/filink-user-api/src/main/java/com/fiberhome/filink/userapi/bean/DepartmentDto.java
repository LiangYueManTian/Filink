package com.fiberhome.filink.userapi.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *      部门以及扩展信息
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */
@Data
public class DepartmentDto implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    private String id;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 责任人
     */
    private String deptChargeuser;

    /**
     * 联系电话
     */
    private String deptPhonenum;

    /**
     * 地址
     */
    private String address;

    /**
     * 类型
     */
    private String deptType;

    /**
     * 父级部门ID
     */
    private String deptFatherid;

    /**
     * 部门级别
     */
    private String deptLevel;

    /**
     * 是否被删除,1没有，0已删除
     */
    private String deleted;

    /**
     * 备注
     */
    private String remark;

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


    @Override
    public String toString() {
        return "DepartmentDto{" +
                "id='" + id + '\'' +
                ", deptName='" + deptName + '\'' +
                ", deptChargeuser='" + deptChargeuser + '\'' +
                ", deptPhonenum='" + deptPhonenum + '\'' +
                ", address='" + address + '\'' +
                ", deptType='" + deptType + '\'' +
                ", deptFatherid='" + deptFatherid + '\'' +
                ", deptLevel='" + deptLevel + '\'' +
                ", deleted='" + deleted + '\'' +
                ", remark='" + remark + '\'' +
                ", createUser='" + createUser + '\'' +
                ", createTime=" + createTime +
                ", updateUser='" + updateUser + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
