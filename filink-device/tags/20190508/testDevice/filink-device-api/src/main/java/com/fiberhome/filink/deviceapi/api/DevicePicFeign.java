package com.fiberhome.filink.deviceapi.api;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.bean.DevicePicReq;
import com.fiberhome.filink.deviceapi.bean.PicRelationInfo;
import com.fiberhome.filink.deviceapi.fallback.DevicePicFeignFallBack;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

/**
 * 设施图片feign
 * @author chaofanrong
 */
@FeignClient(name = "filink-device-server", fallback = DevicePicFeignFallBack.class)
public interface DevicePicFeign {

    /**
     * 批量删除图片列表
     *
     * @param picRelationInfos 图片列表
     * @return Result
     */
    @PostMapping("/picRelationInfo/deleteImageIsDeletedByIds")
    Result deleteImageIsDeletedByIds(@RequestBody List<PicRelationInfo> picRelationInfos);


    /**
     * 批量恢复图片列表
     *
     * @param picRelationInfos 图片列表
     * @return Result
     */
    @PostMapping("/picRelationInfo/recoverImageIsDeletedByIds")
    Result recoverImageIsDeletedByIds(@RequestBody List<PicRelationInfo> picRelationInfos);

    /**
     * 批量上传告警图片列表
     *
     * @param picRelationInfos 图片列表
     * @return Result
     */
    @PostMapping("/picRelationInfo/saveImagesDataForFeign")
    Result saveImagesDataForFeign(@RequestBody List<PicRelationInfo> picRelationInfos);

    /**
     * 根据工单id获取图片信息
     *
     * @param procId 工单id
     * @return Result
     */
    @GetMapping("/picRelationInfo/getPicUrlByProcId/{procId}")
    Result getPicUrlByProcId(@PathVariable("procId") String procId);

    /**
     * 根据告警id获取图片信息
     *
     * @param alarmId 工单id
     * @return Result
     */
    @GetMapping("/picRelationInfo/getPicUrlByAlarmId/{alarmId}")
    Result getPicUrlByAlarmId(@PathVariable("alarmId") String alarmId);

    /**
     * 根据工单ids获取图片url
     *
     * @param procIds 工单ids
     * @return Result
     */
    @PostMapping("/picRelationInfo/getPicUrlByProcIds")
    Result getPicUrlByProcIds(@RequestBody List<String> procIds);

    /**
     * 根据告警ids获取图片url
     *
     * @param alarmIds 告警ids
     * @return Result
     */
    @PostMapping("/picRelationInfo/getPicUrlByAlarmIds")
    Result getPicUrlByAlarmIds(@RequestBody List<String> alarmIds);

    /**
     * 根据告警id及设施id获取图片url
     *
     * @param alarmId 告警id
     * @param deviceId 设施id
     *
     * @return Result
     */
    @GetMapping("/picRelationInfo/getPicUrlByAlarmIdAndDeviceId/{alarmId}/{deviceId}")
    Result getPicUrlByAlarmIdAndDeviceId(@PathVariable("alarmId") String alarmId,@PathVariable("deviceId") String deviceId);

    /**
     * 根据工单id及设施id获取图片url
     *
     * @param procId 工单id
     * @param deviceId 设施id
     *
     * @return Result
     */
    @GetMapping("/picRelationInfo/getPicUrlByProcIdAndDeviceId/{procId}/{deviceId}")
    Result getPicUrlByProcIdAndDeviceId(@PathVariable("procId") String procId,@PathVariable("deviceId") String deviceId);

    /**
     * 根据设施id删除图片列表
     *
     * @param deviceIds 设施ids
     * @return Result
     */
    @PostMapping("/picRelationInfo/deleteImageByDeviceIds")
    Result deleteImageByDeviceIds(@RequestBody Set<String> deviceIds);

    /**
     * 根据设施ids恢复图片列表
     *
     * @param deviceIds 设施id
     * @return Result
     */
    @PostMapping("/picRelationInfo/recoverImageByDeviceIds")
    Result recoverImageByDeviceIds(@RequestBody Set<String> deviceIds);

    /**
     * 根据设施id及来源类型获取图片url
     *
     * @param devicePicReq 图片请求
     *
     * @return Result
     */
    @PostMapping("/picRelationInfo/getPicInfoByDeviceId")
    Result getPicInfoByDeviceId(@RequestBody DevicePicReq devicePicReq);
}

