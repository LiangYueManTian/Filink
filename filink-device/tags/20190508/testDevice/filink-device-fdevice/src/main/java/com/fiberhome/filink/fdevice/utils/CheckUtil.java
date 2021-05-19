package com.fiberhome.filink.fdevice.utils;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.constant.area.AreaI18nConstant;
import com.fiberhome.filink.fdevice.constant.area.AreaResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.util.StringUtils;

/**
 * 检测工具类
 * @author qiqizhu@wistronits.com
 * @Date:  2019/2/28 15:59
 */
public class CheckUtil {
    /**
     * 检测参数并格式化
     * @param areaInfo 接收参数
     * @return 检测结果
     */
    public static Result checkParameterAndFormat(AreaInfo areaInfo) {
        String areaName = areaInfo.getAreaName();
        //参数格式化 当前去空格
        areaInfo.parameterFormat();
        if (StringUtils.isEmpty(areaName)) {
            return ResultUtils.warn(AreaResultCode.AREA_NAME_NULL, I18nUtils.getSystemString(AreaI18nConstant.AREA_NAME_NULL));
        }
        if (!areaInfo.checkAreaName()) {
            return ResultUtils.warn(AreaResultCode.AREA_NAME_FORMAT_IS_INCORRECT, I18nUtils.getSystemString(AreaI18nConstant.AREA_NAME_FORMAT_IS_INCORRECT));
        }
        return null;
    }
}
