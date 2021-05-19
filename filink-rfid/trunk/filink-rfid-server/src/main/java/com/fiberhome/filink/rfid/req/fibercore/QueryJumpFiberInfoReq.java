package com.fiberhome.filink.rfid.req.fibercore;

import com.fiberhome.filink.rfid.bean.fibercore.JumpFiberInfo;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 查询跳接信息请求
 * </p>
 *
 * @author chaofanrong
 * @since 2019-06-12
 */
public class QueryJumpFiberInfoReq extends JumpFiberInfo {

    /**
     * 所属设施id列表（用于批量删除设施）
     */
    private List<String> deviceIds;

    /**
     * 跳接idList
     */
    private Set<String> jumpFiberIdList;

    public List<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }

    public Set<String> getJumpFiberIdList() {
        return jumpFiberIdList;
    }

    public void setJumpFiberIdList(Set<String> jumpFiberIdList) {
        this.jumpFiberIdList = jumpFiberIdList;
    }
}
