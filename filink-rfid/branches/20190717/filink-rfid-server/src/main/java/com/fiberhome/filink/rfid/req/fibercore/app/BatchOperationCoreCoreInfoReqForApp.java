package com.fiberhome.filink.rfid.req.fibercore.app;

import com.fiberhome.filink.rfid.bean.fibercore.CoreCoreInfo;

import java.util.List;

/**
 * <p>
 * app处理熔纤信息请求
 * </p>
 *
 * @author chaofanrong
 * @since 2019-06-10
 */
public class BatchOperationCoreCoreInfoReqForApp {

    /**
     * 上传类型（0、新增；1、删除；2、修改；）
     */
    private String uploadType;

    /**
     * 熔纤信息列表
     */
    private List<OperationCoreCoreInfoReqForApp> operationCoreCoreInfoReqForAppList;

    public List<OperationCoreCoreInfoReqForApp> getOperationCoreCoreInfoReqForAppList() {
        return operationCoreCoreInfoReqForAppList;
    }

    public void setOperationCoreCoreInfoReqForAppList(List<OperationCoreCoreInfoReqForApp> operationCoreCoreInfoReqForAppList) {
        this.operationCoreCoreInfoReqForAppList = operationCoreCoreInfoReqForAppList;
    }

    public String getUploadType() {
        return uploadType;
    }

    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }
}
