package com.fiberhome.filink.parameter.bean;

import com.fiberhome.filink.parameter.constant.DisplayTimeEnum;
import com.fiberhome.filink.parameter.constant.SystemParameterConstants;
import com.fiberhome.filink.parameter.constant.SystemParameterLimitEnum;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 *   显示设置实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-27
 */
@Data
public class DisplaySettings {
    /**
     * 启用大屏显示
     */
    private String screenDisplay;
    /**
     * 启用大屏滚动
     */
    private String screenScroll;
    /**
     * 大屏滚动时间间隔
     */
    private Integer screenScrollTime;
    /**
     * 系统语言
     */
    private String systemLanguage;
    /**
     * 系统Logo
     */
    private String systemLogo;
    /**
     * 时间设置
     */
    private String timeType;

    /**
     * 校验显示参数
     * @return true不符合 false符合
     */
    public boolean checkDisplay() {
        if (StringUtils.isEmpty(screenDisplay) || StringUtils.isEmpty(timeType)
                || StringUtils.isEmpty(systemLanguage) || StringUtils.isEmpty(screenScroll)) {
            return true;
        }
        if (!screenDisplay.matches(SystemParameterConstants.ONE_ZERO_REGEX)
                || !screenScroll.matches(SystemParameterConstants.ONE_ZERO_REGEX)) {
            return true;
        }
        return  !DisplayTimeEnum.hasValue(timeType) || SystemParameterLimitEnum.SCREEN_SCROLL_TIME.checkValue(screenScrollTime);
    }

    /**
     * 校验是否有文件路径
     * @return true 空 false 不为空
     */
    public boolean checkLogo() {
        return StringUtils.isEmpty(systemLogo);
    }
}
