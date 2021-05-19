package com.fiberhome.filink.stationserver.sender;

import com.fiberhome.filink.stationserver.entity.param.AbstractReqParams;

/**
 * 指令下发抽象类
 * @author CongcaiYu
 */
public abstract class AbstractInstructSender {
    /**
     * 解析指令并发送
     * @param abstractReqParams udp参数
     */
    public void sendInstruct(AbstractReqParams abstractReqParams){
        String data = getReqData(abstractReqParams);
        send(abstractReqParams.getDeviceId(),data);
    }
    /**
     * 发送指令
     * @param deviceId 设施id
     * @param data 16进制命令帧
     */
    abstract protected void send(String deviceId,String data);
    /**
     * 生成请求帧
     * @param abstractReqParams 请求参数
     * @return 16进制命令帧
     */
    abstract protected String getReqData(AbstractReqParams abstractReqParams);
}
