package com.fiberhome.filink.picture.controller;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.picture.bean.PicRelationInfo;
import com.fiberhome.filink.picture.req.BatchUploadPicReq;
import com.fiberhome.filink.picture.req.DevicePicReq;
import com.fiberhome.filink.picture.service.PicRelationInfoService;
import com.fiberhome.filink.picture.constant.PicRelationConstants;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.List;

@RunWith(JMockit.class)
public class DevicePictureControllerTests {


    /**
     * 初始化
     */
    @Before
    public void setUp() {}

    /**
     * 测试对象 PicRelationInfoController
     */
    @Tested
    private PicRelationInfoController picRelationInfoController;

    /**
     * Mock PicRelationInfoService
     */
    @Injectable
    private PicRelationInfoService picRelationInfoService;

    /**
     * imageListByPage
     */
    @Test
    public void imageListByPage(){
        QueryCondition<DevicePicReq> queryCondition = new QueryCondition<>();
        //预计执行方法
        new Expectations() {
            {
                result = picRelationInfoService.imageListByPage((QueryCondition) any);
            }
        };
        //判断是否正确
        Result result = picRelationInfoController.imageListByPage(queryCondition);
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * batchDownLoadImages
     */
    @Test
    public void batchDownLoadImages() throws Exception {
        ExportDto exportDto = new ExportDto();
        //预计执行方法
        new Expectations() {
            {
                result = picRelationInfoService.batchDownLoadImages((ExportDto) any);
            }
        };
        //判断是否正确
        Result result = picRelationInfoController.batchDownLoadImages(exportDto);
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * deleteImageIsDeletedByIds
     */
    @Test
    public void deleteImageIsDeletedByIds(){
        List<PicRelationInfo> picRelationInfos = new ArrayList<>();
        //预计执行方法
        new Expectations() {
            {
                result = picRelationInfoService.updateImageIsDeletedByIds((List<PicRelationInfo>) any,"1");
            }
        };
        //判断是否正确
        Result result = picRelationInfoController.deleteImageIsDeletedByIds(picRelationInfos);
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * recoverImageIsDeletedByIds
     */
    @Test
    public void recoverImageIsDeletedByIds(){
//        List<PicRelationInfo> picRelationInfos = new ArrayList<>();
//        //预计执行方法
//        new Expectations() {
//            {
//                result = picRelationInfoService.updateImageIsDeletedByIds((List<PicRelationInfo>) any,"0");
//            }
//        };
        //判断是否正确
//        Result result = picRelationInfoController.recoverImageIsDeletedByIds(picRelationInfos);
//        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * saveImagesDataForFeign
     */
    @Test
    public void saveImagesDataForFeign(){
        List<PicRelationInfo> picRelationInfos = new ArrayList<>();
        //预计执行方法
        new Expectations() {
            {
                result = picRelationInfoService.saveImagesData((List<PicRelationInfo>) any);
            }
        };
        //判断是否正确
        Result result = picRelationInfoController.saveImagesDataForFeign(picRelationInfos);
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * uploadProcImagesForApp
     */
    @Test
    public void uploadProcImagesForApp() throws Exception {
        List<BatchUploadPicReq> batchUploadPicReqs = new ArrayList<>();
        BatchUploadPicReq batchUploadPicReq = new BatchUploadPicReq();
        batchUploadPicReqs.add(batchUploadPicReq);

        //预计执行方法
        new Expectations() {
            {
//                result = picRelationInfoService.uploadImages((List<BatchUploadPicReq>) any);
            }
        };
        //判断是否正确
//        Result result = picRelationInfoController.uploadProcImagesForApp(batchUploadPicReqs);
//        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * getPicUrlByAlarmId
     */
    @Test
    public void getPicUrlByAlarmId() {
        //预计执行方法
        new Expectations() {
            {
//                result = picRelationInfoService.getPicUrlByResourceId(anyString,anyString,null);
            }
        };
        //判断是否正确
        Result result = picRelationInfoController.getPicUrlByAlarmId("dfsafdsafdaf");
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * getPicUrlByProcId
     */
    @Test
    public void getPicUrlByProcId() {
        //预计执行方法
        new Expectations() {
            {
//                result = picRelationInfoService.getPicUrlByResourceId(anyString,anyString,null);
            }
        };
        //判断是否正确
        Result result = picRelationInfoController.getPicUrlByProcId("dfsafdsafdaf");
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * getPicUrlByProcIdAndDeviceId
     */
    @Test
    public void getPicUrlByProcIdAndDeviceId() {
        //预计执行方法
        new Expectations() {
            {
//                result = picRelationInfoService.getPicUrlByResourceId(anyString,anyString,anyString);
            }
        };
        //判断是否正确
        Result result = picRelationInfoController.getPicUrlByProcIdAndDeviceId("dfsafdsafdaf","fdsagfgfdsgfdsfgf");
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }
}

