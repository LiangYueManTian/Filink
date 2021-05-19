package com.fiberhome.filink.picture.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.exportapi.api.ExportFeign;
import com.fiberhome.filink.exportapi.bean.Export;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.fdevice.dto.DeviceInfoDto;
import com.fiberhome.filink.fdevice.exception.FiLinkDeviceException;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.ossapi.bean.ImageUploadBean;
import com.fiberhome.filink.ossapi.bean.ImageUrl;
import com.fiberhome.filink.picture.bean.PicRelationInfo;
import com.fiberhome.filink.picture.constant.PicRelationConstants;
import com.fiberhome.filink.picture.constant.PicRelationI18nConstants;
import com.fiberhome.filink.picture.dao.relation.PicRelationInfoDao;
import com.fiberhome.filink.picture.enums.ImageExtensionEnum;
import com.fiberhome.filink.picture.exception.DeletePicRelationException;
import com.fiberhome.filink.picture.req.BatchUploadPic;
import com.fiberhome.filink.picture.req.DevicePicReq;
import com.fiberhome.filink.picture.resp.DevicePicResp;
import com.fiberhome.filink.picture.resp.app.LivePicInfo;
import com.fiberhome.filink.picture.resp.app.LivePicResp;
import com.fiberhome.filink.picture.service.PicRelationInfoService;
import com.fiberhome.filink.picture.utils.HandleFile;
import com.fiberhome.filink.picture.utils.HexUtil;
import com.fiberhome.filink.picture.utils.PicRelationResultCode;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPageBean;


/**
 * <p>
 * 图片关系表 服务实现类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-03-13
 */
@Service
@RefreshScope
@Slf4j
public class PicRelationInfoServiceImpl extends ServiceImpl<PicRelationInfoDao, PicRelationInfo> implements PicRelationInfoService {

    @Autowired
    private PicRelationInfoDao picRelationInfoDao;

    /**
     * 远程调用export服务
     */
    @Autowired
    private ExportFeign exportFeign;

    /**
     * 远程调用oss服务
     */
    @Autowired
    private FdfsFeign fdfsFeign;

    /**
     * 远程调用日志服务
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * 注入 生成、上传文件类的实体
     */
    @Autowired
    private HandleFile handleFile;

    /**
     * 服务名
     */
    @Value("${exportServerName}")
    private String serverName;

    /**
     * 最大导出条数
     */
    @Value("${maxExportPicDataSize}")
    private Integer maxExportPicDataSize;

    /**
     * 远程调用user服务
     */
    @Autowired
    private UserFeign userFeign;

    /**
     * 远程调用SystemLanguage服务
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 注入设施业务
     */
    @Autowired
    private DeviceInfoService deviceInfoService;

    /**
     * 初始图片流水
     */
    private static Integer initPicSerial = 1;

    /**
     * 初始图片流水
     */
    private static String picSerial;

    /**
     * 图片流水位数
     */
    @Value("${picSerialLength}")
    private Integer picSerialLength;


    /**
     * 分页查询设施图片列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @Override
    public Result imageListByPage(QueryCondition<DevicePicReq> queryCondition) {
        //设置分页beginNum
        Integer beginNum = (queryCondition.getPageCondition().getPageNum() - 1) * queryCondition.getPageCondition().getPageSize();
        queryCondition.getPageCondition().setBeginNum(beginNum);
        // 构造分页条件
        Page page = myBatiesBuildPage(queryCondition);
        //获取权限信息
        String flag = this.getPermissionsInfo(queryCondition);
        if (PicRelationConstants.PERMISSIONS_ERROR.equals(flag)){
            //用户服务异常提示语
            return ResultUtils.warn(PicRelationResultCode.USER_SERVER_ERROR,I18nUtils.getSystemString(PicRelationI18nConstants.USER_SERVER_ERROR));
        } else if (PicRelationConstants.PERMISSIONS_NOT.equals(flag)){
            //用户权限设置有误，直接返回空
            return ResultUtils.success();
        }

        //分页查询
        List<DevicePicResp> devicePicRespList = picRelationInfoDao.imageListByPage(queryCondition);

        //转换日期格式
        for (int i = 0; i<devicePicRespList.size(); i++){
            devicePicRespList.get(i).setFmtDate(getZoneTime(Long.parseLong(devicePicRespList.get(i).getFmtDate())*1000));
        }

        //组装数据
        Map<String,Object> resultMap = new TreeMap<>((Collections.reverseOrder()));
        //取出所有格式化后的时间并去重
        Set<String> fmtDateSet = new HashSet<>();
        for (DevicePicResp devicePicResp : devicePicRespList){
            fmtDateSet.add(devicePicResp.getFmtDate());
        }
        //按照时间分组组装数据
        for (String str : fmtDateSet) {
            List<DevicePicResp> devicePicRespResultList = new ArrayList<>();
            for (DevicePicResp devicePicResp : devicePicRespList){
                String fmtDateStr = devicePicResp.getFmtDate();
                if (str.equals(fmtDateStr)){
                    //获取图片来源名称
                    this.getResourceName(devicePicResp);
                    devicePicRespResultList.add(devicePicResp);
                }
            }
            //格式化时间戳
            this.fmtTimeStamp(devicePicRespResultList);
            resultMap.put(str,devicePicRespResultList);
        }
        //总数
        int count = picRelationInfoDao.imageCountListByPage(queryCondition);
        // 构造返回结果
        PageBean pageBean = myBatiesBuildPageBean(page, count, resultMap);
        // 返回数据
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * 批量下载图片列表
     *
     * @param exportDto 批量导出请求类
     * @return Result
     */
    @Override
    public Result batchDownLoadImages(ExportDto exportDto) throws Exception{
        //获取要导出的图片数据
        List<DevicePicResp> devicePicRespList = picRelationInfoDao.imageListByPage(exportDto.getQueryCondition());

        //必须有文件
        if (ObjectUtils.isEmpty(devicePicRespList)){
            return ResultUtils.warn(PicRelationConstants.FAIL, I18nUtils.getSystemString(PicRelationI18nConstants.DOWNLOAD_FILE_OVER_THE_MIN_NUM));
        }

        //最大导出图片数
        if (maxExportPicDataSize < devicePicRespList.size()){
            return ResultUtils.warn(PicRelationConstants.FAIL, I18nUtils.getSystemString(PicRelationI18nConstants.DOWNLOAD_FILE_OVER_THE_MAX_NUM));
        }

        //创建任务对象
        exportDto.setListName(I18nUtils.getSystemString(PicRelationI18nConstants.PIC_LIST));
        exportDto.setUserId(RequestInfoUtils.getUserId());
        exportDto.setFileNum((int) Math.round(devicePicRespList.size() * (2.1))+1);
        exportDto.setMethodPath(serverName + ExportApiUtils.getRequestUri());
        //远程创建下载任务
        Result result = exportFeign.addTask(exportDto);
        if (!ObjectUtils.isEmpty(result)){
            if (0 == result.getCode()){
                exportDto.setTaskId(result.getData().toString());
            } else if (PicRelationResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS == result.getCode()){
                return ResultUtils.warn(PicRelationConstants.FAIL, I18nUtils.getSystemString(PicRelationI18nConstants.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
            } else if (PicRelationResultCode.TASK_DOES_NOT_EXIST == result.getCode()){
                return ResultUtils.warn(PicRelationConstants.FAIL, I18nUtils.getSystemString(PicRelationI18nConstants.TASK_DOES_NOT_EXIST));
            }
        }
        //获取数据
        Export export = new Export();
        BeanUtils.copyProperties(exportDto,export);
        //异步生成本地文件
        handleFile.generatedPic(devicePicRespList,export,exportDto);
        // 保存下载图片操作日志
        List<PicRelationInfo> picRelationInfos = new ArrayList<>();
        for (DevicePicResp devicePicResp : devicePicRespList){
            PicRelationInfo picRelationInfo = new PicRelationInfo();
            picRelationInfo.setPicName(devicePicResp.getPicName());
            picRelationInfo.setDevicePicId(devicePicResp.getDevicePicId());
            picRelationInfos.add(picRelationInfo);
        }
        this.saveOperatorLog(picRelationInfos,LogConstants.DATA_OPT_TYPE_UPDATE);

        return ResultUtils.success(PicRelationConstants.SUCCESS, I18nUtils.getSystemString(PicRelationI18nConstants.INCREASE_TASK_SUCCESS));
    }

    /**
     * 批量更新图片列表isDeleted字段
     *
     * @param picRelationInfos 图片信息
     * @param isDeleted 逻辑删除标记
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateImageIsDeletedByIds(List<PicRelationInfo> picRelationInfos,String isDeleted){

        //必填参数校验
        if (this.checkProcParamsForUpdate(picRelationInfos)){
            return ResultUtils.warn(PicRelationResultCode.PIC_PARAM_ERROR, I18nUtils.getSystemString(PicRelationI18nConstants.PIC_PARAM_ERROR));
        }

        //获取图片id
        if (!ObjectUtils.isEmpty(picRelationInfos)){
            Set<String> ids = new HashSet<>();
            for (PicRelationInfo picRelationInfo : picRelationInfos){
                ids.add(picRelationInfo.getDevicePicId());
            }
            //批量删除数据
            int count = picRelationInfoDao.updateImagesIsDeleteByIds(ids,null,isDeleted);
            //如果删除条数不一致
            if (picRelationInfos.size() != count){
                throw new DeletePicRelationException(I18nUtils.getSystemString(PicRelationI18nConstants.DELETE_PIC_FAIL));
            }
            log.info("删除图片成功");
            // 保存删除图片操作日志
            this.saveOperatorLog(picRelationInfos, LogConstants.DATA_OPT_TYPE_DELETE);
        }
        return ResultUtils.success(PicRelationConstants.SUCCESS, I18nUtils.getSystemString(PicRelationI18nConstants.DELETE_PIC_SUCCESS));
    }

    /**
     * 批量上传图片列表
     *
     * @param batchUploadPics 批量上传图片信息
     *
     * @return Result
     */
    @Override
    public Result uploadImages(List<BatchUploadPic> batchUploadPics) throws Exception {
        //必填参数校验
        if (this.checkProcParamsForBatchUpload(batchUploadPics)){
            return ResultUtils.warn(PicRelationConstants.FAIL, I18nUtils.getSystemString(PicRelationI18nConstants.PIC_PARAM_ERROR));
        }
        //上传图片文件信息
        Map<String, ImageUploadBean> picMap = new HashMap<>(64);
        //返回图片url
        Map<String, ImageUrl> picsUrlMap = new HashMap<>(64);
        //保存图片数据信息
        List<PicRelationInfo> picRelationInfos = new ArrayList<>();
        //解析图片信息
        if (!this.getFileInfo(batchUploadPics,picMap,picRelationInfos)){
            return ResultUtils.warn(PicRelationResultCode.UPLOAD_PIC_TYPE_FAIL, I18nUtils.getSystemString(PicRelationI18nConstants.UPLOAD_PIC_TYPE_FAIL));
        }
        //批量上传图片
        if (!this.uploadFileImage(picMap,picsUrlMap,picRelationInfos)){
            return ResultUtils.warn(PicRelationResultCode.UPLOAD_PIC_FAIL, I18nUtils.getSystemString(PicRelationI18nConstants.UPLOAD_PIC_FAIL));
        }
        log.info("上传图片至文件服务器成功");
        //设置创建时间
        for (PicRelationInfo picRelationInfo : picRelationInfos) {
            //获取utc时间
            picRelationInfo.setCreateTime(getUtcTime());
        }

        //批量保存图片记录
        if (PicRelationConstants.FAIL.equals(this.saveImagesData(picRelationInfos).getCode())){
            return ResultUtils.warn(PicRelationResultCode.SAVE_PIC_INFO_FAIL, I18nUtils.getSystemString(PicRelationI18nConstants.SAVE_PIC_INFO_FAIL));
        }
        log.info("保存图片记录成功");
        return ResultUtils.success(PicRelationConstants.SUCCESS, I18nUtils.getSystemString(PicRelationI18nConstants.UPLOAD_PIC_SUCCESS));
    }

    /**
     * 批量上传图片文件
     *
     * @param picMap 上传图片文件
     * @param picsUrlMap 返回图片url
     * @param picRelationInfos 保存图片记录
     *
     * @return Boolean
     */
    public Boolean uploadFileImage(Map<String, ImageUploadBean> picMap,Map<String, ImageUrl> picsUrlMap,List<PicRelationInfo> picRelationInfos) {
        picsUrlMap = fdfsFeign.uploadFileImage(picMap);
        if (ObjectUtils.isEmpty(picsUrlMap)){
            return false;
        }
        //获取图片url
        for (int i = 0; i< picRelationInfos.size(); i++){
            picRelationInfos.get(i).setPicUrlBase(picsUrlMap.get(picRelationInfos.get(i).getDevicePicId()).getOriginalUrl());
            picRelationInfos.get(i).setPicUrlThumbnail(picsUrlMap.get(picRelationInfos.get(i).getDevicePicId()).getThumbUrl());
        }
        return true;
    }

    /**
     * 设置图片名字
     *
     * @param picRelationInfos 上传图片信息列表
     *
     * @return List<PicRelationInfo> 上传图片信息列表
     */
    public synchronized List<PicRelationInfo> setPicName(List<PicRelationInfo> picRelationInfos) {
        //获取设施资产编号
        Set<String> deviceIds = new HashSet<>();
        for (PicRelationInfo picRelationInfo : picRelationInfos) {
            deviceIds.add(picRelationInfo.getDeviceId());
        }
        String[] deviceIdArray = new String[deviceIds.size()];
        deviceIds.toArray(deviceIdArray);
        List<DeviceInfoDto> deviceInfoDtoList = null;
        try {
            deviceInfoDtoList = deviceInfoService.getDeviceByIds(deviceIdArray);
        } catch (Exception e) {
            throw new FiLinkDeviceException("the device server error>>>>>");
        }
        Map<String,String> deviceInfoDtoMap = new HashMap<>(16);
        for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList){
            deviceInfoDtoMap.put(deviceInfoDto.getDeviceId(),deviceInfoDto.getDeviceCode());
        }

        //资产编号
        String deviceCode;
        //目的
        String purpose = PicRelationConstants.DEVICE_PIC_PURPOSE_OTHER;
        for (PicRelationInfo picRelationInfo : picRelationInfos) {
            //获取当前图片流水
            Object object = RedisUtils.get("picSerial");
            if (ObjectUtils.isEmpty(object)){
                String picName = picRelationInfoDao.getLastPicSerial();
                if (!StringUtils.isEmpty(picName)){
                    //redis无缓存，且数据库中有数据，则认为取最后一条数据
                    picSerial = picName.substring(picName.length() - getSubCount(picName));
                } else {
                    //redis无缓存，且数据库中无数据，则认为系统初始化情况
                    picSerial = String.format("%0" + picSerialLength + "d",initPicSerial);
                }
            } else {
                //redis有缓存，则直接取缓存
                picSerial = (String) object;
            }
            Integer temp = Integer.parseInt(picSerial);
            temp++;
            //其中0表示补零而不是补空格，7表示至少7位
            picSerial = String.format("%0"+picSerialLength+"d",temp);

            deviceCode = deviceInfoDtoMap.get(picRelationInfo.getDeviceId());
            //设置名字规则
            if (PicRelationConstants.DEVICE_PIC_RESOURCE_1.equals(picRelationInfo.getResource())){
                //告警
                purpose = PicRelationConstants.DEVICE_PIC_PURPOSE_ALARM;
            } else if (PicRelationConstants.DEVICE_PIC_RESOURCE_2.equals(picRelationInfo.getResource())){
                //工单
                if (PicRelationConstants.PIC_RESOURCE_TYPE_INSPECTION.equals(picRelationInfo.getOrderType())){
                    //巡检
                    purpose = PicRelationConstants.DEVICE_PIC_PURPOSE_INSPECTION;
                } else if (PicRelationConstants.PIC_RESOURCE_TYPE_1.equals(picRelationInfo.getOrderType()) || PicRelationConstants.PIC_RESOURCE_TYPE_2.equals(picRelationInfo.getOrderType())){
                    //销障前或销障后
                    purpose =  PicRelationConstants.DEVICE_PIC_PURPOSE_CLEAR;
                }
            } else if (PicRelationConstants.DEVICE_PIC_RESOURCE_3.equals(picRelationInfo.getResource())){
                //实景图
                purpose =  PicRelationConstants.DEVICE_PIC_PURPOSE_LIVE;
            } else {
                //其它
                purpose =  PicRelationConstants.DEVICE_PIC_PURPOSE_OTHER;
            }
            //图片命名规则：
            //资产编号+目的+  图片流水（7位）
            picRelationInfo.setPicName(deviceCode + purpose + picSerial);

            //更新当前图片流水
            RedisUtils.set("picSerial",picSerial);
        }
        return picRelationInfos;
    }

    /**
     * 获取下标
     *
     * @param picName 图片名字
     *
     * @return int 下标
     */
    public static int getSubCount(String picName){
        //截取英文后面的数字
        char[] array = picName.toCharArray();
        int count = 0;
        for (int i = array.length-1; i > -1; i--) {
            char c = array[i];
            Boolean flag = c >= 'A' && c <= 'Z';
            if (flag) {
                System.out.println(count);
                break;
            }
            count++;
        }
        return count;
    }

    /**
     * 批量保存图片记录
     *
     * @param picRelationInfos 图片数据列表
     *
     * @return Result
     */
    @Override
    public Result saveImagesData(List<PicRelationInfo> picRelationInfos) {

        //按规则设置图片名称
        this.setPicName(picRelationInfos);

        //保存数据
        picRelationInfoDao.saveImageInfos(picRelationInfos);
        // 保存上传图片操作日志
        this.saveOperatorLog(picRelationInfos,LogConstants.DATA_OPT_TYPE_ADD);
        return ResultUtils.success(PicRelationConstants.SUCCESS, I18nUtils.getSystemString(PicRelationI18nConstants.UPLOAD_PIC_SUCCESS));
    }

    /**
     * 根据工单id获取图片url
     *
     * @param resource 来源
     * @param resourceId 来源id
     * @param deviceId 设施id
     *
     * @return Result
     */
    @Override
    public Result getPicUrlByResource(String resource,String resourceId,String deviceId,List<String> resourceIds) {
        if (StringUtils.isNotEmpty(resourceId)){
            resourceIds.add(resourceId);
        }
        //校验必填参数
        if (ObjectUtils.isEmpty(resourceIds)){
            return ResultUtils.warn(PicRelationResultCode.PIC_PARAM_ERROR, I18nUtils.getSystemString(PicRelationI18nConstants.PIC_PARAM_ERROR));
        }
        List<DevicePicResp> devicePicRespList = picRelationInfoDao.getPicUrlByResource(resource,deviceId,resourceIds);
        //获取图片来源名称
        for (DevicePicResp devicePicResp : devicePicRespList){
            this.getResourceName(devicePicResp);
        }
        //格式化时间戳
        this.fmtTimeStamp(devicePicRespList);
        return ResultUtils.success(devicePicRespList);
    }

    /**
     * 根据设施id或来源获取图片信息
     *
     * @param devicePicReq 图片请求
     *
     * @return Result
     */
    @Override
    public Result getPicInfoByDeviceId(DevicePicReq devicePicReq) {
        //校验必填参数
        if (StringUtils.isEmpty(devicePicReq.getDeviceId()) || StringUtils.isEmpty(devicePicReq.getPicNum())){
            return ResultUtils.warn(PicRelationResultCode.PIC_PARAM_ERROR, I18nUtils.getSystemString(PicRelationI18nConstants.PIC_PARAM_ERROR));
        }
        List<DevicePicResp> devicePicRespList = picRelationInfoDao.getPicInfoByDeviceIds(devicePicReq);
        //获取图片来源名称
        for (DevicePicResp devicePicResp : devicePicRespList){
            this.getResourceName(devicePicResp);
        }
        //格式化时间戳
        this.fmtTimeStamp(devicePicRespList);
        return ResultUtils.success(devicePicRespList);
    }

    /**
     * 根据deviceId更新图片列表isDeleted字段
     *
     * @param deviceIds 设施ids
     * @param isDeleted 逻辑删除标记
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateImageIsDeletedByDeviceIds(Set<String> deviceIds,String isDeleted){

        //必填参数校验
        if (ObjectUtils.isEmpty(deviceIds) || StringUtils.isEmpty(isDeleted)){
            return ResultUtils.warn(PicRelationResultCode.PIC_PARAM_ERROR, I18nUtils.getSystemString(PicRelationI18nConstants.PIC_PARAM_ERROR));
        }

        //获取所有数据
        DevicePicReq devicePicReq = new DevicePicReq();
        devicePicReq.setDeviceIds(deviceIds);
        List<DevicePicResp> devicePicRespList = picRelationInfoDao.getPicInfoByDeviceIds(devicePicReq);
        //如果图片已经被删，则不用继续删除
        if (ObjectUtils.isEmpty(devicePicRespList)){
            return ResultUtils.success(PicRelationConstants.SUCCESS, I18nUtils.getSystemString(PicRelationI18nConstants.DELETE_PIC_SUCCESS));
        }
        //删除数据
        int count = picRelationInfoDao.updateImagesIsDeleteByIds(null,deviceIds,isDeleted);
        //如果删除条数不一致
        if (devicePicRespList.size() != count){
            throw new DeletePicRelationException(I18nUtils.getSystemString(PicRelationI18nConstants.DELETE_PIC_FAIL));
        }
        log.info("删除图片记录成功");
        // 保存删除图片操作日志
        List<PicRelationInfo> picRelationInfos = new ArrayList<>();
        for (DevicePicResp devicePicResp: devicePicRespList){
            PicRelationInfo picRelationInfo = new PicRelationInfo();
            BeanUtils.copyProperties(devicePicResp,picRelationInfo);
            picRelationInfos.add(picRelationInfo);
        }
        this.saveOperatorLog(picRelationInfos,LogConstants.DATA_OPT_TYPE_DELETE);
        return ResultUtils.success(PicRelationConstants.SUCCESS, I18nUtils.getSystemString(PicRelationI18nConstants.DELETE_PIC_SUCCESS));
    }

    /**
     * app根据设施ids获取实景图
     *
     * @param deviceIds 设施ids
     * @param picNum 张数
     *
     * @return Result
     */
    @Override
    public Result getLivePicInfoByDeviceIdsForApp(Set<String> deviceIds, String picNum) {
        //校验必填参数
        if (ObjectUtils.isEmpty(deviceIds) || StringUtils.isEmpty(picNum)) {
            return ResultUtils.warn(PicRelationResultCode.PIC_PARAM_ERROR, I18nUtils.getSystemString(PicRelationI18nConstants.PIC_PARAM_ERROR));
        }
        List<DevicePicResp> devicePicRespList = picRelationInfoDao.getLivePicInfoByDeviceIds(deviceIds,picNum);

        //组装图片信息
        Map<String,List<LivePicInfo>> livePicRespListMap = new HashMap<>(64);
        List<LivePicInfo> livePicInfoList;
        for (DevicePicResp devicePicResp : devicePicRespList) {
            LivePicInfo livePicInfo = new LivePicInfo();
            if (livePicRespListMap.containsKey(devicePicResp.getDeviceId())){
                livePicInfoList = livePicRespListMap.get(devicePicResp.getDeviceId());
            } else {
                livePicInfoList = new ArrayList<>();
            }
            BeanUtils.copyProperties(devicePicResp,livePicInfo);
            livePicInfoList.add(livePicInfo);
            livePicRespListMap.put(devicePicResp.getDeviceId(),livePicInfoList);
        }

        //组装返回数据
        List<LivePicResp> livePicRespList = new ArrayList<>();
        for (Map.Entry<String,List<LivePicInfo>> entry : livePicRespListMap.entrySet()) {
            LivePicResp livePicResp = new LivePicResp();
            livePicResp.setDeviceId(entry.getKey());
            livePicResp.setPicList(entry.getValue());
            livePicRespList.add(livePicResp);
        }

        return ResultUtils.success(livePicRespListMap);
    }

    /*---------------------------------------格式化时间戳start------------------------------------------*/
    /**
     * 格式化时间戳
     *
     * @param devicePicRespList 返回图片信息
     * @return Boolean
     */
    public List<DevicePicResp> fmtTimeStamp(List<DevicePicResp> devicePicRespList){
        for (DevicePicResp devicePicResp : devicePicRespList){
            if (!ObjectUtils.isEmpty(devicePicResp.getCreateTime())){
                devicePicResp.setCTime(devicePicResp.getCreateTime().getTime());
            }
            if (!ObjectUtils.isEmpty(devicePicResp.getUpdateTime())){
                devicePicResp.setUTime(devicePicResp.getUpdateTime().getTime());
            }
        }
        return devicePicRespList;
    }
    /*---------------------------------------格式化时间戳end------------------------------------------*/


    /*---------------------------------------参数校验start------------------------------------------*/
    /**
     * 校验图片基本参数
     *
     * @param batchUploadPics 批量上传图片信息
     * @return Boolean
     */
    public Boolean checkProcParamsForBatchUpload(List<BatchUploadPic> batchUploadPics) {
        //必填校验
        if (!ObjectUtils.isEmpty(batchUploadPics)){
            for (BatchUploadPic batchUploadPic : batchUploadPics){
                //关联设施不能为空
                if (StringUtils.isEmpty(batchUploadPic.getDeviceId())) {
                    return true;
                }
                //图片不能为空
                if (ObjectUtils.isEmpty(batchUploadPic.getPic())) {
                    return true;
                }
                //工单来源不能为空
                if (StringUtils.isEmpty(batchUploadPic.getResource())){
                    return true;
                }

                //关联告警名称不能为空
                if (PicRelationConstants.PIC_RESOURCE_1.equals(batchUploadPic.getResource())){
                    if (StringUtils.isEmpty(batchUploadPic.getAlarmName())){
                        return true;
                    }
                }
                //关联工单
                if (PicRelationConstants.PIC_RESOURCE_2.equals(batchUploadPic.getResource())){
                    //关联工单名称不能为空
                    if (StringUtils.isEmpty(batchUploadPic.getOrderName())){
                        return true;
                    }
                    //关联工单类型不能为空
                    if (StringUtils.isEmpty(batchUploadPic.getType())){
                        return true;
                    }
                }

                //经纬度不能为空
                if (StringUtils.isEmpty(batchUploadPic.getPositionBase())){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 校验图片基本参数
     *
     * @param picRelationInfos 图片列表
     * @return Boolean
     */
    public Boolean checkProcParamsForUpdate(List<PicRelationInfo> picRelationInfos) {
        Boolean flag = false;
        //必填校验
        for (PicRelationInfo picRelationInfo : picRelationInfos) {
            if (
                //图片名称为空（用于新增日志）
              StringUtils.isEmpty(picRelationInfo.getPicName()) ||
                //图片url为空
//              StringUtils.isEmpty(picRelationInfo.getPicUrlBase()) ||
                //图片id为空
                StringUtils.isEmpty(picRelationInfo.getDevicePicId())
            ) {
                flag = true;
                break;
            }
        }
        return flag;
    }
    /*---------------------------------------参数校验end------------------------------------------*/

    /*---------------------------------------解析图片start------------------------------------------*/
    /**
     * 获取文件信息方法
     *
     * @param batchUploadPics 批量上传图片信息
     * @param picMap 上传图片信息
     * @param picRelationInfos 保存图片信息
     *
     * @return Map<String, ImageUploadBean>
     */
    public Boolean getFileInfo(List<BatchUploadPic> batchUploadPics, Map<String, ImageUploadBean> picMap, List<PicRelationInfo> picRelationInfos) throws IOException {
        //获取图片信息
        for (int i = 0;i<batchUploadPics.size();i++){
            //校验图片类型
            if (!ImageExtensionEnum.containExtension(FilenameUtils.getExtension(batchUploadPics.get(i).getPic().getOriginalFilename()))){
                return false;
            }

            PicRelationInfo picRelationInfo = new PicRelationInfo();
            ImageUploadBean imageUploadBean = new ImageUploadBean();
            //统一设置uuid
            String uuid = NineteenUUIDUtils.uuid();
            String type = batchUploadPics.get(i).getPic().getOriginalFilename().substring(batchUploadPics.get(i).getPic().getOriginalFilename().lastIndexOf("."));

            BeanUtils.copyProperties(batchUploadPics.get(i),picRelationInfo);
            picRelationInfo.setPicSize(Long.toString(batchUploadPics.get(i).getPic().getSize()));
            picRelationInfo.setPicName(batchUploadPics.get(i).getPic().getOriginalFilename().substring(0,batchUploadPics.get(i).getPic().getOriginalFilename().lastIndexOf(".")));
            picRelationInfo.setType(type);

            //销账类型（销账前、销账后、巡检）
            picRelationInfo.setOrderType(batchUploadPics.get(i).getType());

            imageUploadBean.setFileExtension(FilenameUtils.getExtension(batchUploadPics.get(i).getPic().getOriginalFilename()));
            imageUploadBean.setFileHexData(HexUtil.bytesToHexString(batchUploadPics.get(i).getPic().getBytes()));
            picRelationInfo.setDevicePicId(uuid);

            picRelationInfos.add(picRelationInfo);
            picMap.put(uuid,imageUploadBean);
        }
        return true;
    }
    /*---------------------------------------解析图片end------------------------------------------*/

    /*---------------------------------------保存操作日志公共方法start-----------------------------------------*/
    /**
     * 保存操作日志
     *
     * @param picRelationInfos 图片列表
     * @return Boolean
     */
    public void saveOperatorLog(List<PicRelationInfo> picRelationInfos,String operatorType) {
        // 保存图片操作日志
        List list = new ArrayList();
        for (PicRelationInfo picRelationInfo : picRelationInfos) {
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("devicePicId");
            addLogBean.setDataName("picName");
            addLogBean.setOptObj(picRelationInfo.getPicName());
            addLogBean.setOptObjId(picRelationInfo.getDevicePicId());
            addLogBean.setDataOptType(operatorType);
            //保存图片日志
            if (LogConstants.DATA_OPT_TYPE_DELETE.equals(operatorType)){
                addLogBean.setFunctionCode("1301402");
            } else if (LogConstants.DATA_OPT_TYPE_ADD.equals(operatorType)){
                addLogBean.setFunctionCode("1301401");
                //操作类型为pda
                addLogBean.setOptType(LogConstants.OPT_TYPE_PDA);
            } else if (LogConstants.DATA_OPT_TYPE_UPDATE.equals(operatorType)){
                addLogBean.setFunctionCode("1301403");
            }

            list.add(addLogBean);
        }
        //此处通过feign方式调用log服务
        systemLanguageUtil.querySystemLanguage();
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
    }
    /*---------------------------------------保存操作日志公共方法end-----------------------------------------*/

    /*---------------------------------------获取权限公共方法start-----------------------------------------*/
    /**
     * 获取拥有权限信息
     *
     * @param queryCondition    查询对象
     * @return queryCondition    查询对象
     */
    public String getPermissionsInfo(QueryCondition<DevicePicReq> queryCondition) {
        List<String> userIds = new ArrayList<>();

        //admin用户不用校验权限
        String adminUserId = "1";
        String loginUserId = RequestInfoUtils.getUserId();
        if (!ObjectUtils.isEmpty(loginUserId)) {
            //当前用户是admin时不用获取所有权限
            if (adminUserId.equals(loginUserId)) {
                return PicRelationConstants.PERMISSIONS_ADMIN;
            }
        }

        userIds.add(RequestInfoUtils.getUserId());
        User user = new User();
        Object userObj = userFeign.queryUserByIdList(userIds);
        //校验是否有值
        if (userObj == null) {
            return PicRelationConstants.PERMISSIONS_ERROR;
        }
        List<User> userInfoList = JSONArray.parseArray(JSONArray.toJSONString(userObj), User.class);
        //添加用户map
        if (!ObjectUtils.isEmpty(userInfoList)) {
            user = userInfoList.get(0);
        }
        //用户管理区域
        List<String> areaIds = user.getDepartment().getAreaIdList();
        //用户管理设施类型
        List<RoleDeviceType> roleDeviceTypes = user.getRole().getRoleDevicetypeList();

        //如果任何一个数据权限为空
        if (ObjectUtils.isEmpty(areaIds) || ObjectUtils.isEmpty(roleDeviceTypes)){
            return PicRelationConstants.PERMISSIONS_NOT;
        }

        Set<String> deviceTypeSet = new HashSet<>();
        Set<String> areaIdSet = new HashSet<>();
        for (RoleDeviceType roleDeviceType : roleDeviceTypes){
            deviceTypeSet.add(roleDeviceType.getDeviceTypeId());
        }
        for (String areaId : areaIds){
            areaIdSet.add(areaId);
        }
        queryCondition.getBizCondition().setPermissionDeviceTypes(deviceTypeSet);
        queryCondition.getBizCondition().setPermissionAreaIds(areaIdSet);
        return PicRelationConstants.PERMISSIONS_NORMAL;
    }

    /*---------------------------------------获取权限公共方法end-----------------------------------------*/

    /*---------------------------------------获取图片来源名字公共方法start------------------------------------------*/

    /**
     * 获取图片来源名称
     *
     * @param devicePicResp 图片返回类
     * @return DevicePicResp 图片返回类
     */
    public DevicePicResp getResourceName(DevicePicResp devicePicResp){
        if (PicRelationConstants.DEVICE_PIC_RESOURCE_1.equals(devicePicResp.getResource())) {
            //告警
            devicePicResp.setResourceName(devicePicResp.getAlarmName());
        } else if (PicRelationConstants.DEVICE_PIC_RESOURCE_2.equals(devicePicResp.getResource())) {
            //工单
            devicePicResp.setResourceName(devicePicResp.getOrderName());
        } else if (PicRelationConstants.DEVICE_PIC_RESOURCE_3.equals(devicePicResp.getResource())) {
            //实景图
            devicePicResp.setResourceName(PicRelationResultCode.DEVICE_PIC_RESOURCE_NAME_LIVE);
        }
        return devicePicResp;
    }
    /*---------------------------------------获取图片来源名字公共方法end------------------------------------------*/

    /*---------------------------------------获取时区时间公共方法start------------------------------------------*/
    /**
     * 获取时区时间
     *
     * @param time 时间戳
     * @return 时间
     */
    public static String getZoneTime(Long time) {
        if (time == null) {
            return null;
        }
        String timeZone = getZone();
        if (timeZone == null) {
            timeZone = "GMT+0800";
        }
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        return sdf.format(date);
    }

    /**
     * 从请求头获取时区
     *
     * @return
     */
    public static String getZone() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String zone = request.getHeader("zone");
        return zone;
    }
    /*---------------------------------------获取时区时间公共方法end------------------------------------------*/

    /*---------------------------------------获取utc时间公共方法start------------------------------------------*/
    /**
     * java 获取UTC时间
     */
    public static Date getUtcTime() {
        //1、取得本地时间：
        final java.util.Calendar cal = java.util.Calendar.getInstance();

        //2、取得时间偏移量：
        final int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);

        //3、取得夏令时差：
        final int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);

        //4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));

        return cal.getTime();
    }
    /*---------------------------------------获取utc时间公共方法end------------------------------------------*/
}
