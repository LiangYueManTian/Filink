package com.fiberhome.filink.userserver.service.impl;

import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.api.DeviceMapConfigFeign;
import com.fiberhome.filink.license.api.LicenseFeign;
import com.fiberhome.filink.license.bean.License;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.security.api.SecurityFeign;
import com.fiberhome.filink.security.bean.AccountSecurityStrategy;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userserver.bean.*;
import com.fiberhome.filink.userserver.dao.DepartmentDao;
import com.fiberhome.filink.userserver.dao.OnlineUserDao;
import com.fiberhome.filink.userserver.dao.RoleDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.exception.FilinkUserException;
import com.fiberhome.filink.userserver.service.UserStream;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RunWith(JMockit.class)
public class UserServiceTest {

    @Tested
    private UserServiceImpl userService;

    @Injectable
    private UserDao userDao;

    @Injectable
    private DeviceMapConfigFeign deviceMapConfigFeign;

    @Injectable
    private LogProcess logProcess;

    @Injectable
    private UserStream userStream;

    @Injectable
    private OnlineUserDao onlineUserDao;

    @Injectable
    private AreaFeign areaFeign;

    @Injectable
    private SecurityFeign securityFeign;

    @Injectable
    private LicenseFeign licenseFeign;

    @Injectable
    private ProcBaseFeign procBaseFeign;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    @Mocked
    private I18nUtils i18nUtils;

    @Injectable
    private DepartmentDao departmentDao;

    @Injectable
    private RoleDao roleDao;


    @Mocked
    private RedisUtils redisUtils;

    @Injectable
    private PasswordEncoder passwordEncoder;

    @Injectable
    private HttpServletRequest request;

    @Injectable
    private AlarmCurrentFeign alarmCurrentFeign;


    @Test
    public void queryUserInfoByIdTest() {

        String id = "123";
        new Expectations() {
            {
                userDao.queryUserInfoById((String) any);
                User user = new User();
                user.setId("1");
                user.setUserName("123");
                result = user;
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result success(Object data) {
                Result result = new Result();
                result.setMsg("请求成功");
                return result;
            }
        };

        Result result = userService.queryUserInfoById(id);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryUserInfoByIdTestOne() {

        String id = "123";
        new Expectations() {
            {
                userDao.queryUserInfoById((String) any);
                result = null;
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(Object data) {
                Result result = new Result();
                result.setMsg("请求成功");
                return result;
            }
        };

        userService.queryUserInfoById(id);
    }

    @Test
    public void addUser() {
        User user = new User();
        user.setId("123");
        user.setUserName("123");
        user.setUserCode("123");

        License license = new License();

        List<User> userList = new ArrayList<>();

        new MockUp<ResultUtils>() {
            @Mock
            Result warn(Object data) {
                Result result = new Result();
                result.setMsg("请求成功");
                return result;
            }
        };

        new MockUp<ResultUtils>() {
            @Mock
            Result success(Object data) {
                Result result = new Result();
                result.setMsg("请求成功");
                return result;
            }
        };

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };

        new Expectations() {
            {
                licenseFeign.getCurrentLicense();
                result = license;
            }
        };

        new Expectations() {
            {
                userDao.queryAllUser();
                result = userList;
            }
        };

        user.setEmail("12342353454564575678234523452352352435234534645745773466345634");
        try {
            userService.addUser(user);
        } catch (Exception e) {
        }
        new Expectations(){
            {
                RedisUtils.lockWithTimeout(anyString, anyInt, anyInt);
                result = "test";
            }
        };
        userService.addUser(user);
        user.setEmail("1231524523235");
        user.setCountValidityTime("1234d");
        userService.addUser(user);

        user.setCountValidityTime("123d");
        new Expectations() {
            {
                User codeUser = new User();
                userDao.queryUserByUserCode(user);
                result = codeUser;
            }
        };
        userService.addUser(user);

        new Expectations() {
            {
                userDao.queryUserByUserCode(user);
                result = null;
            }
        };
        userService.addUser(user);

        license.maxUserNum = "0";
        userService.addUser(user);

        license.maxUserNum = "123";
        userService.addUser(user);

        new Expectations() {
            {
                deviceMapConfigFeign.insertConfigBatch((String) any);
                result = false;
            }
        };
        userService.addUser(user);

        new Expectations() {
            {
                deviceMapConfigFeign.insertConfigBatch((String) any);
                result = true;
            }
        };

        new Expectations() {
            {
                userDao.insert(user);
                result = 0;
            }
        };
        try {
            userService.addUser(user);
        } catch (FilinkUserException e) {
            Assert.assertTrue(true);
        }

        new Expectations() {
            {
                userDao.insert(user);
                result = 1;
            }
        };
        userService.addUser(user);
    }


    @Test
    public void updateUser() {

        User user = new User();
        userService.updateUser(user);

        user.setId("1");
        user.setEmail("12323423523452435123333333312313423532453345345346354653674574534635754746346");
        userService.updateUser(user);

        user.setEmail("123234");
        user.setCountValidityTime("1242d");
        userService.updateUser(user);

        user.setCountValidityTime("12d");
        user.setUserName("123");
        user.setRoleId("123");

        new MockUp<ResultUtils>() {
            @Mock
            Result warn(Object data) {
                Result result = new Result();
                result.setMsg("请求成功");
                return result;
            }
        };

        new MockUp<ResultUtils>() {
            @Mock
            Result success(Object data) {
                Result result = new Result();
                result.setMsg("请求成功");
                return result;
            }
        };

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "12";
            }
        };

        new Expectations() {
            {
                userDao.queryUserInfoById((String) any);
                result = null;
            }
        };
        userService.updateUser(user);

        new Expectations() {
            {
                userDao.queryUserInfoById((String) any);
                result = user;
            }
        };
        userService.updateUser(user);


        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };

        new Expectations() {
            {
                User updateUser = new User();
                updateUser.setId("123");
                updateUser.setRoleId("123456");
                userDao.queryUserInfoById((String) any);
                result = updateUser;
            }
        };
        userService.updateUser(user);

        user.setId("123");
        new Expectations() {
            {
                userDao.queryUserInfoById((String) any);
                result = user;
            }
        };
        new Expectations() {
            {
                userDao.updateById(user);
                result = 1;
            }
        };
//        new Expectations() {
//            {
//                userDao.updateUserValidityTime(user);
//                result = 0;
//            }
//        };
        try {
            userService.updateUser(user);
        } catch (FilinkUserException e) {
        }


        new Expectations() {
            {
                userDao.queryUserDetailById((String) any);
                result = user;
            }
        };
        new Expectations() {
            {
                RedisUtils.hasKey(anyString);
                result = false;
            }
        };
        userService.updateUser(user);

        new Expectations() {
            {
                RedisUtils.hasKey(anyString);
                result = true;
            }
        };

        new Expectations() {
            {
                Set<String> keys = new HashSet<>();
                RedisUtils.keys(anyString);
                result = keys;
            }
        };
        userService.updateUser(user);

        new Expectations() {
            {
                Set<String> keys = new HashSet<>();
                keys.add("123");
                RedisUtils.keys(anyString);
                result = keys;
            }

            {
                RedisUtils.hasKey(anyString);
                result = true;
            }

//            {
//                RedisUtils.getExpire(anyString);
//                result = 100;
//            }
        };
        try {
            userService.updateUser(user);
        } catch (Exception e) {}
    }

    @Test
    public void deleteUser() {
        Parameters parameters = new Parameters();

        new MockUp<ResultUtils>() {
            @Mock
            Result success(Object data) {
                Result result = new Result();
                result.setMsg("请求成功");
                return result;
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(Object data) {
                Result result = new Result();
                result.setMsg("请求成功");
                return result;
            }
        };
        userService.deleteUser(parameters);

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };

        String[] userIds = {"1", "456"};
        parameters.setFirstArrayParameter(userIds);
        userService.deleteUser(parameters);

        String[] userIdArray = {"123", "456"};
        parameters.setFirstArrayParameter(userIdArray);
        User user = new User();
        user.setId("123");
        user.setUserName("123");
        user.setUserStatus("0");
        new Expectations() {
            {
                List<User> deleteList = new ArrayList<>();
                deleteList.add(user);
                userDao.batchQueryUserByIds(userIdArray);
                result = deleteList;
            }
        };
        userService.deleteUser(parameters);


        User user1 = new User();
        user1.setId("456");
        user1.setUserName("456");
        user1.setUserStatus("1");
        new Expectations() {
            {
                List<User> deleteList = new ArrayList<>();
                deleteList.add(user);
                deleteList.add(user1);
                userDao.batchQueryUserByIds(userIdArray);
                result = deleteList;
            }
        };
        userService.deleteUser(parameters);

        user1.setUserStatus("0");
        new Expectations() {
            {
                List<User> List = new ArrayList<>();
                List.add(user1);
                List.add(user);
                userDao.batchQueryUserByIds(userIdArray);
                result = List;
            }
        };
        new Expectations() {
            {
                List<String> idList = new ArrayList<>();
                procBaseFeign.queryIsExistsAssignUser(Arrays.asList(userIdArray));
                result = true;
            }
        };
        userService.deleteUser(parameters);

        new Expectations() {
            {
                List<String> idList = new ArrayList<>();
                procBaseFeign.queryIsExistsAssignUser(Arrays.asList(userIdArray));
                result = false;
            }
        };

        parameters.setFlag(false);
        new Expectations() {
            {
                Set<String> keys = new HashSet<>();
                keys.add("1");
                RedisUtils.keys(anyString);
                result = keys;
            }
        };
        userService.deleteUser(parameters);


        new Expectations() {
            {
                Set<String> keys = new HashSet<>();
                RedisUtils.keys(anyString);
                result = keys;
            }
        };
        new Expectations() {
            {
                userDao.deleteUser(userIdArray, "1");
                result = 1;
            }
        };
        try {
            userService.deleteUser(parameters);
        } catch (FilinkUserException e) {
            Assert.assertTrue(true);
        }

        parameters.setFlag(true);
        new Expectations() {
            {
                userDao.deleteUser(userIdArray, "1");
                result = 2;
            }
        };
        new Expectations() {
            {
                List<User> userList = new ArrayList<>();
                userList.add(user);
                userDao.queryAllUser();
                result = userList;
            }
        };
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
                Set<String> keys = new HashSet<>();
                keys.add("123");
                RedisUtils.keys(anyString);
                result = keys;
            }
        };
        userService.deleteUser(parameters);


        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "2";
            }
        };

        String[] idArray = {"1"};
        parameters.setFirstArrayParameter(idArray);
        User user2 = new User();
        user2.setId("1");
        user2.setUserStatus("0");
        new Expectations() {
            {
                List<User> userList = new ArrayList<>();
                userList.add(user2);
                userDao.batchQueryUserByIds(idArray);
                result = userList;
            }
        };

        userService.deleteUser(parameters);
    }


    @Test
    public void queryUserByNmae() {
        User user = new User();
        user.setId("123");
        user.setUserName("234");
        user.setLoginTime(12345L);
        user.setLoginIp("127.0.0.1");

        UserParameter userParameter = new UserParameter();

        new Expectations() {
            {
                userDao.queryUserByName(anyString);
                result = user;
            }
        };
        userService.queryUserByName(userParameter);

        userParameter.setToken("123");
        userParameter.setLoginIp("127.0.0.1");
        userParameter.setLoginSourse("1");
        user.setDeptId("123");

        new Expectations() {
            {
                List<String> idList = new ArrayList<>();
                idList.add("123");
                areaFeign.selectAreaIdsByDeptIds(idList);
                result = idList;
            }
        };
        Department department = new Department();
        department.setId("123");
        Role role = new Role();
        role.setId("123");
        user.setDepartment(department);
        user.setRole(role);

        new Expectations() {
            {
                Result dataResult = ResultUtils.success();
                AccountSecurityStrategy strategy = new AccountSecurityStrategy();
                strategy.setNoOperationTime(30);
                dataResult.setData(strategy);
                securityFeign.queryAccountSecurity();
                result = dataResult;
            }
        };
        new Expectations() {
            {
                RedisUtils.hasKey(anyString);
                result = true;
            }
        };
        userService.queryUserByName(userParameter);
    }

    @Test
    public void updateUserStatus() {

        String[] userIdArray = {};
        int userStatus = 1;
        userService.updateUserStatus(userStatus, userIdArray);

        String[] userIds = {"1"};
        userService.updateUserStatus(userStatus, userIds);

        String[] idArray = {"123"};
        new Expectations() {
            {
                List<User> userList = new ArrayList<>();
                userDao.batchQueryUserByIds(idArray);
                result = userList;
            }
        };
        userService.updateUserStatus(userStatus, idArray);

        new Expectations() {
            {
                List<User> userList = new ArrayList<>();
                User user = new User();
                user.setId("123");
                userList.add(user);
                userDao.batchQueryUserByIds(idArray);
                result = userList;
            }
        };
        new Expectations() {
            {
                userDao.updateUserStatus(userStatus, idArray);
                result = 0;
            }
        };
        try {
            userService.updateUserStatus(userStatus, idArray);
        } catch (FilinkUserException e) {
            Assert.assertTrue(true);
        }

        new Expectations() {
            {
                userDao.updateUserStatus(userStatus, idArray);
                result = 1;
            }
        };
        userService.updateUserStatus(userStatus, idArray);

    }


    @Test
    public void queryUserByField() {
        QueryCondition userQueryCondition = new QueryCondition();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(0);
        pageCondition.setPageSize(10);

        userQueryCondition.setPageCondition(pageCondition);
        List<FilterCondition> filterConditionList = new ArrayList<FilterCondition>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("id");
        filterCondition.setFilterValue("403600");
        filterCondition.setOperator("eq");
        filterConditionList.add(filterCondition);
        userQueryCondition.setFilterConditions(filterConditionList);

        List<User> users = new ArrayList<>();
        User user = new User();
        user.setId("1");
        user.setUserName("403600");
        users.add(user);

        userService.queryUserByField(userQueryCondition);
    }

    @Test
    public void modifyPWD() {

        PasswordDto passwordDto = new PasswordDto();
        userService.modifyPWD(passwordDto);

        passwordDto.setNewPWD("");
        userService.modifyPWD(passwordDto);

        passwordDto.setNewPWD("123");
        passwordDto.setOldPWD("");
        userService.modifyPWD(passwordDto);

        passwordDto.setNewPWD("123");
        passwordDto.setOldPWD("1");
        passwordDto.setConfirmPWD("");
        userService.modifyPWD(passwordDto);

        passwordDto.setNewPWD("123");
        passwordDto.setOldPWD("123");
        passwordDto.setConfirmPWD("12");
        userService.modifyPWD(passwordDto);

        passwordDto.setNewPWD("123");
        passwordDto.setOldPWD("1");
        passwordDto.setConfirmPWD("12");
        userService.modifyPWD(passwordDto);

        passwordDto.setNewPWD("123");
        passwordDto.setOldPWD("1");
        passwordDto.setConfirmPWD("123");
        new Expectations() {
            {
                User user = new User();
                user.setId("1");
                user.setPassword("test");
                userDao.queryUserById(anyString);
                result = user;
            }
        };
        userService.modifyPWD(passwordDto);
        new Expectations() {
            {
                userDao.queryUserById(anyString);
                result = null;
            }
        };
        try {
            userService.modifyPWD(passwordDto);
        } catch (Exception e) {
        }
//        new Expectations() {
//            {
//                User user = new User();
//                user.setId("1");
//                userDao.queryUserById(anyString);
//                result = user;
//            }
//        };
//        try {
//            userService.modifyPWD(passwordDto);
//        } catch (FilinkUserException e){
//            Assert.assertTrue(true);
//        }
//
//        userService.modifyPWD(passwordDto);
    }

}
