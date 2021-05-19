package com.fiberhome.filink.userserver.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 部门参数列表
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */
@Data
public class DepartmentParamter implements Serializable {

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
    private String deptChargeUser;

    /**
     * 联系电话
     */
    private String deptPhoneNum;

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
    private String deptFatherId;

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
     * 上级部门名称
     */
    private String parentDepartmentName;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 每页大小
     */
    private int pageSize = 10;

    /**
     * 页数
     */
    private int page = 0;

    /**
     * 开始条数
     */
    private int startNum;

    /**
     * 排序的字段
     */
    private String sortProperties;

    /**
     * 降序还是升序
     */
    private String sort;

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
