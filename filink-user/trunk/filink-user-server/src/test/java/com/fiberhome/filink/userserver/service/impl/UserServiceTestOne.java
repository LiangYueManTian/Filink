package com.fiberhome.filink.userserver.service.impl;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.api.DeviceMapConfigFeign;
import com.fiberhome.filink.deviceapi.bean.AreaDeptInfo;
import com.fiberhome.filink.license.api.LicenseFeign;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.security.api.SecurityFeign;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userserver.bean.*;
import com.fiberhome.filink.userserver.constant.UserConstant;
import com.fiberhome.filink.userserver.dao.OnlineUserDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.exception.FilinkUserException;
import com.fiberhome.filink.userserver.service.UserStream;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

@RunWith(JMockit.class)
public class UserServiceTestOne {

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

    @Mocked
    private RedisUtils redisUtils;


    @Test
    public void resetPWD(){

        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setUserId("1");
        new Expectations(){
            {
                User user = new User();
                user.setId("123");
                userDao.selectById(anyString);
                result = user;
            }
        };

        new Expectations(){
            {
                userDao.modifyPWD(passwordDto);
                result = 0;
            }
        };
        try {
            userService.resetPWD(passwordDto);
        } catch (FilinkUserException e) {
            Assert.assertTrue(true);
        }

        new Expectations(){
            {
                userDao.modifyPWD(passwordDto);
                result = 1;
            }
        };
        userService.resetPWD(passwordDto);
    }

    @Test
    public void getOnLineUser(){
        QueryCondition<OnlineParameter> onlineParameterQueryCondition = new QueryCondition<>();

        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(1);
        onlineParameterQueryCondition.setPageCondition(pageCondition);
        Set<Object> userSet  = new HashSet<>();
        userSet.add("123");

        User user = new User();
        user.setId("123");
        user.setUserName("123");


        Department department = new Department();
        department.setDeptName("123");
        user.setDepartment(department);
        user.setAddress("123");


        Role role = new Role();
        role.setRoleName("123");
        user.setRole(role);
        List<User> userList = new ArrayList<>();
        userList.add(user);

        OnlineParameter onlineParameter = new OnlineParameter();
        OnlineUser onlineUser = new OnlineUser();
        onlineParameterQueryCondition.setBizCondition(onlineParameter);

        List<String> tokenList = new ArrayList<>();
        tokenList.add("123");

        List<OnlineUser> loginList = new ArrayList<>();
        loginList.add(onlineUser);

        Set<String> keySet = new HashSet<>();
        keySet.add("123");

        new Expectations(){
            {
                onlineUserDao.queryAllOnlineUser();
                result = tokenList;
            }
        };

        new Expectations(){
            {
                RedisUtils.keys(anyString);
                result = keySet;
            }
        };

        new Expectations(){
            {
                User user = new User();
                RedisUtils.get(anyString);
                result = user;
            }
        };

        new Expectations(){
            {
                userService.userToOnlineUser("123",user,onlineUser);
                result = null;
            }
        };
        try {
            userService.getOnLineUser(onlineParameterQueryCondition);
        } catch (Exception e) {

        }
    }



    @Test
    public void queryCurrentUser(){
        String userId = "";
        String token = "";
        userService.queryCurrentUser(userId,token);

        userId = "123";
        token = "123";

        new Expectations(){
            {
                RedisUtils.hasKey(anyString);
                result = false;
            }
        };
        userService.queryCurrentUser(userId,token);

        new Expectations(){
            {
                RedisUtils.hasKey(anyString);
                result = true;
            }
        };
        new Expectations(){
            {
                User user = new User();
                user.setId("123");
                RedisUtils.get(anyString);
                result = user;
            }
            {
                RedisUtils.hasKey(anyString);
                result = true;
            }
            {
                User user = new User();
                Department department = new Department();
                department.setId("123");
                Role role = new Role();
                role.setId("123");
                user.setDepartment(department);
                user.setRole(role);
                RedisUtils.get(anyString);
                result = user;
            }
        };

        userService.queryCurrentUser(userId,token);
    }


    @Test
    public void forceOffline(){
        Map<String, String> idMap = new HashMap<>();
        idMap.put(UserConstant.USER_PREFIX + "1||2","456");

        User user = new User();
        user.setId("123");

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getToken() {
                return "2";
            }
        };
        userService.forceOffline(idMap);

        idMap.remove(UserConstant.USER_PREFIX + "1||2");
        idMap.put(UserConstant.USER_PREFIX + "12||23","456");



        new Expectations(){
            {
                User user = new User();
                user.setId("1");
                redisUtils.get(anyString);
                result = user;
            }
        };
        userService.forceOffline(idMap);
    }



    @Test
    public void logout(){
        new Expectations(){
            {
                RedisUtils.remove(UserConstant.USER_PREFIX + "123redis_split456");
                result = null;
            }
        };
        userService.logout("123","456");
    }

    @Test
    public void queryUserByFieldAndCondition(){
        QueryCondition<UserParameter> userParameterQueryCondition = new QueryCondition<>();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(1);
        pageCondition.setPageNum(1);
        userParameterQueryCondition.setPageCondition(pageCondition);

        UserParameter userParameter = new UserParameter();
        userParameter.setPage(1);
        userParameter.setPageSize(5);
        userParameterQueryCondition.setBizCondition(userParameter);

        List<User> userList = new ArrayList<>();
        User user = new User();
        user.setId("123");
        userList.add(user);

        new Expectations(){
            {
                userDao.queryUserByField(userParameter);
                result = userList;
            }
        };

        new Expectations(){
            {
                userDao.queryUserNum(userParameter);
                result = 1;
            }
        };

        userService.queryUserByFieldAndCondition(userParameterQueryCondition);
    }

    @Test
    public void queryUserDefaultPWD(){
        userService.queryUserDefaultPWD();
    }

    @Test
    public void queryUserByDept(){
        Parameters parameters = new Parameters();
        String[] ids = {"123"};
        parameters.setFirstArrayParameter(ids);
        User user = new User();
        List<User> userList = new ArrayList<>();
        user.setId("123");
        userList.add(user);

        new Expectations(){
            {
                userDao.queryUserByDepts(parameters.getFirstArrayParameter());
                result = userList;
            }
        };

        userService.queryUserByDept(parameters);
    }

    @Test
    public void queryUserById(){
        String userId = "123";
        User user = new User();
        user.setId("123");

        new Expectations(){
            {
                userDao.queryUserById(userId);
                result = null;
            }
        };
        userService.queryUserById(userId);

        new Expectations(){
            {
                userDao.queryUserById(userId);
                result = user;
            }
        };
        userService.queryUserById(userId);
    }



    @Test
    public void updateLoginTime(){

        String userId = "1";
        String token = "2";
        new Expectations(){
            {
                RedisUtils.hasKey(anyString);
                result = false;
            }
        };
        userService.updateLoginTime(userId,token);

        new Expectations(){
            {
                RedisUtils.hasKey(anyString);
                result = true;
            }
        };
        new Expectations(){
            {
                RedisUtils.get(anyString);
                result = 1800;
            }
        };
        userService.updateLoginTime(userId,token);
    }

    @Test
    public void queryUserByIdList(){

        List<String> idList = new ArrayList<>();
        idList.add("1");
        new Expectations(){
            {
                List<User> userList = new ArrayList<>();
                User user = new User();
                user.setId("1");
                user.setDeptId("2");
                Department department = new Department();
                department.setId("2");
                user.setDepartment(department);
                userList.add(user);
                userDao.queryUserByIdList(idList);
                result = userList;
            }
        };
        List<String> deptList = new ArrayList<>();
        deptList.add("2");
        new Expectations(){
            {
                List<AreaDeptInfo> areaList = new ArrayList<>();
                AreaDeptInfo areaDeptInfo = new AreaDeptInfo();
                areaDeptInfo.setAreaId("213");
                areaDeptInfo.setDeptId("2");
                areaList.add(areaDeptInfo);
                areaFeign.selectAreaDeptInfoByDeptIds(deptList);
                result = areaList;
            }
        };

        userService.queryUserByIdList(idList);
    }
}
