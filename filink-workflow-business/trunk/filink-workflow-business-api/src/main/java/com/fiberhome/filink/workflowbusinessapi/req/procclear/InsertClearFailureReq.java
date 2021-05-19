package com.fiberhome.filink.workflowbusinessapi.req.procclear;

import com.fiberhome.filink.workflowbusinessapi.bean.procbase.ProcRelatedDepartment;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 新增销障工单请求类
 * </p>
 *
 * @author choafanrong@wistronits.com
 * @since 2019-03-18
 */
@Data
public class InsertClearFailureReq {

    /**
     * 流程标题
     */
    private String title;

    /**
     * 工单来源 1 手动新增  2 巡检任务新增 3 告警新增
     */
    private String procResourceType;

    /**
     * 关联告警编号
     */
    private String refAlarm;

    /**
     * 关联告警名称
     */
    private String refAlarmName;

    /**
     * 关联告警code
     */
    private String refAlarmCode;

    /**
     * 期望完工时间
     */
    private Date ecTime;

    /**
     * 设施编号
     */
    private String deviceId;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 设施区域id
     */
    private String deviceAreaId;

    /**
     * 设施区域名称
     */
    private String deviceAreaName;

    /**
     * 责任单位
     */
    private List<ProcRelatedDepartment> accountabilityDeptList;

    /**
     * 备注
     */
    private String remark;

}