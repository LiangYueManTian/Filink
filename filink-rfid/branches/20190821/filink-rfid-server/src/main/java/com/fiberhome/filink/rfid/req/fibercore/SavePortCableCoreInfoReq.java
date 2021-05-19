package com.fiberhome.filink.rfid.req.fibercore;

import java.util.List;

/**
 * <p>
 * 保存成端信息请求
 * </p>
 *
 * @author chaofanrong
 * @since 2019-07-02
 */
public class SavePortCableCoreInfoReq {

    /**
     * 新增成端信息列表（用于保存成端信息）
     */
    private List<InsertPortCableCoreInfoReq> insertPortCableCoreInfoReqList;
    /**
     * 更新端口信息列表（用于更新端口信息状态）
     */
    private List<UpdatePortCableCoreInfoReq> updatePortCableCoreInfoReqList;

    public List<InsertPortCableCoreInfoReq> getInsertPortCableCoreInfoReqList() {
        return insertPortCableCoreInfoReqList;
    }

    public void setInsertPortCableCoreInfoReqList(List<InsertPortCableCoreInfoReq> insertPortCableCoreInfoReqList) {
        this.insertPortCableCoreInfoReqList = insertPortCableCoreInfoReqList;
    }

    public List<UpdatePortCableCoreInfoReq> getUpdatePortCableCoreInfoReqList() {
        return updatePortCableCoreInfoReqList;
    }

    public void setUpdatePortCableCoreInfoReqList(List<UpdatePortCableCoreInfoReq> updatePortCableCoreInfoReqList) {
        this.updatePortCableCoreInfoReqList = updatePortCableCoreInfoReqList;
    }
}
