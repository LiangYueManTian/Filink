package com.fiberhome.filink.securitystrategy.service.impl;

import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.securitystrategy.bean.IpRange;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyConstants;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyI18n;
import com.fiberhome.filink.securitystrategy.dao.IpRangeDao;
import com.fiberhome.filink.securitystrategy.exception.*;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(JMockit.class)
public class IpRangeServiceImplTest {

    /**测试对象 IpRangeServiceImpl*/
    @Tested
    private IpRangeServiceImpl ipRangeService;
    /**Mock IpRangeDao*/
    @Injectable
    private IpRangeDao ipRangeDao;
    /**Mock LogProcess*/
    @Injectable
    private LogProcess logProcess;
    /**IpRange*/
    private IpRange ipRange;

    /**
     * 初始化
     */
    @Before
    public void setUp() {
        SecurityStrategyConstants constants = new SecurityStrategyConstants();
        SecurityStrategyI18n securityStrategyI18n = new SecurityStrategyI18n();
        Date date = new Date();
        ipRange = new IpRange();
        ipRange.setIsDeleted(constants.toString());
        ipRange.setIsDeleted(securityStrategyI18n.toString());
        ipRange.setUpdateUser(ipRange.getIsDeleted());
        ipRange.setCreateUser(ipRange.getUpdateUser());
        ipRange.setUpdateUser(ipRange.getCreateUser());
        ipRange.setUpdateTime(date);
        ipRange.setCreateTime(ipRange.getUpdateTime());
        ipRange.setUpdateTime(ipRange.getCreateTime());
    }


    /**
     * queryIpRanges
     */
//    @Test
//    public void queryIpRangesTest() {
//        QueryCondition<IpRange> queryCondition = new QueryCondition<>();
//        List<FilterCondition> filterConditions = new ArrayList<>();
//        queryCondition.setFilterConditions(filterConditions);
//        List<IpRange> ipRanges = new ArrayList<>();
//        new Expectations() {
//            {
//                ipRangeDao.selectList((Wrapper<IpRange>) any);
//                result = ipRanges;
//            }
//        };
//        Result result = ipRangeService.queryIpRanges(queryCondition);
//        Assert.assertEquals(0, result.getCode());
//        ipRanges.add(ipRange);
//        result = ipRangeService.queryIpRanges(queryCondition);
//        Assert.assertEquals(0, result.getCode());
//    }

    /**
     * addIpRange
     */
    @Test
    public void addIpRangeTest() {
        IpRange ipRange = new IpRange();
        ipRange.setIpType(SecurityStrategyConstants.IPV4);
        ipRange.setStartIp("1");
        try {
            ipRangeService.addIpRange(ipRange);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkIpRangeFormatException.class);
        }
        ipRange.setStartIp("0.0.0.0");
        ipRange.setEndIp("127.0.0.9");
        ipRange.setMask("255.255.255.0");
        try {
            ipRangeService.addIpRange(ipRange);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkIpRangeNotZeroException.class);
        }
        ipRange.setStartIp("127.0.0.10");
        try {
            ipRangeService.addIpRange(ipRange);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkIpRangeNotRangeException.class);
        }
        ipRange.setStartIp("126.0.0.9");
        try {
            ipRangeService.addIpRange(ipRange);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkIpRangeNotRangeException.class);
        }
        ipRange.setStartIp("127.0.0.1");
        List<IpRange> ipRanges = new ArrayList<>();
        IpRange ipRange1 = new IpRange();
        ipRange1.setStartIp("127.0.0.1");
        ipRange1.setEndIp("127.0.0.6");
        ipRanges.add(ipRange1);
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString);
                result = "${startIp}~${endIp}";

                ipRangeDao.queryRangesByType((IpRange) any);
                result = ipRanges;
            }
        };
        try {
            ipRangeService.addIpRange(ipRange);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkIpRangeOverlapException.class);
        }
        ipRange1.setStartIp("127.0.0.6");
        ipRanges.set(0, ipRange1);
        try {
            ipRangeService.addIpRange(ipRange);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkIpRangeContainException.class);
        }
        ipRange1.setStartIp("127.0.0.55");
        ipRange1.setEndIp("127.0.0.66");
        ipRanges.set(0, ipRange1);
        new Expectations(RequestInfoUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "sjfoiejsoijesoij";
            }
            {
                ipRangeDao.addIpRange((IpRange) any);
                result = 0;
            }
        };
        try {
            ipRangeService.addIpRange(ipRange);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkSecurityStrategyDatabaseException.class);
        }
        new Expectations() {
            {
                ipRangeDao.addIpRange((IpRange) any);
                result = 1;
            }
        };
        Result result = ipRangeService.addIpRange(ipRange);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
    }
    /**
     * queryIpRangeById
     */
    @Test
    public void queryIpRangeByIdTest() {
        IpRange ipRange = new IpRange();
        ipRange.setRangeId("sdhanjkfakjwdoiajd");
        new Expectations() {
            {
                ipRangeDao.queryIpRangeById(anyString);
                result = null;
            }
        };
        try {
            ipRangeService.queryIpRangeById(ipRange);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkIpRangeNotExistException.class);
        }
        new Expectations() {
            {
                ipRangeDao.queryIpRangeById(anyString);
                result = ipRange;
            }
        };
        Result result = ipRangeService.queryIpRangeById(ipRange);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
    }
//    /**
//     * updateIpRange
//     */
//    @Test
//    public void updateIpRangeTest() {
//        IpRange ipRange = new IpRange();
//        ipRange.setRangeId("sdhanjkfakjwdoiajd");
//        new Expectations() {
//            {
//                ipRangeDao.queryIpRangeById(anyString);
//                result = ipRange;
//            }
//        };
//        ipRange.setIpType(SecurityStrategyConstants.IPV6);
//        ipRange.setStartIp("0");
//        try {
//            ipRangeService.updateIpRange(ipRange);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkIpRangeFormatException.class);
//        }
//        ipRange.setStartIp("2:1:1:1:1:1:1:1");
//        ipRange.setEndIp("0:0:0:0:0:0:0:0");
//        ipRange.setMask("32");
//        try {
//            ipRangeService.updateIpRange(ipRange);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkIpRangeNotZeroException.class);
//        }
//        ipRange.setStartIp("2:1:1:1:1:1:1:1");
//        ipRange.setEndIp("1:2:2:1:1:1:1:1");
//        try {
//            ipRangeService.updateIpRange(ipRange);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkIpRangeNotRangeException.class);
//        }
//        ipRange.setStartIp("1:1:1:1:1:1:1:1");
//        try {
//            ipRangeService.updateIpRange(ipRange);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkIpRangeNotRangeException.class);
//        }
//        ipRange.setStartIp("1:2:0:1:1:1:1:1");
//        List<IpRange> ipRanges = new ArrayList<>();
//        IpRange ipRange1 = new IpRange();
//        ipRange1.setStartIp("0001:0001:0001:0001:0001:0001:0001:0001");
//        ipRange1.setEndIp("0001:0002:0001:0001:0001:0001:0001:0001");
//        ipRanges.add(ipRange1);
//        new Expectations(I18nUtils.class) {
//            {
//                I18nUtils.getString(anyString);
//                result = "起始IP或结束IP在${startIp}~${endIp}范围内";
//
//                ipRangeDao.queryRangesByType((IpRange) any);
//                result = ipRanges;
//            }
//        };
//        try {
//            ipRangeService.updateIpRange(ipRange);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkIpRangeOverlapException.class);
//        }
//        ipRange1.setStartIp("0001:0002:0001:0001:0001:0001:0001:0001");
//        ipRanges.set(0, ipRange1);
//        try {
//            ipRangeService.updateIpRange(ipRange);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkIpRangeContainException.class);
//        }
//        ipRange1.setStartIp("0001:0003:0001:0001:0001:0001:0001:0001");
//        ipRange1.setEndIp("0001:0004:0001:0001:0001:0001:0001:0001");
//        new Expectations(RequestInfoUtils.class) {
//            {
//                RequestInfoUtils.getUserId();
//                result = "sjfoiejsoijesoij";
//            }
//            {
//                ipRangeDao.updateById((IpRange) any);
//                result = 0;
//            }
//        };
//        try {
//            ipRangeService.updateIpRange(ipRange);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkSecurityStrategyDatabaseException.class);
//        }
//        new Expectations() {
//            {
//                ipRangeDao.updateById((IpRange) any);
//                result = 1;
//            }
//        };
//        Result result = ipRangeService.updateIpRange(ipRange);
//        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
//    }
//    /**
//     * updateIpRangeStatus
//     */
//    @Test
//    public void updateIpRangeStatusTest() {
//        IpRange ipRange = new IpRange();
//        ipRange.setRangeId("sdhanjkfakjwdoiajd");
//        ipRange.setRangeStatus("1");
//        ipRange.setStartIp("127.0.0.1");
//        ipRange.setEndIp("127.0.0.1");
//        new Expectations(RequestInfoUtils.class) {
//            {
//                ipRangeDao.queryIpRangeById(anyString);
//                result = ipRange;
//
//                RequestInfoUtils.getUserId();
//                result = "sjfoiejsoijesoij";
//            }
//            {
//                ipRangeDao.updateById((IpRange) any);
//                result = 0;
//            }
//        };
//        try {
//            ipRangeService.updateIpRangeStatus(ipRange);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkSecurityStrategyDatabaseException.class);
//        }
//        new Expectations(I18nUtils.class) {
//            {
//                I18nUtils.getString(anyString);
//                result = "启用了IP范围：${startIp}~${endIp}";
//
//                ipRangeDao.updateById((IpRange) any);
//                result = 1;
//            }
//        };
//        Result result = ipRangeService.updateIpRangeStatus(ipRange);
//        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
//        ipRange.setRangeStatus("0");
//        result = ipRangeService.updateIpRangeStatus(ipRange);
//        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
//    }
//    /**
//     * updateAllRangesStatus
//     */
//    @Test
//    public void updateAllRangesStatusTest() {
//        IpRange ipRange = new IpRange();
//        ipRange.setRangeStatus("1");
//        new Expectations(RequestInfoUtils.class, I18nUtils.class) {
//            {
//                RequestInfoUtils.getUserId();
//                result = "sjfoiejsoijesoij";
//
//                I18nUtils.getString(anyString);
//                result = "全部启用了访问控制列表";
//            }
//        };
//        Result result = ipRangeService.updateAllRangesStatus(ipRange);
//        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
//        ipRange.setRangeStatus("0");
//        result = ipRangeService.updateAllRangesStatus(ipRange);
//        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
//    }
//    /**
//     * deleteRanges
//     */
//    @Test
//    public void deleteRangesTest() {
//        String rangeId = "aoksdpokjwpdkjpowak";
//        List<String> rangeIds = new ArrayList<>();
//        rangeIds.add(rangeId);
//        List<IpRange> ipRanges = new ArrayList<>();
//        new Expectations() {
//            {
//                ipRangeDao.queryRangesById(rangeIds);
//                result = ipRanges;
//            }
//        };
//        try {
//            ipRangeService.deleteRanges(rangeIds);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkIpRangeNotExistException.class);
//        }
//        IpRange ipRange = new IpRange();
//        ipRange.setRangeId(rangeId);
//        ipRange.setStartIp("127.0.0.1");
//        ipRange.setEndIp("127.0.0.1");
//        ipRanges.add(ipRange);
//        new Expectations(RequestInfoUtils.class) {
//            {
//                RequestInfoUtils.getUserId();
//                result = "sjfoiejsoijesoij";
//            }
//            {
//                ipRangeDao.deleteRangesByIds(rangeIds, anyString, (String) any);
//                result = 0;
//            }
//        };
//        try {
//            ipRangeService.deleteRanges(rangeIds);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkSecurityStrategyDatabaseException.class);
//        }
//        new Expectations(I18nUtils.class) {
//            {
//                I18nUtils.getString(anyString);
//                result = "IP地址范围删除成功";
//
//                ipRangeDao.deleteRangesByIds(rangeIds, anyString, (String) any);
//                result = rangeIds.size();
//            }
//        };
//        Result result = ipRangeService.deleteRanges(rangeIds);
//        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
//    }
}
