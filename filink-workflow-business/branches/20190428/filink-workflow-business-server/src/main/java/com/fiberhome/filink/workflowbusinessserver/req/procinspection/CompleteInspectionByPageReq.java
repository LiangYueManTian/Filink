package com.fiberhome.filink.workflowbusinessserver.req.procinspection;

import lombok.Data;

import java.util.List;

/**
 * 查询已完成的巡检信息参数类
 * @author hedongwei@wistronits.com
 * @date 2019/3/14 18:54
 */
@Data
public class CompleteInspectionByPageReq {

    /**
     * 工单编号
     */
    private String procId;

    /**
     * 设施编号集合
     */
    private List<String> deviceIds;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 巡检结果
     */
    private List<String> results;

    /**
     * 巡检结果
     */
    private String result;

    /**
     * 异常描述
     */
    private String exceptionDescription;

    /**
     * 责任人
     */
    private List<String> updateUser;

    /**
     * 资源匹配情况
     */
    private String resourceMatching;

    /**
     * 巡检时间集合
     */
    private List<Long> inspectionTimes;

}
