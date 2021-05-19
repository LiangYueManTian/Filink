package com.fiberhome.filink.parameter.bean;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 *   FTP服务设置修改接收实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-02
 */
@Data
public class FtpSettingsParam {
    /**
     * 参数ID
     */
    private String paramId;
    /**
     * 消息通知配置实体类
     */
    private FtpSettings ftpSettings;
    /**
     * 校验参数
     * @return true不符合 false符合
     */
    public boolean check() {
        return StringUtils.isEmpty(paramId) || ObjectUtils.isEmpty(ftpSettings)
                || !ftpSettings.checkValue();
    }
}
