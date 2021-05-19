package com.fiberhome.filink.system_commons.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.system_commons.exception.FilinkSysParamDataException;
import com.fiberhome.filink.system_commons.bean.SysParam;
import com.fiberhome.filink.system_commons.dao.SysParamDao;
import com.fiberhome.filink.system_commons.service.SysParamService;
import com.fiberhome.filink.system_commons.utils.ParamTypeRedisEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  系统服务统一参数服务实现类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-11
 */
@Service
public class SysParamServiceImpl extends ServiceImpl<SysParamDao, SysParam> implements SysParamService {

    /**
     * 自动注入DAO
     */
    @Autowired
    private SysParamDao sysParamDao;
    /**
     * 根据类型查询相应参数信息
     *
     * @param paramType 类型
     * @return 参数信息
     */
    @Override
    public Result queryParam(String paramType) {
        SysParam sysParam = queryParamByType(paramType);
        return ResultUtils.success(sysParam);
    }
    /**
     * 根据类型查询相应参数信息
     *
     * @param paramType 类型
     * @return 参数信息
     */
    @Override
    public SysParam queryParamByType(String paramType) {
        //获取Redis Key
        String key = ParamTypeRedisEnum.getKeyByType(paramType);
        SysParam sysParam;
        if (RedisUtils.hasKey(key)) {
            sysParam = (SysParam)RedisUtils.get(key);
        } else {
            sysParam = sysParamDao.queryParamByType(paramType);
            //数据库一定会有数据,没有报错
            if (sysParam == null || sysParam.checkValue()) {
                throw new FilinkSysParamDataException();
            }
            RedisUtils.set(key, sysParam);
        }
        return sysParam;
    }
}
