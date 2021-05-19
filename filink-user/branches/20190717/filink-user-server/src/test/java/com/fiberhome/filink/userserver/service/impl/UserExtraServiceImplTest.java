package com.fiberhome.filink.userserver.service.impl;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.Export;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.license.api.LicenseFeign;
import com.fiberhome.filink.license.bean.License;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.AliAccessKey;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.security.api.SecurityFeign;
import com.fiberhome.filink.security.bean.AccountSecurityStrategy;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.smsapi.api.SendSmsAndEmail;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userserver.bean.*;
import com.fiberhome.filink.userserver.dao.OnlineUserDao;
import com.fiberhome.filink.userserver.dao.RoleDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.service.UserStream;
import com.fiberhome.filink.userserver.userexport.UserListExport;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(JMockit.class)
public class UserExtraServiceImplTest {

    @Tested
    private UserExtraServiceImpl userExtraService;
    @Injectable
    private UserDao userDao;

    @Injectable
    private OnlineUserDao onlineUserDao;

    @Injectable
    private SecurityFeign securityFeign;

    @Injectable
    private UserListExport userListExport;

    @Injectable
    private UserStream userStream;

    @Injectable
    private LicenseFeign licenseFeign;

    @Injectable
    private ParameterFeign parameterFeign;

    @Injectable
    private SendSmsAndEmail aliyunSendSms;

    @Injectable
    private LogProcess logProcess;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    @Injectable
    private RoleDao roleDao;


    @Mocked
    private RedisUtils redisUtils;

    @Mocked
    private I18nUtils i18nUtils;

    @Mocked
    private RequestInfoUtils requestInfoUtils;

    @Injectable
    private Integer maxExportDataSize = 1000;

    @Mocked
    private MessageFormat messageFormat;

    @Test
    public void queryUserByDeptList() throws Exception {

        List<String> deptIdList = new ArrayList<>();
        userExtraService.queryUserByDeptList(deptIdList);

        deptIdList.add("123");
        new Expectations() {
            {
                List<User> userList = new ArrayList<>();
                User user = new User();
                user.setId("123");
                userList.add(user);
                userDao.queryUserByDeptList(deptIdList);
                result = userList;
            }
        };

        userExtraService.queryUserByDeptList(deptIdList);
    }

    @Test
    public void validateUserLogin() throws Exception {

        UserParameter userParameter = new UserParameter();

        new Expectations() {
            {
                License currentLicense = new License();
                licenseFeign.getCurrentLicense();
                currentLicense.endTime = "2018-10-20";
                result = currentLicense;
            }
        };
        userExtraService.validateUserLogin(userParameter);

        new Expectations() {
            {
                License currentLicense = new License();
                licenseFeign.getCurrentLicense();
                result = currentLicense;
            }
        };
        userExtraService.validateUserLogin(userParameter);

        new Expectations() {
            {
                License currentLicense = new License();
                licenseFeign.getCurrentLicense();
                currentLicense.endTime = "2029-10-20";
                currentLicense.maxOnlineNum = "2";
                result = currentLicense;
            }
        };
        new Expectations() {

            {
                Set<String> keyToKen = new HashSet<>();
                keyToKen.add("1");
                keyToKen.add("2");
                keyToKen.add("3");
                RedisUtils.keys(anyString);
                result = keyToKen;
            }
        };
        userExtraService.validateUserLogin(userParameter);

        new Expectations() {

            {
                Set<String> keyToKen = new HashSet<>();
                RedisUtils.keys(anyString);
                result = keyToKen;
            }
        };

        userParameter.setUserName("admin");
        userExtraService.validateUserLogin(userParameter);

        userParameter.setUserName("admin1000");

        new Expectations() {

            {
                Set<String> keyToKen = new HashSet<>();
                keyToKen.add("123");
                RedisUtils.keys(anyString);
                result = keyToKen;
            }
        };
        new Expectations() {
            {
                User user = new User();
                user.setId("123");
                user.setUserName("admin1000");
                user.setMaxUsers(2);
                Role role = new Role();
                List<Permission> permissionList = new ArrayList<>();
                Permission permission = new Permission();
                permission.setId("12");
                user.setLoginType("0");
                permissionList.add(permission);
                role.setPermissionList(permissionList);
                user.setRole(role);
                userDao.queryUserByName(anyString);
                result = user;
            }
        };

        userParameter.setLoginSourse("0");
        userExtraService.validateUserLogin(userParameter);

        userParameter.setLoginSourse("1");
        new Expectations() {
            {
                User user = new User();
                user.setId("123");
                user.setUserName("admin1000");
                user.setMaxUsers(1);
                Role role = new Role();
                List<Permission> permissionList = new ArrayList<>();
                Permission permission = new Permission();
                permission.setId("12");
                user.setLoginType("0");
                permissionList.add(permission);
                role.setPermissionList(permissionList);
                user.setRole(role);
                userDao.queryUserByName(anyString);
                result = user;
            }
        };
        userExtraService.validateUserLogin(userParameter);

        new Expectations() {
            {
                User user = new User();
                user.setId("123");
                user.setUserName("admin1000");
                user.setMaxUsers(1);
                Role role = new Role();
                List<Permission> permissionList = new ArrayList<>();
                Permission permission = new Permission();
                permission.setId("12");
                user.setLoginType("1");
                permissionList.add(permission);
                role.setPermissionList(permissionList);
                user.setRole(role);
                user.setUnlockTime(123458754656987L);
                userDao.queryUserByName(anyString);
                result = user;
            }
        };

        new Expectations() {
            {
                securityFeign.queryAccountSecurity();
                result = null;
            }
        };
        userExtraService.validateUserLogin(userParameter);

        new Expectations() {
            {
                Result objectResult = new Result<>();
                securityFeign.queryAccountSecurity();
                result = objectResult;
            }
        };

//        new Expectations() {
//            {
//                Result objectResult = new Result<>();
//                IpAddress ipAddress = new IpAddress();
//                ipAddress.setIpAddress("127.0.0.1");
//                securityFeign.hasIpAddress(ipAddress);
//                result = objectResult;
//            }
//        };

        userExtraService.validateUserLogin(userParameter);

        new Expectations() {
            {
                User user = new User();
                user.setId("123");
                user.setUserName("admin1000");
                user.setMaxUsers(1);
                Role role = new Role();
                List<Permission> permissionList = new ArrayList<>();
                Permission permission = new Permission();
                permission.setId("12");
                user.setLoginType("1");
                permissionList.add(permission);
                role.setPermissionList(permissionList);
                user.setRole(role);
                user.setUnlockTime(1234587L);
                user.setUserStatus("0");
                userDao.queryUserByName(anyString);
                result = user;
            }
        };

        userExtraService.validateUserLogin(userParameter);


        new Expectations() {
            {
                User user = new User();
                user.setId("123");
                user.setUserName("admin1000");
                user.setMaxUsers(1);
                Role role = new Role();
                List<Permission> permissionList = new ArrayList<>();
                Permission permission = new Permission();
                permission.setId("12");
                user.setLoginType("1");
                permissionList.add(permission);
                role.setPermissionList(permissionList);
                user.setRole(role);
                user.setUnlockTime(1234587L);
                user.setCreateTime(12323235L);
                user.setCountValidityTime("100d");
                userDao.queryUserByName(anyString);
                result = user;
            }
        };
        userExtraService.validateUserLogin(userParameter);


        new Expectations() {
            {
                User user = new User();
                user.setId("123");
                user.setUserName("admin1000");
                user.setMaxUsers(1);
                Role role = new Role();
                List<Permission> permissionList = new ArrayList<>();
                Permission permission = new Permission();
                permission.setId("12");
                user.setLoginType("1");
                permissionList.add(permission);
                role.setPermissionList(permissionList);
                user.setRole(role);
                user.setUnlockTime(1234587L);
                user.setCreateTime(12323235L);
                user.setCountValidityTime("100y");
                userDao.queryUserByName(anyString);
                result = user;
            }
        };

        new Expectations() {
            {
                Set<String> keys = new HashSet<>();
                keys.add("USER_123_123");
                redisUtils.keys(anyString);
                result = keys;
            }
        };
        userExtraService.validateUserLogin(userParameter);
    }

    @Test
    public void exportUserList() throws Exception {

        ExportDto exportDto = new ExportDto<>();
        new Expectations() {
            {

                Export export = null;
                userListExport.insertTask(exportDto, anyString, anyString);
                result = new FilinkExportNoDataException();
            }
        };
        userExtraService.exportUserList(exportDto);
        new Expectations() {
            {

                Export export = null;
                userListExport.insertTask(exportDto, anyString, anyString);
                result = new FilinkExportTaskNumTooBigException();
            }

        };
        userExtraService.exportUserList(exportDto);        new Expectations() {
            {

                Export export = null;
                userListExport.insertTask(exportDto, anyString, anyString);
                result = new FilinkExportDataTooLargeException("test");
            }
        };
        userExtraService.exportUserList(exportDto);
        new Expectations() {
            {

                Export export = null;
                userListExport.insertTask(exportDto, anyString, anyString);
                result = new NullPointerException();
            }
        };
        userExtraService.exportUserList(exportDto);
        new Expectations() {
            {

                Export export = null;
                userListExport.insertTask(exportDto, anyString, anyString);
                result = export;
            }
        };

        userExtraService.exportUserList(exportDto);
        new Expectations() {
            {
                Export export = null;
                userListExport.insertTask(exportDto, anyString, anyString);
                result = new FilinkExportDataTooLargeException("TEST");
            }

        };
//        new Expectations(){
//            {
//                {
//                    MessageFormat.format(anyString,anyString);
//                    result ="test";
//                }
//            }
//        };
        try {
            userExtraService.exportUserList(exportDto);
        } catch (Exception e) {
        }
    }

    @Test
    public void dealLoginFail() throws Exception {

        UserParameter userParameter = new UserParameter();

        new Expectations() {
            {
                Result objectResult = new Result<>();
                AccountSecurityStrategy accountSecurityStrategy = new AccountSecurityStrategy();
                accountSecurityStrategy.setIllegalLoginCount(1);
                accountSecurityStrategy.setLockStrategy("1");
                accountSecurityStrategy.setLockedTime(30);
                accountSecurityStrategy.setIntervalTime(30);
                objectResult.setData(accountSecurityStrategy);
                securityFeign.queryAccountSecurity();
                result = objectResult;
            }
        };
        userParameter.setUserName("123");
        new Expectations() {
            {
                userDao.queryUserByName("123");
                result = null;
            }
        };
        userExtraService.dealLoginFail(userParameter);

        userParameter.setUserName("123");
        new Expectations() {
            {
                User user = new User();
                user.setUserName("123");
                userDao.queryUserByName("123");
                result = user;
            }
        };
        userParameter.setLoginIp("127.0.0.1");

        new Expectations() {
            {
                List<Long> timeList = new ArrayList<>();
                timeList.add(123234L);
                RedisUtils.get(anyString);
                result = timeList;
            }
        };

        new Expectations() {
            {
                RedisUtils.hasKey(anyString);
                result = true;
            }
        };


        userExtraService.dealLoginFail(userParameter);


        new Expectations() {
            {
                RedisUtils.hasKey(anyString);
                result = false;
            }
        };


        userExtraService.dealLoginFail(userParameter);
    }

    @Test
    public void queryDeviceTypeByPermission() throws Exception {

        List<String> deptList = new ArrayList<>();
        deptList.add("123");
        DataPermission dataPermission = new DataPermission();
        dataPermission.setDeptList(deptList);
        new Expectations() {
            {
                List<String> userIds = new ArrayList<>();
                userIds.add("123");
                userDao.queryUserByDeptAndDeviceType(dataPermission);
                result = userIds;
            }
        };

        new Expectations() {
            {
                Set<String> keys = new HashSet<>();
                keys.add("123||123");
                RedisUtils.keys(anyString);
                result = keys;
            }
        };

        userExtraService.queryDeviceTypeByPermission(dataPermission);
    }

    @Test
    public void sendMessage() throws Exception {

        String phoneNumber = "110";
        new Expectations() {
            {
                userDao.queryUserByPhone(anyString);
                result = null;
            }
        };

        userExtraService.sendMessage(phoneNumber);

        new Expectations() {
            {
                User user = new User();
                user.setPhoneNumber(phoneNumber);
                userDao.queryUserByPhone(anyString);
                result = user;
            }
        };

        new Expectations() {
            {
                AliAccessKey aliAccessKey = new AliAccessKey();
                aliAccessKey.setAccessKeyId("123");
                aliAccessKey.setAccessKeySecret("456");
                parameterFeign.queryMessage();
                result = aliAccessKey;
            }
        };

        userExtraService.sendMessage(phoneNumber);
    }

    @Test
    public void queryUserByDevice() throws Exception {

        List<DataPermission> dataPermissionList = new ArrayList<>();
        List<String> deptList = new ArrayList<>();
        deptList.add("123");
        List<String> deviceTypeList = new ArrayList<>();
        deviceTypeList.add("123");
        DataPermission dataPermission = new DataPermission();
        dataPermission.setDeptList(deptList);
        dataPermission.setDeviceTypes(deviceTypeList);
        dataPermissionList.add(dataPermission);

        new Expectations() {
            {
                List<User> userList = new ArrayList<>();
                User user = new User();
                user.setId("123");
                user.setDeptId("123");
                Role role = new Role();
                List<RoleDeviceType> roleDeviceTypes = new ArrayList<>();
                RoleDeviceType roleDeviceType = new RoleDeviceType();
                roleDeviceType.setDeviceTypeId("123");
                roleDeviceTypes.add(roleDeviceType);
                role.setRoleDevicetypeList(roleDeviceTypes);
                user.setRole(role);

                userList.add(user);
                userDao.queryAllUserDetailInfo();
                result = userList;
            }
        };

        new Expectations() {
            {
                Set<String> keySet = new HashSet<>();
                keySet.add("123||123");
                RedisUtils.keys(anyString);
                result = keySet;
            }
        };

        userExtraService.queryUserByDevice(dataPermissionList);
    }

    @Test
    public void getSmsMessage() throws Exception {

        String phoneNumber = "123";
        new Expectations() {
            {
                RedisUtils.hasKey(anyString);
                result = false;
            }
        };
        userExtraService.getSmsMessage(phoneNumber);

        new Expectations() {
            {
                RedisUtils.hasKey(anyString);
                result = true;
            }
        };
        userExtraService.getSmsMessage(phoneNumber);
    }

    @Test
    public void queryUserByPhone() throws Exception {

        String phoneNumber = "123";
        new Expectations() {
            {
                User user = new User();
                userDao.queryUserByPhone(phoneNumber);
                result = user;
            }
        };
        userExtraService.queryUserByPhone(phoneNumber);
    }

    @Test
    public void queryTokenByUserId() throws Exception {

        String userId = "123";
        new Expectations() {
            {
                RedisUtils.keys(anyString);
                result = null;
            }
        };
        userExtraService.queryTokenByUserId(userId);

        new Expectations() {
            {
                Set<String> userKeys = new HashSet<>();
                userKeys.add("USER_123||123");
                RedisUtils.keys(anyString);
                result = userKeys;
            }
        };
        userExtraService.queryTokenByUserId(userId);
    }

    @Test
    public void queryPhoneIdByUserIds() throws Exception {

        List<String> idList = new ArrayList<>();
        userExtraService.queryPhoneIdByUserIds(idList);

        idList.add("123");
        new Expectations() {
            {
                Set<String> keys = new HashSet<>();
                keys.add("123");
                RedisUtils.keys(anyString);
                result = keys;
            }
        };

        new Expectations() {
            {
                User user = new User();
                user.setPushId("USER_123||123");
                RedisUtils.get(anyString);
                result = user;
            }
        };
        userExtraService.queryPhoneIdByUserIds(idList);
    }

    @Test
    public void queryUserIdByName() throws Exception {

        String userName = "123";
        new Expectations() {
            {
                User user = new User();
                user.setPushId("USER_123||123");
                userDao.queryUserIdByName(userName);
                result = user;
            }
        };
        userExtraService.queryUserIdByName(userName);
    }

    @Test
    public void queryUserNumber() throws Exception {

        new Expectations() {
            {
                List<User> userList = new ArrayList<>();
                User user = new User();
                user.setPushId("USER_123||123");
                userDao.queryAllUser();
                userList.add(user);
                result = userList;
            }
        };

        new Expectations() {
            {
                Set<String> tokenKey = new HashSet<>();
                tokenKey.add("123");
                RedisUtils.keys(anyString);
                result = tokenKey;
            }
        };

        new Expectations() {
            {
                RedisUtils.hasKey(anyString);
                result = true;
            }
        };

        userExtraService.queryUserNumber();
    }

    @Test
    public void queryOnlineUserByIdList() throws Exception {

        List<String> idList = new ArrayList<>();
        idList.add("123");

        new Expectations() {
            {
                Set<String> tokenKey = new HashSet<>();
                tokenKey.add("123||123");
                RedisUtils.keys(anyString);
                result = tokenKey;
            }
        };

        new Expectations() {
            {
                User user = new User();
                user.setId("123");
                RedisUtils.get(anyString);
                result = user;
            }
        };

        userExtraService.queryOnlineUserByIdList(idList);
    }

    @Test
    public void modifyUserPhoneIdAndAppKey() throws Exception {

        User user = new User();
        user.setPushId("123");
        user.setAppKey("456");
        user.setPhoneType(0);
        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
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
                RedisUtils.hasKey(anyString);
                result = false;
            }
        };

        userExtraService.modifyUserPhoneIdAndAppKey(user);

        new Expectations() {
            {
                RedisUtils.hasKey(anyString);
                result = true;
            }
        };

        new Expectations() {
            {
                User userObject = new User();
                userObject.setId("123");
                RedisUtils.get(anyString);
                result = userObject;
            }
        };

        new Expectations() {
            {
                RedisUtils.getExpire(anyString);
                result = 100L;
            }
        };

        userExtraService.modifyUserPhoneIdAndAppKey(user);
    }

    @Test
    public void queryAllUserInfo() throws Exception {

        new Expectations() {
            {
                List<User> userList = new ArrayList<>();
                User user = new User();
                user.setPushId("123");
                userDao.queryAllUser();
                userList.add(user);
                result = userList;
            }
        };

        userExtraService.queryAllUserInfo();
    }

    @Test
    public void queryUserByPermission() throws Exception {

        QueryCondition<UserParameter> queryCondition = new QueryCondition<>();

        UserParameter userParameter = new UserParameter();
        userParameter.setLoginIp("127.0.0.1");
        userParameter.setPermissionId("123");
        queryCondition.setBizCondition(userParameter);

        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(1);
        pageCondition.setPageNum(1);
        queryCondition.setPageCondition(pageCondition);

        new Expectations() {
            {
                List<String> roleIdList = new ArrayList<>();
                roleIdList.add("123");
                roleDao.queryRoleByPermission(anyString);
                result = roleIdList;
            }
        };

        new Expectations() {
            {
                List<User> userList = new ArrayList<>();
                User user = new User();
                userList.add(user);
                userDao.queryUserByPermission(userParameter);
                result = userList;
            }
        };

        new Expectations() {
            {
                userDao.queryUserNumberByPermission(userParameter);
                result = 10;
            }
        };

        userExtraService.queryUserByPermission(queryCondition);
    }

    @Test
    public void queryUserInfoByDeptAndDeviceType() throws Exception {

        DataPermission dataPermission = new DataPermission();
        new Expectations() {
            {
                List<User> userList = new ArrayList<>();
                userDao.queryUserInfoByDeptAndDeviceType(dataPermission);
                result = userList;
            }
        };

        userExtraService.queryUserInfoByDeptAndDeviceType(dataPermission);
    }

    @Test
    public void queryOnlieUserId() throws Exception {

        new Expectations() {
            {
                Set<String> keyToken = new HashSet<>();
                RedisUtils.keys(anyString);
                result = keyToken;
            }
        };

        userExtraService.queryOnlieUserId();

        new Expectations() {
            {
                Set<String> keyToken = new HashSet<>();
                keyToken.add("123");
                RedisUtils.keys(anyString);
                result = keyToken;
            }
        };

        userExtraService.queryOnlieUserId();
    }

}