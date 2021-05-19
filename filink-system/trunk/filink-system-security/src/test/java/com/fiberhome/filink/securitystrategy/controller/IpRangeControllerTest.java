package com.fiberhome.filink.securitystrategy.controller;

import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.securitystrategy.bean.ChangeIpRangesStatus;
import com.fiberhome.filink.securitystrategy.bean.IpAddress;
import com.fiberhome.filink.securitystrategy.bean.IpRange;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyI18n;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyResultCode;
import com.fiberhome.filink.securitystrategy.service.IpRangeService;
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
     * hasIpAddress
     */
    @Test
    public void hasIpAddressTest() {
        IpAddress ipAddress = new IpAddress();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR);
                result = "请求参数错误";
            }
        };
        Result result = ipRangeController.hasIpAddress(ipAddress);
        Assert.assertEquals(result.getCode(), (int) SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR);
        ipAddress.setIpAddress("0.0.0.0");
        result = ipRangeController.hasIpAddress(ipAddress);
        Assert.assertEquals(result.getCode(), (int) SecurityStrategyResultCode.SUCCESS);
    }

    /**
     * queryIpRanges
     */
    @Test
    public void queryIpRangeTest() {
        QueryCondition<IpRange> queryCondition = new QueryCondition<>();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR);
                result = "请求参数错误";
            }
        };
        Result result = ipRangeController.queryIpRange(queryCondition);
        Assert.assertEquals(result.getCode(), (int) SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR);
        List<FilterCondition> filterConditions = new ArrayList<>();
        queryCondition.setFilterConditions(filterConditions);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
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
                I18nUtils.getSystemString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR);
                result = "请求参数错误";
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
                I18nUtils.getSystemString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR);
                result = "请求参数错误";
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
                I18nUtils.getSystemString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR);
                result = "请求参数错误";
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

    /**
     * updateIpRangeStatus
     */
    @Test
    public void updateIpRangeStatusTest() {
        ChangeIpRangesStatus changeIpRangesStatus = new ChangeIpRangesStatus();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR);
                result = "请求参数错误";
            }
        };
        Result result = ipRangeController.updateIpRangeStatus(changeIpRangesStatus);
        Assert.assertEquals(result.getCode(), (int) SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR);
        List<String> rangeIds = new ArrayList<>();
        rangeIds.add("sdjkjd");
        changeIpRangesStatus.setRangeIds(rangeIds);
        changeIpRangesStatus.setRangeStatus("1");
        result = ipRangeController.updateIpRangeStatus(changeIpRangesStatus);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
    }

    /**
     * updateAllRangesStatus
     */
    @Test
    public void updateAllRangesStatusTest() {
        IpRange ipRange = new IpRange();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR);
                result = "请求参数错误";
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
                I18nUtils.getSystemString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR);
                result = "请求参数错误";
            }
        };
        Result result = ipRangeController.deleteRanges(rangeIds);
        Assert.assertEquals(result.getCode(), (int) SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR);
        rangeIds.add("");
        result = ipRangeController.deleteRanges(rangeIds);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
    }

}
