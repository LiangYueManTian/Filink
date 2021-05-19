package com.fiberhome.filink.workflowbusinessserver.vo.procinspection;

import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author hedongwei@wistronits.com
 * description
 * @date 2019/3/20 19:28
 */
@Data
public class GetInspectionProcByIdVo {

    /**
     * 设施名称
     */
    private StringBuilder deviceName;

    /**
     * 设施类型
     */
    private StringBuilder deviceType;


    /**
     * 设施区域id
     */
    private StringBuilder deviceAreaId;

    /**
     * 设施区域名称
     */
    private StringBuilder deviceAreaName;

    /**
     * 部门名称
     */
    private StringBuilder accountabilityDeptName;

    /**
     * 告警名称
     */
    private String alarmName;

    /**
     * 巡检任务开始时间
     */
    private Date inspectionStartDate;

    /**
     * 巡检任务开始时间时间戳
     */
    private Long inspectionStartTime;

    /**
     * 巡检任务结束时间
     */
    private Date inspectionEndDate;

    /**
     * 巡检任务结束时间时间戳
     */
    private Long inspectionEndTime;

    /**
     * 巡检设施总数
     */
    private Integer inspectionDeviceCount;

    /**
     * 巡检完成数量
     */
    private Integer inspectionCompletedCount;

    /**
     * 进度
     */
    private double progressSpeed;

    /**
     * 创建时间戳
     */
    private Long cTime;

    /**
     * 修改时间戳
     */
    private Long uTime;

    /**
     * 期望完工时间戳
     */
    private Long ecTime;

    /**
     * 实际完工时间戳
     */
    private Long rcTime;

    /**
     * 剩余天数
     */
    private String lastDays;


    public Long getcTime() {
        return cTime;
    }

    public void setcTime(Long cTime) {
        this.cTime = cTime;
    }

    public Long getuTime() {
        return uTime;
    }

    public void setuTime(Long uTime) {
        this.uTime = uTime;
    }

    private List<ProcRelatedDevice> procRelatedDevices;
    private List<ProcRelatedDepartment> procRelatedDepartments;
}
