package com.fiberhome.filink.picture.controller;


import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.picture.bean.PicRelationInfo;
import com.fiberhome.filink.picture.constant.PicRelationConstants;
import com.fiberhome.filink.picture.constant.PicRelationI18nConstants;
import com.fiberhome.filink.picture.req.BatchUploadInfo;
import com.fiberhome.filink.picture.req.BatchUploadPic;
import com.fiberhome.filink.picture.req.BatchUploadPicReq;
import com.fiberhome.filink.picture.req.DevicePicReq;
import com.fiberhome.filink.picture.req.app.LivePicReq;
import com.fiberhome.filink.picture.service.PicRelationInfoService;
import com.fiberhome.filink.picture.utils.PicRelationResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * <p>
 * 图片关系表 前端控制器
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-03-13
 */
@RestController
@RequestMapping("/picRelationInfo")
public class PicRelationInfoController {

    @Autowired
    private PicRelationInfoService picRelationInfoService;

    /**
     * todo 图片测试
     * @return
     */
    @GetMapping("/getPicTime")
    public Object getPicTime(){
        return PerformanceTest.picMap;
    }

    /**
     * 图片测试
     * @return
     */
    @GetMapping("/resetPicTime")
    public String resetPicTime(){
        PerformanceTest.picMap = new HashMap<>();
        return "success";
    }

    /**
     * 分页查询设施图片列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @PostMapping("/imageListByPage")
    public Result imageListByPage(@RequestBody QueryCondition<DevicePicReq> queryCondition) {
        return picRelationInfoService.imageListByPage(queryCondition);
    }

    /**
     * 批量下载图片列表
     *
     * @param exportDto 批量导出请求类
     * @return Result
     */
    @PostMapping("/batchDownLoadImages")
    public Result batchDownLoadImages(@RequestBody ExportDto exportDto) throws Exception {
        return picRelationInfoService.batchDownLoadImages(exportDto);
    }

    /**
     * 批量删除图片列表
     *
     * @param picRelationInfos 图片列表
     * @return Result
     */
    @PostMapping("/deleteImageIsDeletedByIds")
    public Result deleteImageIsDeletedByIds(@RequestBody List<PicRelationInfo> picRelationInfos) {
        return picRelationInfoService.updateImageIsDeletedByIds(picRelationInfos, PicRelationConstants.IS_DELETED_1);
    }

    /**
     * 根据设施ids删除图片列表
     *
     * @param deviceIds 设施ids
     * @return Result
     */
    @PostMapping("/deleteImageByDeviceIds")
    public Result deleteImageByDeviceIds(@RequestBody Set<String> deviceIds) {
        return picRelationInfoService.updateImageIsDeletedByDeviceIds(deviceIds, PicRelationConstants.IS_DELETED_1);
    }

    /**
     * 根据设施ids恢复图片列表
     *
     * @param deviceIds 设施ids
     * @return Result
     */
    @PostMapping("/recoverImageByDeviceIds")
    public Result recoverImageByDeviceIds(@RequestBody Set<String> deviceIds) {
        return picRelationInfoService.updateImageIsDeletedByDeviceIds(deviceIds, PicRelationConstants.IS_DELETED_0);
    }

    /**
     * app批量上传图片列表
     *
     * @param batchUploadPicReq 批量上传图片请求
     * @return Result
     */
    @PostMapping("/uploadImagesForApp")
    public Result uploadImagesForApp(BatchUploadPicReq batchUploadPicReq) throws Exception{
        if (ObjectUtils.isEmpty(batchUploadPicReq)){
            return ResultUtils.warn(PicRelationConstants.FAIL, I18nUtils.getSystemString(PicRelationI18nConstants.PIC_PARAM_ERROR));
        }
        String str = batchUploadPicReq.getBatchUploadPics();
        if (StringUtils.isEmpty(str)){
            return ResultUtils.warn(PicRelationConstants.FAIL, I18nUtils.getSystemString(PicRelationI18nConstants.PIC_PARAM_ERROR));
        }
        List<BatchUploadInfo> batchUploadInfos = JSON.parseArray(str, BatchUploadInfo.class);
        //将图片名字设为key
        Map<String,MultipartFile> fileHashMap = new HashMap<>(10);
        if (ObjectUtils.isEmpty(batchUploadPicReq.getPics())){
            return ResultUtils.warn(PicRelationConstants.FAIL, I18nUtils.getSystemString(PicRelationI18nConstants.PIC_PARAM_ERROR));
        }
        for (MultipartFile multipartFile : batchUploadPicReq.getPics()){
            fileHashMap.put(multipartFile.getOriginalFilename(),multipartFile);
        }
        //组装图片及关联信息
        List<BatchUploadPic> batchUploadPics = new ArrayList<>();
        for (BatchUploadInfo batchUploadInfo : batchUploadInfos){
            if (ObjectUtils.isEmpty(batchUploadInfo)){
                return ResultUtils.warn(PicRelationConstants.FAIL, I18nUtils.getSystemString(PicRelationI18nConstants.PIC_PARAM_ERROR));
            }
            BatchUploadPic batchUploadPic = new BatchUploadPic();
            BeanUtils.copyProperties(batchUploadInfo,batchUploadPic);
            batchUploadPic.setPic(fileHashMap.get(batchUploadInfo.getPicName()));
            batchUploadPics.add(batchUploadPic);
        }
        //最大张数限制
        if (ObjectUtils.isEmpty(batchUploadPicReq.getPics()) && PicRelationConstants.PIC_UPLOAD_MAX_NUM < batchUploadPicReq.getPics().size()){
            return ResultUtils.warn(PicRelationResultCode.UPLOAD_FILE_OVER_THE_MAX_NUM, I18nUtils.getSystemString(PicRelationI18nConstants.UPLOAD_FILE_OVER_THE_MAX_NUM));
        }

        //每张图片最大100kb
        if (!ObjectUtils.isEmpty(batchUploadPicReq.getPics())){
            for (MultipartFile multipartFile : batchUploadPicReq.getPics()){
                if (PicRelationConstants.PIC_UPLOAD_MAX_SIZE < multipartFile.getSize()){
                    return ResultUtils.warn(PicRelationResultCode.UPLOAD_FILE_OVER_THE_MAX_SIZE, I18nUtils.getSystemString(PicRelationI18nConstants.UPLOAD_FILE_OVER_THE_MAX_SIZE));
                }
            }
        }
        return picRelationInfoService.uploadImages(batchUploadPics);
    }

    /**
     * feign批量上传图片列表
     *
     * @param picRelationInfos
     * @return Result
     */
    @PostMapping("/saveImagesDataForFeign")
    public Result saveImagesDataForFeign(@RequestBody List<PicRelationInfo> picRelationInfos) {
        return picRelationInfoService.saveImagesData(picRelationInfos);
    }

    /**
     * 根据告警id获取图片url
     *
     * @param alarmId 告警id
     * @return Result
     */
    @GetMapping("/getPicUrlByAlarmId/{alarmId}")
    public Result getPicUrlByAlarmId(@PathVariable("alarmId") String alarmId) {
        return picRelationInfoService.getPicUrlByResource(PicRelationConstants.PIC_RESOURCE_1,alarmId,null,new ArrayList<>());
    }

    /**
     * 根据工单id获取图片url
     *
     * @param procId 工单id
     * @return Result
     */
    @GetMapping("/getPicUrlByProcId/{procId}")
    public Result getPicUrlByProcId(@PathVariable("procId")  String procId) {
        return picRelationInfoService.getPicUrlByResource(PicRelationConstants.PIC_RESOURCE_2,procId,null,new ArrayList<>());
    }

    /**
     * 根据工单ids获取图片url
     *
     * @param procIds 工单ids
     * @return Result
     */
    @PostMapping("/getPicUrlByProcIds")
    public Result getPicUrlByProcIds(@RequestBody List<String> procIds) {
        return picRelationInfoService.getPicUrlByResource(PicRelationConstants.PIC_RESOURCE_2,null,null,procIds);
    }

    /**
     * 根据告警ids获取图片url
     *
     * @param alarmIds 告警ids
     * @return Result
     */
    @PostMapping("/getPicUrlByAlarmIds")
    public Result getPicUrlByAlarmIds(@RequestBody List<String> alarmIds) {
        return picRelationInfoService.getPicUrlByResource(PicRelationConstants.PIC_RESOURCE_1,null,null,alarmIds);
    }


    /**
     * 根据工单id及设施id获取图片url
     *
     * @param procId 工单id
     * @param deviceId 设施id
     *
     * @return Result
     */
    @GetMapping("/getPicUrlByProcIdAndDeviceId/{procId}/{deviceId}")
    public Result getPicUrlByProcIdAndDeviceId(@PathVariable("procId") String procId,@PathVariable("deviceId") String deviceId) {
        return picRelationInfoService.getPicUrlByResource(PicRelationConstants.PIC_RESOURCE_2,procId,deviceId,new ArrayList<>());
    }

    /**
     * 根据告警id及设施id获取图片url
     *
     * @param alarmId 告警id
     * @param deviceId 设施id
     *
     * @return Result
     */
    @GetMapping("/getPicUrlByAlarmIdAndDeviceId/{alarmId}/{deviceId}")
    public Result getPicUrlByAlarmIdAndDeviceId(@PathVariable("alarmId") String alarmId,@PathVariable("deviceId") String deviceId) {
        return picRelationInfoService.getPicUrlByResource(PicRelationConstants.PIC_RESOURCE_2,alarmId,deviceId,new ArrayList<>());
    }

    /**
     * 根据设施id及来源类型获取图片url
     *
     * @param devicePicReq 图片请求
     *
     * @return Result
     */
    @PostMapping("/getPicInfoByDeviceId")
    public Result getPicInfoByDeviceId(@RequestBody DevicePicReq devicePicReq) {
        return picRelationInfoService.getPicInfoByDeviceId(devicePicReq);
    }

    /**
     * app根据设施ids获取实景图
     *
     * @param livePicReq 实景图请求
     *
     * @return Result
     */
    @PostMapping("/getLivePicInfoByDeviceIdsForApp")
    public Result getLivePicInfoByDeviceIdsForApp(@RequestBody LivePicReq livePicReq) {
        return picRelationInfoService.getLivePicInfoByDeviceIdsForApp(livePicReq.getDeviceIds(),livePicReq.getPicNum());
    }
}
