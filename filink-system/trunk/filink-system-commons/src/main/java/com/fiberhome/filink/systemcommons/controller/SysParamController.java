package com.fiberhome.filink.systemcommons.controller;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemcommons.constant.ParamTypeRedisEnum;
import com.fiberhome.filink.systemcommons.constant.SysParamI18n;
import com.fiberhome.filink.systemcommons.constant.SysParamResultCode;
import com.fiberhome.filink.systemcommons.service.SysParamService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  系统服务统一参数前端控制器
 * </p>
* @author chaofang@fiberhome.com
 * @since 2019-03-11
 */
@RestController
@RequestMapping("/systemParam")
public class SysParamController {

    /**
     * 自动注入Service
     */
    @Autowired
    private SysParamService sysParamService;

    /**
     * 根据类型查询相应参数信息
     * @param paramType 类型
     * @return 参数信息
     */
    @GetMapping("/queryParam/{paramType}")
    public Result queryParam(@PathVariable String paramType) {
        //校验参数
        if (StringUtils.isEmpty(paramType) || !ParamTypeRedisEnum.hasType(paramType)) {
            return ResultUtils.warn(SysParamResultCode.SYSTEM_PARAM_ERROR,
                    I18nUtils.getSystemString(SysParamI18n.SYSTEM_PARAM_ERROR));
        }
        return sysParamService.queryParam(paramType);
    }

    /**
     * 查询系统语言
     * @return 系统语言
     */
    @GetMapping("/querySystemLanguage")
    public String querySystemLanguage() {
        return sysParamService.querySystemLanguage();
    }
}
