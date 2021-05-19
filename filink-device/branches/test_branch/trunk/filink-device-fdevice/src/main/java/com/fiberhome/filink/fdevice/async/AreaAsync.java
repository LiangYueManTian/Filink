package com.fiberhome.filink.fdevice.async;

import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.dto.AreaInfoForeignDto;
import com.fiberhome.filink.fdevice.service.area.impl.AreaInfoServiceImpl;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 区域异步线程类
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-22
 */
@Slf4j
@Component
public class AreaAsync {
    /**
     * 注入区域实现类
     */
    @Autowired
    private AreaInfoServiceImpl areaInfoService;
    /**
     * 自动注入设施service
     */
    @Autowired
    private DeviceInfoService deviceInfoService;

    /**
     * 更新区域成功后的异步方法
     * 更新区域缓存、更新设施缓存、发送消息到用户服务
     *
     * @param areaInfo 区域信息
     */
    @Async
    public void afterUpdateAreaSuccess(AreaInfo areaInfo) {
        String areaId = areaInfo.getAreaId();
        areaInfoService.updateRedisArea(changeAreaInfoToDto(areaInfo));
        deviceInfoService.refreshDeviceAreaRedis(areaId);
        areaInfoService.sendUpdateUserInfo();
    }

    /**
     * 删除区域成功后的异步方法
     * 更新区域缓存、发送消息到用户服务
     *
     * @param areaIds
     */
    @Async
    public void afterDeleteAreaSuccess(List<String> areaIds) {
        //发送消息给用户服务通知区域信息已经被删除
        areaInfoService.sendUpdateUserInfo();
        //根据id删除缓存中的区域信息
        areaInfoService.deleteRedisArea(areaIds);

    }

    /**
     * 新增区域后的异步方法
     * 更新区域缓存、发送消息到用户服务
     *
     * @param areaInfo
     */
    @Async
    public void afterAddAreaSuccess(AreaInfo areaInfo) {
        areaInfoService.updateRedisArea(changeAreaInfoToDto(areaInfo));
        Set<String> accountabilityUnit = areaInfo.getAccountabilityUnit();
        if (accountabilityUnit != null && accountabilityUnit.size() > 0) {
            areaInfoService.sendUpdateUserInfo();
        }
    }

    /**
     * 将Area对象转换成Dto对象
     *
     * @param areaInfo
     * @return
     */
    private AreaInfoForeignDto changeAreaInfoToDto(AreaInfo areaInfo) {
        AreaInfoForeignDto areaInfoForeignDto = new AreaInfoForeignDto();
        areaInfoForeignDto.setAreaId(areaInfo.getAreaId());
        areaInfoForeignDto.setAreaName(areaInfo.getAreaName());
        areaInfoForeignDto.setParentAreaId(areaInfo.getParentId());
        areaInfoForeignDto.setAreaLevel(areaInfo.getLevel());
        areaInfoForeignDto.setCreateTime(areaInfo.getCreateTime());
        return areaInfoForeignDto;
    }
}
