package com.fiberhome.filink.workflowbusinessserver.bean.procbase;

import java.util.ArrayList;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fiberhome.filink.workflowbusinessserver.bean.procclear.ProcClearFailure;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.List;

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
     * 工单创建时间
     */
    @TableField("proc_create_time")
    private Date procCreateTime;

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

    /**
     * 将销障工单关联设施集合加到工单关联设施集合中
     * @author hedongwei@wistronits.com
     * @date  2019/6/25 18:51
     * @param procClearRelatedDeviceList 销障工单关联设施集合
     * @param procRelatedDeviceList 关联设施集合
     * @return 工单关联设施集合
     */
    public static List<ProcRelatedDevice> setClearProcRelatedDeviceIntoRelatedDevice(List<ProcClearFailure> procClearRelatedDeviceList, List<ProcRelatedDevice> procRelatedDeviceList) {
        if (!ObjectUtils.isEmpty(procClearRelatedDeviceList)) {
            //将销障工单设施信息转换成关联的设施信息
            List<ProcRelatedDevice> relatedDeviceList = new ArrayList<>();
            ProcRelatedDevice procRelatedDevice;
            for (ProcClearFailure procClearFailureOne : procClearRelatedDeviceList) {
                procRelatedDevice = new ProcRelatedDevice();
                BeanUtils.copyProperties(procClearFailureOne, procRelatedDevice);
                relatedDeviceList.add(procRelatedDevice);
            }
            procRelatedDeviceList.addAll(relatedDeviceList);
        }
        return procRelatedDeviceList;
    }

    /**
     * 获取销障工单关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/6/26 11:08
     * @param processInfoList 工单信息集合
     * @param procRelatedDevices 工单关联设施信息
     * @return 销障工单关联设施信息
     */
    public static List<ProcRelatedDevice> getClearProcRelatedDevice(List<ProcessInfo> processInfoList, List<ProcRelatedDevice> procRelatedDevices) {
        List<ProcRelatedDevice> clearFailureRelatedDeviceList = new ArrayList<>();
        ProcRelatedDevice procRelatedDevice;
        if (!ObjectUtils.isEmpty(processInfoList) ) {
            for (ProcessInfo processInfo : processInfoList) {
                if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(processInfo.getProcBase().getProcType())) {
                    procRelatedDevice = new ProcRelatedDevice();
                    BeanUtils.copyProperties(processInfo.getProcBase(), procRelatedDevice);
                    clearFailureRelatedDeviceList.add(procRelatedDevice);
                }
            }

            if (!ObjectUtils.isEmpty(clearFailureRelatedDeviceList)) {
                if (!ObjectUtils.isEmpty(procRelatedDevices)) {
                    procRelatedDevices.addAll(clearFailureRelatedDeviceList);
                } else {
                    procRelatedDevices = new ArrayList<>();
                    procRelatedDevices.addAll(clearFailureRelatedDeviceList);
                }
            }
        }
        return procRelatedDevices;

    }
}
