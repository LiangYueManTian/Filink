package com.fiberhome.filink.license.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.about.bean.About;
import com.fiberhome.filink.about.service.AboutService;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.license.bean.*;
import com.fiberhome.filink.license.constant.*;
import com.fiberhome.filink.license.dao.LicenseInfoDao;
import com.fiberhome.filink.license.exception.FilinkLicenseException;
import com.fiberhome.filink.license.service.LicenseInfoService;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.UserCount;
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private AboutService aboutService;

    /**
     * 文件大小
     */
    @Value("${LicenseFileSize}")
    private Long maxFileSize;

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

        if(ObjectUtils.isEmpty(licenseXml)) {
            return ResultUtils.warn(LicenseResultCode.ILLEGAL_LICENSE_FILE, I18nUtils.getSystemString(LicenseI18n.ILLEGAL_LICENSE_FILE));
        }
        //文件大小控制
        if (licenseXml.getSize() > maxFileSize) {
            return ResultUtils.warn(LicenseResultCode.LICENSE_FILE_TOO_LARGE, I18nUtils.getSystemString(LicenseI18n.LICENSE_FILE_TOO_LARGE));
        }
        License license;
        try {
            //解析License文件
            license = getLicenseFromXml(licenseXml);
            if (ObjectUtils.isEmpty(license)) {
                return ResultUtils.warn(LicenseResultCode.ILLEGAL_LICENSE_FILE, I18nUtils.getSystemString(LicenseI18n.ILLEGAL_LICENSE_FILE));
            }
        } catch (Exception e) {
            return ResultUtils.warn(LicenseResultCode.ILLEGAL_LICENSE_FILE, I18nUtils.getSystemString(LicenseI18n.ILLEGAL_LICENSE_FILE));
        }

        //校验License值
        if (!checkLicense(license)) {
            return ResultUtils.warn(LicenseResultCode.ILLEGAL_LICENSE_FILE, I18nUtils.getSystemString(LicenseI18n.ILLEGAL_LICENSE_FILE));
        }

        // 存入缓存
        if (!saveLicenseToRedis(license)) {
            return ResultUtils.warn(LicenseResultCode.FAIL, I18nUtils.getSystemString(LicenseI18n.ERROR_SAVING_LICENSE_TO_REDIS));
        }

        // 存入FastDFS，文件路径存入数据库
        String path = fdfsFeign.uploadFile(licenseXml);

        //获取当前用户
        String userId = RequestInfoUtils.getUserId();

        //关于
        About about = new About();

        LicenseInfo licenseInfo;
        if (StringUtils.equalsIgnoreCase(license.getTryRemark(), LicenseParameterValues.TRY_REMARK_YES)) {
            // 保存试用license
            licenseInfo = licenseDao.findDefaultLicense();
            if (ObjectUtils.isEmpty(licenseInfo)) {
                licenseInfo = new LicenseInfo(NineteenUUIDUtils.uuid(), path,
                        LicenseParameterValues.LICENSE_IS_DEFAULT, Timestamp.valueOf(LocalDateTime.now()),
                        userId, null, null);
                licenseDao.insert(licenseInfo);
            } else {
                updateLicenseInfo(path, licenseInfo, userId, LicenseParameterValues.LICENSE_IS_DEFAULT);
            }
            about.setLicenseAuthorize(Constant.ABOUT_NOT_AUTHORISED);
        } else {
            // 保存非试用License
            licenseInfo = licenseDao.findNonDefaultLicense();
            if (ObjectUtils.isEmpty(licenseInfo)) {
                licenseInfo = new LicenseInfo(NineteenUUIDUtils.uuid(), path,
                        LicenseParameterValues.LICENSE_IS_NOT_DEFAULT, Timestamp.valueOf(LocalDateTime.now()),
                        userId, null, null);
                licenseDao.insert(licenseInfo);
            } else {
                updateLicenseInfo(path, licenseInfo, userId, LicenseParameterValues.LICENSE_IS_NOT_DEFAULT);
            }
            about.setLicenseAuthorize(Constant.ABOUT_AUTHORISED);
        }

        //更新关于信息
        if(!ObjectUtils.isEmpty(about.getLicenseAuthorize())) {
            aboutService.updateAboutById(about);
        }

        //操作日志
        addLicenseOperationLog(licenseInfo.getLicenseId(),licenseXml.getOriginalFilename());

        //查询用户，设施当前数量值存入Redis
        refreshLicenseThreshold();
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(LicenseI18n.LICENSE_SUCCESSFUL_UPLOADED));
    }

    /**
     * 记录license操作日志
     * @param licenseId
     * @param licenseName
     */
    private void addLicenseOperationLog(String licenseId, String licenseName) {
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("licenseId");
        addLogBean.setDataName("licenseName");
        addLogBean.setFunctionCode("2105101");
        addLogBean.setOptObjId(licenseId);
        addLogBean.setOptObj(licenseName);
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 查询当前设施，用户，在线数存入Redis
     * @return
     */
    @Override
    public LicenseThreshold refreshLicenseThreshold() {
        //用户数，在线用户数
        UserCount userCount = userFeign.queryUserNumber();
        //设施数
        Integer deviceCount = deviceFeign.queryCurrentDeviceCount();
        if (userCount == null || deviceCount == null) {
            return new LicenseThreshold();
        }
        //更新当前活跃值到redis
        updateLicenseThresholdNum(LicenseType.USER, userCount.getUserAccountNumber());
        updateLicenseThresholdNum(LicenseType.ONLINE, userCount.getOnlineUserNumber());
        updateLicenseThresholdNum(LicenseType.DEVICE, deviceCount);
        //返回对象给前端显示
        String userNum = userCount.getUserAccountNumber() == null ? "" : userCount.getUserAccountNumber().toString();
        String onlineNum = userCount.getOnlineUserNumber() == null ? "" : userCount.getOnlineUserNumber().toString();
        return new LicenseThreshold(userNum, onlineNum, deviceCount.toString());
    }

    /**
     * 校验license值合法性
     *
     * @param license
     * @return
     */
    private boolean checkLicense(License license) {
        if (StringUtils.isEmpty(license.beginTime) || StringUtils.isEmpty(license.endTime)
                || StringUtils.isEmpty(license.maxDeviceNum) || StringUtils.isEmpty(license.maxOnlineNum)
                || StringUtils.isEmpty(license.maxUserNum)) {
            return false;
        }
        //校验beginTime,endTime为yyyy-mm-dd日期
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            formatter.parse(license.beginTime);
            formatter.parse(license.endTime);
        } catch (Throwable e) {
            return false;
        }

        //校验maxDeviceNum，maxOnlineNum，maxUserNum为整数
        try {
            Integer.parseInt(license.maxDeviceNum);
            Integer.parseInt(license.maxOnlineNum);
            Integer.parseInt(license.maxUserNum);
        } catch (NumberFormatException e) {
            return false;
        }

        //校验tryRemark为Y或N
        if(!LicenseParameterValues.TRY_REMARK_YES.equalsIgnoreCase(license.tryRemark)
                && !LicenseParameterValues.TRY_REMARK_NO.equalsIgnoreCase(license.tryRemark)) {
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
//        license.setLicenseThreshold(getLicenseThresholdFromRedis());
        license.setLicenseThreshold(refreshLicenseThreshold());

        //试用标记改为True,false
        if (license.getTryRemark().equalsIgnoreCase(LicenseParameterValues.TRY_REMARK_YES)) {
            license.setTryRemark(I18nUtils.getSystemString(LicenseParameterValues.TRY_REMARK_TRUE));
        } else {
            license.setTryRemark(I18nUtils.getSystemString(LicenseParameterValues.TRY_REMARK_FALSE));
        }
        //返回给前端需要的值
        List<LicenseResult> licenseResults = new ArrayList<>();
        licenseResults.add(new LicenseResult(LicenseParameter.BEGIN_TIME, I18nUtils.getSystemString(LicenseDesc.BEGIN_TIME),
                license.getBeginTime()));
        licenseResults.add(new LicenseResult(LicenseParameter.END_TIME, I18nUtils.getSystemString(LicenseDesc.END_TIME),
                license.getEndTime()));
        licenseResults.add(new LicenseResult(LicenseParameter.TRIAL_LICENSE, I18nUtils.getSystemString(LicenseDesc.IS_TRY),
                license.getTryRemark()));
        licenseResults.add(new LicenseResult(LicenseParameter.MAX_DEVICE_NUM, I18nUtils.getSystemString(LicenseDesc.MAX_DEVICE),
                license.getMaxDeviceNum(), license.getLicenseThreshold().getDeviceNum()));
        licenseResults.add(new LicenseResult(LicenseParameter.MAX_USER_NUM, I18nUtils.getSystemString(LicenseDesc.MAX_USER),
                license.getMaxUserNum(), license.getLicenseThreshold().getUserNum()));
        licenseResults.add(new LicenseResult(LicenseParameter.MAX_ONLINE, I18nUtils.getSystemString(LicenseDesc.MAX_ONLINE),
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

        // redis获取license失败，则从数据库获取，并更新到Redis
        if (ObjectUtils.isEmpty(license)) {
            license = updateLicenseToRedis();
        }
        return license;
    }


    /**
     * 初始化License数据到Redis中
     * @return
     */
    @Override
    public License updateLicenseToRedis() throws FilinkLicenseException {
        //从数据库查询license数据
        License license = getLicenseFromDatabase();

        //校验license数据
        if(ObjectUtils.isEmpty(license)) {
            if (ObjectUtils.isEmpty(license)) {
                throw new FilinkLicenseException(I18nUtils.getSystemString(LicenseI18n.ERROR_GETTING_LICENSE_FILE));
            }
        }
        //保存license到redis
        if (!saveLicenseToRedis(license)) {
            throw new FilinkLicenseException(I18nUtils.getSystemString(LicenseI18n.ERROR_SAVING_LICENSE_TO_REDIS));
        }
        return license;
    }

    private License getLicenseFromDatabase() {
        //先查询非默认的License,再查询默认的
        LicenseInfo licenseInfo = licenseDao.findNonDefaultLicense();
        if(ObjectUtils.isEmpty(licenseInfo)) {
            licenseInfo = licenseDao.findDefaultLicense();
            if(ObjectUtils.isEmpty(licenseInfo)) {
                return null;
            }
        }

        License license;
        //根据文件路径获取文件
        String path = fdfsFeign.getBasePath() + licenseInfo.getPath();
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
            throw new FilinkLicenseException(I18nUtils.getSystemString(LicenseI18n.ERROR_GETTING_LICENSE_FILE));
        } catch (DocumentException e) {
            throw new FilinkLicenseException(I18nUtils.getSystemString(LicenseI18n.ERROR_ANALYZING_LICENSE_FILE));
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
    public boolean validateLicenseTime() {
        License license;
        try {
            license = getCurrentLicense();
        } catch (Throwable e) {
            log.error(e.getMessage());
            return false;
        }
        if (license != null && license.getBeginTime() != null && license.getEndTime() != null) {
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

        switch (licenseFeignBean.getOperationTarget()) {
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
    }


    /**
     * 更新Redis license活跃值
     *
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
        Object numObj = RedisUtils.hGet(Constant.LICENSE_THRESHOLD, thresholdItem);
        //赋予默认值
        if (numObj == null) {
            RedisUtils.hSet(Constant.LICENSE_THRESHOLD, thresholdItem, thresholdDefault);
        }
        int delta = 0;
        if (LicenseType.ADD.equalsIgnoreCase(licenseFeignBean.operationWay.getValue())) {
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
        if (ObjectUtils.isEmpty(licenseThresholdFeignBean) || StringUtils.isEmpty(licenseThresholdFeignBean.getName())) {
            return false;
        }
        return updateLicenseThresholdNum(licenseThresholdFeignBean.getName(), licenseThresholdFeignBean.getNum());
    }

    /**
     * 修改LicenseThreshold
     *
     * @param name
     * @param num
     * @return
     */
    private boolean updateLicenseThresholdNum(String name, int num) {
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
        log.warn(">>>>>>>>>>>>>license使用量中名称不对应");
        return false;
    }

    /**
     * 从缓存中获取LicenseThreshold
     *
     * @return License
     */
    private LicenseThreshold getLicenseThresholdFromRedis() {
        //从redis获取
        Object userNumObj = (RedisUtils.hGet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_USER_NUM));
        String userNum = userNumObj == null ? null : userNumObj.toString();

        Object onlineNumObj = RedisUtils.hGet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_ONLINE);
        String onlineNum = onlineNumObj == null ? null : onlineNumObj.toString();

        Object deviceNumOjb = RedisUtils.hGet(Constant.LICENSE_THRESHOLD, LicenseParameter.THRESHOLD_DEVICE_NUM);
        String deviceNum = deviceNumOjb == null ? null : deviceNumOjb.toString();

        //redis没有查询到数据，查询Threshold存入redis
        if (StringUtils.isEmpty(userNum) || StringUtils.isEmpty(onlineNum) || StringUtils.isEmpty(deviceNum)) {
            return refreshLicenseThreshold();
        }
        return new LicenseThreshold(userNum, onlineNum, deviceNum);
    }

    /**
     * license存入缓存
     *
     * @param license License
     * @return boolean
     */
    private boolean saveLicenseToRedis(License license) {
        try {
            RedisUtils.hSet(Constant.LICENSE, LicenseParameter.TRY_REMARK, license.getTryRemark());
            RedisUtils.hSet(Constant.LICENSE, LicenseParameter.BEGIN_TIME, license.getBeginTime());
            RedisUtils.hSet(Constant.LICENSE, LicenseParameter.END_TIME, license.getEndTime());
            RedisUtils.hSet(Constant.LICENSE, LicenseParameter.MAX_USER_NUM, license.getMaxUserNum());
            RedisUtils.hSet(Constant.LICENSE, LicenseParameter.MAX_ONLINE, license.getMaxOnlineNum());
            RedisUtils.hSet(Constant.LICENSE, LicenseParameter.MAX_DEVICE_NUM, license.getMaxDeviceNum());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
     *
     * @param key       Redis中的key
     * @param item      Redis中的item
     * @param delta     修改增量值
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
        if (StringUtils.isEmpty(lockIdentifier)) {
            throw new FilinkLicenseException(I18nUtils.getSystemString(LicenseI18n.ERROR_GETTING_REDIS_LOCK));
        }
        Object o = RedisUtils.hGet(key, item);
        if (o == null) {
            RedisUtils.releaseLock(lockKey, lockIdentifier);
            throw new FilinkLicenseException(I18nUtils.getSystemString(LicenseI18n.MISSING_VALUE_FROM_REDIS));
        }
        int currentNum;
        if (o instanceof Integer) {
            currentNum = (Integer) o;
        } else if (o instanceof String) {
            try {
                currentNum = Integer.valueOf((String) o);
            } catch (NumberFormatException e) {
                throw new FilinkLicenseException(I18nUtils.getSystemString(LicenseI18n.ERROR_PARAM_FORMAT_IN_REDIS));
            }
        } else {
            //Redis查出来不是Integer，也不是String
            RedisUtils.releaseLock(lockKey, lockIdentifier);
            throw new FilinkLicenseException(I18nUtils.getSystemString(LicenseI18n.ERROR_PARAM_FORMAT_IN_REDIS));
        }
        if (currentNum + delta <= threshold || delta < 0) {
            RedisUtils.hSet(key, item, currentNum + delta);
            RedisUtils.releaseLock(lockKey, lockIdentifier);
            return true;
        }
        RedisUtils.releaseLock(lockKey, lockIdentifier);
        return false;
    }
}
