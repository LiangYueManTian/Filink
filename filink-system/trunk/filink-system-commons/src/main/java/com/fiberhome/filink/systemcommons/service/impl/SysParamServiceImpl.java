package com.fiberhome.filink.systemcommons.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.systemcommons.bean.SysParam;
import com.fiberhome.filink.systemcommons.constant.ParamTypeRedisEnum;
import com.fiberhome.filink.systemcommons.dao.SysParamDao;
import com.fiberhome.filink.systemcommons.exception.FilinkSysParamDataException;
import com.fiberhome.filink.systemcommons.service.SysParamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
@Slf4j
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

    /**
     * 查询系统语言
     *
     * @return 系统语言
     */
    @Override
    public String querySystemLanguage() {
        //系统语言 Redis缓存Key
        String languageKey = ParamTypeRedisEnum.SYSTEM_LANGUAGE.getKey();
        //查询Redis缓存
        String language = (String) RedisUtils.get(languageKey);
        if (StringUtils.isEmpty(language)) {
            //缓存不存在查询数据库显示设置参数
            SysParam sysParam = queryParamByType(ParamTypeRedisEnum.DISPLAY_SETTINGS.getType());
            JSONObject displaySettings = JSONObject.parseObject(sysParam.getPresentValue());
            language = displaySettings.getString(ParamTypeRedisEnum.SYSTEM_LANGUAGE.getType());
            //数据异常，信息丢失
            if (StringUtils.isEmpty(language)) {
                //数据库数据问题时默认值
                log.error("The sys_param table of database is exception, param_type is 9 >>>>>>>>");
                return null;
            } else {
                RedisUtils.set(languageKey, language);
            }
        }
        return language;
    }
}
