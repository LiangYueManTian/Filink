package com.fiberhome.filink.commonstation.entity.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * 请求实体类
 * @author CongcaiYu
 */
@Data
public class FiLinkRequest implements Serializable {

    /**
     * 公共头参数对象
     */
    private FiLinkCommonHeader fiLinkCommonHeader;
}
