package com.fiberhome.filink.fdevice.dto;

import lombok.Data;

import java.util.List;

/**
 * <p>
 *     首页大数据设施和区域信息
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-06-24
 */
@Data
public class HomeDeviceAreaInfo {
    /**
     * 区域设施数量信息
     */
    private List<AreaDeviceNum> areaDeviceNumList;
    /**
     * 设施信息
     */
    private List<HomeDeviceInfoDto> homeDeviceInfoDtoList;
    /**
     * 数据标识1大数据0正常数据
     */
    private String hugeData;
    /**
     * 首次查询单个区域设施数量是否超越限制
     * true是 false否
     */
    private boolean exceedLimit;

}
