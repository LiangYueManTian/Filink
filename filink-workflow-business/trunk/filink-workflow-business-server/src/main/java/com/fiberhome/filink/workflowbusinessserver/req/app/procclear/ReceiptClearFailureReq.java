package com.fiberhome.filink.workflowbusinessserver.req.app.procclear;

import lombok.Data;

/**
 * <p>
 * 销障工单回单请求类
 * </p>
 *
 * @author choafanrong@wistronits.com
 * @since 2019-04-03
 */
@Data
public class ReceiptClearFailureReq {

    /**
     * 工单id
     */
    private String procId;

    /**
     * 执行人
     */
    private String assign;

    /**
     * 故障原因
     */
    private String errorReason;

    /**
     * 自定义原因
     */
    private String userDefinedErrorReason;

    /**
     * 处理方案
     */
    private String processingScheme;

    /**
     * 自定义处理方案
     */
    private String userDefinedProcessingScheme;
}