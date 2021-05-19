package com.fiberhome.filink.dump.service.impl;

import com.fiberhome.filink.dump.bean.DumpBean;
import com.fiberhome.filink.dump.bean.DumpParam;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemcommons.dao.SysParamDao;
import com.fiberhome.filink.systemcommons.utils.SystemLanguageUtil;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/7/2
 */
@RunWith(JMockit.class)
public class DumpServiceImplTest {

    @Tested
    private DumpServiceImpl dumpService;

    @Injectable
    private SysParamDao sysParamDao;
    @Injectable
    private LogProcess logProcess;
    @Injectable
    private SystemLanguageUtil systemLanguageUtil;
    @Mocked
    private RedisUtils redisUtils;
    @Mocked
    private I18nUtils i18nUtils;

    DumpParam dumpParam;
    DumpBean dumpBean;

    @Before
    public void setUp() {
        dumpBean = new DumpBean();
        dumpBean.setDumpField("dumpField");
        dumpBean.setDumpInterval("100");
        dumpBean.setDumpOperation("operate");
        dumpBean.setDumpPlace("1");
        dumpBean.setDumpQuantityThreshold("1");
        dumpBean.setDumpScope("0");
        dumpBean.setTriggerCondition("10000");
        dumpBean.setTurnOutNumber("1000");
        dumpBean.setEnableDump("0");

        dumpParam = new DumpParam();
        dumpParam.setDumpBean(dumpBean);
        dumpParam.setParamId("20120311");
        dumpParam.setParamType("13");

    }

    @Test
    public void updateDump() {
        //13
        dumpService.updateDump(dumpParam);
        //12
        dumpParam.setParamType("12");
        dumpService.updateDump(dumpParam);
        //11
        dumpParam.setParamType("11");
        dumpService.updateDump(dumpParam);
        //异常
        new Expectations() {
            {
                sysParamDao.queryParamById(anyString);
                result = null;
            }
        };
        dumpService.updateDump(dumpParam);

    }

    @Test
    public void queryDump() {
        dumpService.queryDump("13");

        new Expectations(BeanUtils.class){
            {
                try {
                    BeanUtils.populate( any, (Map<String, ? extends Object>) any);
                }catch (Exception e){

                }
                result = new IllegalAccessException();
            }
        };
        dumpService.queryDump("13");
    }
}