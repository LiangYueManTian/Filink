package com.fiberhome.filink.securitystrategy.service.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.securitystrategy.bean.IpAddress;
import com.fiberhome.filink.securitystrategy.bean.IpRange;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyConstants;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyI18n;
import com.fiberhome.filink.securitystrategy.dao.IpRangeDao;
import com.fiberhome.filink.securitystrategy.exception.*;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemcommons.utils.SystemLanguageUtil;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.apache.ibatis.session.RowBounds;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

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
     * 系统语言
     */
    @Injectable
    private SystemLanguageUtil languageUtil;
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
     * hasIpAddress
     */
    @Test
    public void hasIpAddressTest() {
        Map<String, IpRange> ipRangeMap = new HashMap<>();
        IpAddress ipAddress = new IpAddress();
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hasKey(SecurityStrategyConstants.IP_RANGE_REDIS);
                result = true;

                RedisUtils.hGetMap(SecurityStrategyConstants.IP_RANGE_REDIS);
                result = ipRangeMap;
            }
        };
        Result result = ipRangeService.hasIpAddress(ipAddress);
        Assert.assertEquals(0, result.getCode());
        ipAddress.setIpAddress("10.5.24.224");
        IpRange ipRange = new IpRange();
        ipRange.setIpType(SecurityStrategyConstants.IPV4);
        ipRange.setStartIp("1.1.1.1");
        ipRange.setEndIp("1.1.1.2");
        ipRangeMap.put("1", ipRange);
        result = ipRangeService.hasIpAddress(ipAddress);
        Assert.assertEquals(-1, result.getCode());
        IpRange ipRange1 = new IpRange();
        ipRange1.setIpType(SecurityStrategyConstants.IPV4);
        ipRange1.setStartIp("10.5.1.1");
        ipRange1.setEndIp("10.5.32.2");
        ipRangeMap.put("2", ipRange1);
        result = ipRangeService.hasIpAddress(ipAddress);
        Assert.assertEquals(0, result.getCode());
        IpRange ipRange2 = new IpRange();
        ipRange2.setIpType(SecurityStrategyConstants.IPV4);
        ipRange2.setStartIp("0.0.0.0");
        ipRange2.setEndIp("0.0.0.0");
        ipRangeMap.put("2", ipRange2);
        result = ipRangeService.hasIpAddress(ipAddress);
        Assert.assertEquals(0, result.getCode());
        ipAddress.setIpAddress("5:5:5:5:5:5:5:5");
        IpRange ipRange3 = new IpRange();
        ipRange3.setIpType(SecurityStrategyConstants.IPV6);
        ipRange3.setStartIp("1:5:5:5:5:5:5:5");
        ipRange3.setEndIp("2:5:5:5:5:5:5:5");
        ipRangeMap.put("3", ipRange3);
        result = ipRangeService.hasIpAddress(ipAddress);
        Assert.assertEquals(-1, result.getCode());
        IpRange ipRange4 = new IpRange();
        ipRange4.setIpType(SecurityStrategyConstants.IPV6);
        ipRange4.setStartIp("0004:0005:0005:0005:0005:0005:0005:0005");
        ipRange4.setEndIp("0006:0005:0005:0005:0005:0005:0005:0005");
        ipRangeMap.put("4", ipRange4);
        result = ipRangeService.hasIpAddress(ipAddress);
        Assert.assertEquals(0, result.getCode());
        ipRange4.setStartIp("0000:0000:0000:0000:0000:0000:0000:0000");
        ipRange4.setEndIp("0000:0000:0000:0000:0000:0000:0000:0000");
        ipRangeMap.put("4", ipRange4);
        result = ipRangeService.hasIpAddress(ipAddress);
        Assert.assertEquals(0, result.getCode());
    }


    /**
     * queryIpRanges
     */
    @Test
    public void queryIpRangesTest() {
        QueryCondition<IpRange> queryCondition = new QueryCondition<>();
        List<FilterCondition> filterConditions = new ArrayList<>();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        queryCondition.setPageCondition(pageCondition);
        queryCondition.setFilterConditions(filterConditions);
        List<IpRange> ipRanges = new ArrayList<>();
        new Expectations() {
            {
                ipRangeDao.selectPage((RowBounds) any,(Wrapper<IpRange>) any);
                result = ipRanges;
            }
        };
        Result result = ipRangeService.queryIpRanges(queryCondition);
        Assert.assertEquals(0, result.getCode());
        ipRanges.add(ipRange);
        result = ipRangeService.queryIpRanges(queryCondition);
        Assert.assertEquals(0, result.getCode());
    }

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
                I18nUtils.getSystemString(anyString);
                result = "起始IP或结束IP在${startIp}~${endIp}范围内";

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
    /**
     * updateIpRange
     */
    @Test
    public void updateIpRangeTest() {
        IpRange ipRange = new IpRange();
        ipRange.setRangeId("sdhanjkfakjwdoiajd");
        new Expectations() {
            {
                ipRangeDao.queryIpRangeById(anyString);
                result = ipRange;
            }
        };
        ipRange.setIpType(SecurityStrategyConstants.IPV6);
        ipRange.setStartIp("0");
        try {
            ipRangeService.updateIpRange(ipRange);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkIpRangeFormatException.class);
        }
        ipRange.setStartIp("2:1:1:1:1:1:1:1");
        ipRange.setEndIp("0:0:0:0:0:0:0:0");
        ipRange.setMask("32");
        try {
            ipRangeService.updateIpRange(ipRange);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkIpRangeNotZeroException.class);
        }
        ipRange.setStartIp("2:1:1:1:1:1:1:1");
        ipRange.setEndIp("1:2:2:1:1:1:1:1");
        try {
            ipRangeService.updateIpRange(ipRange);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkIpRangeNotRangeException.class);
        }
        ipRange.setStartIp("1:1:1:1:1:1:1:1");
        try {
            ipRangeService.updateIpRange(ipRange);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkIpRangeNotRangeException.class);
        }
        ipRange.setStartIp("1:2:0:1:1:1:1:1");
        List<IpRange> ipRanges = new ArrayList<>();
        IpRange ipRange1 = new IpRange();
        ipRange1.setStartIp("0001:0001:0001:0001:0001:0001:0001:0001");
        ipRange1.setEndIp("0001:0002:0001:0001:0001:0001:0001:0001");
        ipRanges.add(ipRange1);
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "起始IP或结束IP在${startIp}~${endIp}范围内";

                ipRangeDao.queryRangesByType((IpRange) any);
                result = ipRanges;
            }
        };
        try {
            ipRangeService.updateIpRange(ipRange);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkIpRangeOverlapException.class);
        }
        ipRange1.setStartIp("0001:0002:0001:0001:0001:0001:0001:0001");
        ipRanges.set(0, ipRange1);
        try {
            ipRangeService.updateIpRange(ipRange);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkIpRangeContainException.class);
        }
        ipRange1.setStartIp("0001:0003:0001:0001:0001:0001:0001:0001");
        ipRange1.setEndIp("0001:0004:0001:0001:0001:0001:0001:0001");
        new Expectations(RequestInfoUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "sjfoiejsoijesoij";
            }
            {
                ipRangeDao.updateById((IpRange) any);
                result = 0;
            }
        };
        try {
            ipRangeService.updateIpRange(ipRange);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkSecurityStrategyDatabaseException.class);
        }
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hHasKey(SecurityStrategyConstants.IP_RANGE_REDIS, anyString);
                result = true;

                RedisUtils.hSet(SecurityStrategyConstants.IP_RANGE_REDIS, anyString, any);

                ipRangeDao.updateById((IpRange) any);
                result = 1;
            }
        };
        Result result = ipRangeService.updateIpRange(ipRange);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
    }
    /**
     * updateIpRangeStatus
     */
    @Test
    public void updateIpRangeStatusTest() {
        List<String> rangeIds = new ArrayList<>();
        rangeIds.add("sahkdj");
        List<IpRange> ipRangeList = new ArrayList<>();
        IpRange ipRange = new IpRange();
        ipRange.setRangeId("sdhanjkfakjwdoiajd");
        ipRange.setRangeStatus("1");
        ipRange.setStartIp("127.0.0.1");
        ipRange.setEndIp("127.0.0.1");
        ipRangeList.add(ipRange);
        new Expectations(RequestInfoUtils.class) {
            {
                ipRangeDao.queryRangesById((List) any);
                result = ipRangeList;

                RequestInfoUtils.getUserId();
                result = "sjfoiejsoijesoij";

                ipRangeDao.updateRangesStatus((List) any, anyString, anyString);
                result = 0;
            }
        };
        try {
            ipRangeService.updateIpRangeStatus(rangeIds, "1");
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkSecurityStrategyDatabaseException.class);
        }
        new Expectations(I18nUtils.class, RedisUtils.class) {
            {
                RedisUtils.hasKey(SecurityStrategyConstants.IP_RANGE_REDIS);
                result = true;

                I18nUtils.getSystemString(anyString);
                result = "启用了IP范围：${startIp}~${endIp}";

                RedisUtils.hGetMap(SecurityStrategyConstants.IP_RANGE_REDIS);
                result = new HashMap<>(16);

                RedisUtils.hRemove(SecurityStrategyConstants.IP_RANGE_REDIS, any);

                ipRangeDao.updateRangesStatus((List) any, anyString, anyString);
                result = 1;

                RedisUtils.hSetMap(SecurityStrategyConstants.IP_RANGE_REDIS, (Map) any);
            }
        };
        Result result = ipRangeService.updateIpRangeStatus(rangeIds, "1");
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
        ipRange.setRangeStatus("0");
        result = ipRangeService.updateIpRangeStatus(rangeIds, "0");
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
    }
    /**
     * updateAllRangesStatus
     */
    @Test
    public void updateAllRangesStatusTest() {
        IpRange ipRange = new IpRange();
        ipRange.setRangeId("5445645");
        ipRange.setRangeStatus("1");
        List<IpRange> ipRanges = new ArrayList<>();
        new Expectations(RequestInfoUtils.class, I18nUtils.class, RedisUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "sjfoiejsoijesoij";

                languageUtil.getI18nString(anyString);
                result = "全部启用了访问控制列表";

                RedisUtils.remove(anyString);
                RedisUtils.hasKey(SecurityStrategyConstants.IP_RANGE_REDIS);
                result = false;
                ipRangeDao.queryIpRangeAll();
                result = ipRanges;
                I18nUtils.getSystemString(SecurityStrategyI18n.IP_RANGE_ENABLE_DISABLE_SUCCESS);
                result = "成功";
                RedisUtils.hSetMap(SecurityStrategyConstants.IP_RANGE_REDIS, (Map) any);
            }
        };
        Result result = ipRangeService.updateAllRangesStatus(ipRange);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
        ipRanges.add(ipRange);
        result = ipRangeService.updateAllRangesStatus(ipRange);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
        ipRange.setRangeStatus("0");
        ipRanges.add(ipRange);
        result = ipRangeService.updateAllRangesStatus(ipRange);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
    }
    /**
     * deleteRanges
     */
    @Test
    public void deleteRangesTest() {
        String rangeId = "aoksdpokjwpdkjpowak";
        List<String> rangeIds = new ArrayList<>();
        rangeIds.add(rangeId);
        List<IpRange> ipRanges = new ArrayList<>();
        new Expectations() {
            {
                ipRangeDao.queryRangesById(rangeIds);
                result = ipRanges;
            }
        };
        try {
            ipRangeService.deleteRanges(rangeIds);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkIpRangeNotExistException.class);
        }
        IpRange ipRange = new IpRange();
        ipRange.setRangeId(rangeId);
        ipRange.setStartIp("127.0.0.1");
        ipRange.setEndIp("127.0.0.1");
        ipRanges.add(ipRange);
        new Expectations(RequestInfoUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "sjfoiejsoijesoij";
            }
            {
                ipRangeDao.deleteRangesByIds(rangeIds, anyString, (String) any);
                result = 0;
            }
        };
        try {
            ipRangeService.deleteRanges(rangeIds);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkSecurityStrategyDatabaseException.class);
        }
        new Expectations(I18nUtils.class, RedisUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "IP地址范围删除成功";

                RedisUtils.hasKey(SecurityStrategyConstants.IP_RANGE_REDIS);
                result = true;

                RedisUtils.hRemove(SecurityStrategyConstants.IP_RANGE_REDIS, any);

                ipRangeDao.deleteRangesByIds(rangeIds, anyString, (String) any);
                result = rangeIds.size();
            }
        };
        Result result = ipRangeService.deleteRanges(rangeIds);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
    }
}
