package com.fiberhome.filink.workflowbusinessapi.req.procbase;

import lombok.Data;

import java.util.List;

/**
 * 根据设施删除工单信息
 * @author hedongwei@wistronits.com
 * @date 2019/4/23 22:24
 */
@Data
public class DeleteProcBaseForDeviceReq {

    /**
     * 设施编号集合
     */
    private List<String> deviceIdList;

    /**
     * 是否删除
     */
    private String isDeleted;
}
