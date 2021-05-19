package com.fiberhome.filink.filinkoceanconnectserver.entity.profile;

import lombok.Data;

import java.util.Map;

/**
 * profile信息
 *
 * @author CongcaiYu
 */
@Data
public class Platform {

    /**
     * 下行map参数
     */
    private Map<String, ServiceBean> downMap;

    /**
     * 上行map参数
     */
    private Map<String, ServiceBean> uploadMap;

}
