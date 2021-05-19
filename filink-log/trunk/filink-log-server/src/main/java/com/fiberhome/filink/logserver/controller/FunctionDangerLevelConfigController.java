package com.fiberhome.filink.logserver.controller;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logserver.service.FunctionDangerLevelConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 功能危险级别配置表 前端控制器
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-01-22
 */
@RestController("functionDangerLevelConfigController")
@RequestMapping("/functionDangerLevelConfig")
public class FunctionDangerLevelConfigController {

    @Autowired
    private FunctionDangerLevelConfigService functionDangerLevelConfigService;

    /**
     * @author hedongwei@wistronits.com
     * description 查询文件级别信息
     * date 15:40 2019/1/22
     * param [functionCode]
     */
    @PostMapping("/getDangerLevel")
    public Result getDangerLevel(@RequestBody String functionCode) {
        return ResultUtils.success(functionDangerLevelConfigService.getDangerLevelConfigByFunctionCode(functionCode));
    }

}
