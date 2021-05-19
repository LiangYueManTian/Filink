package com.fiberhome.filink.workflowbusinessserver.req.procclear;

import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 新增销障工单请求类
 * </p>
 *
 * @author choafanrong@wistronits.com
 * @since 2019-03-20
 */
@Data
public class UpdateClearFailureReq {

    /**
     * 流程编号
     */
    private String procId;

    /**
     * 流程标题
     */
    private String title;

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
     * 责任单位列表
     */
    private List<ProcRelatedDepartment> accountabilityDeptList;

    /**
     * 备注
     */
    private String remark;

}