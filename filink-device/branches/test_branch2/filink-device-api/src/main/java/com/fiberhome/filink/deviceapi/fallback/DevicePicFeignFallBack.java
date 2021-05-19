package com.fiberhome.filink.deviceapi.fallback;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.api.DevicePicFeign;
import com.fiberhome.filink.deviceapi.bean.DevicePicReq;
import com.fiberhome.filink.deviceapi.bean.PicRelationInfo;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 设施图片feign熔断类
 * @author chaofanrong
 */
@Log4j
@Component
public class DevicePicFeignFallBack implements DevicePicFeign {

    /**
     * 批量删除图片列表
     *
     * @param picRelationInfos 图片列表
     * @return 操作结果saveImagesDataForFeign
     */
    @Override
    public Result deleteImageIsDeletedByIds(List<PicRelationInfo> picRelationInfos) {
        log.info("save device pic failed>>>>>>");
        return null;
    }

    /**
     * 批量恢复图片列表
     *
     * @param picRelationInfos 图片列表
     * @return 操作结果
     */
    @Override
    public Result recoverImageIsDeletedByIds(List<PicRelationInfo> picRelationInfos) {
        log.info("save device pic failed>>>>>>");
        return null;
    }

    /**
     * 批量保存图片记录
     *
     * @param picRelationInfos 图片列表
     * @return 操作结果
     */
    @Override
    public Result saveImagesDataForFeign(List<PicRelationInfo> picRelationInfos) {
        log.info("upload device pic failed>>>>>>");
        return null;
    }

    /**
     * 根据工单id获取图片信息
     *
     * @param procId 工单id
     * @return Result
     */
    @Override
    public Result getPicUrlByProcId(String procId) {
        log.info("get process pic failed>>>>>>");
        return null;
    }

    /**
     * 根据告警id获取图片信息
     *
     * @param alarmId 工单id
     * @return Result
     */
    @Override
    public Result getPicUrlByAlarmId(String alarmId) {
        log.info("get alarm pic failed>>>>>>");
        return null;
    }

    /**
     * 根据工单ids获取图片url
     *
     * @param procIds 工单ids
     * @return Result
     */
    @Override
    public Result getPicUrlByProcIds(List<String> procIds) {
        log.info("get process pic failed>>>>>>");
        return null;
    }

    /**
     * 根据告警ids获取图片url
     *
     * @param alarmIds 告警ids
     * @return Result
     */
    @Override
    public Result getPicUrlByAlarmIds(List<String> alarmIds) {
        log.info("get alarm pic failed>>>>>>");
        return null;
    }

    /**
     * 根据告警id及设施id获取图片url
     *
     * @param alarmId 告警id
     * @param deviceId 设施id
     *
     * @return Result
     */
    @Override
    public Result getPicUrlByAlarmIdAndDeviceId(String alarmId, String deviceId) {
        log.info("get alarm and device pic failed>>>>>>");
        return null;
    }

    /**
     * 根据工单id及设施id获取图片url
     *
     * @param procId 工单id
     * @param deviceId 设施id
     *
     * @return Result
     */
    @Override
    public Result getPicUrlByProcIdAndDeviceId(String procId, String deviceId) {
        log.info("get proc and device pic failed>>>>>>");
        return null;
    }

    /**
     * 根据设施ids删除图片列表
     *
     * @param deviceIds 设施ids
     * @return Result
     */
    @Override
    public Result deleteImageByDeviceIds(Set<String> deviceIds) {
        log.info("delete device pic failed>>>>>>");
        return null;
    }

    /**
     * 根据设施ids恢复图片列表
     *
     * @param deviceIds 设施ids
     * @return Result
     */
    @Override
    public Result recoverImageByDeviceIds(Set<String> deviceIds) {
        log.info("recover device pic failed>>>>>>");
        return null;
    }

    /**
     * 根据设施id及来源类型获取图片url
     *
     * @param devicePicReq 图片请求
     *
     * @return Result
     */
    @Override
    public Result getPicInfoByDeviceId(DevicePicReq devicePicReq) {
        log.info("get device pic failed>>>>>>");
        return null;
    }
}
