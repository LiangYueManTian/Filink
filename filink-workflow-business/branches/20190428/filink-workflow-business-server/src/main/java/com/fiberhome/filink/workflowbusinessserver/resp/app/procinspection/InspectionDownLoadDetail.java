package com.fiberhome.filink.workflowbusinessserver.resp.app.procinspection;

import lombok.Data;

import java.util.List;

/**
 * app巡检工单下载明细类
 *
 * @author hedongwei@wistronits.com
 */
@Data
public class InspectionDownLoadDetail {
    /**
     * 工单id
     */
    private String procId;

    /**
     * 工单名称
     */
    private String title;

    /**
     * 工单类型
     */
    private String procType;

    /**
     * 执行人
     */
    private String assign;

    /**
     * 关联设施
     */
    private List<ProcInspectionRelatedDeviceForApp> procRelatedDeviceRespList;

    /**
     * 工单开始时间（创建时间）
     */
    private Long startTime;


    /**
     * 工单结束时间（实际完工时间）
     */
    private Long endTime;


}
