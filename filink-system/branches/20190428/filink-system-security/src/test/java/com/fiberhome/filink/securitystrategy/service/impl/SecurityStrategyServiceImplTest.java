//package com.fiberhome.filink.securitystrategy.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.fiberhome.filink.logapi.log.LogProcess;
//import com.fiberhome.filink.securitystrategy.bean.AccountParam;
//import com.fiberhome.filink.securitystrategy.bean.AccountSecurityStrategy;
//import com.fiberhome.filink.securitystrategy.bean.PasswordParam;
//import com.fiberhome.filink.securitystrategy.bean.PasswordSecurityStrategy;
//import com.fiberhome.filink.systemcommons.bean.SysParam;
//import com.fiberhome.filink.systemcommons.dao.SysParamDao;
//import mockit.Injectable;
//import mockit.Tested;
//import mockit.integration.junit4.JMockit;
//import org.junit.Before;
//import org.junit.runner.RunWith;
//
//@RunWith(JMockit.class)
//public class SecurityStrategyServiceImplTest {
//    /**测试对象 SecurityStrategyServiceImpl*/
//    @Tested
//    private SecurityStrategyServiceImpl securityStrategyService;
//    /**Mock SysParamDao*/
//    @Injectable
//    private SysParamDao sysParamDao;
//    /**Mock LogProcess*/
//    @Injectable
//    private LogProcess logProcess;
//    /**PasswordParam*/
//    private PasswordParam passwordParam;
//    /**AccountParam*/
//    private AccountParam accountParam;
//    /**SysParam*/
//    private SysParam sysParam;
//
//    /**
//     * 初始化
//     */
//    @Before
//    public void setUp() {
//        passwordParam = new PasswordParam();
//        PasswordSecurityStrategy password = new PasswordSecurityStrategy();
//        password.setMinLength(10);
//        password.setContainLower("1");
//        password.setContainNumber("1");
//        password.setContainUpper("1");
//        password.setContainSpecialCharacter("1");
//        passwordParam.setPasswordSecurityStrategy(password);
//        passwordParam.setParamId("1001");
//
//        accountParam = new AccountParam();
//        AccountSecurityStrategy account = new AccountSecurityStrategy();
//        account.setMinLength(11);
//        account.setIllegalLoginCount(11);
//        account.setIntervalTime(11);
//        account.setForbidStrategy("1");
//        account.setLockStrategy("1");
//        account.setNoOperationTime(11);
//        accountParam.setAccountSecurityStrategy(account);
//        accountParam.setParamId("100001");
//
//        String passwordStrategy = JSON.toJSONString(passwordParam.getPasswordSecurityStrategy());
//        sysParam = new SysParam();
//        sysParam.setParamId("jdaiojojaoiwjd");
//        sysParam.setParamType("1");
//        sysParam.setPresentValue(passwordStrategy);
//        sysParam.setDefaultValue(passwordStrategy);
//    }
//
////    /**
////     * updatePasswordStrategy
////     */
////    @Test
////    public void updatePasswordStrategyTest() {
////        new Expectations() {
////            {
////                sysParamDao.queryParamById(anyString);
////                result = null;
////            }
////        };
////        try {
////            securityStrategyService.updatePasswordStrategy(passwordParam);
////        } catch (Exception e) {
////            Assert.assertSame(e.getClass(), FilinkSecurityStrategyDataException.class);
////        }
////        new Expectations(RequestInfoUtils.class) {
////            {
////                sysParamDao.queryParamById(anyString);
////                result = sysParam;
////            }
////            {
////                RequestInfoUtils.getUserId();
////                result = "sjadoijad";
////            }
////            {
////                sysParamDao.updateParamById((SysParam) any);
////                result = 0;
////            }
////        };
////        try {
////            securityStrategyService.updatePasswordStrategy(passwordParam);
////        } catch (Exception e) {
////            Assert.assertSame(e.getClass(), FilinkSecurityStrategyDatabaseException.class);
////        }
////        new Expectations(RedisUtils.class, I18nUtils.class) {
////            {
////                sysParamDao.updateParamById((SysParam) any);
////                result = 1;
////            }
////            {
////                RedisUtils.set(anyString, any);
////                result = true;
////            }
////            {
////                I18nUtils.getString(anyString);
////                result = "sadjuihawoid";
////            }
////        };
////        Result result = securityStrategyService.updatePasswordStrategy(passwordParam);
////        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
////    }
////    /**
////     * updateAccountStrategy
////     */
////    @Test
////    public void updateAccountStrategyTest() {
////        accountParam.getAccountSecurityStrategy().setLockedTime(0);
////        try {
////            securityStrategyService.updateAccountStrategy(accountParam);
////        } catch (Exception e) {
////            Assert.assertSame(e.getClass(), FilinkSecurityStrategyParamException.class);
////        }
////        accountParam.getAccountSecurityStrategy().setLockStrategy("0");
////        accountParam.getAccountSecurityStrategy().setNoLoginTime(0);
////        try {
////            securityStrategyService.updateAccountStrategy(accountParam);
////        } catch (Exception e) {
////            Assert.assertSame(e.getClass(), FilinkSecurityStrategyParamException.class);
////        }
////        accountParam.getAccountSecurityStrategy().setForbidStrategy("0");
////        new Expectations(RequestInfoUtils.class, RedisUtils.class, I18nUtils.class) {
////            {
////                sysParamDao.queryParamById(anyString);
////                result = sysParam;
////
////                RequestInfoUtils.getUserId();
////                result = "sjadoijad";
////
////                sysParamDao.updateParamById((SysParam) any);
////                result = 1;
////
////                RedisUtils.set(anyString, any);
////                result = true;
////
////                I18nUtils.getString(anyString);
////                result = "sadjuihawoid";
////            }
////        };
////        Result result = securityStrategyService.updateAccountStrategy(accountParam);
////        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
////    }
//}
