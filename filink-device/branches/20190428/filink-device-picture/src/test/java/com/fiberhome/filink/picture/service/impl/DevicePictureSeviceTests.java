package com.fiberhome.filink.picture.service.impl;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.api.ExportFeign;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.ossapi.bean.ImageUploadBean;
import com.fiberhome.filink.ossapi.bean.ImageUrl;
import com.fiberhome.filink.picture.bean.PicRelationInfo;
import com.fiberhome.filink.picture.constant.PicRelationConstants;
import com.fiberhome.filink.picture.dao.relation.PicRelationInfoDao;
import com.fiberhome.filink.picture.req.DevicePicReq;
import com.fiberhome.filink.picture.resp.DevicePicResp;
import com.fiberhome.filink.picture.utils.HandleFile;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.UserFeign;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.*;

@RunWith(JMockit.class)
public class DevicePictureSeviceTests {

    /**
     * 测试对象 picRelationInfoServiceImpl
     */
    @Tested
    private PicRelationInfoServiceImpl picRelationInfoService;

    /**
     * 自动注入 PicRelationInfoDao
     */
    @Injectable
    private PicRelationInfoServiceImpl picRelationInfoServiceImpl;

    /**
     * 自动注入 PicRelationInfoDao
     */
    @Injectable
    private PicRelationInfoDao picRelationInfoDao;

    /**
     * 自动注入 HandleFile
     */
    @Injectable
    private HandleFile handleFile;

    /**
     * 自动注入 ExportFeign
     */
    @Injectable
    private ExportFeign exportFeign;

    /**
     * 自动注入 FdfsFeign
     */
    @Injectable
    private FdfsFeign fdfsFeign;

    /**
     * 自动注入 FdfsFeign
     */
    @Injectable
    private UserFeign userFeign;

    /**
     * 自动注入 LogProcess
     */
    @Injectable
    private LogProcess logProcess;

    /**
     * Mock RequestContextHolder
     */
    @Mocked
    private RequestContextHolder requestContextHolder;
    /**
     * Mock ServletRequestAttributes
     */
    @Mocked
    private ServletRequestAttributes servletRequestAttributes;

    /**
     * Mock I18nUtils
     */
    @Mocked
    private I18nUtils i18nUtils;

    /**
     * 自动注入 maxExportPicDataSize
     */
    @Injectable
    private Integer maxExportPicDataSize;

    /**
     * 自动注入 exportServerName
     */
    @Injectable
    private Integer exportServerName;

    /**
     * 初始化
     */
    @Before
    public void setUp() {
        /*PicRelationInfoServiceImpl picRelationInfoServiceImpl = new PicRelationInfoServiceImpl();
        ReflectionTestUtils.setField(picRelationInfoServiceImpl, "maxExportPicDataSize", 123);
        ReflectionTestUtils.setField(picRelationInfoServiceImpl, "exportServerName", "vcxzvzdsafd");*/
    }

    /**
     * imageListByPage
     * */
    @Test
    public void imageListByPage(){
//        QueryCondition<DevicePicReq> queryCondition = new QueryCondition();
//        PageCondition pageCondition = new PageCondition();
//        pageCondition.setPageNum(1);
//        pageCondition.setPageSize(10);
//        queryCondition.setPageCondition(pageCondition);
//        List<DevicePicResp> devicePicResps = new ArrayList<>();
//        DevicePicResp devicePicResp = new DevicePicResp();
//        devicePicResp.setDevicePicId("4321434321432143");
//        devicePicResp.setFmtDate("2019-03-26");
//        devicePicResps.add(devicePicResp);
//
//        //预计执行方法
//        new Expectations() {
//            {
//                picRelationInfoDao.imageListByPage((QueryCondition) any);
//                result = devicePicResps;
//                picRelationInfoDao.imageCountListByPage((QueryCondition) any);
//                result = 0;
//            }
//        };
//        //判断是否正确
//        Result result = picRelationInfoService.imageListByPage(queryCondition);
//        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * uploadImages
     * */
    /*@Test
    public void uploadImages() throws Exception {
        List<BatchUploadPicReq> batchUploadPicReqs = new ArrayList<>();
        Map<String, ImageUploadBean> picMap = new HashMap<>();
        List<PicRelationInfo> picRelationInfos = new ArrayList<>();
        Map<String, ImageUrl> picsUrlMap = new HashMap<>(64);
        Result finalResult = new Result();
        finalResult.setCode(0);
        //预计执行方法
        new Expectations() {
            {
                picRelationInfoServiceImpl.checkProcParamsForBatchUpload((List<BatchUploadPicReq>) any);
                result = false;
                picRelationInfoServiceImpl.getFileInfo((List<BatchUploadPicReq>) any,(Map<String, ImageUploadBean>) any,(List<PicRelationInfo>) any);
                result = true;
                picRelationInfoServiceImpl.uploadFileImage((Map<String, ImageUploadBean>) any,(Map<String, ImageUrl>) any,(List<PicRelationInfo>) any);
                result = true;
                picRelationInfoServiceImpl.saveImagesData((List<PicRelationInfo>) any);
                result = finalResult;
            }
        };
        //判断是否正确
        Result result = picRelationInfoService.uploadImages(batchUploadPicReqs);
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }*/

    /**
     * batchDownLoadImages
     * */
    @Test
    public void batchDownLoadImages() throws Exception{
//        ExportDto exportDto = new ExportDto();
//
//        List<DevicePicResp> devicePicResps = new ArrayList<>();
//        DevicePicResp devicePicResp = new DevicePicResp();
//        devicePicResp.setDevicePicId("4321434321432143");
//        devicePicResp.setPicUrlBase("fdsafdsafdssafdsafd");
//        devicePicResp.setPicUrlThumbnail("fdsafdsafdssafdsafd");
//        devicePicResp.setPicName("fdsafdsafdsafd");
//        devicePicResp.setType(".png");
//        devicePicResps.add(devicePicResp);
//
//        Result finalResult = new Result();
//        finalResult.setData("4321434321432143");
//
//        //预计执行方法
//        new Expectations() {
//            {
//                picRelationInfoDao.imageListByPage((QueryCondition) any);
//                result = devicePicResps;
//                exportFeign.addTask((ExportDto) any);
//                result = finalResult;
//                //生成本地文件
////                handleFile.generatedPic((Export) any,(ExportDto) any);
//            }
//        };
//        //判断是否正确
//        Result result1 = picRelationInfoService.batchDownLoadImages(exportDto);
//        Assert.assertTrue(result1.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * updateImageIsDeletedByIds
     * */
    @Test
    public void updateImageIsDeletedByIds(){
        List<PicRelationInfo> picRelationInfos = new ArrayList<>();
        PicRelationInfo picRelationInfo = new PicRelationInfo();
        picRelationInfo.setPicName("testPicName1");
        picRelationInfo.setDevicePicId("fdsafdsafd");
        picRelationInfos.add(picRelationInfo);
        //预计执行方法
        new Expectations() {
            {
                picRelationInfoDao.updateImagesIsDeleteByIds((Set<String>) any,null,anyString);
                result = 1;
            }
        };
        //判断是否正确
        Result result = picRelationInfoService.updateImageIsDeletedByIds(picRelationInfos,"1");
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }


    /**
     * uploadFileImage
     * */
    @Test
    public void uploadFileImage(){
        Map<String, ImageUploadBean> picMap = new HashMap<>();
        Map<String, ImageUrl> picsUrlMap = new HashMap<>();
        ImageUrl imageUrl = new ImageUrl();
        imageUrl.setOriginalUrl("http://10.5.24.142/group1/M00/00/00/CgUYjlxJt0yASRKDAABRI5QY7ZQ431.PNG");
        imageUrl.setThumbUrl("http://10.5.24.142/group1/M00/00/00/CgUYjlxJt0yASRKDAABRI5QY7ZQ431.PNG");
        picsUrlMap.put("dfsafdsaafds",imageUrl);

        List<PicRelationInfo> picRelationInfos = new ArrayList<>();
        //预计执行方法
        new Expectations() {
            {
                fdfsFeign.uploadFileImage(picMap);
                result = picsUrlMap;
            }
        };
        //判断是否正确
        Boolean b = picRelationInfoService.uploadFileImage(picMap,picsUrlMap,picRelationInfos);
        Assert.assertTrue(b);
    }

    /**
     * saveImagesData
     * */
    @Test
    public void saveImagesData(){
        Map<String, ImageUploadBean> picMap = new HashMap<>();
        Map<String, ImageUrl> picsUrlMap = new HashMap<>();
        ImageUrl imageUrl = new ImageUrl();
        imageUrl.setOriginalUrl("http://10.5.24.142/group1/M00/00/00/CgUYjlxJt0yASRKDAABRI5QY7ZQ431.PNG");
        imageUrl.setThumbUrl("http://10.5.24.142/group1/M00/00/00/CgUYjlxJt0yASRKDAABRI5QY7ZQ431.PNG");
        picsUrlMap.put("dfsafdsaafds",imageUrl);

        List<PicRelationInfo> picRelationInfos = new ArrayList<>();
        //预计执行方法
        new Expectations() {
            {
                picRelationInfoDao.saveImageInfos((List<PicRelationInfo>) any);
            }
        };
        //判断是否正确
        Result result = picRelationInfoService.saveImagesData(picRelationInfos);
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * getPicUrlByResourceId
     * */
    @Test
    public void getPicUrlByResourceId(){
        List<Map<String,String>> urls = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        map.put("fdsa","fdsaf");
        urls.add(map);

        List<PicRelationInfo> picRelationInfos = new ArrayList<>();
        //预计执行方法
        new Expectations() {
            {
//                picRelationInfoDao.getPicUrlByResourceId(anyString,anyString,anyString);
                result = urls;
            }
        };
        //判断是否正确
//        Result result = picRelationInfoService.getPicUrlByResourceId("1","dfsafdsa","fdsafdsafd");
//        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }


    /**
     * checkProcParamsForBatchUpload
     * */
    @Test
    public void checkProcParamsForBatchUpload(){
        /*List<BatchUploadPic> batchUploadPics = new ArrayList<>();
        BatchUploadPic batchUploadPic = new BatchUploadPic();
        batchUploadPic.setDeviceId("testPicName1");
        batchUploadPic.setPic(new MultipartFile() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getOriginalFilename() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {

            }
        });
        batchUploadPicReq.setResource("1");
        batchUploadPicReq.setAlarmName("fdssafd");
        batchUploadPicReq.setType(1);
        batchUploadPicReqs.add(batchUploadPicReq);

        BatchUploadPicReq batchUploadPicReq2 = new BatchUploadPicReq();
        batchUploadPicReq2.setDeviceId("testPicName2");
        batchUploadPicReq2.setPic(new MultipartFile() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getOriginalFilename() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {

            }
        });
        batchUploadPicReq2.setResource("2");
        batchUploadPicReq2.setOrderName("fdssafd");
        batchUploadPicReq2.setType(2);
        batchUploadPicReqs.add(batchUploadPicReq2);
        //预计执行方法
        new Expectations() {
            {

            }
        };
        //判断是否正确
        Boolean b = picRelationInfoService.checkProcParamsForBatchUpload(batchUploadPicReqs);
        Assert.assertTrue(!b);*/
    }

    /**
     * getFileInfo
     * */
    @Test
    public void getFileInfo() throws Exception {
        /*List<BatchUploadPicReq> batchUploadPicReqs = new ArrayList<>();
        BatchUploadPicReq batchUploadPicReq = new BatchUploadPicReq();
        batchUploadPicReq.setPic(new MultipartFile() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getOriginalFilename() {
                return ".JPG";
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {

            }
        });
        batchUploadPicReq.setType(1);
        batchUploadPicReqs.add(batchUploadPicReq);

        Map<String, ImageUploadBean> picMap = new HashMap<>(64);
        List<PicRelationInfo> picRelationInfos = new ArrayList<>();
        //预计执行方法
        new Expectations() {
            {

            }
        };
        //判断是否正确
        Boolean b = picRelationInfoService.getFileInfo(batchUploadPicReqs,picMap,picRelationInfos);
        Assert.assertTrue(b);*/
    }

}

