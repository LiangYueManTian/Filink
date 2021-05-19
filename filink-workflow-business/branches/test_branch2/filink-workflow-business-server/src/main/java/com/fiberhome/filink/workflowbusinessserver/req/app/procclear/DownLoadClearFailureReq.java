package com.fiberhome.filink.workflowbusinessserver.req.app.procclear;

import lombok.Data;

/**
 * <p>
 * 销障工单下载请求类
 * </p>
 *
 * @author choafanrong@wistronits.com
 * @since 2019-04-11
 */
@Data
public class DownLoadClearFailureReq {

    /**
     * 工单id
     */
    private String orderId;

    /**
     * 工单类型
     */
    private String orderType;
}