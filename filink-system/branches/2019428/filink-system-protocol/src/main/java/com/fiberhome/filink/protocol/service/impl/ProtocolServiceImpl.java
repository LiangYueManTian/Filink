package com.fiberhome.filink.protocol.service.impl;

import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.utils.LogConstants;
import com.fiberhome.filink.oss_api.api.FdfsFeign;
import com.fiberhome.filink.protocol.bean.ProtocolI18n;
import com.fiberhome.filink.protocol.bean.ProtocolLogCode;
import com.fiberhome.filink.protocol.dto.CertificateFile;
import com.fiberhome.filink.protocol.dto.ProtocolField;
import com.fiberhome.filink.protocol.dto.ProtocolParams;
import com.fiberhome.filink.protocol.exception.ProtocolSystemException;
import com.fiberhome.filink.protocol.service.ProtocolService;
import com.fiberhome.filink.protocol.utils.JsonChange;
import com.fiberhome.filink.protocol.utils.ProtocolCheckUtil;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.system_commons.bean.SysParam;
import com.fiberhome.filink.system_commons.dao.SysParamDao;
import com.fiberhome.filink.system_commons.utils.ParamTypeRedisEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 通信协议  服务实现类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-02-20
 */
@Service
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
     * 更新协议内容(Json数据)
     *
     * @param protocolParams 协议参数实体
     * @param file           协议证书
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_UPDATE, logType = LogConstants.LOG_TYPE_OPERATE, functionCode = ProtocolLogCode.UPDATE_FUNCTION_CODE, dataGetColumnName = "paramType", dataGetColumnId = "paramId")
    public Result updateProtocol(ProtocolParams protocolParams, MultipartFile file) {
        //检验通过后，获得协议id，获得协议内容
        String protocolId = protocolParams.getParamId().trim();
        ProtocolField protocolField = protocolParams.getProtocolField();
        //去掉ProtocolField实体属性值的尾部空格
        ProtocolCheckUtil.trimBean(protocolField);
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

        //根据协议id更新协议
        Integer result = sysParamDao.updateParamById(protocol);
        if (null != result && result == 1) {
            //更新缓存
            protocolDto.setPresentValue(protocol.getPresentValue());
            //根据类型获得Key
            String key = ParamTypeRedisEnum.getKeyByType(protocolDto.getParamType());
            //更新缓存
            RedisUtils.set(key, protocolDto);
        } else {
            //更新失败，报系统异常
            throw new ProtocolSystemException();
        }

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(ProtocolI18n.UPDATE_PROTOCOL_SUCCESS));
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


}
