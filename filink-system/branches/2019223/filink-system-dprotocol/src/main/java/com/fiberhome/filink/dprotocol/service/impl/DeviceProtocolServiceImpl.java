package com.fiberhome.filink.dprotocol.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocol;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocolFileBean;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocolI18n;
import com.fiberhome.filink.dprotocol.bean.ProtocolVersionBean;
import com.fiberhome.filink.dprotocol.bean.xmlBean.FiLinkProtocolBean;
import com.fiberhome.filink.dprotocol.dao.DeviceProtocolDao;
import com.fiberhome.filink.dprotocol.exception.*;
import com.fiberhome.filink.dprotocol.service.DeviceProtocolService;
import com.fiberhome.filink.dprotocol.utils.FiLinkProtocolResolver;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.utils.LogConstants;
import com.fiberhome.filink.oss_api.api.FdfsFeign;
import com.fiberhome.filink.oss_api.bean.DeviceProtocolDto;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.UUIDUtil;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-01-12
 */
@Service
public class DeviceProtocolServiceImpl extends ServiceImpl<DeviceProtocolDao, DeviceProtocol> implements DeviceProtocolService {


    /**
     * 自动注入设施协议dao
     */
    @Autowired
    private DeviceProtocolDao deviceProtocolDao;
    /**
     * 自动注入文件服务远程调用
     */
    @Autowired
    private FdfsFeign fdfsFeign;
    /**
     * 自动注入文件大小限制
     */
    @Autowired
    private DeviceProtocolFileBean deviceProtocolFileBean;
    /**
     * 自动注入日志服务
     */
    @Autowired
    private LogProcess logProcess;
    /**国际化信息硬件版本*/
    private static final String HARDWARE_VERSION = "${hardwareVersion}";
    /**国际化信息软件版本*/
    private static final String SOFTWARE_VERSION = "${softwareVersion}";
    /**国际化信息文件大小*/
    private static final String FILE_SIZE = "${size}";
    /**国际化信息文件名称长度*/
    private static final String FILE_NAME_LENGTH = "${nameLength}";
    /**XML设施协议名称*/
    private static final String PROTOCOL_NAME = "protocolName";
    /**XML设施协议ID*/
    private static final String  PROTOCOL_ID = "protocolId";
    /**新增设施协议日志XMLFunctionCode*/
    private static final String  ADD_FUNCTION_CODE = "2102101";
    /**修改设施协议日志XMLFunctionCode*/
    private static final String  UPDATE_FUNCTION_CODE = "2102102";
    /**删除设施协议日志XMLFunctionCode*/
    private static final String  DELETE_FUNCTION_CODE = "2102103";
    /**用户ID请求头*/
    private static final String REQUEST_USER = "userId";
    /**设施协议名称正则*/
    private static final String NAME_REGEX = "^(?!_)[\\w\\s_\\u4e00-\\u9fa5]{1,32}$";
    /**
     * 新增设施协议
     *
     * @param protocolName 设施协议名称
     * @param file         设施协议文件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addDeviceProtocol(String protocolName, MultipartFile file){
        //检查设施文件
        checkFile(file);
        //去除尾部空格并校验名称
        protocolName = checkRegexName(protocolName);
        //查询是否存在该设施协议名称
        hasDeviceProtocolName(protocolName, null);
        //解析XML文件
        FiLinkProtocolBean fiLinkProtocolBean = readXMLDeviceProtocol(file);
        DeviceProtocol deviceProtocol = new DeviceProtocol();
        deviceProtocol.setHardwareVersion(fiLinkProtocolBean.getHardwareVersion());
        deviceProtocol.setSoftwareVersion(fiLinkProtocolBean.getSoftwareVersion());
        //查询是否存在该设施协议文件
        hasDeviceProtocolVersion(deviceProtocol);
        //上传至oss文件服务器
        String url = updateFile(file);
        deviceProtocol.setProtocolId(UUIDUtil.getInstance().UUID32());
        deviceProtocol.setProtocolName(protocolName);
        deviceProtocol.setFileName(file.getOriginalFilename());
        deviceProtocol.setFileLength(String.valueOf(file.getSize()));
        deviceProtocol.setFileDownloadUrl(url);
        //获取当前用户
        String user = getUserId();
        deviceProtocol.setCreateUser(user);
        int result = deviceProtocolDao.addDeviceProtocol(deviceProtocol);
        if (result != 1) {
            //删除文件
            List<String> list = new ArrayList<>();
            list.add(url);
            fdfsFeign.deleteFilesPhy(list);
            throw new FilinkDeviceProtocolAddException();
        }
        //将解析数据存入Redis缓存
        String key = fiLinkProtocolBean.getHardwareVersion() + fiLinkProtocolBean.getSoftwareVersion();
        RedisUtils.set(key, fiLinkProtocolBean);
        //记录日志
        addLog(deviceProtocol, ADD_FUNCTION_CODE, LogConstants.DATA_OPT_TYPE_ADD);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_ADD_SUCCESS));
    }

    /**
     * 查询上传设施协议文件限制
     *
     * @return 结果
     */
    @Override
    public Result queryFileLimit() {
        //文件大小B转化成KB
        String size= String.valueOf(deviceProtocolFileBean.getSize()/1024);
        //文件大小和名称长度国际化提示信息获取
        String sizeI18n = I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_SIZE);
        String nameI18n = I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_NAME);
        //替换信息中数据
        sizeI18n = sizeI18n.replace(FILE_SIZE, size);
        nameI18n = nameI18n.replace(FILE_NAME_LENGTH, String.valueOf(deviceProtocolFileBean.getNameLength()));
        //封装
        deviceProtocolFileBean.setSizeI18n(sizeI18n);
        deviceProtocolFileBean.setNameI18n(nameI18n);
        return ResultUtils.success(deviceProtocolFileBean);
    }

    /**
     * 修改设施协议
     *
     * @param deviceProtocol 设施协议信息
     * @param file 设施协议文件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateDeviceProtocol(DeviceProtocol deviceProtocol, MultipartFile file) {
        //检查设施文件是否有内容
        checkFile(file);
        //去除尾部空格并校验名称
        String protocolName = deviceProtocol.getProtocolName();
        deviceProtocol.setProtocolName(checkRegexName(protocolName));
        //通过ID查看设施协议是否存在
        DeviceProtocol deviceProtocolDb = deviceProtocolDao.getDeviceProtocolById(deviceProtocol.getProtocolId());
        hasDeviceProtocol(deviceProtocolDb);
        //查询是否存在该设施协议名称
        hasDeviceProtocolName(deviceProtocol.getProtocolName(), deviceProtocol.getProtocolId());
        //解析XML文件
        FiLinkProtocolBean fiLinkProtocolBean = readXMLDeviceProtocol(file);
        deviceProtocol.setHardwareVersion(fiLinkProtocolBean.getHardwareVersion());
        deviceProtocol.setSoftwareVersion(fiLinkProtocolBean.getSoftwareVersion());
        //校验是否已存在该设施协议
        hasDeviceProtocolVersion(deviceProtocol);
        //上传至文件服务器
        String url = updateFile(file);
        deviceProtocol.setFileName(file.getOriginalFilename());
        deviceProtocol.setFileLength(String.valueOf(file.getSize()));
        deviceProtocol.setFileDownloadUrl(url);
        //获取当前用户
        String user = getUserId();
        deviceProtocol.setUpdateUser(user);
        int result = deviceProtocolDao.updateById(deviceProtocol);
        if (result != 1) {
            //删除文件
            List<String> list = new ArrayList<>();
            list.add(url);
            fdfsFeign.deleteFilesPhy(list);
            throw new FilinkDeviceProtocolUpdateException();
        }
        //删除原文件
        List<String> list = new ArrayList<>();
        list.add(deviceProtocolDb.getFileDownloadUrl());
        fdfsFeign.deleteFilesPhy(list);
        //修改Redis缓存
        String key = deviceProtocolDb.getHardwareVersion() + deviceProtocolDb.getSoftwareVersion();
        RedisUtils.remove(key);
        key = fiLinkProtocolBean.getHardwareVersion() + fiLinkProtocolBean.getSoftwareVersion();
        RedisUtils.set(key, fiLinkProtocolBean);
        //记录日志
        addLog(deviceProtocol, UPDATE_FUNCTION_CODE, LogConstants.DATA_OPT_TYPE_UPDATE);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_UPDATE_SUCCESS));
    }

    /**
     * 修改设施协议名称
     *
     * @param deviceProtocol 设施协议信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateDeviceProtocol(DeviceProtocol deviceProtocol) {
        //去除尾部空格并校验名称
        String protocolName = deviceProtocol.getProtocolName();
        deviceProtocol.setProtocolName(checkRegexName(protocolName));
        //通过ID查看设施协议是否存在
        DeviceProtocol deviceProtocolDb = deviceProtocolDao.getDeviceProtocolById(deviceProtocol.getProtocolId());
        hasDeviceProtocol(deviceProtocolDb);
        //查询是否存在该设施协议名称
        hasDeviceProtocolName(deviceProtocol.getProtocolName(), deviceProtocol.getProtocolId());
        //获取当前用户
        String user = getUserId();
        deviceProtocol.setUpdateUser(user);
        int result = deviceProtocolDao.updateById(deviceProtocol);
        if (result != 1) {
            throw new FilinkDeviceProtocolUpdateException();
        }
        //记录日志
        addLog(deviceProtocol, UPDATE_FUNCTION_CODE, LogConstants.DATA_OPT_TYPE_UPDATE);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_UPDATE_SUCCESS));
    }

    /**
     * 删除设施协议
     *
     * @param protocolIds 设施协议ID List
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteDeviceProtocol(List<String> protocolIds) {
        //判断是否存在该协议
        List<DeviceProtocol> deviceProtocolDbs = deviceProtocolDao.getDeviceProtocolListById(protocolIds);
        if (deviceProtocolDbs == null || deviceProtocolDbs.size() != protocolIds.size()) {
            throw new FilinkDeviceProtocolNotExistException();
        }
       //逻辑删除文件(转换实体类)
        List<DeviceProtocolDto> deviceProtocolDtos = selectUrls(deviceProtocolDbs);
        deviceProtocolDtos = fdfsFeign.deleteFilesLogic(deviceProtocolDtos);
        //判断是否删除成功
        if(deviceProtocolDtos == null || deviceProtocolDtos.size() != deviceProtocolDbs.size()) {
            throw new FilinkDeviceProtocolDeleteException();
        }
        //转换为逻辑删除对象
        List<DeviceProtocol> deviceProtocols = selectDbUrls(deviceProtocolDtos);
        //获取用户信息
        String updateUser = getUserId();
        //数据库逻辑删除
        int result = deviceProtocolDao.batchDeleteDeviceProtocolList(deviceProtocols, updateUser);
        //判断删除是否成功
        if (result != deviceProtocolDbs.size()) {
            //删除逻辑文件
            deleteFiles(deviceProtocols);
            throw new FilinkDeviceProtocolDeleteException();
        }
        //删除原文件
        deleteFiles(deviceProtocolDbs);
        //修改Redis缓存
        for (DeviceProtocol deviceP : deviceProtocolDbs) {
            String key = deviceP.getHardwareVersion() + deviceP.getSoftwareVersion();
            RedisUtils.remove(key);
        }
        //记录日志
        addLogList(deviceProtocolDbs);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_DELETE_SUCCESS));
    }

    /**
     * 上传文件
     * @param file 文件
     * @return 文件路径
     */
    private String updateFile(MultipartFile file) {
        //上传至oss文件服务器
        String url = fdfsFeign.uploadFile(file);
        //校验是否上传成功
        if(StringUtils.isEmpty(url)) {
            throw new FilinkDeviceProtocolFileUploadException();
        }
        return url;
    }

    /**
     * 批量物理删除文件服务器文件
     *
     * @param deviceProtocols 设施协议信息List
     */
    private void deleteFiles(List<DeviceProtocol> deviceProtocols) {
        List<String> fileUtils = new ArrayList<>();
        for (DeviceProtocol deviceProtocol : deviceProtocols) {
            fileUtils.add(deviceProtocol.getFileDownloadUrl());
        }
        fdfsFeign.deleteFilesPhy(fileUtils);
    }

    /**
     * 复制设施协议实体类到文件服务实体
     * @param deviceProtocolDbs 设施协议信息List
     * @return 文件路径List
     */
    private List<DeviceProtocolDto> selectUrls(List<DeviceProtocol> deviceProtocolDbs) {
        List<DeviceProtocolDto> deviceProtocolDtos = new ArrayList<>();
        for (DeviceProtocol deviceProtocol : deviceProtocolDbs) {
            DeviceProtocolDto deviceProtocolDto = new DeviceProtocolDto();
            BeanUtils.copyProperties(deviceProtocol, deviceProtocolDto);
            deviceProtocolDtos.add(deviceProtocolDto);
        }
        return deviceProtocolDtos;
    }

    /**
     * 复制文件服务实体到设施协议实体类
     * @param deviceProtocolDtos 文件服务实体信息List
     * @return 文件路径List
     */
    private List<DeviceProtocol> selectDbUrls(List<DeviceProtocolDto> deviceProtocolDtos) {
        List<DeviceProtocol> deviceProtocols = new ArrayList<>();
        for (DeviceProtocolDto deviceProtocolDto : deviceProtocolDtos) {
            DeviceProtocol deviceProtocol = new DeviceProtocol();
            BeanUtils.copyProperties(deviceProtocolDto, deviceProtocol);
            deviceProtocols.add(deviceProtocol);
        }
        return deviceProtocols;
    }

    /**
     * 查询设施协议列表
     *
     * @return 查询结果
     */
    @Override
    public Result queryDeviceProtocolList() {
        List<DeviceProtocol> deviceProtocols = deviceProtocolDao.queryDeviceProtocolList();
        if (ObjectUtils.isEmpty(deviceProtocols)) {
            deviceProtocols = new ArrayList<>();
        }
        return ResultUtils.success(deviceProtocols);
    }

    /**
     * 查询缓存设施协议文件信息
     * @param protocolVersionBean 设施协议文件信息缓存key
     * @return 设施协议文件信息
     */
    @Override
    public FiLinkProtocolBean queryProtocolXmlBean(ProtocolVersionBean protocolVersionBean) {
        String key = protocolVersionBean.getHardwareVersion() + protocolVersionBean.getSoftwareVersion();
        FiLinkProtocolBean fiLinkProtocolBean = (FiLinkProtocolBean)RedisUtils.get(key);
        return fiLinkProtocolBean;
    }

    /**
     * 获取国际化信息
     * @param deviceProtocol 设施协议信息
     * @return 国际化信息
     */
    private String getFileExistMsg(DeviceProtocol deviceProtocol) {
        String msg = I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_EXIST);
        msg = msg.replace(HARDWARE_VERSION, deviceProtocol.getHardwareVersion());
        msg = msg.replace(SOFTWARE_VERSION, deviceProtocol.getSoftwareVersion());
        return msg;
    }

    /**
     * 读取XML文件中软硬件版本信息
     *
     * @param file xml文件流MultipartFile
     * @return 软硬件版本信息
     */
    private FiLinkProtocolBean readXMLDeviceProtocol(MultipartFile file){
        //读取XML文件信息
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(file.getInputStream());
        } catch (Exception e) {
            throw new FilinkDeviceProtocolFileFormatException();
        }
        FiLinkProtocolResolver protocolResolver = new FiLinkProtocolResolver();
        FiLinkProtocolBean fiLinkProtocolBean = protocolResolver.resolve(document);
        //校验文件内容
        if (checkVersion(fiLinkProtocolBean)) {
            throw new FilinkDeviceProtocolFileContentException();
        }
        return fiLinkProtocolBean;
    }

    /**
     *新增日志
     * @author hedongwei@wistronits.com
     * @since 2019/1/30
     */
    private void addLogList(List<DeviceProtocol> deviceProtocols) {
        List<AddLogBean> addLogBeanList = new ArrayList<>();
        for (DeviceProtocol deviceP : deviceProtocols){
            AddLogBean addLogBean = addLogBean(deviceP, DELETE_FUNCTION_CODE, LogConstants.DATA_OPT_TYPE_DELETE);
            addLogBeanList.add(addLogBean);
        }
        //新增操作日志
        logProcess.addOperateLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     *新增日志
     * @author hedongwei@wistronits.com
     * @since 2019/1/30
     */
    private void addLog(DeviceProtocol deviceProtocol, String functionCode, String logConstants) {
        AddLogBean addLogBean = addLogBean(deviceProtocol, functionCode, logConstants);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }
    /**
     *新增日志
     * @author hedongwei@wistronits.com
     * @since 2019/1/30
     */
    private AddLogBean addLogBean(DeviceProtocol deviceProtocol, String functionCode, String logConstants) {
        //获取日志类型
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(PROTOCOL_ID);
        addLogBean.setDataName(PROTOCOL_NAME);
        //获得操作对象名称
        addLogBean.setOptObj(deviceProtocol.getProtocolName());
        addLogBean.setFunctionCode(functionCode);
        //获得操作对象id
        addLogBean.setOptObjId(deviceProtocol.getProtocolId());
        //操作为新增
        addLogBean.setDataOptType(logConstants);
        return addLogBean;
    }


    /**
     * 查询是否存在该设施协议名称
     * @param protocolName 施协议名称
     */
    private void hasDeviceProtocolName(String protocolName, String protocolId) {
        String protocolIdDb = deviceProtocolDao.queryDeviceProtocolByName(protocolName);
        //判断是否有该名称
        if(!StringUtils.isEmpty(protocolIdDb)){
            //该名称设施是否是这个设施自己
            if (protocolId != null && protocolId.equals(protocolIdDb)) {
                return;
            }
            throw new FilinkDeviceProtocolNameExistException();
        }
    }

    /**
     * 查询是否存在该设施协议名称
     * @param deviceProtocol 设施协议
     */
    private void hasDeviceProtocolVersion(DeviceProtocol deviceProtocol) {
        String protocolId = deviceProtocolDao.queryDeviceProtocolByVersion(deviceProtocol);
        if(!StringUtils.isEmpty(protocolId)) {
            //该名称设施是否是这个设施自己
            if (deviceProtocol.getProtocolId() != null && deviceProtocol.getProtocolId().equals(protocolId)) {
                return;
            }
            throw new FilinkDeviceProtocolFileExistException(getFileExistMsg(deviceProtocol));
        }
    }


    /**
     * 判断是否存在该协议
     * @param deviceProtocol 设施协议
     */
    private void hasDeviceProtocol(DeviceProtocol deviceProtocol) {
        if (ObjectUtils.isEmpty(deviceProtocol)) {
            throw new FilinkDeviceProtocolNotExistException();
        }
    }

    /**
     * 校验文件
     * @param file 传入文件
     */
    private void checkFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FilinkDeviceProtocolFileContentException();
        }
        if (file.getOriginalFilename().length() > deviceProtocolFileBean.getNameLength()) {
            throw new FilinkDeviceProtocolFileNameException();
        }
        if (file.getSize() > deviceProtocolFileBean.getSize()) {
            throw new FilinkDeviceProtocolFileSizeException();
        }
    }
    /**
     * 校验设施协议名称
     *
     * @param protocolName 设施协议名称
     */
    private String checkRegexName(String protocolName) {
        //去除尾部空格
        protocolName = protocolName.replaceAll("\\s+$","");
        //校验正则
        if (!protocolName.matches(NAME_REGEX)) {
            throw new FilinkDeviceProtocolNameException();
        }
        return protocolName;
    }
    /**
     * 检查是否存在硬件和软件信息
     *
     * @param fiLinkProtocolBean 设施协议信息
     * @return true存在 flase失败
     */
    private boolean checkVersion(FiLinkProtocolBean fiLinkProtocolBean) {
        //判断解析是否成功
        if (ObjectUtils.isEmpty(fiLinkProtocolBean)) {
            return true;
        }
        return StringUtils.isEmpty(fiLinkProtocolBean.getHardwareVersion()) || StringUtils.isEmpty(fiLinkProtocolBean.getSoftwareVersion());
    }

    /**
     * 获取用户ID
     * @return 用户ID
     */
    private String getUserId() {
        String userId = null;
        //获取请求
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            //获取请求头
            HttpServletRequest request = requestAttributes.getRequest();
            userId = request.getHeader(REQUEST_USER);
        }
        return userId;
    }
}
