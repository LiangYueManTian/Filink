package com.fiberhome.filink.rfid.req.fibercore.app;

import java.util.List;

/**
 * <p>
 * app处理跳接信息基础类
 * </p>
 *
 * @author chaofanrong
 * @since 2019-06-03
 */
public class BatchOperationJumpFiberInfoForApp {

    /**
     * 上传类型（0、新增；1、删除；2、修改；）
     */
    private String uploadType;

    /**
     * 跳接信息列表
     */
    private List<OperationJumpFiberInfoReqForApp> operationJumpFiberInfoReqForAppList;

    public String getUploadType() {
        return uploadType;
    }

    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }

    public List<OperationJumpFiberInfoReqForApp> getOperationJumpFiberInfoReqForAppList() {
        return operationJumpFiberInfoReqForAppList;
    }

    public void setOperationJumpFiberInfoReqForAppList(List<OperationJumpFiberInfoReqForApp> operationJumpFiberInfoReqForAppList) {
        this.operationJumpFiberInfoReqForAppList = operationJumpFiberInfoReqForAppList;
    }
}
