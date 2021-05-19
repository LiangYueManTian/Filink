package com.fiberhome.filink.protocol.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.filinkoceanconnectapi.bean.HttpsConfig;
import com.fiberhome.filink.filinkoceanconnectapi.feign.OceanConnectFeign;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.onenetapi.api.OneNetFeign;
import com.fiberhome.filink.onenetapi.bean.HostBean;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.protocol.constant.ProtocolConstants;
import com.fiberhome.filink.protocol.constant.ProtocolI18n;
import com.fiberhome.filink.protocol.constant.ProtocolLogCode;
import com.fiberhome.filink.protocol.constant.ProtocolResultCode;
import com.fiberhome.filink.protocol.dto.CertificateFile;
import com.fiberhome.filink.protocol.dto.ProtocolField;
import com.fiberhome.filink.protocol.dto.ProtocolParams;
import com.fiberhome.filink.protocol.exception.ProtocolSystemException;
import com.fiberhome.filink.protocol.service.ProtocolService;
import com.fiberhome.filink.protocol.utils.JsonChange;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemcommons.bean.SysParam;
import com.fiberhome.filink.systemcommons.constant.ParamTypeRedisEnum;
import com.fiberhome.filink.systemcommons.dao.SysParamDao;
import com.fiberhome.filink.systemcommons.utils.SystemLanguageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static com.fiberhome.filink.systemcommons.constant.ParamTypeRedisEnum.HTTPS_PROTOCOL;
import static com.fiberhome.filink.systemcommons.constant.ParamTypeRedisEnum.HTTP_PROTOCOL;
import static com.fiberhome.filink.systemcommons.constant.ParamTypeRedisEnum.WEBSERVICE_PROTOCOL;

/**
 * <p>
 * 通信协议  服务实现类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-02-20
 */
@Service
@Slf4j
public class ProtocolServiceImpl implements ProtocolService {
    /**
     * 注入ProtocolDao对象
     */
    @Autowired
    private SysParamDao sysParamDao;
    /**
     * 自动注入文件服务远程调用
     */
    @Autowired
    private FdfsFeign fdfsFeign;
    /**
     * 自动注入日志服务
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * 自动注入OneNet服务
     */
    @Autowired
    private OneNetFeign oneNetFeign;
    /**
     * 自动注入OceanConnect服务
     */
    @Autowired
    private OceanConnectFeign oceanConnectFeign;
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;



    /**
     * 更新协议内容(Json数据)
     *
     * @param protocolParams 协议参数实体
     * @param file           协议证书
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateProtocol(ProtocolParams protocolParams, MultipartFile file) {
        //检验通过后，获得协议id，获得协议内容
        String protocolId = protocolParams.getParamId();
        ProtocolField protocolField = protocolParams.getProtocolField();

        //证书不为空，上传证书，并更新ProtocolField的证书属性
        if (!StringUtils.isEmpty(file)) {
            //上传文件，并转换成字符串
            CertificateFile certificateFile = addCertificateFile(file);
            protocolField.setCertificateFile(certificateFile);
        }

        //构建更新时要用的实体
        SysParam protocol = new SysParam();
        protocol.setParamId(protocolId);
        protocol.setPresentValue(JsonChange.objToJson(protocolField));
        protocol.setUpdateUser(RequestInfoUtils.getUserId());

        //更新前，根据id,查看协议
        SysParam protocolDto = sysParamDao.queryParamById(protocolId);

        if (StringUtils.isEmpty(protocolDto)) {
            //查不到报错，说明数据库已没有这个协议,报系统异常
            throw new ProtocolSystemException();
        }
        //协议类型
        String paramType = protocolDto.getParamType();
        //根据协议id更新协议
        Integer result = sysParamDao.updateParamById(protocol);
        if (null != result && result == 1) {
            String presentValue = protocol.getPresentValue();
            //设置当前值
            protocolDto.setPresentValue(presentValue);
            //根据类型获得Key
            String key = ParamTypeRedisEnum.getKeyByType(paramType);
            //更新缓存
            RedisUtils.set(key, protocolDto);
            //更新平台缓存
            updateRedisForPlat(paramType, presentValue);
            //记日志
            addLogByType(protocolId, paramType);
        } else {
            //更新失败，报系统异常
            throw new ProtocolSystemException();
        }

        return generateResulByType(paramType);
    }


    /**
     * 调用文件上传方法
     *
     * @param file
     * @return CertificateFile
     */
    public CertificateFile addCertificateFile(MultipartFile file) {
        // todo 可能有验证文件是否符合要求的代码
        String fileUrl = fdfsFeign.uploadFile(file);
        if (StringUtils.isEmpty(fileUrl)) {
            //fileUrl为空，说明调用fdfs微服务失败，报系统异常
            throw new ProtocolSystemException();
        }
        //构造协议证书实例
        CertificateFile certificateFile = new CertificateFile();
        certificateFile.setFileName(file.getOriginalFilename());
        certificateFile.setFileUrl(fileUrl);
        return certificateFile;
    }

    /**
     * 新增日志
     *
     * @param paramId      参数ID
     * @param protocolName 协议名称
     * @param functionCode XML functionCode
     */
    private void addLog(String paramId, String protocolName, String functionCode) {
        systemLanguageUtil.querySystemLanguage();
        //获取日志类型
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(ProtocolConstants.PARAM_ID);
        addLogBean.setDataName(ProtocolConstants.PARAM_TYPE);
        //获得操作对象名称
        addLogBean.setOptObj(protocolName);
        addLogBean.setFunctionCode(functionCode);
        //获得操作对象id
        addLogBean.setOptObjId(paramId);
        //操作为更新
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 调用新增日志方法
     *
     * @param paramId 参数ID
     * @param type    协议类型
     */
    private void addLogByType(String paramId, String type) {
        if (HTTP_PROTOCOL.getType().equals(type)) {
            addLog(paramId, I18nUtils.getSystemString(ProtocolI18n.HTTP_PROTOCOL), ProtocolLogCode.UPDATE_HTTP_FUNCTION_CODE);
        } else if (HTTPS_PROTOCOL.getType().equals(type)) {
            addLog(paramId, I18nUtils.getSystemString(ProtocolI18n.HTTPS_PROTOCOL), ProtocolLogCode.UPDATE_HTTPS_FUNCTION_CODE);
        } else if (WEBSERVICE_PROTOCOL.getType().equals(type)) {
            addLog(paramId, I18nUtils.getSystemString(ProtocolI18n.WEBSERVICE_PROTOCOL), ProtocolLogCode.UPDATE_WEBSERVICE_FUNCTION_CODE);
        }
    }

    /**
     * 根据协议类型响应
     *
     * @param type 协议类型
     * @return 响应结果
     */
    private Result generateResulByType(String type) {
        Result result = new Result();
        if (HTTP_PROTOCOL.getType().equals(type)) {
            result = ResultUtils.success(ProtocolResultCode.SUCCESS, I18nUtils.getSystemString(ProtocolI18n.UPDATE_HTTP_PROTOCOL_SUCCESS));
        } else if (HTTPS_PROTOCOL.getType().equals(type)) {
            result = ResultUtils.success(ProtocolResultCode.SUCCESS, I18nUtils.getSystemString(ProtocolI18n.UPDATE_HTTPS_PROTOCOL_SUCCESS));
        } else if (WEBSERVICE_PROTOCOL.getType().equals(type)) {
            result = ResultUtils.success(ProtocolResultCode.SUCCESS, I18nUtils.getSystemString(ProtocolI18n.UPDATE_WEBSERVICE_PROTOCOL_SUCCESS));
        }
        return result;
    }

    /**
     * 查询协议内容
     *
     * @param type 协议类型
     * @return 协议内容
     */
    @Override
    public ProtocolField queryProtocol(String type) {
        SysParam sysParam = sysParamDao.queryParamByType(type);
        String presentValue = sysParam.getPresentValue();
        ProtocolField protocolField = new ProtocolField();
        Map<String, String> map = JSONObject.parseObject(presentValue, Map.class);
        try {
            BeanUtils.populate(protocolField, map);
        } catch (Exception e) {
            log.error("protocolMap转成protocolBean过程出错：{}", e.getMessage());
        }
        return protocolField;
    }


    /**
     * 更新平台缓存
     *
     * @param type 协议类型
     * @param value 值
     */
    private void updateRedisForPlat(String type, String value) {
        ProtocolField protocolField = new ProtocolField();
        Map<String, String> map = JSONObject.parseObject(value, Map.class);
        try {
            BeanUtils.populate(protocolField, map);
        } catch (Exception e) {
            log.error("protocolMap转成protocolBean过程出错：{}", e.getMessage());
        }
        String ip = protocolField.getIp();
        if(ParamTypeRedisEnum.HTTP_PROTOCOL.getType().equals(type)){
            HostBean hostBean = new HostBean();
            hostBean.setHost(ip);
            oneNetFeign.updateOneNetHost(hostBean);
        }else if(ParamTypeRedisEnum.HTTPS_PROTOCOL.getType().equals(type)){
            HttpsConfig httpsConfig = new HttpsConfig();
            httpsConfig.setAddress(ip);
            oceanConnectFeign.updateHttpsConfig(httpsConfig);
        }

    }

}
