package com.fiberhome.filink.onenetserver.utils;

import com.fiberhome.filink.commonstation.entity.param.AbstractReqParams;
import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.commonstation.entity.xmlbean.FiLinkProtocolBean;
import com.fiberhome.filink.commonstation.exception.ProtocolException;
import com.fiberhome.filink.commonstation.utils.FiLinkProtocolResolver;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.onenetserver.constant.RedisKey;
import com.fiberhome.filink.protocol.api.ProtocolFeign;
import com.fiberhome.filink.protocol.bean.ProtocolVersionBean;
import com.fiberhome.filink.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


/**
 * 协议获取工具类
 *
 * @author CongcaiYu
 */
@Component
public class ProtocolUtil {


    @Autowired
    private ControlFeign controlFeign;

    @Autowired
    private ProtocolFeign protocolFeign;


    /**
     * 获取协议实体
     *
     * @param abstractReqParams 请求参数信息
     * @return 协议实体
     */
    public FiLinkProtocolBean getProtocolBean(AbstractReqParams abstractReqParams) {
        String hardwareVersion = abstractReqParams.getHardwareVersion();
        String softwareVersion = abstractReqParams.getSoftwareVersion();
        FiLinkProtocolBean protocolBean;
        if (StringUtils.isEmpty(hardwareVersion) || StringUtils.isEmpty(softwareVersion)) {
            ControlParam control = getControlById(abstractReqParams.getEquipmentId());
            protocolBean = getProtocolBeanByControl(control);
        } else {
            protocolBean = getProtocolBeanInRedis(softwareVersion, hardwareVersion);
        }
        return protocolBean;
    }

    /**
     * 根据主控id查询主控信息
     *
     * @param equipmentId 主控id
     * @return 主控信息
     */
    public ControlParam getControlById(String equipmentId) {
        //从redis查询主控信息
        ControlParam controlParam = (ControlParam) RedisUtils.hGet(RedisKey.CONTROL_INFO, equipmentId);
        if (controlParam == null) {
            controlParam = queryControlById(equipmentId);
            RedisUtils.hSet(RedisKey.CONTROL_INFO, equipmentId, controlParam);
        }
        return controlParam;
    }

    /**
     * 调用电子锁服务查询主控信息
     *
     * @param equipmentId 主控id
     * @return 主控对象信息
     */
    private ControlParam queryControlById(String equipmentId) {
        //根据设施id获取软硬件版本信息
        ControlParam control = controlFeign.getControlParamById(equipmentId);
        if (control == null || StringUtils.isEmpty(control.getHostId())) {
            throw new ProtocolException("control info is null>>>>>>>>>>>>");
        }
        return control;
    }


    /**
     * 根据序列id获取协议信息
     *
     * @param control 主控
     * @return 协议实体
     */
    public FiLinkProtocolBean getProtocolBeanByControl(ControlParam control) {
        //查询主控信息
        String hardwareVersion = control.getHardwareVersion();
        String softwareVersion = control.getSoftwareVersion();
        if (StringUtils.isEmpty(hardwareVersion) || StringUtils.isEmpty(softwareVersion)) {
            throw new ProtocolException("softwareVersion or hardwareVersion is null>>>>>>>>>>>>");
        }
        return getProtocolBeanInRedis(softwareVersion, hardwareVersion);
    }


    /**
     * 从redis获取协议实体
     *
     * @param softwareVersion 软件版本
     * @param hardwareVersion 硬件版本
     * @return 协议实体
     */
    private FiLinkProtocolBean getProtocolBeanInRedis(String softwareVersion, String hardwareVersion) {
        //根据软硬件版本获取协议信息
        String key = softwareVersion + hardwareVersion;
        //从缓存中获取设施协议信息
        FiLinkProtocolBean fiLinkProtocolBean = (FiLinkProtocolBean) RedisUtils.hGet(RedisKey.PROTOCOL_KEY, key);
        if (fiLinkProtocolBean == null) {
            //从文件服务器解析协议存入缓存
            ProtocolVersionBean protocolVersionBean = new ProtocolVersionBean();
            protocolVersionBean.setSoftwareVersion(softwareVersion);
            protocolVersionBean.setHardwareVersion(hardwareVersion);
            String hexFile = protocolFeign.queryProtocol(protocolVersionBean);
            fiLinkProtocolBean = this.setProtocolToRedis(hexFile);
        }
        return fiLinkProtocolBean;
    }


    /**
     * 删除缓存中协议实体
     *
     * @param protocolDto 协议条件对象
     */
    public void deleteProtocolToRedis(ProtocolDto protocolDto) {
        //根据软硬件版本删除redis中的协议
        String softwareVersion = protocolDto.getSoftwareVersion();
        String hardwareVersion = protocolDto.getHardwareVersion();
        String key = softwareVersion + hardwareVersion;
        RedisUtils.hRemove(RedisKey.PROTOCOL_KEY, key);
    }


    /**
     * 将协议信息存入redis
     *
     * @param fileHexData 16进制文件
     */
    public FiLinkProtocolBean setProtocolToRedis(String fileHexData) {
        //将协议文件转换成流
        InputStream protocolInputSteam;
        try {
            byte[] bytes = HexUtil.hexStringToByte(fileHexData);
            protocolInputSteam = new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            throw new ProtocolException("protocol file to inputStream failed>>>>>>");
        }
        //解析协议文件
        FiLinkProtocolBean fiLinkProtocolBean = new FiLinkProtocolResolver().resolve(protocolInputSteam);
        if (fiLinkProtocolBean == null) {
            throw new ProtocolException("filink protocol bean is null>>>>>>>>>>>>");
        }
        //获取软硬件版本
        String hardwareVersion = fiLinkProtocolBean.getHardwareVersion();
        String softwareVersion = fiLinkProtocolBean.getSoftwareVersion();
        //判断软硬件版本是否为空
        if (StringUtils.isEmpty(hardwareVersion) || StringUtils.isEmpty(softwareVersion)) {
            throw new ProtocolException("softwareVersion or hardwareVersion of the xml is null>>>>>");
        }
        //将协议实体存入缓存
        String key = softwareVersion + hardwareVersion;
        RedisUtils.hSet(RedisKey.PROTOCOL_KEY, key, fiLinkProtocolBean);
        return fiLinkProtocolBean;
    }

}
