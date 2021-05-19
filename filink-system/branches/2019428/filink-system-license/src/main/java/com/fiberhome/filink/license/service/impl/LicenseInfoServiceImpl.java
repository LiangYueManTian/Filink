package com.fiberhome.filink.license.service.impl;

import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.license.bean.*;
import com.fiberhome.filink.license.exception.FilinkLicenseException;
import com.fiberhome.filink.license.util.*;
import com.fiberhome.filink.license.dao.LicenseInfoDao;
import com.fiberhome.filink.license.service.LicenseInfoService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.utils.LogConstants;
import com.fiberhome.filink.oss_api.api.FdfsFeign;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gzp
 * @since 2019-02-19
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class LicenseInfoServiceImpl extends ServiceImpl<LicenseInfoDao, LicenseInfo>
        implements LicenseInfoService {

    @Autowired
    private FdfsFeign fdfsFeign;

    @Autowired
    private LicenseInfoDao licenseDao;

    @Autowired
    private LogProcess logProcess;

    /**
     * 文件大小
     */
    @Value("${LicenseFileSize}")
    private Long fileSize;
    /**
     * 上传license
     *
     * @param licenseXml license文件
     * @return Result
     * @throws Exception 异常
     */
    @Override
    public Result uploadLicense(MultipartFile licenseXml) throws Exception {

        // TODO 调用烽火解密的方法获取数据或解密文件
        long fileSize= licenseXml.getSize();
        if(fileSize > fileSize) {
            return ResultUtils.warn(LicenseResultCode.LICENSE_FILE_TOO_LARGE, I18nUtils.getString(LicenseI18n.LICENSE_FILE_TOO_LARGE));

        }
        License license;
        try {
            //解析License文件
            license = getLicenseFromXml(licenseXml);
            if (ObjectUtils.isEmpty(license)) {
                return ResultUtils.warn(LicenseResultCode.ILLEGAL_LICENSE_FILE, I18nUtils.getString(LicenseI18n.ILLEGAL_LICENSE_FILE));
            }
        } catch (Exception e) {
            return ResultUtils.warn(LicenseResultCode.ILLEGAL_LICENSE_FILE, I18nUtils.getString(LicenseI18n.ILLEGAL_LICENSE_FILE));
        }

        //校验License值
        if(!checkLicense(license)) {
            return ResultUtils.warn(LicenseResultCode.ILLEGAL_LICENSE_FILE, I18nUtils.getString(LicenseI18n.ILLEGAL_LICENSE_FILE));
        }

        // 存入缓存
        if (!saveLicenseToRedis(license)) {
            return ResultUtils.warn(LicenseResultCode.FAIL, I18nUtils.getString(LicenseI18n.ERROR_SAVING_LICENSE_TO_REDIS));
        }

        // 存入FastDFS，文件路径存入数据库
        String path = fdfsFeign.uploadFile(licenseXml);

        //获取当前用户
        String userId = RequestInfoUtils.getUserId();

        LicenseInfo licenseInfo;
        if (StringUtils.equalsIgnoreCase(license.getTryRemark(), LicenseParameterValues.TRY_REMARK_YES)) {
            // 保存试用license
            licenseInfo = licenseDao.findDefaultLicense();
            if (ObjectUtils.isEmpty(licenseInfo)) {
                licenseInfo = new LicenseInfo(String.valueOf(UUID.randomUUID()), path,
                        LicenseParameterValues.LICENSE_IS_DEFAULT, Timestamp.valueOf(LocalDateTime.now()),
                        userId, null, null);
                licenseDao.insert(licenseInfo);
            } else {
                updateLicenseInfo(path, licenseInfo, userId, LicenseParameterValues.LICENSE_IS_DEFAULT);
            }
        } else {
            // 保存非试用License
            licenseInfo = licenseDao.findNonDefaultLicense();
            if (ObjectUtils.isEmpty(licenseInfo)) {
                licenseInfo = new LicenseInfo(String.valueOf(UUID.randomUUID()), path,
                        LicenseParameterValues.LICENSE_IS_NOT_DEFAULT, Timestamp.valueOf(LocalDateTime.now()),
                        userId, null, null);
                licenseDao.insert(licenseInfo);
            } else {
                updateLicenseInfo(path, licenseInfo, userId, LicenseParameterValues.LICENSE_IS_NOT_DEFAULT);
            }
        }

        //操作日志
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("licenseId");
        addLogBean.setDataName("licenseName");
        addLogBean.setFunctionCode("2105101");
        addLogBean.setOptObj(licenseInfo.getIsDefault());
        addLogBean.setOptObjId(licenseInfo.getLicenseId());
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(LicenseI18n.LICENSE_SUCCESSFUL_UPLOADED));
    }

    /**
     * 校验license值合法性
     * @param license
     * @return
     */
    private boolean checkLicense(License license) {
        if(StringUtils.isEmpty(license.beginTime) || StringUtils.isEmpty(license.endTime)
            || StringUtils.isEmpty(license.maxDeviceNum) ||StringUtils.isEmpty(license.maxOnlineNum)
            || StringUtils.isEmpty(license.maxUserNum) ) {
            return false;
        }
        if(StringUtils.isEmpty(license.tryRemark) || license.tryRemark.trim().length()>1) {
            return false;
        }
        return true;
    }

    /**
     * 更新License数据库信息
     *
     * @param path        文件存储路径
     * @param licenseInfo license信息
     * @param userId      用户ID
     * @param isDefault   是否默认
     */
    private void updateLicenseInfo(String path, LicenseInfo licenseInfo, String userId, String isDefault) {
        String oldPath = licenseInfo.getPath();
        licenseInfo.setPath(path);
        licenseInfo.setUpdateUser(userId);
        licenseInfo.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
        licenseInfo.setIsDefault(isDefault);
        licenseDao.updateById(licenseInfo);

        //物理删除原有文件
        fdfsFeign.deleteFilesPhy(new ArrayList<>(Arrays.asList(oldPath)));
    }

    /**
     * 获取license详情
     *
     * @return Result
     * @throws Exception 异常
     */
    @Override
    public Result getLicenseDetail() {
        License license;
        try {
            license = getCurrentLicense();
        } catch (FilinkLicenseException e) {
            return ResultUtils.warn(ResultCode.FAIL, e.getMessage());
        }

        //获取消耗值（前端需要）
        license.setLicenseThreshold(getLicenseThresholdFromRedis());

        //试用标记改为True,false
        if (license.getTryRemark().equalsIgnoreCase(LicenseParameterValues.TRY_REMARK_YES)) {
            license.setTryRemark(LicenseParameterValues.TRY_REMARK_TRUE);
        } else {
            license.setTryRemark(LicenseParameterValues.TRY_REMARK_FALSE);
        }
        //返回给前端需要的值
        List<LicenseResult> licenseResults = new ArrayList<>();
        licenseResults.add(new LicenseResult(LicenseParameter.BEGIN_TIME, I18nUtils.getString(LicenseDesc.BEGIN_TIME),
                license.getBeginTime()));
        licenseResults.add(new LicenseResult(LicenseParameter.END_TIME, I18nUtils.getString(LicenseDesc.END_TIME),
                license.getEndTime()));
        licenseResults.add(new LicenseResult(LicenseParameter.TRIAl_LICENSE, I18nUtils.getString(LicenseDesc.IS_TRY),
                license.getTryRemark()));
        licenseResults.add(new LicenseResult(LicenseParameter.MAX_DEVICE_NUM, I18nUtils.getString(LicenseDesc.MAX_DEVICE),
                license.getMaxDeviceNum(), license.getLicenseThreshold().getDeviceNum()));
        licenseResults.add(new LicenseResult(LicenseParameter.MAX_USER_NUM, I18nUtils.getString(LicenseDesc.MAX_USER),
                license.getMaxUserNum(), license.getLicenseThreshold().getUserNum()));
        licenseResults.add(new LicenseResult(LicenseParameter.MAX_ONLINE, I18nUtils.getString(LicenseDesc.MAX_ONLINE),
                license.getMaxOnlineNum(), license.getLicenseThreshold().getOnlineNum()));

        return ResultUtils.success(licenseResults);
    }


    /**
     * 从Redis和数据库中获取当前License信息
     *
     * @return 当前License信息，不包含LicenseThreshold
     * @throws FilinkLicenseException
     */
    @Override
    public License getCurrentLicense() throws FilinkLicenseException {
        // 先从redis中获取License
        License license = getLicenseFromRedis();
        LicenseInfo licenseInfo;
        if (ObjectUtils.isEmpty(license)) {
            // redis中不存在,从数据库获取非默认的license
            if (ObjectUtils.isEmpty(licenseInfo = licenseDao.findNonDefaultLicense())) {
                //没有非默认的license，获取默认的license
                if (ObjectUtils.isEmpty(licenseInfo = licenseDao.findDefaultLicense())) {
                    throw new FilinkLicenseException(I18nUtils.getString(LicenseI18n.LICENSE_NOT_FOUND));
                }
            }
            //根据文件路径获取文件
            String path = licenseInfo.getPath();
            try {
                URL url = new URL(path);
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();
                File file = new File(path);
                //转换file为MultipartFile
                MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                        ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream);
                //调用从xml中获取license的方法
                license = getLicenseFromXml(multipartFile);
            } catch (IOException e) {
                throw new FilinkLicenseException(I18nUtils.getString(LicenseI18n.ERROR_GETTING_LICENSE_FILE));
            } catch (DocumentException e) {
                throw new FilinkLicenseException(I18nUtils.getString(LicenseI18n.ERROR_ANALYZING_LICENSE_FILE));
            }
            if (ObjectUtils.isEmpty(license)) {
                throw new FilinkLicenseException(I18nUtils.getString(LicenseI18n.ERROR_GETTING_LICENSE_FILE));
            }
            //保存license到redis
            if (!saveLicenseToRedis(license)) {
                throw new FilinkLicenseException(I18nUtils.getString(LicenseI18n.ERROR_SAVING_LICENSE_TO_REDIS));
            }
        }
        return license;
    }


    /**
     * 校验时间是否过期
     *
     * @return boolean
     * @throws Exception 异常
     */
    @Override
    public boolean validateLicenseTime() throws Exception {
        License license = getCurrentLicense();
        if (license.getBeginTime() != null && license.getEndTime() != null) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (LocalDate.parse(license.getBeginTime(), df).isBefore(LocalDate.now())
                    && LocalDate.parse(license.getEndTime(), df).isAfter(LocalDate.now())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 修改Redis用户，在线用户，设施活跃值
     *
     * @param licenseFeignBean 新增删除格式Bean
     * @return boolean
     * @throws Exception 异常
     */
    @Override
    public boolean updateRedisLicenseThreshold(LicenseFeignBean licenseFeignBean) throws Exception {
        License license = getCurrentLicense();

        switch(licenseFeignBean.getOperationTarget()){
            case USER:
                return updateLicenseThreshold(LicenseParameter.THRESHOLD_USER_NUM, LicenseParameterValues.THRESHOLD_USER_NUM_DEFAULT,
                        licenseFeignBean, Integer.parseInt(license.maxUserNum));

            case ONLINE:
                return updateLicenseThreshold(LicenseParameter.THRESHOLD_ONLINE, LicenseParameterValues.THRESHOLD_ONLINE_DEFAULT,
                        licenseFeignBean, Integer.parseInt(license.maxOnlineNum));

            case DEVICE:
                return updateLicenseThreshold(LicenseParameter.THRESHOLD_DEVICE_NUM, LicenseParameterValues.THRESHOLD_DEVICE_NUM_DEFAULT,
                        licenseFeignBean, Integer.parseInt(license.maxDeviceNum));

            default:
                return false;
        }

//        if(LicenseType.USER.equalsIgnoreCase(licenseFeignBean.getOperationTarget().getValue())) {
//
//        }
//        //Redis查询当前用户总数
//        Object userNumObj = (RedisUtils.hGet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_USER_NUM));
//        if(userNumObj == null) {
//            RedisUtils.hSet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_USER_NUM,
//                    LicenseParameterValues.THRESHOLD_USER_NUM_DEFAULT);
//        }
//        int delta =0;
//        if(LicenseType.ADD.equalsIgnoreCase(licenseFeignBean.operationWay.getValue())) {
//            delta = licenseFeignBean.getNum();
//        } else if (LicenseType.DELETE.equalsIgnoreCase(licenseFeignBean.operationWay.getValue())) {
//            delta = -licenseFeignBean.getNum();
//        }
//        boolean b = updateRedisMapValueWithLock(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_USER_NUM,
//                delta, Integer.parseInt(license.getMaxUserNum()));
//        return b;

    }


    /**
     * 更新Redis license活跃值
     * @param thresholdItem
     * @param thresholdDefault
     * @param licenseFeignBean
     * @param maxNum
     * @return
     * @throws FilinkLicenseException
     */
    private boolean updateLicenseThreshold(String thresholdItem, int thresholdDefault,
                                           LicenseFeignBean licenseFeignBean, int maxNum) throws FilinkLicenseException {
        //Redis查询当前阀值
        Object numObj = (RedisUtils.hGet(Constant.LICENSE_THRESHOLD, thresholdItem));
        //赋予默认值
        if(numObj == null) {
            RedisUtils.hSet(Constant.LICENSE_THRESHOLD, thresholdItem, thresholdDefault);
        }
        int delta = 0;
        if(LicenseType.ADD.equalsIgnoreCase(licenseFeignBean.operationWay.getValue())) {
            delta = licenseFeignBean.getNum();
        } else if (LicenseType.DELETE.equalsIgnoreCase(licenseFeignBean.operationWay.getValue())) {
            delta = -licenseFeignBean.getNum();
        }
        boolean b = updateRedisMapValueWithLock(Constant.LICENSE_THRESHOLD, thresholdItem, delta, maxNum);
        return b;
    }

    /**
     * 同步LicenseThreshold
     * 单位ID
     *
     * @param licenseThresholdFeignBean
     * @return
     */
    @Override
    public boolean synchronousLicenseThreshold(LicenseThresholdFeignBean licenseThresholdFeignBean) {
        if (ObjectUtils.isEmpty(licenseThresholdFeignBean) || StringUtils.isEmpty(licenseThresholdFeignBean.getName())
                || StringUtils.isEmpty(licenseThresholdFeignBean.getNum() + "")) {
            // throw new Exception();
        }
        String name = licenseThresholdFeignBean.getName();
        int num = licenseThresholdFeignBean.getNum();
        if (StringUtils.equalsIgnoreCase(name, LicenseType.USER)) {
            RedisUtils.hSet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_USER_NUM, num);
            return true;
        }
        if (StringUtils.equalsIgnoreCase(name, LicenseType.DEVICE)) {
            RedisUtils.hSet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_DEVICE_NUM, num);
            return true;
        }
        if (StringUtils.equalsIgnoreCase(name, LicenseType.ONLINE)) {
            RedisUtils.hSet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_ONLINE, num);
            return true;
        }
        log.warn(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>license使用量中名称不对应");
        return false;
    }

//    /**
//     * 保存默认的License使用量（全为0）
//     *
//     * @return booleann
//     */
//    private boolean saveDefaultLicenseThreshold() {
//        if (RedisUtils.hSet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_USER_NUM,
//                LicenseParameterValues.THRESHOLD_USER_NUM_DEFAULT)
//                && RedisUtils.hSet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_ONLINE,
//                LicenseParameterValues.THRESHOLD_ONLINE_DEFAULT)
//                && RedisUtils.hSet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_DEVICE_NUM,
//                LicenseParameterValues.THRESHOLD_DEVICE_NUM_DEFAULT)) {
//            return true;
//        }
//        return false;
//    }

    /**
     * 从缓存中获取LicenseThreshold
     *
     * @return License
     */
    private LicenseThreshold getLicenseThresholdFromRedis() {
        // TODO 同步用户、在线数、设施到redis

        //从redis获取
        Object userNumObj = (RedisUtils.hGet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_USER_NUM));
        String userNum = userNumObj == null ? null : userNumObj.toString();

        Object onlineNumObj = RedisUtils.hGet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_ONLINE);
        String onlineNum = onlineNumObj == null ? null : onlineNumObj.toString();

        Object deviceNumOjb = RedisUtils.hGet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_DEVICE_NUM);
        String deviceNum = deviceNumOjb == null ? null : deviceNumOjb.toString();

        return new LicenseThreshold(userNum, onlineNum, deviceNum);
    }

//    /**
//     * 新增删除设施校验
//     *
//     * @param licenseFeignBean 校验格式Bean
//     * @param license          缓存中license
//     * @param licenseThreshold 缓存中license使用量
//     * @return boolean
//     */
//    @Synchronized
//    private boolean validateDevice(LicenseFeignBean licenseFeignBean, License license,
//                                   LicenseThreshold licenseThreshold) {
//        if (StringUtils.equalsIgnoreCase(licenseFeignBean.getType(), LicenseType.ADD)) {
//            int resultNum = Integer.parseInt(licenseThreshold.getDeviceNum()) + licenseFeignBean.getNum();
//            if (resultNum > Integer.parseInt(license.getMaxDeviceNum())) {
//                return false;
//            } else {
//                RedisUtils.hSet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_DEVICE_NUM, resultNum);
//            }
//        } else if (StringUtils.equalsIgnoreCase(licenseFeignBean.getType(), LicenseType.DELETE)) {
//            int resultNum = Integer.parseInt(licenseThreshold.getDeviceNum()) - licenseFeignBean.getNum();
//            RedisUtils.hSet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_DEVICE_NUM, resultNum);
//        } else {
//            log.warn(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>新增修改类型不对应");
//        }
//        return true;
//    }
//
//    /**
//     * 新增删除用户校验
//     *
//     * @param licenseFeignBean 校验格式Bean
//     * @param license          缓存中license
//     * @param licenseThreshold 缓存中license使用量
//     * @return boolean
//     */
//    @Synchronized
//    private boolean validateUser(LicenseFeignBean licenseFeignBean, License license,
//                                 LicenseThreshold licenseThreshold) {
//        if (StringUtils.equalsIgnoreCase(licenseFeignBean.getType(), LicenseType.ADD)) {
//            int resultNum = Integer.parseInt(licenseThreshold.getUserNum()) + licenseFeignBean.getNum();
//            if (resultNum > Integer.parseInt(license.getMaxUserNum())) {
//                return false;
//            } else {
//                RedisUtils.hSet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_USER_NUM, resultNum);
//            }
//        } else if (StringUtils.equalsIgnoreCase(licenseFeignBean.getType(), LicenseType.DELETE)) {
//            int resultNum = Integer.parseInt(licenseThreshold.getUserNum()) - licenseFeignBean.getNum();
//            RedisUtils.hSet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_USER_NUM, resultNum);
//        } else {
//            log.warn(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>新增修改类型不对应");
//        }
//        return true;
//    }
//
//    /**
//     * 在线用户校验
//     *
//     * @param licenseFeignBean 校验格式Bean
//     * @param license          缓存中license
//     * @param licenseThreshold 缓存中license使用量
//     * @return boolean
//     */
//    @Synchronized
//    private boolean validateOnline(LicenseFeignBean licenseFeignBean, License license,
//                                   LicenseThreshold licenseThreshold) {
//        if (StringUtils.equalsIgnoreCase(licenseFeignBean.getType(), LicenseType.ADD)) {
//            int resultNum = Integer.parseInt(licenseThreshold.getOnlineNum()) + licenseFeignBean.getNum();
//            if (resultNum > Integer.parseInt(license.getMaxOnlineNum())) {
//                return false;
//            } else {
//                RedisUtils.hSet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_ONLINE, resultNum);
//            }
//        } else if (StringUtils.equalsIgnoreCase(licenseFeignBean.getType(), LicenseType.DELETE)) {
//            int resultNum = Integer.parseInt(licenseThreshold.getOnlineNum()) - licenseFeignBean.getNum();
//            RedisUtils.hSet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_ONLINE, resultNum);
//        } else {
//            log.warn(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>新增修改类型不对应");
//        }
//        return true;
//    }

    /**
     * license存入缓存
     *
     * @param license License
     * @return boolean
     */
    private boolean saveLicenseToRedis(License license) {
        RedisUtils.hSet(Constant.LICENSE, LicenseParameter.TRY_REMARK, license.getTryRemark());
        RedisUtils.hSet(Constant.LICENSE, LicenseParameter.BEGIN_TIME, license.getBeginTime());
        RedisUtils.hSet(Constant.LICENSE, LicenseParameter.END_TIME, license.getEndTime());
        RedisUtils.hSet(Constant.LICENSE, LicenseParameter.MAX_USER_NUM, license.getMaxUserNum());
        RedisUtils.hSet(Constant.LICENSE, LicenseParameter.MAX_ONLINE, license.getMaxOnlineNum());
        RedisUtils.hSet(Constant.LICENSE, LicenseParameter.MAX_DEVICE_NUM, license.getMaxDeviceNum());
        return true;
    }

    /**
     * 从缓存中获取license数据
     *
     * @return License
     */
    private License getLicenseFromRedis() {
        String tryRemark = (String) RedisUtils.hGet(Constant.LICENSE, LicenseParameter.TRY_REMARK);
        String beginTime = (String) RedisUtils.hGet(Constant.LICENSE, LicenseParameter.BEGIN_TIME);
        String endTime = (String) RedisUtils.hGet(Constant.LICENSE, LicenseParameter.END_TIME);
        String maxUserNum = (String) RedisUtils.hGet(Constant.LICENSE, LicenseParameter.MAX_USER_NUM);
        String maxOnline = (String) RedisUtils.hGet(Constant.LICENSE, LicenseParameter.MAX_ONLINE);
        String maxDeviceNum = (String) RedisUtils.hGet(Constant.LICENSE, LicenseParameter.MAX_DEVICE_NUM);
        if (ObjectUtils.isEmpty(tryRemark) && ObjectUtils.isEmpty(beginTime) && ObjectUtils.isEmpty(endTime)
                && ObjectUtils.isEmpty(maxUserNum) && ObjectUtils.isEmpty(maxOnline)
                && ObjectUtils.isEmpty(maxDeviceNum)) {
            return null;
        }
        return new License(tryRemark, beginTime, endTime, maxUserNum, maxOnline, maxDeviceNum);
    }

    /**
     * 从文件中获取License
     *
     * @param licenseXml license文件
     * @return License
     * @throws Exception 异常
     */
    private License getLicenseFromXml(MultipartFile licenseXml) throws IOException, DocumentException {
        if (checkXml(licenseXml)) {
            return null;
        }
        SAXReader reader = new SAXReader();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(licenseXml.getBytes());
        Document document = reader.read(byteArrayInputStream);
        Element element = document.getRootElement();
        String tryRemark = element.elementText("TryRemark");
        String beginTime = element.elementText("BeginTime");
        String endTime = element.elementText("EndTime");
        String maxUserNum = element.elementText("MaxUserNum");
        String maxOnline = element.elementText("MaxOnline");
        String maxDeviceNum = element.elementText("MaxDeviceNum");
        License license = new License(tryRemark, beginTime, endTime, maxUserNum, maxOnline, maxDeviceNum);
        return license;
    }

    /**
     * 校验文件是否为空
     *
     * @param licenseXml license文件
     * @return boolean
     * @throws Exception 异常
     */
    private boolean checkXml(MultipartFile licenseXml) throws IOException {
        if (licenseXml == null || licenseXml.getBytes() == null) {
            return true;
        }
        return false;
    }

    /**
     * 更新Redis中值为int的Map数据，更新成功返回true
     * @param key Redis中的key
     * @param item Redis中的item
     * @param delta 修改增量值
     * @param threshold 最大阀值
     * @return
     * @throws FilinkLicenseException
     */
    private boolean updateRedisMapValueWithLock(String key, String item, int delta, int threshold) throws FilinkLicenseException {
        String lockKey = "lock_" + key;
        int acquireTimeout = 10000;
        int timeout = 5000;
        //获取时间锁
        String lockIdentifier = RedisUtils.lockWithTimeout(lockKey, acquireTimeout, timeout);
        if(StringUtils.isEmpty(lockIdentifier)) {
            throw new FilinkLicenseException(I18nUtils.getString(LicenseI18n.ERROR_GETTING_REDIS_LOCK));
        }
        Object o = RedisUtils.hGet(key, item);
        if(o == null) {
            RedisUtils.releaseLock(lockKey,lockIdentifier);
            throw new FilinkLicenseException(I18nUtils.getString(LicenseI18n.MISSING_VALUE_FROM_REDIS));
        }
        if(o instanceof String) {
            o = Integer.valueOf((String)o);
        }
        if(!(o instanceof Integer)) {
            RedisUtils.releaseLock(lockKey,lockIdentifier);
            throw new FilinkLicenseException(I18nUtils.getString(LicenseI18n.ERROR_PARAM_FORMAT_IN_REDIS));
        }
        int currentNum = (Integer) o;
        if(currentNum+delta <= threshold) {
            RedisUtils.hSet(key,item,currentNum+delta);
            RedisUtils.releaseLock(lockKey,lockIdentifier);
            return true;
        }
        RedisUtils.releaseLock(lockKey,lockIdentifier);
        return false;
    }
}
