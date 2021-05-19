package com.fiberhome.filink.picture.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.api.ExportFeign;
import com.fiberhome.filink.exportapi.bean.Export;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.fdevice.dto.DeviceInfoDto;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.ossapi.bean.ImageUploadBean;
import com.fiberhome.filink.ossapi.bean.ImageUrl;
import com.fiberhome.filink.picture.bean.PicRelationInfo;
import com.fiberhome.filink.picture.constant.PicRelationConstants;
import com.fiberhome.filink.picture.dao.relation.PicRelationInfoDao;
import com.fiberhome.filink.picture.req.BatchUploadPic;
import com.fiberhome.filink.picture.req.DevicePicReq;
import com.fiberhome.filink.picture.resp.DevicePicResp;
import com.fiberhome.filink.picture.utils.HandleFile;
import com.fiberhome.filink.picture.utils.PicRelationResultCode;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.Role;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


@RunWith(JMockit.class)
public class DevicePictureSeviceTests {

    /**
     * 测试对象 picRelationInfoServiceImpl
     */
    @Tested
    private PicRelationInfoServiceImpl picRelationInfoService;

    /**
     * 自动注入 picRelationInfoServiceImpl
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
     * 自动注入 UserFeign
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
     * Mock redisUtils
     */
    @Mocked
    private RedisUtils redisUtils;

    /**
     * 自动注入 maxExportPicDataSize
     */
    @Injectable
    private Integer maxExportPicDataSize;

    /**
     * 自动注入 picSerialLength
     */
    @Injectable
    private Integer picSerialLength;

    /**
     * 自动注入 picSerial
     */
    @Injectable
    private Integer picSerial;

    /**
     * 自动注入 exportServerName
     */
    @Injectable
    private Integer exportServerName;

    /**
     * 自动注入SystemLanguage服务
     */
    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 自动注入DeviceInfoService服务
     */
    @Injectable
    private DeviceInfoService deviceInfoService;

    /**
     * 初始化
     */
    @Before
    public void setUp() {
        maxExportPicDataSize = 100;
        picSerialLength = 7;
    }

    /**
     * imageListByPage
     * */
    @Test
    public void imageListByPage(){
        QueryCondition<DevicePicReq> queryCondition = new QueryCondition();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
        queryCondition.setPageCondition(pageCondition);

        DevicePicReq devicePicReq = new DevicePicReq();
        queryCondition.setBizCondition(devicePicReq);

        List<DevicePicResp> devicePicRespList = new ArrayList<>();
        DevicePicResp devicePicResp = new DevicePicResp();
        devicePicResp.setDevicePicId("4321434321432143");
        devicePicResp.setFmtDate("233335435");
        devicePicResp.setCreateTime(new Date());
        devicePicRespList.add(devicePicResp);

        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("hHpsXy3GmVr1osbpA9j");
        Department department = new Department();
        department.setAreaIdList(areaIdList);

        List<RoleDeviceType> roleDeviceTypeList = new ArrayList<>();
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setDeviceTypeId("001");
        roleDeviceTypeList.add(roleDeviceType);
        Role role = new Role();
        role.setRoleDevicetypeList(roleDeviceTypeList);

        List<User> userList = new ArrayList<>();
        User user = new User();
        user.setDepartment(department);
        user.setRole(role);
        userList.add(user);
        Object object = JSONArray.parseArray(JSON.toJSONString(userList));
        //预计执行方法
        new Expectations() {
            {
                userFeign.queryUserByIdList((List<String>) any);
                result = object;
            }
        };
        //判断是否正确
        String string = picRelationInfoService.getPermissionsInfo(queryCondition);
        Assert.assertTrue(string.equals(PicRelationConstants.PERMISSIONS_NORMAL));

        //预计执行方法
        new Expectations() {
            {
                picRelationInfoDao.imageListByPage((QueryCondition) any);
                result = devicePicRespList;
            }
        };
        //判断是否正确
        Result result = picRelationInfoService.imageListByPage(queryCondition);
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * uploadImages
     * */
    @Test
    public void uploadImages() throws Exception {
        List<BatchUploadPic> batchUploadPics = new ArrayList<>();
        BatchUploadPic batchUploadPic = new BatchUploadPic();
        batchUploadPic.setDeviceId("MEx96pQaeYv0huy7TmV");
        batchUploadPic.setResource("MEx96pQaeYv0huy7TmV");
        batchUploadPic.setPositionBase("123,234");
        batchUploadPics.add(batchUploadPic);

        Map<String, ImageUploadBean> picMap = new HashMap<>();
        ImageUploadBean imageUploadBean = new ImageUploadBean();
        imageUploadBean.setFileExtension(".jpg");
        imageUploadBean.setFileHexData("erwqrdsafdsfadsafdsafdss");
        picMap.put("fdsafdsafds",imageUploadBean);

        List<PicRelationInfo> picRelationInfos = new ArrayList<>();
        PicRelationInfo picRelationInfo = new PicRelationInfo();
        picRelationInfo.setDevicePicId("MEx96pQaeYv0huy7TmV");
        picRelationInfo.setPicUrlBase("fdsafdsfdsafdsafds");
        picRelationInfo.setPicUrlThumbnail("fdfdsafdsafds");
        picRelationInfos.add(picRelationInfo);

        Map<String, ImageUrl> imageUrlMap = new HashMap<>();
        ImageUrl imageUrl = new ImageUrl();
        imageUrl.setOriginalUrl("fdsfdsafdsafds");
        imageUrl.setThumbUrl("fdsafdsafdsafds");
        imageUrlMap.put("MEx96pQaeYv0huy7TmV",imageUrl);

        batchUploadPic.setDeviceId("2GS7cSSY2AiuovUVQ8g");
        MultipartFile file = new MockMultipartFile(
                "s.png",
                "s.png",
                ContentType.APPLICATION_OCTET_STREAM.toString(),
                new byte[23]
        );
        batchUploadPic.setPic(file);

        //预计执行方法
        new Expectations() {
            {
                fdfsFeign.uploadFileImage(picMap);
                result = imageUrlMap;
            }
        };
        //判断是否正确
        Boolean b = picRelationInfoService.uploadFileImage(picMap,imageUrlMap,picRelationInfos);
        Assert.assertTrue(b);

        //判断是否正确
        Boolean b2 = picRelationInfoService.checkProcParamsForBatchUpload(batchUploadPics);
        Assert.assertTrue(!b2);

        //判断是否正确
        Boolean b3 = picRelationInfoService.getFileInfo(batchUploadPics,picMap,picRelationInfos);
        Assert.assertTrue(b3);

        //保存数据
        Result result1 = picRelationInfoService.saveImagesData(picRelationInfos);
        Assert.assertTrue(result1.getCode() == PicRelationConstants.SUCCESS);

        //批量上传图片
//        this.uploadFileImage();

        List<BatchUploadPic> batchUploadPics1 = new ArrayList<>();
        BatchUploadPic batchUploadPic1 = new BatchUploadPic();
        batchUploadPic1.setResource(PicRelationConstants.PIC_RESOURCE_1);
        batchUploadPics1.add(batchUploadPic1);
        //判断是否正确
        Boolean b4 = picRelationInfoService.checkProcParamsForBatchUpload(batchUploadPics1);
        Assert.assertTrue(b4);

        List<BatchUploadPic> batchUploadPics2 = new ArrayList<>();
        BatchUploadPic batchUploadPic2 = new BatchUploadPic();
        batchUploadPic2.setResource(PicRelationConstants.PIC_RESOURCE_2);
        batchUploadPics2.add(batchUploadPic1);
        //判断是否正确
        Boolean b5 = picRelationInfoService.checkProcParamsForBatchUpload(batchUploadPics2);
        Assert.assertTrue(b5);

        Result result = picRelationInfoService.uploadImages(batchUploadPics);
        Assert.assertTrue(result.getCode() == PicRelationResultCode.UPLOAD_PIC_FAIL);
    }

    /**
     * saveImagesData
     * */
    @Test
    public void saveImagesDataList(){
        List<PicRelationInfo> picRelationInfos = new ArrayList<>();
        PicRelationInfo picRelationInfo = new PicRelationInfo();
        picRelationInfo.setDevicePicId("MEx96pQaeYv0huy7TmV");
        picRelationInfo.setPicUrlBase("fdsafdsfdsafdsafds");
        picRelationInfo.setPicUrlThumbnail("fdfdsafdsafds");
        picRelationInfos.add(picRelationInfo);

        //预计执行方法
        new Expectations() {
            {
                //按规则设置图片名称
                picRelationInfoService.setPicName(picRelationInfos);
                result = picRelationInfos;
                //保存数据
                picRelationInfoDao.saveImageInfos(picRelationInfos);
                result = 1;
            }
        };
        //保存数据
        Result result1 = picRelationInfoService.saveImagesData(picRelationInfos);
        Assert.assertTrue(result1.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * batchDownLoadImages
     * */
    @Test
    public void batchDownLoadImages() throws Exception{
        ExportDto exportDto = new ExportDto();

        List<DevicePicResp> devicePicRespList = new ArrayList<>();
        DevicePicResp devicePicResp = new DevicePicResp();
        devicePicResp.setDevicePicId("4321434321432143");
        devicePicResp.setPicUrlBase("fdsafdsafdssafdsafd");
        devicePicResp.setPicUrlThumbnail("fdsafdsafdssafdsafd");
        devicePicResp.setPicName("fdsafdsafdsafd");
        devicePicResp.setType(".png");
        devicePicRespList.add(devicePicResp);

        Result finalResult = new Result();
        finalResult.setData("4321434321432143");

        //预计执行方法
        new Expectations() {
            {
                picRelationInfoDao.imageListByPage((QueryCondition) any);
                result = devicePicRespList;
                exportFeign.addTask((ExportDto) any);
                result = finalResult;
                //生成本地文件
                handleFile.generatedPic((List<DevicePicResp>) any,(Export) any,(ExportDto) any);
//                picRelationInfoService.saveOperatorLog((List<PicRelationInfo>) any,anyString);
            }
        };
        //判断是否正确
        Result result1 = picRelationInfoService.batchDownLoadImages(exportDto);
        Assert.assertTrue(result1.getCode() == PicRelationConstants.SUCCESS);
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
                picRelationInfoDao.updateImagesIsDeleteByIds((Set<String>) any,(Set<String>) any,anyString);
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
     * setPicName
     * */
    @Test
    public void setPicName(){
        List<PicRelationInfo> picRelationInfoList = new ArrayList<>();
        PicRelationInfo picRelationInfo = new PicRelationInfo();
        picRelationInfo.setDevicePicId("2GS7cSSY2AiuovUVQ8g");
        picRelationInfo.setDeviceId("2GS7cSSY2AiuovUVQ8g");
        picRelationInfoList.add(picRelationInfo);

        Set<String> deviceIds = new HashSet<>();
        deviceIds.add("2GS7cSSY2AiuovUVQ8g");
        String[] deviceIdArray = new String[deviceIds.size()];
        deviceIds.toArray(deviceIdArray);

        List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        deviceInfoDto.setDeviceId("2GS7cSSY2AiuovUVQ8g");
        deviceInfoDto.setDeviceCode("34254365365");
        deviceInfoDtoList.add(deviceInfoDto);

        //预计执行方法
        new Expectations() {
            {
                try {
                    RedisUtils.get("picSerial");
                    result = "0000009";
                    deviceInfoService.getDeviceByIds(deviceIdArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                result = deviceInfoDtoList;
            }
        };

        //判断是否正确
        List<PicRelationInfo> picRelationInfoListResp = picRelationInfoService.setPicName(picRelationInfoList);
        Assert.assertTrue(picRelationInfoListResp.size() == 1);

        List<PicRelationInfo> picRelationInfoList1 = new ArrayList<>();
        PicRelationInfo picRelationInfo1 = new PicRelationInfo();
        picRelationInfo1.setDevicePicId("2GS7cSSY2AiuovUVQ8g");
        picRelationInfo1.setDeviceId("2GS7cSSY2AiuovUVQ8g");
        picRelationInfo1.setResource(PicRelationConstants.DEVICE_PIC_RESOURCE_2);
        picRelationInfo1.setOrderType(PicRelationConstants.PIC_RESOURCE_TYPE_INSPECTION.toString());
        picRelationInfoList1.add(picRelationInfo1);
        //预计执行方法
        new Expectations() {
            {
                try {
                    RedisUtils.get("picSerial");
                    result = "0000009";
                    deviceInfoService.getDeviceByIds(deviceIdArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                result = deviceInfoDtoList;
            }
        };

        //判断是否正确
        List<PicRelationInfo> picRelationInfoListResp1 = picRelationInfoService.setPicName(picRelationInfoList1);
        Assert.assertTrue(picRelationInfoListResp1.size() == 1);

        List<PicRelationInfo> picRelationInfoList2 = new ArrayList<>();
        PicRelationInfo picRelationInfo2 = new PicRelationInfo();
        picRelationInfo2.setDevicePicId("2GS7cSSY2AiuovUVQ8g");
        picRelationInfo2.setDeviceId("2GS7cSSY2AiuovUVQ8g");
        picRelationInfo2.setResource(PicRelationConstants.DEVICE_PIC_RESOURCE_2);
        picRelationInfo2.setOrderType(PicRelationConstants.PIC_RESOURCE_TYPE_1.toString());
        picRelationInfoList2.add(picRelationInfo2);
        //预计执行方法
        new Expectations() {
            {
                try {
                    RedisUtils.get("picSerial");
                    result = "0000009";
                    deviceInfoService.getDeviceByIds(deviceIdArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                result = deviceInfoDtoList;
            }
        };

        //判断是否正确
        List<PicRelationInfo> picRelationInfoListResp2 = picRelationInfoService.setPicName(picRelationInfoList2);
        Assert.assertTrue(picRelationInfoListResp2.size() == 1);

        List<PicRelationInfo> picRelationInfoList3 = new ArrayList<>();
        PicRelationInfo picRelationInfo3 = new PicRelationInfo();
        picRelationInfo3.setDevicePicId("2GS7cSSY2AiuovUVQ8g");
        picRelationInfo3.setDeviceId("2GS7cSSY2AiuovUVQ8g");
        picRelationInfo3.setResource(PicRelationConstants.DEVICE_PIC_RESOURCE_3);
        picRelationInfoList3.add(picRelationInfo3);
        //预计执行方法
        new Expectations() {
            {
                try {
                    RedisUtils.get("picSerial");
                    result = "0000009";
                    deviceInfoService.getDeviceByIds(deviceIdArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                result = deviceInfoDtoList;
            }
        };

        //判断是否正确
        List<PicRelationInfo> picRelationInfoListResp3 = picRelationInfoService.setPicName(picRelationInfoList3);
        Assert.assertTrue(picRelationInfoListResp3.size() == 1);
    }

    /**
     * getPicUrlByResource
     * */
    @Test
    public void getPicUrlByResource(){
        List<DevicePicResp> devicePicRespList = new ArrayList<>();
        DevicePicResp devicePicResp = new DevicePicResp();
        devicePicResp.getDeviceType();
        devicePicResp.getDeviceName();
        devicePicResp.getDeviceStatus();
        devicePicResp.getDeviceCode();
        devicePicResp.getAddress();
        devicePicResp.getDeployStatus();
        devicePicResp.getProvinceName();
        devicePicResp.getCityName();
        devicePicResp.getDistrictName();
        devicePicResp.getAreaId();
        devicePicResp.getAreaName();
        devicePicResp.getResourceName();
        devicePicResp.getRemarks();
        devicePicResp.getCTime();
        devicePicResp.getUTime();
        devicePicResp.setResource("3");
        devicePicRespList.add(devicePicResp);

        String resource = "3";
        String resourceId = "fdsafdsafd";
        String deviceId = "fdsafdsafd";
        List<String> resourceIds = new ArrayList<>();
        //预计执行方法
        new Expectations() {
            {
                picRelationInfoDao.getPicUrlByResource(resource,deviceId,resourceIds);
                result = devicePicRespList;
            }
        };

        //判断是否正确
        Result result = picRelationInfoService.getPicUrlByResource(resource,resourceId,deviceId,resourceIds);
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * getPicInfoByDeviceId
     * */
    @Test
    public void getPicInfoByDeviceId(){
        DevicePicReq devicePicReq = new DevicePicReq();
        devicePicReq.setDeviceId("fdsafdsafcsafdcdsafd");
        devicePicReq.setPicNum("9");

        List<DevicePicResp> devicePicRespList = new ArrayList<>();
        DevicePicResp devicePicResp = new DevicePicResp();
        devicePicResp.setResource("3");
        devicePicRespList.add(devicePicResp);
        //预计执行方法
        new Expectations() {
            {
                picRelationInfoDao.getPicInfoByDeviceIds(devicePicReq);
                result = devicePicRespList;
            }
        };

        //判断是否正确
        Result result = picRelationInfoService.getPicInfoByDeviceId(devicePicReq);
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * updateImageIsDeletedByDeviceIds
     * */
    @Test
    public void updateImageIsDeletedByDeviceIds(){
        Set<String> deviceIds = new HashSet<>();
        deviceIds.add("fdsafdsafdsaf");
        String isDeleted = "1";

        List<DevicePicReq> devicePicReqList = new ArrayList<>();
        DevicePicReq devicePicReq = new DevicePicReq();
        devicePicReq.setDeviceIds(deviceIds);
        devicePicReqList.add(devicePicReq);

        List<DevicePicResp> devicePicRespList = new ArrayList<>();
        DevicePicResp devicePicResp = new DevicePicResp();
        devicePicRespList.add(devicePicResp);
        //预计执行方法
        new Expectations() {
            {
                picRelationInfoDao.getPicInfoByDeviceIds(devicePicReq);
                result = devicePicRespList;
                picRelationInfoDao.updateImagesIsDeleteByIds(null,deviceIds,isDeleted);
                result = 1;
            }
        };

        //判断是否正确
        Result result = picRelationInfoService.updateImageIsDeletedByDeviceIds(deviceIds,isDeleted);
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * getLivePicInfoByDeviceIdsForApp
     * */
    @Test
    public void getLivePicInfoByDeviceIdsForApp(){
        Set<String> deviceIds = new HashSet<>();
        deviceIds.add("fdsafdsafdsaf");
        String picNum = "9";

        List<DevicePicReq> devicePicReqList = new ArrayList<>();
        DevicePicReq devicePicReq = new DevicePicReq();
        devicePicReq.setDeviceIds(deviceIds);
        devicePicReqList.add(devicePicReq);

        List<DevicePicResp> devicePicRespList = new ArrayList<>();
        DevicePicResp devicePicResp = new DevicePicResp();
        devicePicResp.setDeviceId("fdsafdsafdsaf");
        devicePicRespList.add(devicePicResp);
        //预计执行方法
        new Expectations() {
            {
                picRelationInfoDao.getLivePicInfoByDeviceIds(deviceIds,picNum);
                result = devicePicRespList;
            }
        };
        //判断是否正确
        Result result = picRelationInfoService.getLivePicInfoByDeviceIdsForApp(deviceIds,picNum);
        Assert.assertTrue(result.getCode() == PicRelationConstants.SUCCESS);
    }

    /**
     * getSubCount
     * */
    @Test
    public void getSubCount(){
        int count = picRelationInfoService.getSubCount("B00000007");
        Assert.assertTrue(count == 8);
    }

    /**
     * getResourceName
     */
    @Test
    public void getResourceName() {
        DevicePicResp devicePicResp = new DevicePicResp();
        devicePicResp.setResource(PicRelationConstants.DEVICE_PIC_RESOURCE_1);
        //判断是否正确
        picRelationInfoService.getResourceName(devicePicResp);

        devicePicResp.setResource(PicRelationConstants.DEVICE_PIC_RESOURCE_2);
        //判断是否正确
        picRelationInfoService.getResourceName(devicePicResp);

        devicePicResp.setResource(PicRelationConstants.DEVICE_PIC_RESOURCE_3);
        //判断是否正确
        picRelationInfoService.getResourceName(devicePicResp);
    }

    /**
     * checkProcParamsForBatchUpload
     */
    @Test
    public void checkProcParamsForBatchUpload(){
        List<BatchUploadPic> batchUploadPics = new ArrayList<>();
        BatchUploadPic batchUploadPic = new BatchUploadPic();
        batchUploadPic.setDeviceId("MEx96pQaeYv0huy7TmV");
//        batchUploadPic.setResource("MEx96pQaeYv0huy7TmV");
        batchUploadPic.setPositionBase("123,234");
        batchUploadPics.add(batchUploadPic);

        picRelationInfoService.checkProcParamsForBatchUpload(batchUploadPics);

        batchUploadPics.remove(batchUploadPic);
        BatchUploadPic batchUploadPic1 = new BatchUploadPic();
        MultipartFile file = new MockMultipartFile(
                "s.png",
                "s.png",
                ContentType.APPLICATION_OCTET_STREAM.toString(),
                new byte[23]
        );
        batchUploadPic1.setPic(file);
        batchUploadPic1.setDeviceId("MEx96pQaeYv0huy7TmV");
        batchUploadPic1.setResource(PicRelationConstants.PIC_RESOURCE_1);
        batchUploadPics.add(batchUploadPic1);

        picRelationInfoService.checkProcParamsForBatchUpload(batchUploadPics);

        batchUploadPic1.setResource(PicRelationConstants.PIC_RESOURCE_2);
        batchUploadPics.set(0,batchUploadPic1);

        picRelationInfoService.checkProcParamsForBatchUpload(batchUploadPics);

        batchUploadPic1.setPic(file);
        batchUploadPic1.setDeviceId("MEx96pQaeYv0huy7TmV");
        batchUploadPic1.setResource(PicRelationConstants.PIC_RESOURCE_1);
        batchUploadPics.add(batchUploadPic1);

        picRelationInfoService.checkProcParamsForBatchUpload(batchUploadPics);

        batchUploadPic1.setResource(PicRelationConstants.PIC_RESOURCE_2);
        batchUploadPic1.setOrderName("fdsafdafdsafd");
        batchUploadPics.set(0,batchUploadPic1);

        picRelationInfoService.checkProcParamsForBatchUpload(batchUploadPics);

        batchUploadPic1.setResource(PicRelationConstants.PIC_RESOURCE_2);
        batchUploadPic1.setOrderName("fdsafdafdsafd");
        batchUploadPic1.setType("fdsafdsafdsafd");
        batchUploadPics.set(0,batchUploadPic1);

        picRelationInfoService.checkProcParamsForBatchUpload(batchUploadPics);
    }

}

