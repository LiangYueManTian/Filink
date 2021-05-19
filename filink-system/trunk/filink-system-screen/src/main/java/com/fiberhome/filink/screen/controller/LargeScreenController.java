package com.fiberhome.filink.screen.controller;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.screen.bean.LargeScreen;
import com.fiberhome.filink.screen.constant.LargeScreenI18n;
import com.fiberhome.filink.screen.constant.LargeScreenResultCode;
import com.fiberhome.filink.screen.service.LargeScreenService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *     大屏管理 前端控制器
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-05-28
 */
@RestController
@RequestMapping("/largeScreen")
public class LargeScreenController {
    @Autowired
    private LargeScreenService largeScreenService;
    /**
     * 查询所有大屏
     *
     * @return 大屏信息 List
     */
    @GetMapping("/queryLargeScreenAll")
    public Result queryLargeScreenAll() {
        return largeScreenService.queryLargeScreenAll();
    }

    /**
     * 查询大屏名称是否重复
     *
     * @param largeScreen 大屏信息
     * @return 结果
     */
    @PostMapping("/queryLargeScreenNameRepeat")
    public Result queryLargeScreenNameRepeat(@RequestBody LargeScreen largeScreen) {
        if (largeScreen == null || largeScreen.checkValue()) {
            return ResultUtils.warn(LargeScreenResultCode.LARGE_SCREEN_PARAM_ERROR,
                    I18nUtils.getSystemString(LargeScreenI18n.LARGE_SCREEN_PARAM_ERROR));
        }
        return largeScreenService.queryLargeScreenNameRepeat(largeScreen);
    }

    /**
     * 根据大屏ID修改大屏名称
     *
     * @param largeScreen 大屏信息
     * @return 结果
     */
    @PostMapping("/updateLargeScreenNameById")
    public Result updateLargeScreenNameById(@RequestBody LargeScreen largeScreen) {
        if (largeScreen == null || largeScreen.checkValue()) {
            return ResultUtils.warn(LargeScreenResultCode.LARGE_SCREEN_PARAM_ERROR,
                    I18nUtils.getSystemString(LargeScreenI18n.LARGE_SCREEN_PARAM_ERROR));
        }
        return largeScreenService.updateLargeScreenNameById(largeScreen);
    }
}
