package com.fiberhome.filink.rfid.req.rfid.app;

import com.fiberhome.filink.rfid.bean.rfid.OpticCableSectionRfidInfo;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 光缆段rfid信息app上传请求
 * </p>
 *
 * @author congcongsun2
 * @since 2019-06-05
 */
public class UploadOpticCableSectionRfidInfoReqApp{
    /**
     * 上传类型(0新增1删除2修改)
     */
    private String uploadType;
    /**
     * GIS标签信息
     */
    private List<OpticCableSectionRfidInfo> segmentGISList;

    /**
     *  光缆段id列表（用于删除光缆段关联gis信息）
     */
    private List<String> opticCableSectionIdList;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 修改人
     */
    private String updateUser;

    public String getUploadType() {
        return uploadType;
    }

    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }

    public List<OpticCableSectionRfidInfo> getSegmentGISList() {
        return segmentGISList;
    }

    public void setSegmentGISList(List<OpticCableSectionRfidInfo> segmentGISList) {
        this.segmentGISList = segmentGISList;
    }

    public List<String> getOpticCableSectionIdList() {
        return opticCableSectionIdList;
    }

    public void setOpticCableSectionIdList(List<String> opticCableSectionIdList) {
        this.opticCableSectionIdList = opticCableSectionIdList;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
}
