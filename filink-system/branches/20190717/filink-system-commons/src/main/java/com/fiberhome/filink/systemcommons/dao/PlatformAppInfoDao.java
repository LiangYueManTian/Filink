package com.fiberhome.filink.systemcommons.dao;

import com.fiberhome.filink.systemcommons.bean.PlatformAppInfo;
import com.fiberhome.filink.systemcommons.dto.PlatformAppInfoDto;

import java.util.List;

/**
 * <p>
 *  设备平台APP/产品信息Mapper 接口
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-05-05
 */
public interface PlatformAppInfoDao {
    /**
     * 根据应用/产品 ID获取应用/产品信息
     * @param appId 应用/产品 ID
     * @return 应用/产品信息
     */
    PlatformAppInfo findPlatformAppInfoByAppId(String appId);

    /**
     *根据平台类型获取应用/产品信息
     * @param platformType 平台类型
     * @return 应用/产品信息List
     */
    List<PlatformAppInfo> findPlatformAppInfoMapByType(Integer platformType);
    /**
     * 获取所有应用/产品信息
     * @return 应用/产品信息List
     */
    List<PlatformAppInfoDto> findPlatformAppInfoAll();
}
