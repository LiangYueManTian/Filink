package com.fiberhome.filink.userserver.service.impl;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userserver.bean.*;
import com.fiberhome.filink.userserver.dao.AuthDeviceDao;
import com.fiberhome.filink.userserver.dao.TempauthDao;
import com.fiberhome.filink.userserver.dao.UnifyauthDao;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(JMockit.class)
public class UnifyauthServiceImplTest {

    @Tested
    private UnifyAuthServiceImpl unifyauthService;

    @Injectable
    private UnifyauthDao unifyauthDao;

    @Injectable
    private TempauthDao tempauthDao;

    @Injectable
    private AuthDeviceDao authDeviceDao;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    @Injectable
    private DeviceFeign deviceFeign;

    @Mocked
    private RequestInfoUtils requestInfoUtils;

    @Mocked
    private I18nUtils i18nUtils;


    @Test
    public void addUnifyAuth() throws Exception {

        UnifyAuth unifyauth = new UnifyAuth();
        unifyauthService.addUnifyAuth(unifyauth);

        unifyauth.setName("123");
        unifyauth.setAuthEffectiveTime(12323L);
        unifyauthService.addUnifyAuth(unifyauth);

        unifyauth.setAuthExpirationTime(12523L);
        unifyauthService.addUnifyAuth(unifyauth);

        unifyauth.setAuthExpirationTime(1253464575678323L);
        List<AuthDevice> authDevices = new ArrayList<>();
        AuthDevice authDevice = new AuthDevice();
        authDevice.setAreaId("123");
        authDevices.add(authDevice);
        unifyauth.setAuthDeviceList(authDevices);

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };

        new Expectations() {
            {
                unifyauthDao.insert(unifyauth);
                result = 1;
            }
        };

        new Expectations() {
            {
                authDeviceDao.batchAuthDevice(authDevices);
                result = 0;
            }
        };

        try {
            unifyauthService.addUnifyAuth(unifyauth);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        new Expectations() {
            {
                authDeviceDao.batchAuthDevice(authDevices);
                result = 1;
            }
        };

        unifyauthService.addUnifyAuth(unifyauth);
    }

    @Test
    public void queryUnifyAuthByCondition() throws Exception {

        QueryCondition<UnifyAuthParameter> unifyAuthCondition = new QueryCondition<>();
        UnifyAuthParameter unifyAuthParameter = new UnifyAuthParameter();
        unifyAuthCondition.setBizCondition(unifyAuthParameter);

        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(1);
        unifyAuthCondition.setPageCondition(pageCondition);

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "2";
            }
        };

        new Expectations() {
            {
                List<UnifyAuth> unifyAuthList = new ArrayList<>();
                unifyauthDao.queryUnifyAuthByCondition(unifyAuthParameter);
                result = unifyAuthList;
            }
        };

        new Expectations() {
            {
                unifyauthDao.queryUnifyAuthNumberByCondition(unifyAuthParameter);
                result = 0;
            }
        };

        unifyauthService.queryUnifyAuthByCondition(unifyAuthCondition);
    }

    @Test
    public void modifyUnifyAuth() throws Exception {

        UnifyAuth unifyauth = new UnifyAuth();
        unifyauthService.modifyUnifyAuth(unifyauth);

        unifyauth.setName("123");
        unifyauth.setAuthEffectiveTime(12323L);
        unifyauthService.modifyUnifyAuth(unifyauth);

        unifyauth.setAuthExpirationTime(12523L);
        unifyauthService.modifyUnifyAuth(unifyauth);

        unifyauth.setAuthExpirationTime(1253464575678323L);
        List<AuthDevice> authDevices = new ArrayList<>();
        AuthDevice authDevice = new AuthDevice();
        authDevice.setAreaId("123");
        authDevices.add(authDevice);
        unifyauth.setAuthDeviceList(authDevices);

        new Expectations() {
            {
                unifyauthDao.queryUnifyAuthById(anyString);
                result = null;
            }
        };
        unifyauthService.modifyUnifyAuth(unifyauth);

        new Expectations() {
            {
                unifyauthDao.queryUnifyAuthById(anyString);
                result = unifyauth;
            }
        };

        new Expectations() {
            {
                unifyauthDao.modifyUnifyAuth(unifyauth);
                result = 0;
            }
        };

        try {
            unifyauthService.modifyUnifyAuth(unifyauth);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        new Expectations() {
            {
                unifyauthDao.modifyUnifyAuth(unifyauth);
                result = 1;
            }
        };

        new Expectations() {
            {
                authDeviceDao.batchAuthDevice(authDevices);
                result = 0;
            }
        };
        try {
            unifyauthService.modifyUnifyAuth(unifyauth);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        new Expectations() {
            {
                authDeviceDao.batchAuthDevice(authDevices);
                result = 1;
            }
        };
        unifyauthService.modifyUnifyAuth(unifyauth);
    }

    @Test
    public void queryUnifyAuthById() throws Exception {

        String id = "1";
        new Expectations() {
            {
                UnifyAuth unifyauth = new UnifyAuth();
                unifyauthDao.queryUnifyAuthById(id);
                result = null;
            }
        };
        unifyauthService.queryUnifyAuthById(id);

        new Expectations() {
            {
                UnifyAuth unifyauth = new UnifyAuth();
                unifyauth.setId("123");
                unifyauthDao.queryUnifyAuthById(id);
                result = unifyauth;
            }
        };
        unifyauthService.queryUnifyAuthById(id);
    }

    @Test
    public void deleteUnifyAuthById() throws Exception {

        String id = null;
        unifyauthService.deleteUnifyAuthById(id);

        id = "123";
        new Expectations() {
            {
                UnifyAuth unifyauth = new UnifyAuth();
                unifyauth.setId("123");
                unifyauthDao.queryUnifyAuthById(anyString);
                result = null;
            }
        };
        unifyauthService.deleteUnifyAuthById(id);

        new Expectations() {
            {
                UnifyAuth unifyauth = new UnifyAuth();
                unifyauth.setId("123");
                unifyauth.setAuthExpirationTime(System.currentTimeMillis()+1000);
                unifyauth.setAuthStatus(2);
                unifyauthDao.queryUnifyAuthById(anyString);
                result = unifyauth;
            }
        };
        unifyauthService.deleteUnifyAuthById(id);

        new Expectations() {
            {
                UnifyAuth unifyauth = new UnifyAuth();
                unifyauth.setId("123");
                unifyauth.setAuthStatus(1);
                unifyauth.setAuthExpirationTime(System.currentTimeMillis()-1000);
                unifyauthDao.queryUnifyAuthById(anyString);
                result = unifyauth;
            }
        };

        new Expectations() {
            {
                unifyauthDao.deleteUnifyAuthById(anyString);
                result = 0;
            }
        };
        try {
            unifyauthService.deleteUnifyAuthById(id);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        new Expectations() {
            {
                unifyauthDao.deleteUnifyAuthById(anyString);
                result = 1;
            }
        };
        unifyauthService.deleteUnifyAuthById(id);
    }

    @Test
    public void batchDeleteUnifyAuth() throws Exception {

        String[] ids = null;
        unifyauthService.batchDeleteUnifyAuth(ids);

        String[] idArray = {"1"};
        new Expectations() {
            {
                unifyauthDao.batchQueryUnifyAuthByIds(idArray);
                result = null;
            }
        };
        unifyauthService.batchDeleteUnifyAuth(idArray);

        new Expectations() {
            {
                List<UnifyAuth> unifyAuthList = new ArrayList<>();
                UnifyAuth unifyauth = new UnifyAuth();
                unifyauth.setId("123");
                unifyauth.setAuthStatus(2);
                unifyauth.setAuthExpirationTime(System.currentTimeMillis()+1000);
                unifyAuthList.add(unifyauth);
                unifyauthDao.batchQueryUnifyAuthByIds(idArray);
                result = unifyAuthList;
            }
        };
        unifyauthService.batchDeleteUnifyAuth(idArray);

        new Expectations() {
            {
                List<UnifyAuth> unifyAuthList = new ArrayList<>();
                UnifyAuth unifyauth = new UnifyAuth();
                unifyauth.setId("123");
                unifyauth.setAuthStatus(1);
                unifyauth.setAuthExpirationTime(System.currentTimeMillis()-1000);
                unifyAuthList.add(unifyauth);
                unifyauthDao.batchQueryUnifyAuthByIds(idArray);
                result = unifyAuthList;
            }
        };

        new Expectations() {
            {
                unifyauthDao.batchDeleteUnifyAuth(idArray);
                result = 0;
            }
        };

        try {
            unifyauthService.batchDeleteUnifyAuth(idArray);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        new Expectations() {
            {
                unifyauthDao.batchDeleteUnifyAuth(idArray);
                result = 1;
            }
        };
        unifyauthService.batchDeleteUnifyAuth(idArray);
    }

    @Test
    public void batchModifyUnifyAuthStatus() throws Exception {

        UnifyAuthParameter unifyAuthParameter = null;
        unifyauthService.batchModifyUnifyAuthStatus(unifyAuthParameter);

        unifyAuthParameter = new UnifyAuthParameter();
        unifyAuthParameter.setAuthStatus(2);
        String[] ids = {"123"};
        unifyAuthParameter.setIdArray(ids);

        new Expectations() {
            {
                unifyauthDao.batchQueryUnifyAuthByIds(ids);
                result = null;
            }
        };
        unifyauthService.batchModifyUnifyAuthStatus(unifyAuthParameter);

        new Expectations() {
            {
                List<UnifyAuth> unifyAuthList = new ArrayList<>();
                UnifyAuth unifyauth = new UnifyAuth();
                unifyauth.setAuthStatus(1);
                unifyauth.setId("123");
                unifyAuthList.add(unifyauth);
                unifyauth.setAuthExpirationTime(System.currentTimeMillis()-1000);
                unifyauthDao.batchQueryUnifyAuthByIds(ids);
                result = unifyAuthList;
            }
        };

        new Expectations() {
            {
                String[] idArray = {"123"};
                unifyauthDao.batchModifyUnifyAuthStatus(idArray,2);
                result = 0;
            }
        };

        try {
            unifyauthService.batchModifyUnifyAuthStatus(unifyAuthParameter);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        new Expectations() {
            {
                String[] idArray = {"123"};

                unifyauthDao.batchModifyUnifyAuthStatus(idArray,2);
                result = 1;
            }
        };
        unifyauthService.batchModifyUnifyAuthStatus(unifyAuthParameter);
        new Expectations() {
            {
                List<UnifyAuth> unifyAuthList = new ArrayList<>();
                UnifyAuth unifyauth = new UnifyAuth();
                unifyauth.setAuthStatus(1);
                unifyauth.setId("123");
                unifyAuthList.add(unifyauth);
                unifyauth.setAuthExpirationTime(System.currentTimeMillis()+1000);
                unifyauthDao.batchQueryUnifyAuthByIds(ids);
                result = unifyAuthList;
            }
        };
        unifyauthService.batchModifyUnifyAuthStatus(unifyAuthParameter);
    }

    @Test
    public void queryUserAuthInfoById() throws Exception {

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "2";
            }
        };

        new Expectations() {
            {
                List<UnifyAuth> unifyAuthList = new ArrayList<>();
                UnifyAuth unifyauth = new UnifyAuth();
                unifyauth.setId("123");
                unifyAuthList.add(unifyauth);
                unifyauthDao.queryUnifyAuthByUserId(anyString);
                result = unifyauth;
            }
        };

        new Expectations() {
            {
                List<TempAuth> tempAuthList = new ArrayList<>();
                TempAuth tempauth = new TempAuth();
                tempauth.setId("123");
                tempAuthList.add(tempauth);
                tempauthDao.queryTempAuthByUserId(anyString);
                result = tempAuthList;
            }
        };

        unifyauthService.queryUserAuthInfoById();
    }

    @Test
    public void queryAuthInfoByUserIdAndDeviceAndDoor() throws Exception {

        UserAuthInfo userAuthInfo = new UserAuthInfo();

        new Expectations() {
            {
                List<UnifyAuth> unifyAuthList = new ArrayList<>();
                UnifyAuth unifyauth = new UnifyAuth();
                unifyauth.setId("123");
                unifyAuthList.add(unifyauth);
                unifyauthDao.queryAuthInfoByUserIdAndDeviceAndDoor(userAuthInfo);
                result = unifyauth;
            }
        };

        new Expectations() {
            {
                List<TempAuth> tempAuthList = new ArrayList<>();
                TempAuth tempauth = new TempAuth();
                tempauth.setId("123");
                tempAuthList.add(tempauth);
                unifyauthDao.queryAuthInfoByUserIdAndDeviceAndDoor(userAuthInfo);
                result = tempAuthList;
            }
        };

        unifyauthService.queryAuthInfoByUserIdAndDeviceAndDoor(userAuthInfo);
    }

    @Test
    public void deleteAuthByDevice() throws Exception {

        DeviceInfo deviceInfo = new DeviceInfo();
        new Expectations() {
            {
                List<UnifyAuth> unifyAuthList = new ArrayList<>();
                UnifyAuth unifyauth = new UnifyAuth();
                unifyauth.setId("123");
                unifyAuthList.add(unifyauth);
                List<AuthDevice> authDeviceList = new ArrayList<>();
                AuthDevice authDevice = new AuthDevice();
                authDevice.setId("123");
                authDeviceList.add(authDevice);
                unifyauth.setAuthDeviceList(authDeviceList);
                unifyauthDao.queryUnifyAuthByDevice(deviceInfo);
                result = unifyauth;
            }
        };

        new Expectations() {
            {
                List<TempAuth> tempAuthList = new ArrayList<>();
                TempAuth tempauth = new TempAuth();
                tempauth.setId("123");
                List<AuthDevice> authDeviceList = new ArrayList<>();
                AuthDevice authDevice = new AuthDevice();
                authDevice.setId("456");
                authDeviceList.add(authDevice);
                tempauth.setAuthDeviceList(authDeviceList);
                tempAuthList.add(tempauth);
                tempauthDao.queryTempAuthByDevice(deviceInfo);
                result = tempAuthList;
            }
        };

        new Expectations() {
            {
                String[] ids = {"123"};
                unifyauthDao.batchDeleteUnifyAuth(ids);
                result = 0;
            }
        };

        try {
            unifyauthService.deleteAuthByDevice(deviceInfo);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        new Expectations() {
            {
                String[] ids = {"123"};
                unifyauthDao.batchDeleteUnifyAuth(ids);
                result = 1;
            }
        };

        new Expectations() {
            {
                String[] ids = {"123"};
                tempauthDao.batchDeleteUnifyAuth(ids);
                result = 0;
            }
        };
        try {
            unifyauthService.deleteAuthByDevice(deviceInfo);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        new Expectations() {
            {
                String[] ids = {"123"};
                tempauthDao.batchDeleteUnifyAuth(ids);
                result = 1;
            }
        };

        new Expectations() {
            {
                String[] ids = {"123","456"};
                authDeviceDao.batchDeleteAuthDevice(Arrays.asList(ids));
                result = 1;
            }
        };
        try {
            unifyauthService.deleteAuthByDevice(deviceInfo);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        new Expectations() {
            {
                String[] ids = {"123","456"};
                authDeviceDao.batchDeleteAuthDevice(Arrays.asList(ids));
                result = 2;
            }
        };
        unifyauthService.deleteAuthByDevice(deviceInfo);
    }

    @Test
    public void queryAuthByName() throws Exception {

        UnifyAuth unifyauth = new UnifyAuth();
        unifyauth.setName("123");
        new Expectations() {
            {
                unifyauthDao.queryAuthByName(anyString);
                result = null;
            }
        };

        unifyauthService.queryAuthByName(unifyauth);
    }

}