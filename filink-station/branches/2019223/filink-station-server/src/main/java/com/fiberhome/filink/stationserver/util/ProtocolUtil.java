package com.fiberhome.filink.stationserver.util;

import com.fiberhome.filink.device_api.api.DeviceFeign;
import com.fiberhome.filink.device_api.bean.DeviceInfoDto;
import com.fiberhome.filink.filinklockapi.bean.Control;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.protocol.api.ProtocolFeign;
import com.fiberhome.filink.protocol.bean.ProtocolVersionBean;
import com.fiberhome.filink.protocol.bean.xmlBean.AbstractProtocolBean;
import com.fiberhome.filink.protocol.bean.xmlBean.FiLinkProtocolBean;
import com.fiberhome.filink.stationserver.exception.ProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;

/**
 * 协议获取工具类
 * @author CongcaiYu
 */
@Component
public class ProtocolUtil {


    @Autowired
    private ProtocolFeign protocolFeign;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private ControlFeign controlFeign;

    /**
     * 根据序列id获取协议信息
     * @param serialNum 设施序列id
     * @return 协议实体
     * @throws Exception Exception
     */
    public AbstractProtocolBean getProtocolBeanBySerialNum(String serialNum){
        //根据序列id获取设施id
        DeviceInfoDto deviceInfoDto;
        try {
            deviceInfoDto = deviceFeign.findDeviceBySeriaNumber(serialNum);
        }catch (Exception e){
            e.printStackTrace();
            throw new ProtocolException("device feign execute failed>>>>>>>>");
        }
        if(deviceInfoDto == null){
            throw new ProtocolException("device info is null>>>>>>>>>>>>");
        }
        String deviceId = deviceInfoDto.getDeviceId();
        //根据设施id获取软硬件版本信息
        Control control = controlFeign.getControlParams(deviceId);
        if(control == null){
            throw new ProtocolException("control info is null>>>>>>>>>>>>");
        }
        String hardwareVersion = control.getHardwareVersion();
        String softwareVersion = control.getSoftwareVersion();
        if(StringUtils.isEmpty(hardwareVersion) || StringUtils.isEmpty(softwareVersion)){
            throw new ProtocolException("softwareVersion or hardwareVersion is null>>>>>>>>>>>>");
        }
        ProtocolVersionBean versionBean = new ProtocolVersionBean();
        versionBean.setHardwareVersion(hardwareVersion);
        versionBean.setSoftwareVersion(softwareVersion);
        //根据软硬件版本获取协议信息
        InputStream inputStream;
        try {
            ClassPathResource classPathResource = new ClassPathResource("/config/protocolConfig.xml");
            inputStream = classPathResource.getInputStream();
        }catch (Exception e){
            throw new ProtocolException("the protocolConfig xml is not exist>>>>>>>>>>>>");
        }
        FiLinkProtocolBean fiLinkProtocolBean = new FiLinkProtocolResolver().resolve(inputStream);
        if(fiLinkProtocolBean == null){
            throw new ProtocolException("filink protocol bean is null>>>>>>>>>>>>");
        }
        return fiLinkProtocolBean;
    }
}
