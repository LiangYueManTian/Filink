package com.fiberhome.filink.workflowbusinessserver.req.procbase.procrelated;

import lombok.Data;

import java.util.List;

/**
 * 工单关联设施参数
 * @author hedongwei@wistronits.com
 * @date 2019/4/23 19:38
 */
@Data
public class ProcRelatedDeviceListForDeviceIdsReq {

    /**
     *  设施编号集合
     */
    private List<String> deviceIdList;

    /**
     * 是否删除
     */
    private String isDeleted;
}
