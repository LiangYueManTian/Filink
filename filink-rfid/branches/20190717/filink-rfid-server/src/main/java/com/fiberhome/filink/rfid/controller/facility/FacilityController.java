package com.fiberhome.filink.rfid.controller.facility;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.rfid.bean.facility.ChangeLabelBean;
import com.fiberhome.filink.rfid.bean.facility.DeviceEntity;
import com.fiberhome.filink.rfid.bean.facility.DeviceQueryBean;
import com.fiberhome.filink.rfid.bean.facility.DeviceUploadDto;
import com.fiberhome.filink.rfid.bean.facility.FacilityBusUploadDto;
import com.fiberhome.filink.rfid.bean.facility.FacilityQueryBean;
import com.fiberhome.filink.rfid.bean.facility.FacilityUploadDto;
import com.fiberhome.filink.rfid.service.mobile.MobileService;
import com.fiberhome.filink.rfid.service.template.TemplateService;
import com.google.common.collect.Lists;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 设施标签信息controller
 * </p>
 *
 * @author yqhu103
 * @since 2019-06-06
 */
@RestController
@RequestMapping("/facility")
public class FacilityController {

    /**
     * 移动端交互service
     */
    @Autowired
    private MobileService mobileService;

    /**
     * templateService
     */
    @Autowired
    private TemplateService templateService;

    /**
     * 请求设施标签信息
     *
     * @param queryBean 查询条件
     * @return 标签信息
     */
    @PostMapping("/queryFacilityInfo")
    public Result queryFacilityInfo(@RequestBody FacilityQueryBean queryBean) {
        return mobileService.queryFacilityINfo(queryBean);
    }

    /**
     * 设施标签信息上传
     *
     * @param uploadDto 设施标签信息
     * @return 上传结果
     */

    @PostMapping("/uploadFacilityInfo")
    public Result uploadFacilityInfo(@RequestBody FacilityUploadDto uploadDto) {
        return mobileService.uploadFacilityInfo(uploadDto);
    }

    /**
     * 设施标签信息变更（针对标签损坏的场景，信息不变只更改ID）
     *
     * @param bean 变更bean
     * @return 上传结果
     */

    @PostMapping("/changeFacilityLabel")
    public Result changeFacilityLabel(@RequestBody ChangeLabelBean bean) {
        return mobileService.changeFacilityLabel(bean.getOldLabel(), bean.getNewLabel(), bean.getDeviceType());
    }

    /**
     * 请求设施实体信息
     *
     * @param queryBean 查询条件
     * @return 标签信息
     */
    @PostMapping("/queryDeviceInfo")
    public Result queryDeviceInfo(@RequestBody DeviceQueryBean queryBean) {
        return ResultUtils.success(mobileService.queryDeviceInfo(queryBean));
    }

    /**
     * 设施实体信息上传
     *
     * @param uploadDto 设施实体信息
     * @return 上传结果
     */
    @PostMapping("/uploadDeviceInfo")
    public Result uploadDeviceInfo(@RequestBody DeviceUploadDto uploadDto) {
        return mobileService.uploadDeviceInfo(uploadDto);
    }

    /**
     * 请求设施模板信息(全模板下载)
     *
     * @return 标签信息
     */
    @PostMapping("/queryAllDeviceTemp")
    public Result queryAllDeviceTemp() {
        return ResultUtils.success(mobileService.queryAllTemplate());
    }

    /**
     * 设施模板信息上传
     *
     * @param deviceEntity 设施模板信息
     * @return 上传结果
     */
    @PostMapping("/uploadDeviceTemp")
    public Result uploadDeviceTemp(@RequestBody DeviceEntity deviceEntity) {
        return mobileService.uploadDeviceTemplate(deviceEntity);
    }

    /**
     * 设施业务信息上传
     *
     * @param uploadDto 设施业务信息
     * @return 上传结果
     */

    @PostMapping("/uploadFacilityBusInfo")
    public Result uploadFacilityBusInfo(@RequestBody FacilityBusUploadDto uploadDto) {
        return mobileService.uploadFacilityBusInfo(uploadDto);
    }


    /**
     * 查询设施业务信息
     *
     * @param deviceId   设施id
     * @param deviceType 设施类型
     * @return 设施业务信息
     */

    @PostMapping("/queryFacilityBusInfoById")
    public Result queryFacilityBusInfoById(@Param("deviceId") String deviceId, @Param("deviceType") String deviceType) {
        // 数据权限 验证
        Boolean rfIdDataAuthInfo = templateService.getRfIdDataAuthInfo(deviceId);
        if (!rfIdDataAuthInfo) {
            return ResultUtils.success(Lists.newArrayList());
        }
        return mobileService.queryBusInfoByDeviceId(deviceId, deviceType);
    }


    /**
     * 删除设施--回收标签
     *
     * @param deviceIds   设施id
     * @return 结果
     */

    @PostMapping("/recoverLabelWithDeviceId")
    public Result recoverLabelWithDeviceId(@RequestBody List<String> deviceIds) {
        return mobileService.deleteDeviceEntity(deviceIds.get(0));
    }



}
