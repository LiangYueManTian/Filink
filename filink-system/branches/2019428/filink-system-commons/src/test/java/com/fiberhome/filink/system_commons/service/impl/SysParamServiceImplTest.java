package com.fiberhome.filink.system_commons.service.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.system_commons.exception.FilinkSysParamDataException;
import com.fiberhome.filink.system_commons.bean.SysParam;
import com.fiberhome.filink.system_commons.bean.SysParamI18n;
import com.fiberhome.filink.system_commons.bean.SysParamResultCode;
import com.fiberhome.filink.system_commons.dao.SysParamDao;
import com.fiberhome.filink.system_commons.utils.ParamTypeRedisEnum;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;


/**
 * <p>
 * 系统服务统一参数服务实现类 测试类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/20
 */
@RunWith(JMockit.class)
public class SysParamServiceImplTest {

    /**
     * 测试对象
     */
    @Tested
    private SysParamServiceImpl sysParamService;
    /**
     * 自动注入DAO
     */
    @Injectable
    private SysParamDao sysParamDao;
    /**paramType*/
    private String paramType;
    /**SysParam*/
    private SysParam sysParam;
    /**sysParamI18n*/
    private SysParamI18n sysParamI18n;


    /**
     * 初始化
     */
    @Before
    public void setUp() {
        sysParamI18n = new SysParamI18n();
        paramType = "0";
        sysParam = new SysParam();
        sysParam.setParamType(paramType);
        sysParam.setParamId(sysParam.getParamType());
        sysParam.setDefaultValue(sysParam.getParamId());
        sysParam.setPresentValue(sysParam.getDefaultValue());
        sysParam.setUpdateUser(sysParamI18n.toString());
        sysParam.setUpdateUser(sysParam.getPresentValue());
        sysParam.setCreateUser(sysParam.getUpdateUser());
        sysParam.setUpdateUser(sysParam.getCreateUser());
        Date date = new Date();
        sysParam.setUpdateTime(date);
        sysParam.setCreateTime(sysParam.getUpdateTime());
        sysParam.setUpdateTime(sysParam.getCreateTime());
    }


    /**
     * 测试根据类型查询相应参数信息
     */
    @Test
    public void queryParamTest() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hasKey(ParamTypeRedisEnum.getKeyByType(paramType));
                result = true;

                RedisUtils.get(ParamTypeRedisEnum.getKeyByType(paramType));
                result = sysParam;
            }
        };
        Result result = sysParamService.queryParam(paramType);
        Assert.assertEquals(result.getCode(), (int) SysParamResultCode.SUCCESS);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hasKey(ParamTypeRedisEnum.getKeyByType(paramType));
                result = false;
                sysParamDao.queryParamByType(paramType);
                result = new SysParam();
            }
        };
        try {
            sysParamService.queryParam(paramType);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkSysParamDataException.class);
        }
        new Expectations(RedisUtils.class) {
            {
                sysParamDao.queryParamByType(paramType);
                result = sysParam;

                RedisUtils.set(ParamTypeRedisEnum.getKeyByType(paramType), sysParam);
                result = true;
            }
        };
        result = sysParamService.queryParam(paramType);
        Assert.assertEquals(result.getCode(), (int) SysParamResultCode.SUCCESS);
    }
}
