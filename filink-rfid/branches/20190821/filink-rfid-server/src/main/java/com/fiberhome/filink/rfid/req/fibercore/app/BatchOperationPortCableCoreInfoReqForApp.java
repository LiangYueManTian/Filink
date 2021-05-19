package com.fiberhome.filink.rfid.req.fibercore.app;

import com.fiberhome.filink.rfid.bean.fibercore.PortCableCoreInfo;

import java.util.List;

/**
 * <p>
 * app处理成端信息请求
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-31
 */
public class BatchOperationPortCableCoreInfoReqForApp extends PortCableCoreInfo {

    /**
     * 上传类型（0、新增；1、删除；2、修改；）
     */
    private String uploadType;

    /**
     * 成端信息列表
     */
    private List<OperationPortCableCoreInfoReqForApp> operationPortCableCoreInfoReqForAppList;

    public List<OperationPortCableCoreInfoReqForApp> getOperationPortCableCoreInfoReqForAppList() {
        return operationPortCableCoreInfoReqForAppList;
    }

    public void setOperationPortCableCoreInfoReqForAppList(List<OperationPortCableCoreInfoReqForApp> operationPortCableCoreInfoReqForAppList) {
        this.operationPortCableCoreInfoReqForAppList = operationPortCableCoreInfoReqForAppList;
    }

    public String getUploadType() {
        return uploadType;
    }

    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }
}
