package com.fiberhome.filink.workflowbusinessapi.bean.inspectiontask;

import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 巡检任务设施表
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-26
 */
@Data
public class InspectionTaskDevice {

    private static final long serialVersionUID = 1L;


    private String inspectionTaskDeviceId;

    /**
     * 编号
     */
    private String inspectionTaskId;

    /**
     * 设施编号
     */
    private String deviceId;

    /**
     * 设施区域id
     */
    private String deviceAreaId;

    /**
     * 选择设备的类型 区域  1  设备 0
     */
    private String selectDeviceType;

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
