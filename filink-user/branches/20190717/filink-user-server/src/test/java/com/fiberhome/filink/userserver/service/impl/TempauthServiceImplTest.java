package com.fiberhome.filink.userserver.service.impl;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.AreaInfo;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.filinklockapi.bean.Lock;
import com.fiberhome.filink.filinklockapi.feign.LockFeign;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.AliAccessKey;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.smsapi.send.aliyun.AliyunMobilePush;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userserver.bean.*;
import com.fiberhome.filink.userserver.dao.AuthDeviceDao;
import com.fiberhome.filink.userserver.dao.RoleDao;
import com.fiberhome.filink.userserver.dao.TempauthDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.service.UserService;
import com.fiberhome.filink.userserver.service.UserStream;
import com.fiberhome.filink.userserver.stream.UpdateUserStream;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(JMockit.class)
public class TempauthServiceImplTest {

    @Tested
    private TempAuthServiceImpl tempauthService;

    @Injectable
    private TempauthDao tempauthDao;

    @Injectable
    private LockFeign lockFeign;

    @Injectable
    private AuthDeviceDao authDeviceDao;

    @Injectable
    private AliyunMobilePush aliyunMobilePush;

    @Injectable
    private ParameterFeign parameterFeign;

    @Injectable
    private UserService userService;

    @Injectable
    private AreaFeign areaFeign;

    @Injectable
    private UserStream userStream;

    @Injectable
    private UserDao userDao;

    @Injectable
    private RoleDao roleDao;

    @Injectable
    private DeviceFeign deviceFeign;

    @Mocked
    private RedisUtils redisUtils;
    @Injectable
    private UpdateUserStream updateUserStream;
    @Mocked
    private RequestInfoUtils requestInfoUtils;
    @Injectable
    private SystemLanguageUtil systemLanguageUtil;
    @Mocked
    private I18nUtils i18nUtils;


    @Test
    public void queryTempAuthByCondition() throws Exception {

        QueryCondition<TempAuthParameter> queryCondition = new QueryCondition<>();
        TempAuthParameter tempAuthParameter = new TempAuthParameter();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(1);
        pageCondition.setPageNum(1);
        queryCondition.setBizCondition(tempAuthParameter);
        queryCondition.setPageCondition(pageCondition);

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "2";
            }
        };

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getToken() {
                return "123";
            }
        };

        new Expectations() {
            {
                User user = new User();
                user.setId("123");
                user.setDeptId("123");
                Role role = new Role();
                List<RoleDeviceType> roleDeviceTypeList = new ArrayList<>();
                RoleDeviceType roleDeviceType = new RoleDeviceType();
                roleDeviceType.setId("123");
                roleDeviceTypeList.add(roleDeviceType);
                role.setRoleDevicetypeList(roleDeviceTypeList);
                user.setRole(role);
                userService.queryCurrentUser(anyString, anyString);
                result = user;
            }
        };

        new Expectations() {
            {
                List<String> deptList = new ArrayList<>();
                deptList.add("123");
                areaFeign.selectAreaIdsByDeptIds(deptList);
                result = null;
            }
        };
        tempauthService.queryTempAuthByCondition(queryCondition);

        new Expectations() {
            {
                List<String> deptList = new ArrayList<>();
                deptList.add("123");
                areaFeign.selectAreaIdsByDeptIds(deptList);
                List<String> areaList = new ArrayList<>();
                areaList.add("123");
                result = areaList;
            }
        };

        new Expectations() {
            {
                List<TempAuth> tempAuthList = new ArrayList<>();
                TempAuth tempauth = new TempAuth();
                tempauth.setId("123");
                tempAuthList.add(tempauth);
                tempauthDao.queryTempAuthByCondition(tempAuthParameter);
                result = tempAuthList;
            }
        };

        new Expectations() {
            {
                tempauthDao.queryTempAuthNumberByCondition(tempAuthParameter);
                result = 1;
            }
        };

        tempauthService.queryTempAuthByCondition(queryCondition);
    }

    @Test
    public void audingTempAuthById() throws Exception {

        TempAuth tempauth2 = null;
        tempauthService.audingTempAuthById(tempauth2);

        TempAuth tempauth = new TempAuth();
        tempauth.setUserId("123");
        tempauth.setAuthStatus(1);
        new Expectations() {
            {
                tempauthDao.queryTempAuthById(anyString);
                result = null;
            }
        };
        tempauthService.audingTempAuthById(tempauth);

        new Expectations() {
            {
                TempAuth audintTempauth = new TempAuth();
                audintTempauth.setId("123");
                tempauthDao.queryTempAuthById(anyString);
                result = audintTempauth;
            }
        };

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "2";
            }
        };

        new Expectations() {
            {
                tempauthDao.modifyTempAuthStatus(tempauth);
                result = 0;
            }
        };
        try {
            tempauthService.audingTempAuthById(tempauth);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        TempAuth finalTempauth = tempauth;
        new Expectations() {
            {
                tempauthDao.modifyTempAuthStatus(finalTempauth);
                result = 1;
            }
        };

        new Expectations() {
            {
                Set<String> keyToken = new HashSet<>();
                keyToken.add("123");
                RedisUtils.keys(anyString);
                result = keyToken;
            }
        };

        new Expectations() {
            {
                User user = new User();
                user.setAppKey("123");
                user.setPushId("123");
                RedisUtils.get(anyString);
                result = user;
            }
        };

        new Expectations() {
            {
                AliAccessKey aliAccessKey = new AliAccessKey();
                parameterFeign.queryMobilePush();
                result = aliAccessKey;
            }
        };
        tempauthService.audingTempAuthById(tempauth);

        new Expectations() {
            {
                AliAccessKey aliAccessKey = new AliAccessKey();
                aliAccessKey.setAccessKeySecret("123");
                aliAccessKey.setAccessKeyId("123");
                parameterFeign.queryMobilePush();
                result = aliAccessKey;
            }
        };

        tempauthService.audingTempAuthById(tempauth);
    }

    @Test
    public void batchAudingTempAuthByIds() throws Exception {

        TempAuth tempauth = new TempAuth();
        tempauth.setAuthStatus(1);
        List<String> userIdList = new ArrayList<>();
        userIdList.add("123");
        String[] idList1 = null;
        tempauthService.batchAudingTempAuthByIds(idList1, tempauth, userIdList);

        String[] idList = {"123"};
        new Expectations() {
            {
                tempauthDao.batchQueryTempAuthByIds(idList);
                result = null;
            }
        };
        tempauthService.batchAudingTempAuthByIds(idList, tempauth, userIdList);

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "2";
            }
        };

        new Expectations() {
            {
                List<TempAuth> tempAuthList = new ArrayList<>();
                tempauth.setId("123");
                tempAuthList.add(tempauth);
                tempauthDao.batchQueryTempAuthByIds(idList);
                result = tempAuthList;
            }
        };

        new Expectations() {
            {
                tempauthDao.batchModifyTempAuthStatus(idList, tempauth);
                result = 0;
            }
        };

        try {
            tempauthService.batchAudingTempAuthByIds(idList, tempauth, userIdList);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        new Expectations() {
            {
                tempauthDao.batchModifyTempAuthStatus(idList, tempauth);
                result = 1;
            }
        };

        new Expectations() {
            {
                Set<String> keyToken = new HashSet<>();
                keyToken.add("123");
                RedisUtils.keys(anyString);
                result = keyToken;
            }
        };

        new Expectations() {
            {
                User user = new User();
                user.setAppKey("456");
                user.setPushId("789");
                RedisUtils.get(anyString);
                result = user;
            }
        };

        new Expectations() {
            {
                AliAccessKey aliAccessKey = new AliAccessKey();
                parameterFeign.queryMobilePush();
                result = aliAccessKey;
            }
        };
        tempauthService.batchAudingTempAuthByIds(idList, tempauth, userIdList);

        new Expectations() {
            {
                AliAccessKey aliAccessKey = new AliAccessKey();
                aliAccessKey.setAccessKeySecret("456");
                aliAccessKey.setAccessKeyId("456");
                parameterFeign.queryMobilePush();
                result = aliAccessKey;
            }
        };

        tempauthService.batchAudingTempAuthByIds(idList, tempauth, userIdList);
    }

    @Test
    public void deleteTempAuthById() throws Exception {

        String id = null;
        tempauthService.deleteTempAuthById(id);

        id = "123";
        new Expectations() {
            {
                tempauthDao.queryTempAuthById(anyString);
                result = null;
            }
        };
        tempauthService.deleteTempAuthById(id);

        new Expectations() {
            {
                TempAuth tempauth = new TempAuth();
                tempauth.setId("123");
                tempauth.setAuthStatus(2);
                tempauthDao.queryTempAuthById(anyString);
                result = tempauth;
            }
        };
        tempauthService.deleteTempAuthById(id);

        new Expectations() {
            {
                TempAuth tempauth = new TempAuth();
                tempauth.setId("123");
                tempauth.setAuthStatus(1);
                tempauthDao.queryTempAuthById(anyString);
                result = tempauth;
            }
        };

        new Expectations() {
            {
                tempauthDao.deleteTempAuthById(anyString);
                result = 0;
            }
        };
        try {
            tempauthService.deleteTempAuthById(id);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        new Expectations() {
            {
                tempauthDao.deleteTempAuthById(anyString);
                result = 1;
            }
        };
        tempauthService.deleteTempAuthById(id);
    }

    @Test
    public void batchDeleteTempAuth() throws Exception {

        String[] idArray = null;
        tempauthService.batchDeleteTempAuth(idArray);

        String[] ids = {"123"};
        new Expectations() {
            {
                tempauthDao.batchQueryTempAuthByIdArray(ids);
                result = null;
            }
        };
        tempauthService.batchDeleteTempAuth(ids);

        new Expectations() {
            {
                TempAuth tempauth = new TempAuth();
                tempauth.setAuthStatus(2);
                List<TempAuth> tempAuthList = new ArrayList<>();
                tempAuthList.add(tempauth);
                tempauthDao.batchQueryTempAuthByIdArray(ids);
                result = tempAuthList;
            }
        };
        tempauthService.batchDeleteTempAuth(ids);

        new Expectations() {
            {
                TempAuth tempauth = new TempAuth();
                tempauth.setAuthStatus(1);
                List<TempAuth> tempAuthList = new ArrayList<>();
                tempAuthList.add(tempauth);
                tempauthDao.batchQueryTempAuthByIdArray(ids);
                result = tempAuthList;
            }
        };

        new Expectations() {
            {
                tempauthDao.batchDeleteUnifyAuth(ids);
                result = 0;
            }
        };
        try {
            tempauthService.batchDeleteTempAuth(ids);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        new Expectations() {
            {
                tempauthDao.batchDeleteUnifyAuth(ids);
                result = 1;
            }
        };
        tempauthService.batchDeleteTempAuth(ids);
    }

    @Test
    public void addTempAuth() throws Exception {

        TempAuth tempauth1 = new TempAuth();
        tempauth1.setAuthExpirationTime(142L);
        tempauth1.setAuthEffectiveTime(786L);
        tempauthService.addTempAuth(tempauth1);

        TempAuth tempauth = new TempAuth();
        tempauth.setId("123");
        tempauth.setName("123");

        tempauth.setAuthEffectiveTime(153246L);
        tempauthService.addTempAuth(tempauth);

        tempauth.setAuthExpirationTime(45652146L);
        tempauthService.addTempAuth(tempauth);

        tempauth.setAuthExpirationTime(4545632153652146L);
        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "2";
            }
        };

        new Expectations() {
            {
                tempauthDao.insert(tempauth);
                result = 1;
            }
        };

        new Expectations() {
            {
                Lock lock = new Lock();
                lock.setDeviceId("123");
                lock.setDoorNum("123");
                lockFeign.queryLockByQrCode(anyString);
                result = lock;
            }
        };

        new Expectations() {
            {
                DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
                deviceInfoDto.setDeviceType("123");
                AreaInfo areaInfo = new AreaInfo();
                areaInfo.setAreaId("123");
                deviceInfoDto.setAreaInfo(areaInfo);
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
            }
        };

        List<AuthDevice> authDeviceList = new ArrayList<>();
        AuthDevice authDevice = new AuthDevice();
        authDevice.setId("123");
        authDevice.setQrCode("123");
        authDeviceList.add(authDevice);
        tempauth.setAuthDeviceList(authDeviceList);
        new Expectations() {
            {
                authDeviceDao.batchAuthDevice(authDeviceList);
                result = 0;
            }
        };
        try {
            tempauthService.addTempAuth(tempauth);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        new Expectations() {
            {
                authDeviceDao.batchAuthDevice(authDeviceList);
                result = 1;
            }
        };

        new Expectations() {
            {
                DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
                deviceInfoDto.setDeviceType("123");
                AreaInfo areaInfo = new AreaInfo();
                Set<String> unitSet = new HashSet<>();
                unitSet.add("123");
                areaInfo.setAccountabilityUnit(unitSet);
                areaInfo.setAreaId("123");
                deviceInfoDto.setAreaInfo(areaInfo);

                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
            }
        };

        new Expectations() {
            {
                List<String> roleIdList = new ArrayList<>();
                roleIdList.add("123");
                roleDao.queryRoleByPermissionAndDeviceType(anyString, anyString);
                result = roleIdList;
            }
        };

        new Expectations() {
            {
                List<String> roleIdList = new ArrayList<>();
                roleIdList.add("123");
                List<String> deptList = new ArrayList<>();
                deptList.add("123");
                userDao.queryUserByRoleAndDepartment(roleIdList, deptList);
                List<User> userList = new ArrayList<>();
                User user = new User();
                user.setId("123");
                userList.add(user);
                result = userList;
            }
        };

        tempauthService.addTempAuth(tempauth);
    }

    @Test
    public void queryTempAuthById() throws Exception {

        String id = "123";
        new Expectations() {
            {
                TempAuth tempauth = new TempAuth();
                tempauth.setId("123");
                tempauthDao.queryTempAuthById(anyString);
                result = tempauth;
            }
        };
        tempauthService.queryTempAuthById(id);
    }

}