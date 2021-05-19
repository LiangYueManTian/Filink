package com.fiberhome.filink.dprotocol.bean;

import com.fiberhome.filink.dprotocol.bean.xmlBean.FiLinkProtocolBean;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 *     设施协议文件信息Redis实体类
 * </p>
 *
 * @author chaofang@wistrontis.com
 * @since 2019-02-16
 */
@Data
public class ProtocolVersionBean {

    /**
     * 软件版本
     */
    private String softwareVersion;


    /**
     * 硬件版本
     */
    private String hardwareVersion;

    /**
     * 校验参数是否为空
     * @return true是 false不是
     */
    public boolean check() {
        return StringUtils.isEmpty(softwareVersion) || StringUtils.isEmpty(hardwareVersion);
    }
}
