package com.fiberhome.filink.commonstation.entity.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * 响应实体类
 * @author CongcaiYu
 */
@Data
public class FiLinkResponse implements Serializable {

    /**
     * 公共头参数对象
     */
    private FiLinkCommonHeader fiLinkCommonHeader;

    /**
     * 命令成功
     */
    private String cmdOk;

}
