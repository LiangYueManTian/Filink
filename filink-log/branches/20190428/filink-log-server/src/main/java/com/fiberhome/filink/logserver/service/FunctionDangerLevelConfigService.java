package com.fiberhome.filink.logserver.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.logserver.bean.FunctionDangerLevelConfig;


/**
 * <p>
 * 功能危险级别配置表 服务类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-01-22
 */
public interface FunctionDangerLevelConfigService extends IService<FunctionDangerLevelConfig> {

    /**
     * 根据功能编码查询危险级别
     * @author hedongwei@wistronits.com
     * @date 15:19 2019/1/22
     * @param functionCode 描述信息
     * @return 危险级别
     */
    FunctionDangerLevelConfig getDangerLevelConfigByFunctionCode(String functionCode);
}
