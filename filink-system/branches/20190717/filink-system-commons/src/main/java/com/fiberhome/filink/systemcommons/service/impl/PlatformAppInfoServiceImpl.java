package com.fiberhome.filink.systemcommons.service.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.systemcommons.bean.PlatformAppInfo;
import com.fiberhome.filink.systemcommons.dao.PlatformAppInfoDao;
import com.fiberhome.filink.systemcommons.dto.PlatformAppInfoDto;
import com.fiberhome.filink.systemcommons.service.PlatformAppInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  设备平台APP/产品信息服务实现类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-05-05
 */
@Service
public class PlatformAppInfoServiceImpl implements PlatformAppInfoService {

    /**
     * 自动注入DAO
     */
    @Autowired
    private PlatformAppInfoDao appInfoDao;
    /**
     * 根据应用/产品 ID获取应用/产品信息
     *
     * @param appId 应用/产品 ID
     * @return 应用/产品信息
     */
    @Override
    public PlatformAppInfo findPlatformAppInfoByAppId(String appId) {
        return appInfoDao.findPlatformAppInfoByAppId(appId);
    }

    /**
     * 根据平台类型获取应用/产品信息
     *
     * @param platformType 平台类型
     * @return 应用/产品信息Map
     */
    @Override
    public Map<String, PlatformAppInfo> findPlatformAppInfoMapByType(Integer platformType) {
        List<PlatformAppInfo> appInfoList = appInfoDao.findPlatformAppInfoMapByType(platformType);
        Map<String, PlatformAppInfo> appInfoMap = new HashMap<>(16);
        if (!CollectionUtils.isEmpty(appInfoList)) {
            for (PlatformAppInfo appInfo : appInfoList) {
                appInfoMap.put(appInfo.getAppId(), appInfo);
            }
        }
        return appInfoMap;
    }

    /**
     * 获取所有应用/产品信息
     * @return 应用/产品信息List
     */
    @Override
    public Result findPlatformAppInfoAll() {
        List<PlatformAppInfoDto> appInfoList = appInfoDao.findPlatformAppInfoAll();
        if (CollectionUtils.isEmpty(appInfoList)) {
            appInfoList = new ArrayList<>();
        }
        return ResultUtils.success(appInfoList);
    }
}
