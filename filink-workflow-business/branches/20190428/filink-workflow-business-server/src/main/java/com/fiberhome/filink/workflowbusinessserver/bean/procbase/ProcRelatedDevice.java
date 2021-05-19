package com.fiberhome.filink.workflowbusinessserver.bean.procbase;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 工单关联设施表
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-02-20
 */
@TableName("proc_related_device")
@Data
public class ProcRelatedDevice extends Model<ProcRelatedDevice> {

    private static final long serialVersionUID = 1L;

    @TableField("proc_related_device_id")
    private String procRelatedDeviceId;

    /**
     * 工单编码
     */
    @TableField("proc_id")
    private String procId;

    /**
     * 设施编号
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 设施类型
     */
    @TableField("device_type")
    private String deviceType;

    /**
     * 设施区域id
     */
    @TableField("device_area_id")
    private String deviceAreaId;

    /**
     * 选择设备的类型 区域  1  设备 0
     */
    @TableField("select_device_type")
    private String selectDeviceType;

    /**
     * 设施名称
     */
    @TableField("device_name")
    private String deviceName;

    /**
     * 设施区域名称
     */
    @TableField("device_area_name")
    private String deviceAreaName;

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
        return this.procRelatedDeviceId;
    }

    @Override
    public String toString() {
        return "ProcRelatedDevice{" +
        "procRelatedDeviceId=" + procRelatedDeviceId +
        ", procId=" + procId +
        ", deviceId=" + deviceId +
        ", deviceAreaId=" + deviceAreaId +
        ", selectDeviceType=" + selectDeviceType +
        ", isDeleted=" + isDeleted +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        "}";
    }
}
