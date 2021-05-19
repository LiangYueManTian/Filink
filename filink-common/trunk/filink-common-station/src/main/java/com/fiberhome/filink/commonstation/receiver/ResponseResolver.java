package com.fiberhome.filink.commonstation.receiver;


import com.fiberhome.filink.commonstation.entity.param.AbstractResInputParams;
import com.fiberhome.filink.commonstation.entity.param.AbstractResOutputParams;

/**
 * 响应帧解析接口
 * @author CongcaiYu
 */
public interface ResponseResolver {

    /**
     * 解析响应帧接口
     *
     * @param inputParams 响应帧输入参数
     * @return 响应帧输出参数
     */
    AbstractResOutputParams resolveRes(AbstractResInputParams inputParams);
}
