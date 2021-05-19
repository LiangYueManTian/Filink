package com.fiberhome.filink.securitystrategy.controller;

import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.securitystrategy.bean.IpRange;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyI18n;
import com.fiberhome.filink.securitystrategy.service.IpRangeService;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(JMockit.class)
public class IpRangeControllerTest {
    /**测试对象 IpRangeController*/
    @Tested
    private IpRangeController ipRangeController;
    /**Mock IpRangeService*/
    @Injectable
    private IpRangeService ipRangeService;
    /**
     * queryIpRanges
     */
    @Test
    public void queryIpRangesTest() {
        QueryCondition<IpRange> queryCondition = new QueryCondition<>();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR);
                result = "parameter error";
            }
        };
        List<FilterCondition> filterConditions = new ArrayList<>();
        queryCondition.setFilterConditions(filterConditions);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        queryCondition.setPageCondition(pageCondition);
        Result result = ipRangeController.queryIpRange(queryCondition);
        Assert.assertEquals(result.getCode(), (int) SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR);
        pageCondition.setPageSize(1);
        queryCondition.setPageCondition(pageCondition);
        result = ipRangeController.queryIpRange(queryCondition);
        Assert.assertEquals(result.getCode(), (int) SecurityStrategyResultCode.SUCCESS);
    }

    /**
     * addIpRange
     */
    @Test
    public void addIpRangeTest() {
        IpRange ipRange = new IpRange();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString);
                result = null;
            }
        };
        Result result = ipRangeController.addIpRange(ipRange);
        Assert.assertEquals(result.getCode(), (int) SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR);
        ipRange.setIpType("ipv4");
        ipRange.setStartIp("127.0.0.1");
        ipRange.setEndIp("127.0.0.1");
        ipRange.setMask("127.0.0.1");
        result = ipRangeController.addIpRange(ipRange);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
    }

    /**
     * queryIpRangeById
     */
    @Test
    public void queryIpRangeByIdTest() {
        IpRange ipRange = new IpRange();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString);
                result = null;
            }
        };
        Result result = ipRangeController.queryIpRangeById(ipRange);
        Assert.assertEquals(result.getCode(), (int) SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR);
        ipRange.setRangeId("sfesafkushfciuawghfui");
        result = ipRangeController.queryIpRangeById(ipRange);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
    }

    /**
     * updateIpRange
     */
    @Test
    public void updateIpRangeTest() {
        IpRange ipRange = new IpRange();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString);
                result = null;
            }
        };
        Result result = ipRangeController.updateIpRange(ipRange);
        Assert.assertEquals(result.getCode(), (int) SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR);
        ipRange.setIpType("ipv4");
        ipRange.setStartIp("127.0.0.1");
        ipRange.setEndIp("127.0.0.1");
        ipRange.setMask("127.0.0.1");
        ipRange.setRangeId("sfesafkushfciuawghfui");
        result = ipRangeController.updateIpRange(ipRange);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
    }

//    /**
//     * updateIpRangeStatus
//     */
//    @Test
//    public void updateIpRangeStatusTest() {
//        IpRange ipRange = new IpRange();
//        new Expectations(I18nUtils.class) {
//            {
//                I18nUtils.getString(anyString);
//                result = null;
//            }
//        };
//        Result result = ipRangeController.updateIpRangeStatus(ipRange);
//        Assert.assertEquals(result.getCode(), (int) SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR);
//        ipRange.setRangeStatus("1");
//        ipRange.setRangeId("sfesafkushfciuawghfui");
//        result = ipRangeController.updateIpRangeStatus(ipRange);
//        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
//    }

    /**
     * updateAllRangesStatus
     */
    @Test
    public void updateAllRangesStatusTest() {
        IpRange ipRange = new IpRange();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString);
                result = null;
            }
        };
        Result result = ipRangeController.updateAllRangesStatus(ipRange);
        Assert.assertEquals(result.getCode(), (int) SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR);
        ipRange.setRangeStatus("1");
        result = ipRangeController.updateAllRangesStatus(ipRange);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
    }

    /**
     * deleteRanges
     */
    @Test
    public void deleteRangesTest() {
        List<String> rangeIds = new ArrayList<>();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString);
                result = null;
            }
        };
        Result result = ipRangeController.deleteRanges(rangeIds);
        Assert.assertEquals(result.getCode(), (int) SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR);
        rangeIds.add("ssadadasdwsdafawda");
        result = ipRangeController.deleteRanges(rangeIds);
        Assert.assertEquals(result.getCode(), (int) SecurityStrategyResultCode.SUCCESS);
    }

}
