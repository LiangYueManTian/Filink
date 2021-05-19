package com.fiberhome.filink.parameter.bean;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;
/**
 * <p>
 *   显示设置修改接收实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-27
 */
@Data
public class DisplaySettingsParam {
    /**
     * 参数ID
     */
    private String paramId;
    /**
     * 消息通知配置实体类
     */
    private DisplaySettings displaySettings;
    /**
     * 校验参数
     * @return true不符合 false符合
     */
    public boolean check() {
        return StringUtils.isEmpty(paramId);
    }
}
