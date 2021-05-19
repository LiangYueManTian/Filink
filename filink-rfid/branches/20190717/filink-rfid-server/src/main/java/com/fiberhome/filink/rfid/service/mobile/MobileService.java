package com.fiberhome.filink.rfid.service.mobile;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfid.bean.facility.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Qing on 2019/6/6.
 * 与移动端交互的service
 */
public interface MobileService {

    /**
     * 上传设施标签信息
     *
     * @param uploadDto 标签信息
     */
    Result uploadFacilityInfo(FacilityUploadDto uploadDto);

    /**
     * 条件查询设施标签信息
     *
     * @param queryBean 查询条件
     * @return 设施标签信息
     */
    Result queryFacilityINfo(FacilityQueryBean queryBean);

    /**
     * 上传设施实体信息
     *
     * @param uploadDto 实体信息
     */
    Result uploadDeviceInfo(DeviceUploadDto uploadDto);

    /**
     * 条件查询设施实体信息
     *
     * @param queryBean 查询条件
     * @return 设施实体信息
     */
    DeviceEntity queryDeviceInfo(DeviceQueryBean queryBean);

    /**
     * 上传设施模板信息
     *
     * @param deviceEntity 设施模板信息
     */
    Result uploadDeviceTemplate(DeviceEntity deviceEntity);

    /**
     * 查询所有模板信息
     *
     * @return 模板信息
     */
    List<DeviceEntity> queryAllTemplate();

    /**
     * 上传设施业务信息
     *
     * @param uploadDto 设施业务信息
     */
    Result uploadFacilityBusInfo(FacilityBusUploadDto uploadDto);

    /**
     * 查询设施业务信息
     *
     * @param deviceId   设施id
     * @param deviceType 设施类型
     * @return 设施业务信息
     */
    Result queryBusInfoByDeviceId(String deviceId, String deviceType);


    /**
     * 设施标签信息变更（针对标签损坏的场景，信息不变只更改ID）
     *
     * @param oldLabel   旧标签
     * @param newLabel   新标签
     * @param deviceType 设施类型（1-箱架 2-盘 3-端口）
     * @return 上传结果
     */
    Result changeFacilityLabel(String oldLabel, String newLabel, Integer deviceType);

    /**
     * 删除device Id
     *
     * @param deviceId
     * @return Result
     */
    Result deleteDeviceEntity(String deviceId);
}
