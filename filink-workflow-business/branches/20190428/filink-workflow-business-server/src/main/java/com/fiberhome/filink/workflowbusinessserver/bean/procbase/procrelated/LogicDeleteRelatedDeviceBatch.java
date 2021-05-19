package com.fiberhome.filink.workflowbusinessserver.bean.procbase.procrelated;

import lombok.Data;

import java.util.List;

/**
 * 逻辑删除关联设施
 * @author hedongwei@wistronits.com
 * @date 2019/4/23 19:38
 */
@Data
public class LogicDeleteRelatedDeviceBatch {

    /**
     *  工单关联设施编号
     */
    private List<String> relatedDeviceList;

    /**
     * 是否删除
     */
    private String isDeleted;
}
