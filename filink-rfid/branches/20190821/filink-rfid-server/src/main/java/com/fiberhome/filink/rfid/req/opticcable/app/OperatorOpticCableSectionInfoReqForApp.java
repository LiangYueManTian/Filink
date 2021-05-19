package com.fiberhome.filink.rfid.req.opticcable.app;

import com.fiberhome.filink.rfid.bean.opticcable.OpticCableSectionInfo;

/**
 * <p>
 * 处理光缆段信息请求
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-20
 */
public class OperatorOpticCableSectionInfoReqForApp extends OpticCableSectionInfo {
    /**
     * 上传类型(0新增1删除2修改)
     */
    private String uploadType;

    public String getUploadType() {
        return uploadType;
    }

    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }
}
