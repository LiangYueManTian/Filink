package com.fiberhome.filink.workflowbusinessserver.vo.procinspection;

import lombok.Data;

/**
 * 巡检记录返回类
 * @author hedongwei@wistronits.com
 * @date 2019/4/11 9:38
 */
@Data
public class ProcInspectionRecordVo {

    /**
     * 流程巡检记录编号
     */
    private String procInspectionRecordId;

    /**
     * 巡检任务名称
     */
    private String inspectionTaskName;

    /**
     * 巡检任务编号
     */
    private String inspectionTaskId;

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
     * 设施区域编号
     */
    private String deviceAreaId;

    /**
     * 设施区域名称
     */
    private String deviceAreaName;

    /**
     * 关联工单编码
     */
    private String procId;

    /**
     * 巡检时间
     */
    private Long inspectionTime;

    /**
     * 描述
     */
    private String description;

    /**
     * 责任人编码
     */
    private String updateUser;

    /**
     * 责任人
     */
    private String updateUserName;

    /**
     * 异常描述
     */
    private String exceptionDescription;

    /**
     * 巡检结果 0 正常 1 异常
     */
    private String result;

    /**
     * 资源匹配情况
     */
    private String resourceMatching;

    /**
     * 是否删除
     */
    private String isDeleted;
}
