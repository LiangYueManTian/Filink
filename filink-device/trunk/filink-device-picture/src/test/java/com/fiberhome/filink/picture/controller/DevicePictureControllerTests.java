package com.fiberhome.filink.picture.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.picture.bean.PicRelationInfo;
import com.fiberhome.filink.picture.req.BatchUploadInfo;
import com.fiberhome.filink.picture.req.BatchUploadPic;
import com.fiberhome.filink.picture.req.BatchUploadPicReq;
import com.fiberhome.filink.picture.req.DevicePicReq;
import com.fiberhome.filink.picture.req.app.LivePicReq;
import com.fiberhome.filink.picture.service.PicRelationInfoService;
import com.fiberhome.filink.picture.constant.PicRelationConstants;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.picture.utils.PicRelationResultCode;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * deleteImageByDeviceIds
     */
    @Test
    public void deleteImageByDeviceIds(){
        Set<String> deviceIds = new HashSet<>();
        //预计执行方法
        new Expectations() {
            {
                result = picRelationInfoService.updateImageIsDeletedByDeviceIds(deviceIds, PicRelationConstants.IS_DELETED_1);
            }
        };
        //判断是否正确
        Result result = picRelationInfoController.deleteImageByDeviceIds(deviceIds);
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * recoverImageByDeviceIds
     */
    @Test
    public void recoverImageByDeviceIds(){
        Set<String> deviceIds = new HashSet<>();
        //预计执行方法
        new Expectations() {
            {
                result = picRelationInfoService.updateImageIsDeletedByDeviceIds(deviceIds, PicRelationConstants.IS_DELETED_0);
            }
        };
        //判断是否正确
        Result result = picRelationInfoController.recoverImageByDeviceIds(deviceIds);
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * uploadImagesForApp
     */
    @Test
    public void uploadImagesForApp(){
        BatchUploadPicReq batchUploadPicReq = new BatchUploadPicReq();

        List<BatchUploadInfo> batchUploadInfoList = new ArrayList<>();
        BatchUploadInfo batchUploadInfo = new BatchUploadInfo();
        batchUploadInfo.setPicName("s.png");
        batchUploadInfoList.add(batchUploadInfo);
        batchUploadPicReq.setBatchUploadPics(JSONArray.parseArray(JSON.toJSONString(batchUploadInfoList)).toJSONString());

        List<MultipartFile> pics = new ArrayList<>();
        MultipartFile file = new MockMultipartFile(
                "s.png",
                "s.png",
                ContentType.APPLICATION_OCTET_STREAM.toString(),
                new byte[23]
        );
        pics.add(file);
        batchUploadPicReq.setPics(pics);

        //预计执行方法
        new Expectations() {
            {
                try {
                    result = picRelationInfoService.uploadImages((List<BatchUploadPic>) any);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        //判断是否正确
        Result result = null;
        try {
            result = picRelationInfoController.uploadImagesForApp(batchUploadPicReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);

        //判断是否正确
        Result result1 = null;
        try {
            result1 = picRelationInfoController.uploadImagesForApp(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue(result1.getCode() == PicRelationConstants.FAIL);

        MultipartFile file2 = new MockMultipartFile(
                "s.png",
                "s.png",
                ContentType.APPLICATION_OCTET_STREAM.toString(),
                new byte[200000]
        );
        pics.add(file2);
        batchUploadPicReq.setPics(pics);

        //判断是否正确
        Result result4 = null;
        try {
            result4 = picRelationInfoController.uploadImagesForApp(batchUploadPicReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue(result4.getCode() == PicRelationResultCode.UPLOAD_FILE_OVER_THE_MAX_SIZE);

        //判断是否正确
        Result result2 = null;
        batchUploadPicReq.setBatchUploadPics("");
        try {
            result2 = picRelationInfoController.uploadImagesForApp(batchUploadPicReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue(result2.getCode() == PicRelationConstants.FAIL);


        //判断是否正确
        Result result3 = null;
        batchUploadPicReq.setBatchUploadPics(JSONArray.parseArray(JSON.toJSONString(batchUploadInfoList)).toJSONString());
        batchUploadPicReq.setPics(null);
        try {
            result3 = picRelationInfoController.uploadImagesForApp(batchUploadPicReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue(result3.getCode() == PicRelationConstants.FAIL);
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
     * getPicUrlByAlarmId
     */
    @Test
    public void getPicUrlByAlarmId() {
        //预计执行方法
        new Expectations() {
            {
                result = picRelationInfoService.getPicUrlByResource(PicRelationConstants.PIC_RESOURCE_1,"dfsafdsafdaf",null,new ArrayList<>());
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
                result = picRelationInfoService.getPicUrlByResource(PicRelationConstants.PIC_RESOURCE_2,"dfsafdsafdaf",null,new ArrayList<>());
            }
        };
        //判断是否正确
        Result result = picRelationInfoController.getPicUrlByProcId("dfsafdsafdaf");
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * getPicUrlByProcIds
     */
    @Test
    public void getPicUrlByProcIds() {
        List<String> procIds = new ArrayList<>();
        procIds.add("dfsafdsafdaf");
        //预计执行方法
        new Expectations() {
            {
                result = picRelationInfoService.getPicUrlByResource(PicRelationConstants.PIC_RESOURCE_2,null,null,procIds);
            }
        };
        //判断是否正确
        Result result = picRelationInfoController.getPicUrlByProcIds(procIds);
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * getPicUrlByAlarmIds
     */
    @Test
    public void getPicUrlByAlarmIds() {
        List<String> alarmIds = new ArrayList<>();
        alarmIds.add("dfsafdsafdaf");
        //预计执行方法
        new Expectations() {
            {
                result = picRelationInfoService.getPicUrlByResource(PicRelationConstants.PIC_RESOURCE_1,null,null,alarmIds);
            }
        };
        //判断是否正确
        Result result = picRelationInfoController.getPicUrlByAlarmIds(alarmIds);
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
                result = picRelationInfoService.getPicUrlByResource(PicRelationConstants.PIC_RESOURCE_2,"dfsafdsafdaf","fdsagfgfdsgfdsfgf",new ArrayList<>());
            }
        };
        //判断是否正确
        Result result = picRelationInfoController.getPicUrlByProcIdAndDeviceId("dfsafdsafdaf","fdsagfgfdsgfdsfgf");
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * getPicUrlByAlarmIdAndDeviceId
     */
    @Test
    public void getPicUrlByAlarmIdAndDeviceId() {
        //预计执行方法
        new Expectations() {
            {
                result = picRelationInfoService.getPicUrlByResource(PicRelationConstants.PIC_RESOURCE_2,"dfsafdsafdaf","fdsagfgfdsgfdsfgf",new ArrayList<>());
            }
        };
        //判断是否正确
        Result result = picRelationInfoController.getPicUrlByAlarmIdAndDeviceId("dfsafdsafdaf","fdsagfgfdsgfdsfgf");
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * getPicInfoByDeviceId
     */
    @Test
    public void getPicInfoByDeviceId() {
        DevicePicReq devicePicReq = new DevicePicReq();
        //预计执行方法
        new Expectations() {
            {
                result = picRelationInfoService.getPicInfoByDeviceId(devicePicReq);
            }
        };
        //判断是否正确
        Result result = picRelationInfoController.getPicInfoByDeviceId(devicePicReq);
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * getLivePicInfoByDeviceIdsForApp
     */
    @Test
    public void getLivePicInfoByDeviceIdsForApp() {
        LivePicReq livePicReq = new LivePicReq();
        //预计执行方法
        new Expectations() {
            {
                result = picRelationInfoService.getLivePicInfoByDeviceIdsForApp(livePicReq.getDeviceIds(),livePicReq.getPicNum());
            }
        };
        //判断是否正确
        Result result = picRelationInfoController.getLivePicInfoByDeviceIdsForApp(livePicReq);
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

}

