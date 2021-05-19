package com.fiberhome.filink.logserver.service.impl;

import com.fiberhome.filink.logserver.bean.FunctionDangerLevelConfig;
import com.fiberhome.filink.logserver.dao.FunctionDangerLevelConfigDao;
import com.fiberhome.filink.logserver.service.FunctionDangerLevelConfigService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * <p>
 * 功能危险级别配置表 服务实现类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-01-22
 */
@Service
public class FunctionDangerLevelConfigServiceImpl extends ServiceImpl<FunctionDangerLevelConfigDao, FunctionDangerLevelConfig> implements FunctionDangerLevelConfigService {

    @Autowired
    private FunctionDangerLevelConfigDao functionDangerLevelConfigDao;

    /**
     * @author hedongwei@wistronits.com
     * description 根据功能编码查询危险级别
     * date 15:19 2019/1/22
     * param [functionCode]
     */
    @Override
    public FunctionDangerLevelConfig getDangerLevelConfigByFunctionCode(String functionCode) {
        //当工单编码不为空时
        if (!StringUtils.isEmpty(functionCode)) {
            FunctionDangerLevelConfig levelConfig = new FunctionDangerLevelConfig();
            levelConfig.setFunctionCode(functionCode);
            //查询危险级别配置信息
            FunctionDangerLevelConfig retLevelConfig = functionDangerLevelConfigDao.selectOne(levelConfig);
            return retLevelConfig;
        } else {
            return null;
        }
    }
}
