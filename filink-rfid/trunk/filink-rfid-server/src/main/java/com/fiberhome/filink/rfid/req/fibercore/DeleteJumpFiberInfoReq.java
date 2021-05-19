package com.fiberhome.filink.rfid.req.fibercore;

import com.fiberhome.filink.rfid.bean.fibercore.JumpFiberInfo;

import java.util.Set;

/**
 * <p>
 * 删除跳接信息请求
 * </p>
 *
 * @author chaofanrong
 * @since 2019-07-03
 */
public class DeleteJumpFiberInfoReq extends JumpFiberInfo {

    /**
     * 跳接idList
     */
    private Set<String> jumpFiberIdList;

    public Set<String> getJumpFiberIdList() {
        return jumpFiberIdList;
    }

    public void setJumpFiberIdList(Set<String> jumpFiberIdList) {
        this.jumpFiberIdList = jumpFiberIdList;
    }
}
