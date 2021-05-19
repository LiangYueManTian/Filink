package com.fiberhome.filink.systemcommons.controller;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.systemcommons.bean.PlatformAppInfo;
import com.fiberhome.filink.systemcommons.service.PlatformAppInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 *  设备平台APP/产品信息前端控制器
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-05-05
 */
@RestController
@RequestMapping("/platformApp")
public class PlatformAppInfoController {

    @Autowired
    private PlatformAppInfoService appInfoService;
    /**
     * 根据应用/产品 ID获取应用/产品信息
     * @param appId 应用/产品 ID
     * @return 应用/产品信息
     */
    @GetMapping("/findPlatformAppInfoByAppId/{appId}")
    public PlatformAppInfo findPlatformAppInfoByAppId(@PathVariable(value = "appId") String appId) {
        if (StringUtils.isEmpty(appId)) {
            return null;
        }
        return appInfoService.findPlatformAppInfoByAppId(appId);
    }

    /**
     *根据平台类型获取应用/产品信息
     * @param platformType 平台类型
     * @return 应用/产品信息Map
     */
    @GetMapping("/findPlatformAppInfoMapByType/{platformType}")
    public Map<String, PlatformAppInfo> findPlatformAppInfoMapByType(@PathVariable(value = "platformType") Integer platformType){
        if (platformType == null) {
            return null;
        }
        return appInfoService.findPlatformAppInfoMapByType(platformType);
    }

    /**
     * 获取所有应用/产品信息
     * @return 应用/产品信息Map
     */
    @GetMapping("/findPlatformAppInfoAll")
    public Result findPlatformAppInfoAll(){
        return appInfoService.findPlatformAppInfoAll();
    }
}
