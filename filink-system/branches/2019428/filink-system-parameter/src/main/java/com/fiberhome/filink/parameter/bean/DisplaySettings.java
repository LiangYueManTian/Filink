package com.fiberhome.filink.parameter.bean;

import com.fiberhome.filink.parameter.utils.DisplayTimeEnum;
import com.fiberhome.filink.parameter.utils.SystemParameterConstants;
import com.fiberhome.filink.parameter.utils.SystemParameterLimitEnum;
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
     * 校验显示
     * @return true不符合 false符合
     */
    public boolean checkDisplay() {
        return StringUtils.isEmpty(screenDisplay) || !screenDisplay.matches(SystemParameterConstants.ONE_ZERO_REGEX)
                || StringUtils.isEmpty(timeType) || StringUtils.isEmpty(systemLanguage) || !DisplayTimeEnum.hasValue(timeType);
    }

    /**
     *校验启用大屏滚动
     * @return true不符合 false符合
     */
    public boolean checkEnableScreen() {
        return StringUtils.isEmpty(screenScroll) || !screenScroll.matches(SystemParameterConstants.ONE_ZERO_REGEX);
    }

    /**
     * 启用大屏滚动时校验大屏滚动时间间隔
     * @return true 不符合 false 符合
     */
    public boolean checkScreenScrollTime() {
        return SystemParameterLimitEnum.SCREEN_SCROLL_TIME.checkValue(screenScrollTime);
    }

    /**
     * 校验是否有文件路径
     * @return true 空 false 不为空
     */
    public boolean checkLogo() {
        return StringUtils.isEmpty(systemLogo);
    }
}
