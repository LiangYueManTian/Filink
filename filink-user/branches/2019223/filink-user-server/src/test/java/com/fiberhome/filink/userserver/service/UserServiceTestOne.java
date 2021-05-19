package com.fiberhome.filink.userserver.service;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.device_api.api.DeviceMapConfigFeign;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userserver.bean.*;
import com.fiberhome.filink.userserver.dao.DepartmentDao;
import com.fiberhome.filink.userserver.dao.OnlineUserDao;
import com.fiberhome.filink.userserver.dao.RoleDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.service.impl.UserServiceImpl;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
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

    @Mocked
    private I18nUtils i18nUtils;

    @Injectable
    private DepartmentDao departmentDao;

    @Injectable
    private RoleDao roleDao;

    @Injectable
    private UserStream userStream;

    @Mocked
    private RedisUtils redisUtils;

    @Injectable
    private PasswordEncoder passwordEncoder;

    @Injectable
    private OnlineUserDao onlineUserDao;

    @Injectable
    private HttpServletRequest request;

    private List<User> list = new ArrayList<>();


    @Test
    public void modifyPWD0(){

        PasswordDto passwordDto = null;

        userService.modifyPWD(passwordDto);
    }




    @Test
    public void modifyPWDOne(){

        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setConfirmPWD("123");
        passwordDto.setOldPWD("456");
        passwordDto.setUserId("678");
        User user = new User();
        user.setId("678");
        user.setPassword("456");

        userService.modifyPWD(passwordDto);
    }

    @Test
    public void modifyPWDTwo(){

        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setConfirmPWD("123");
        passwordDto.setNewPWD("123");
        passwordDto.setUserId("678");
        User user = new User();
        user.setId("678");
        user.setPassword("456");

        userService.modifyPWD(passwordDto);
    }


    @Test
    public void modifyPWDThree(){

        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setNewPWD("123");
        passwordDto.setOldPWD("456");
        passwordDto.setUserId("678");
        User user = new User();
        user.setId("678");
        user.setPassword("456");

        userService.modifyPWD(passwordDto);
    }

    @Test
    public void modifyPWDFour(){

        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setConfirmPWD("123");
        passwordDto.setNewPWD("456");
        passwordDto.setOldPWD("456");
        passwordDto.setUserId("678");
        User user = new User();
        user.setId("678");
        user.setPassword("456");

        userService.modifyPWD(passwordDto);
    }


    @Test
    public void modifyPWDFive(){

        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setConfirmPWD("123");
        passwordDto.setNewPWD("123");
        passwordDto.setOldPWD("456");
        passwordDto.setUserId("678");
        User user = new User();
        user.setId("678");
        user.setPassword("456");

        new Expectations(){
            {
                userDao.queryUserById(passwordDto.getUserId());
                result = null;
            }
        };

        userService.modifyPWD(passwordDto);
    }


    @Test
    public void resetPWD(){

        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setConfirmPWD("123");
        passwordDto.setNewPWD("123");
        passwordDto.setUserId("678");
        User user = new User();
        user.setId("678");

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
        QueryCondition<OnlineParameter> userParameterQueryCondition = new QueryCondition<>();

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
        userParameterQueryCondition.setBizCondition(onlineParameter);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(5);
        pageCondition.setPageNum(1);
        userParameterQueryCondition.setPageCondition(pageCondition);

        OnlineUser onlineUser = new OnlineUser();

        List<String> tokenList = new ArrayList<>();
        tokenList.add("123");

        List<OnlineUser> loginList = new ArrayList<>();
        loginList.add(onlineUser);
        String[] ids = {"123"};

        Set<String> keySet = new HashSet<>();
        keySet.add("123");

        new Expectations(){
            {
                userDao.queryAllUser();
                result = userList;
            }
        };

        new Expectations(){
            {
                RedisUtils.keys("123*");
                result = keySet;
            }
        };

        new Expectations(){
            {
                userService.userToOnlineUser("123",user,onlineUser);
                result = null;
            }
        };

        new Expectations(){
            {
                onlineUserDao.queryAllOnlineUser();
                result = tokenList;
            }
        };


        new Expectations(){
            {
                onlineUserDao.queryOnlineUserList(onlineParameter);
                result = loginList;
            }
        };

        new Expectations(){
            {
                onlineUserDao.queryOnlineUserNumber(onlineParameter);
                result = 1;
            }
        };

        userService.getOnLineUser(userParameterQueryCondition);
    }



    @Test
    public void getOnLineUserZero(){
        QueryCondition<OnlineParameter> userParameterQueryCondition = new QueryCondition<>();

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

        OnlineParameter onlineParameter = new OnlineParameter();
        userParameterQueryCondition.setBizCondition(onlineParameter);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(5);
        pageCondition.setPageNum(1);
        userParameterQueryCondition.setPageCondition(pageCondition);

        OnlineUser onlineUser = new OnlineUser();

        List<String> tokenList = new ArrayList<>();
        tokenList.add("123");

        List<OnlineUser> loginList = new ArrayList<>();
        loginList.add(onlineUser);
        String[] ids = {"123"};

        Set<String> keySet = new HashSet<>();
        keySet.add("123");

        new Expectations(){
            {
                userDao.queryAllUser();
                result = userList;
            }
        };

        userService.getOnLineUser(userParameterQueryCondition);
    }


    @Test
    public void getOnLineUserOne(){
        QueryCondition<OnlineParameter> userParameterQueryCondition = new QueryCondition<>();

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
        userParameterQueryCondition.setBizCondition(onlineParameter);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(5);
        pageCondition.setPageNum(1);
        userParameterQueryCondition.setPageCondition(pageCondition);

        OnlineUser onlineUser = new OnlineUser();

        List<String> tokenList = new ArrayList<>();
        tokenList.add("123456");

        List<OnlineUser> loginList = new ArrayList<>();
        loginList.add(onlineUser);
        String[] ids = {"123"};

        Set<String> keySet = new HashSet<>();
        keySet.add("123");

        new Expectations(){
            {
                userDao.queryAllUser();
                result = userList;
            }
        };

        new Expectations(){
            {
                RedisUtils.keys("123*");
                result = keySet;
            }
        };

//        new Expectations(){
//            {
//                RedisUtils.get((String) any);
//
//                result = (Object) user;
//            }
//        };

        new Expectations(){
            {
                userService.userToOnlineUser("123",user,onlineUser);
                result = null;
            }
        };

        new Expectations(){
            {
                onlineUserDao.queryAllOnlineUser();
                result = tokenList;
            }
        };


        new Expectations(){
            {
                onlineUserDao.queryOnlineUserList(onlineParameter);
                result = loginList;
            }
        };

        new Expectations(){
            {
                onlineUserDao.queryOnlineUserNumber(onlineParameter);
                result = 1;
            }
        };

        userService.getOnLineUser(userParameterQueryCondition);
    }

    @Test
    public void queryCurrentUser(){

        new Expectations(){
            {
                RedisUtils.hasKey("123_123");
                result = true;
            }
        };

        new Expectations(){
            {
                User user = new User();
                user.setId("123");
                user.setUserName("456");
                RedisUtils.get("123_123");
                result = user;
            }
        };
        userService.queryCurrentUser("123","123");
    }


    @Test
    public void queryCurrentUserOne(){

        new Expectations(){
            {
                RedisUtils.hasKey((String) any);
                result = false;
            }
        };

        userService.queryCurrentUser("123","123");
    }


    @Test
    public void forceOffline(){
        Map<String, String> idMap = new HashMap<>();
        idMap.put("123_456","456");
        Collection<String> values = idMap.values();

        User user = new User();
        user.setId("123");

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "12";
            }
        };

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getToken() {
                return "45";
            }
        };


        new Expectations(){
            {
                redisUtils.get("123_456");
                result = user;
            }
        };

        userService.forceOffline(idMap);
    }


    @Test
    public void forceOfflineOne(){
        Map<String, String> idMap = new HashMap<>();
        idMap.put("123_456","456");
        Collection<String> values = idMap.values();

        User user = new User();
        user.setId("123");

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "123";
            }
        };

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getToken() {
                return "456";
            }
        };

        userService.forceOffline(idMap);
    }



    @Test
    public void logout(){
        new Expectations(){
            {
                redisUtils.remove("123_456");
                result = null;
            }
        };
        userService.logout("123","456");
    }

    @Test
    public void queryUserByFieldAndCondition(){

        QueryCondition<UserParameter> userParameterQueryCondition = new QueryCondition<>();
        UserParameter userParameter = new UserParameter();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(5);
        pageCondition.setPageNum(1);
        userParameter.setPage(1);
        userParameter.setPageSize(5);
        userParameterQueryCondition.setBizCondition(userParameter);
        userParameterQueryCondition.setPageCondition(pageCondition);

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
        parameters.setFirstArrayParamter(ids);
        User user = new User();
        List<User> userList = new ArrayList<>();
        user.setId("123");
        userList.add(user);

        new Expectations(){
            {
                userDao.queryUserByDepts(parameters.getFirstArrayParamter());
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
                result = user;
            }
        };

        userService.queryUserById(userId);
    }

    @Test
    public void queryUserByIdOne(){
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
    }

    @Test
    public void updateLoginTime(){

        new Expectations(){
            {
                RedisUtils.hasKey("123_456");
                result = true;
            }
        };
        userService.updateLoginTime("123","456");
    }

    @Test
    public void updateLoginTimeOne(){

        new Expectations(){
            {
                RedisUtils.hasKey("123_456");
                result = false;
            }
        };
        userService.updateLoginTime("123","456");
    }
}
