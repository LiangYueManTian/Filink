package com.fiberhome.filink.dprotocol.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocol;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocolFileBean;
import com.fiberhome.filink.dprotocol.constant.DeviceProtocolConstants;
import com.fiberhome.filink.dprotocol.constant.DeviceProtocolI18n;
import com.fiberhome.filink.dprotocol.dao.DeviceProtocolDao;
import com.fiberhome.filink.dprotocol.exception.*;
import com.fiberhome.filink.dprotocol.service.DeviceProtocolService;
import com.fiberhome.filink.dprotocol.utils.HexUtil;
import com.fiberhome.filink.filinkoceanconnectapi.feign.OceanConnectFeign;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.onenetapi.api.OneNetFeign;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.ossapi.bean.FileBean;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.MpQueryHelper;
import com.fiberhome.filink.stationapi.bean.ProtocolDto;
import com.fiberhome.filink.stationapi.feign.FiLinkStationFeign;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 *     设施协议服务实现类
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
     *自动注入station服务远程调用
     */
    @Autowired
    private FiLinkStationFeign stationFeign;
    /**
     *自动注入OceanConnect服务远程调用
     */
    @Autowired
    private OceanConnectFeign oceanConnectFeign;
    /**
     *自动注入OneNet服务远程调用
     */
    @Autowired
    private OneNetFeign oneNetFeign;
    /**
     * 自动注入日志服务
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * 校验设施协议名称是否重复
     *
     * @param deviceProtocol 设施协议
     * @return 是否重复
     */
    @Override
    public Result checkDeviceProtocolNameRepeat(DeviceProtocol deviceProtocol) {
        hasDeviceProtocolName(deviceProtocol);
        return ResultUtils.success();
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
        sizeI18n = sizeI18n.replace(DeviceProtocolConstants.FILE_SIZE, size);
        nameI18n = nameI18n.replace(DeviceProtocolConstants.FILE_NAME_LENGTH, String.valueOf(deviceProtocolFileBean.getNameLength()));
        //封装
        deviceProtocolFileBean.setSizeI18n(sizeI18n);
        deviceProtocolFileBean.setNameI18n(nameI18n);
        return ResultUtils.success(deviceProtocolFileBean);
    }

    /**
     * 新增设施协议
     *
     * @param protocolName 设施协议名称
     * @param file         设施协议文件
     * @return 结果
     */
    @Override
    public Result addDeviceProtocol(String protocolName, MultipartFile file){
        DeviceProtocol deviceProtocol = new DeviceProtocol();
        deviceProtocol.setProtocolName(protocolName);
        //校验并上传设施协议文件
        checkAddOrUpdate(deviceProtocol, file);
        //远程调用解析数据存入Redis缓存
        ProtocolDto protocolDto = getProtocolDto(file);
        if (!stationFeign.addProtocol(protocolDto)) {
            deleteFile(deviceProtocol);
            throw new FilinkDeviceProtocolAddException();
        }
        com.fiberhome.filink.filinkoceanconnectapi.bean.ProtocolDto ocean = new com.fiberhome.filink.filinkoceanconnectapi.bean.ProtocolDto();
        ocean.setFileHexData(protocolDto.getFileHexData());
        com.fiberhome.filink.onenetapi.bean.ProtocolDto oneNet = new com.fiberhome.filink.onenetapi.bean.ProtocolDto();
        oneNet.setFileHexData(protocolDto.getFileHexData());
        oceanConnectFeign.addProtocol(ocean);
        oneNetFeign.addProtocol(oneNet);
        //封装数据
        deviceProtocol.setProtocolId(NineteenUUIDUtils.uuid());
        deviceProtocol.setFileName(file.getOriginalFilename());
        deviceProtocol.setFileLength(String.valueOf(file.getSize()));
        //获取当前用户
        deviceProtocol.setCreateUser(RequestInfoUtils.getUserId());
        //新增设施协议
        Integer result = deviceProtocolDao.addDeviceProtocol(deviceProtocol);
        if (result != 1) {
            List<DeviceProtocol> deviceProtocols = new ArrayList<>();
            deviceProtocols.add(deviceProtocol);
            deleteProtocolRedis(deviceProtocols);
            deleteFile(deviceProtocol);
            throw new FilinkDeviceProtocolAddException();
        }
        //记录日志
        addLog(deviceProtocol, DeviceProtocolConstants.ADD_FUNCTION_CODE, LogConstants.DATA_OPT_TYPE_ADD);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_ADD_SUCCESS));
    }

    /**
     * 校验是否可以新增或修改设施协议
     * @param deviceProtocol 设施协议
     * @param file 设施协议文件
     */
    private void checkAddOrUpdate(DeviceProtocol deviceProtocol, MultipartFile file) {
        //检查设施文件是否有内容,文件大小和名称
        if (file.isEmpty()) {
            throw new FilinkDeviceProtocolVersionException();
        }
        if (file.getOriginalFilename().length() > deviceProtocolFileBean.getNameLength()) {
            throw new FilinkDeviceProtocolFileNameException();
        }
        if (file.getSize() > deviceProtocolFileBean.getSize()) {
            throw new FilinkDeviceProtocolFileSizeException();
        }
        //查询是否存在该设施协议名称
        hasDeviceProtocolName(deviceProtocol);
        //解析XML文件
        readXMLDeviceProtocol(file, deviceProtocol);
        //查询是否存在该设施协议文件
        String protocolId = deviceProtocolDao.queryDeviceProtocolByVersion(deviceProtocol);
        if(!StringUtils.isEmpty(protocolId)) {
            throw new FilinkDeviceProtocolFileExistException(getFileExistMsg(deviceProtocol));
        }
        //上传至oss文件服务器
        String url = updateFile(file);
        deviceProtocol.setFileDownloadUrl(url);
    }

    /**
     * 修改设施协议
     *
     * @param deviceProtocol 设施协议信息
     * @param file 设施协议文件
     * @return 结果
     */
    @Override
    public Result updateDeviceProtocol(DeviceProtocol deviceProtocol, MultipartFile file) {
        //通过ID查看设施协议是否存在
        DeviceProtocol deviceProtocolDb = deviceProtocolDao.getDeviceProtocolById(deviceProtocol.getProtocolId());
        hasDeviceProtocol(deviceProtocolDb);
        //校验并上传设施协议文件
        checkAddOrUpdate(deviceProtocol, file);
        //远程调用解析数据存入Redis缓存
        ProtocolDto protocolDto = getProtocolDto(file);
        protocolDto.setHardwareVersion(deviceProtocolDb.getHardwareVersion());
        protocolDto.setSoftwareVersion(deviceProtocolDb.getSoftwareVersion());
        if (!stationFeign.updateProtocol(protocolDto)) {
            deleteFile(deviceProtocol);
            throw new FilinkDeviceProtocolUpdateException();
        }
        com.fiberhome.filink.filinkoceanconnectapi.bean.ProtocolDto ocean = new com.fiberhome.filink.filinkoceanconnectapi.bean.ProtocolDto();
        ocean.setHardwareVersion(deviceProtocolDb.getHardwareVersion());
        ocean.setFileHexData(protocolDto.getFileHexData());
        ocean.setSoftwareVersion(deviceProtocolDb.getSoftwareVersion());
        oceanConnectFeign.updateProtocol(ocean);
        com.fiberhome.filink.onenetapi.bean.ProtocolDto oneNet = new com.fiberhome.filink.onenetapi.bean.ProtocolDto();
        oneNet.setHardwareVersion(deviceProtocolDb.getHardwareVersion());
        oneNet.setFileHexData(protocolDto.getFileHexData());
        oneNet.setSoftwareVersion(deviceProtocolDb.getSoftwareVersion());
        oneNetFeign.updateProtocol(oneNet);
        //封装数据
        deviceProtocol.setFileName(file.getOriginalFilename());
        deviceProtocol.setFileLength(String.valueOf(file.getSize()));
        //获取当前用户
        deviceProtocol.setUpdateUser(RequestInfoUtils.getUserId());
        //修改设施协议
        Integer result = deviceProtocolDao.updateById(deviceProtocol);
        if (result != 1) {
            List<ProtocolDto> protocolDtoList = new ArrayList<>();
            protocolDtoList.add(protocolDto);
            stationFeign.deleteProtocol(protocolDtoList);
            List<com.fiberhome.filink.onenetapi.bean.ProtocolDto> oneNetList = new ArrayList<>();
            oneNetList.add(oneNet);
            oneNetFeign.deleteProtocol(oneNetList);
            List<com.fiberhome.filink.filinkoceanconnectapi.bean.ProtocolDto> oceanList = new ArrayList<>();
            oceanList.add(ocean);
            oceanConnectFeign.deleteProtocol(oceanList);
            deleteFile(deviceProtocol);
            throw new FilinkDeviceProtocolUpdateException();
        }
        //删除原文件
        deleteFile(deviceProtocolDb);
        //记录日志
        addLog(deviceProtocol, DeviceProtocolConstants.UPDATE_FUNCTION_CODE, LogConstants.DATA_OPT_TYPE_UPDATE);
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
        //通过ID查看设施协议是否存在
        DeviceProtocol deviceProtocolDb = deviceProtocolDao.getDeviceProtocolById(deviceProtocol.getProtocolId());
        hasDeviceProtocol(deviceProtocolDb);
        //获取当前用户
        deviceProtocol.setUpdateUser(RequestInfoUtils.getUserId());
        //查询是否存在该设施协议名称
        hasDeviceProtocolName(deviceProtocol);
        //修改设施协议
        Integer result = deviceProtocolDao.updateById(deviceProtocol);
        if (result != 1) {
            throw new FilinkDeviceProtocolUpdateException();
        }
        //记录日志
        addLog(deviceProtocol, DeviceProtocolConstants.UPDATE_FUNCTION_CODE, LogConstants.DATA_OPT_TYPE_UPDATE);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_UPDATE_SUCCESS));
    }

    /**
     * 删除设施协议
     *
     * @param protocolIds 设施协议ID List
     * @return 结果
     */
    @Override
    public Result deleteDeviceProtocol(List<String> protocolIds) {
        //判断是否存在该协议
        List<DeviceProtocol> deviceProtocolDbs = deviceProtocolDao.getDeviceProtocolListById(protocolIds);
        if (deviceProtocolDbs == null || deviceProtocolDbs.size() != protocolIds.size()) {
            throw new FilinkDeviceProtocolNotExistException();
        }
        //逻辑删除文件(转换实体类)
        List<FileBean> fileBeans = selectUrls(deviceProtocolDbs);
        fileBeans = fdfsFeign.deleteFilesLogic(fileBeans);
        //判断是否删除成功
        if (fileBeans == null || fileBeans.size() != deviceProtocolDbs.size()) {
            throw new FilinkDeviceProtocolFileUploadException();
        }
        //转换为逻辑删除对象
        List<DeviceProtocol> deviceProtocols = selectDbUrls(fileBeans);
        //获取用户信息
        String updateUser = RequestInfoUtils.getUserId();
        //数据库逻辑删除
        Integer result = deviceProtocolDao.batchDeleteDeviceProtocolList(deviceProtocols, updateUser);
        if (result < protocolIds.size()) {
            deleteFiles(deviceProtocols);
            throw new FilinkDeviceProtocolDeleteException();
        }
        //修改缓存
        deleteProtocolRedis(deviceProtocolDbs);
        //删除原文件
        deleteFiles(deviceProtocolDbs);
        //记录日志
        addLogList(deviceProtocolDbs);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_DELETE_SUCCESS));
    }

    /**
     * 修改缓存
     * @param deviceProtocols 设施协议List
     */
    private void deleteProtocolRedis( List<DeviceProtocol> deviceProtocols) {
        List<ProtocolDto> protocols = new ArrayList<>();
        List<com.fiberhome.filink.onenetapi.bean.ProtocolDto> oneNetList = new ArrayList<>();
        List<com.fiberhome.filink.filinkoceanconnectapi.bean.ProtocolDto> oceanList = new ArrayList<>();
        for (DeviceProtocol protocol : deviceProtocols) {
            ProtocolDto protocolDto = new ProtocolDto();
            com.fiberhome.filink.filinkoceanconnectapi.bean.ProtocolDto ocean = new com.fiberhome.filink.filinkoceanconnectapi.bean.ProtocolDto();
            com.fiberhome.filink.onenetapi.bean.ProtocolDto oneNet = new com.fiberhome.filink.onenetapi.bean.ProtocolDto();
            protocolDto.setHardwareVersion(protocol.getHardwareVersion());
            protocolDto.setSoftwareVersion(protocol.getSoftwareVersion());
            ocean.setHardwareVersion(protocol.getHardwareVersion());
            ocean.setSoftwareVersion(protocol.getSoftwareVersion());
            oneNet.setHardwareVersion(protocol.getHardwareVersion());
            oneNet.setSoftwareVersion(protocol.getSoftwareVersion());
            protocols.add(protocolDto);
            oneNetList.add(oneNet);
            oceanList.add(ocean);
        }
        oceanConnectFeign.deleteProtocol(oceanList);
        oneNetFeign.deleteProtocol(oneNetList);
        stationFeign.deleteProtocol(protocols);
    }

    /**
     * 查询设施协议列表
     * @param queryCondition 查询条件
     * @return 查询结果
     */
    @Override
    public Result queryDeviceProtocolList(QueryCondition<DeviceProtocol> queryCondition) {
        //封装逻辑删除查询
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("is_deleted");
        filterCondition.setOperator("neq");
        filterCondition.setFilterValue(1);
        queryCondition.getFilterConditions().add(filterCondition);
        // 无排序时的默认排序（当前按照创建时间降序）
        if (queryCondition.getSortCondition() == null || StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())) {
            SortCondition sortCondition = new SortCondition();
            sortCondition.setSortRule("desc");
            sortCondition.setSortField("create_time");
            queryCondition.setSortCondition(sortCondition);
        }
        // 构造分页条件
        Page page = MpQueryHelper.myBatiesBuildPage(queryCondition);
        // 构造过滤、排序等条件
        EntityWrapper wrapper =  MpQueryHelper.myBatiesBuildQuery(queryCondition);
        List<DeviceProtocol> deviceProtocols = deviceProtocolDao.selectPage(page, wrapper);
        Integer count = deviceProtocolDao.selectCount(wrapper);
        //判断是否有数据
        if (ObjectUtils.isEmpty(deviceProtocols)) {
            //没有数据替换null
            deviceProtocols = new ArrayList<>();
        }
        PageBean pageBean =  MpQueryHelper.myBatiesBuildPageBean(page, count, deviceProtocols);
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * 根据软硬件版本获取文件
     *@param deviceProtocol 设施协议
     * @return 文件16进制字符串
     */
    @Override
    public String queryProtocol(DeviceProtocol deviceProtocol) {
        String filePath = null;
        //根据软硬件版本查询下载路径
        String url = deviceProtocolDao.queryUrlByVersion(deviceProtocol);
        String basePath = fdfsFeign.getBasePath();
        if (StringUtils.isEmpty(basePath) ||StringUtils.isEmpty(url) ) {
            return filePath;
        }
        InputStream inputStream = null;
        url = basePath + url;
        try {
            //创建链接URL
            URL file = new URL(url);
            URLConnection urlConnection = file.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            //和获取输入流
            inputStream = urlConnection.getInputStream();
            //读取文件类容为字节数组
            byte[] tmpByte = new byte[1024];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len;
            while ((len = inputStream.read(tmpByte))!= -1){
                outputStream.write(tmpByte,0,len);
            }
            filePath = HexUtil.bytesToHexString(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭流
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
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
     * 物理删除文件服务器文件
     * @param deviceProtocol 设施协议信息
     */
    private void deleteFile(DeviceProtocol deviceProtocol) {
        List<DeviceProtocol> deviceProtocols = new ArrayList<>();
        deviceProtocols.add(deviceProtocol);
        deleteFiles(deviceProtocols);
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
    private List<FileBean> selectUrls(List<DeviceProtocol> deviceProtocolDbs) {
        List<FileBean> fileBeans = new ArrayList<>();
        for (DeviceProtocol deviceProtocol : deviceProtocolDbs) {
            FileBean fileBean = new FileBean();
            fileBean.setFileId(deviceProtocol.getProtocolId());
            fileBean.setFileUrl(deviceProtocol.getFileDownloadUrl());
            fileBeans.add(fileBean);
        }
        return fileBeans;
    }

    /**
     * 复制文件服务实体到设施协议实体类
     * @param fileBeans 文件服务实体信息List
     * @return 文件路径List
     */
    private List<DeviceProtocol> selectDbUrls(List<FileBean> fileBeans) {
        List<DeviceProtocol> deviceProtocols = new ArrayList<>();
        for (FileBean fileBean : fileBeans) {
            DeviceProtocol deviceProtocol = new DeviceProtocol();
            deviceProtocol.setProtocolId(fileBean.getFileId());
            deviceProtocol.setFileDownloadUrl(fileBean.getFileUrl());
            deviceProtocols.add(deviceProtocol);
        }
        return deviceProtocols;
    }

    /**
     * 获取国际化信息
     * @param deviceProtocol 设施协议信息
     * @return 国际化信息
     */
    private String getFileExistMsg(DeviceProtocol deviceProtocol) {
        String msg = I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_FILE_EXIST);
        msg = msg.replace(DeviceProtocolConstants.HARDWARE_VERSION, deviceProtocol.getHardwareVersion());
        msg = msg.replace(DeviceProtocolConstants.SOFTWARE_VERSION, deviceProtocol.getSoftwareVersion());
        return msg;
    }

    /**
     * 读取XML文件中软硬件版本信息
     *
     * @param file xml文件流MultipartFile
     */
    private void readXMLDeviceProtocol(MultipartFile file, DeviceProtocol deviceProtocol){
        try {
            //读取XML文件信息
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(file.getInputStream());
            Element root = document.getRootElement();
            //解析软硬件版本
            deviceProtocol.setHardwareVersion(root.elementText(DeviceProtocolConstants.XML_HARDWARE_VERSION));
            deviceProtocol.setSoftwareVersion(root.elementText(DeviceProtocolConstants.XML_SOFTWARE_VERSION));
        } catch (Exception e) {
            throw new FilinkDeviceProtocolFileFormatException();
        }
        //校验文件内容
        if (deviceProtocol.checkVersion()) {
            throw new FilinkDeviceProtocolVersionException();
        }
    }

    /**
     * 将文件转为16进制字符串
     * @param file 文件
     * @return ProtocolDto（16进制字符串）
     */
    private ProtocolDto getProtocolDto(MultipartFile file) {
        ProtocolDto protocolDto = new ProtocolDto();
        try {
            protocolDto.setFileHexData(HexUtil.bytesToHexString(file.getBytes()));
        } catch (IOException e) {
            throw new FilinkDeviceProtocolFileFormatException();
        }
        return protocolDto;
    }

    /**
     * 批量新增日志
     * @param deviceProtocols 设施协议信息List
     */
    private void addLogList(List<DeviceProtocol> deviceProtocols) {
        List<AddLogBean> addLogBeanList = new ArrayList<>();
        for (DeviceProtocol deviceP : deviceProtocols){
            AddLogBean addLogBean = addLogBean(deviceP, DeviceProtocolConstants.DELETE_FUNCTION_CODE, LogConstants.DATA_OPT_TYPE_DELETE);
            addLogBeanList.add(addLogBean);
        }
        //新增操作日志
        logProcess.addOperateLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 新增日志
     * @param deviceProtocol 设施协议信息
     * @param functionCode XML functionCode
     * @param logConstants 操作类型
     */
    private void addLog(DeviceProtocol deviceProtocol, String functionCode, String logConstants) {
        AddLogBean addLogBean = addLogBean(deviceProtocol, functionCode, logConstants);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 新增日志bean
     * @param deviceProtocol 设施协议信息
     * @param functionCode XML functionCode
     * @param logConstants 操作类型
     * @return 新增日志bean
     */
    private AddLogBean addLogBean(DeviceProtocol deviceProtocol, String functionCode, String logConstants) {
        //获取日志类型
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(DeviceProtocolConstants.PROTOCOL_ID);
        addLogBean.setDataName(DeviceProtocolConstants.PROTOCOL_NAME);
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
     * @param deviceProtocol 施协议名称
     */
    private void hasDeviceProtocolName(DeviceProtocol deviceProtocol) {
        String protocolId = deviceProtocolDao.queryDeviceProtocolByName(deviceProtocol);
        if(!StringUtils.isEmpty(protocolId)){
            throw new FilinkDeviceProtocolNameExistException();
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
}
