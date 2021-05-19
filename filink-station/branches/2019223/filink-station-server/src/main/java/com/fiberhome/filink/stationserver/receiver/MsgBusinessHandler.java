package com.fiberhome.filink.stationserver.receiver;

import com.fiberhome.filink.stationserver.entity.param.AbstractResOutputParams;

/**
 * 数据业务处理接口
 * @author CongcaiYu
 */
public interface MsgBusinessHandler {

    /**
     * 数据业务处理
     *
     * @param abstractResOutputParams AbstractResOutputParams
     * @throws Exception Exception
     */
    void handleMsg(AbstractResOutputParams abstractResOutputParams) throws Exception;
}
