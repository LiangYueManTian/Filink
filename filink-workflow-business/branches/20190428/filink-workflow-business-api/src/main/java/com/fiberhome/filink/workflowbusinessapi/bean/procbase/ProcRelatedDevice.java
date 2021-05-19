package com.fiberhome.filink.workflowbusinessapi.bean.procbase;

import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 工单关联设施表
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-02-20
 */
@Data
public class ProcRelatedDevice {

    private static final long serialVersionUID = 1L;

    private String procRelatedDeviceId;

    /**
     * 工单编码
     */
    private String procId;

    /**
     * 设施编号
     */
    private String deviceId;

    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 设施区域id
     */
    private String deviceAreaId;

    /**
     * 选择设备的类型 区域  1  设备 0
     */
    private String selectDeviceType;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 设施区域名称
     */
    private String deviceAreaName;

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
