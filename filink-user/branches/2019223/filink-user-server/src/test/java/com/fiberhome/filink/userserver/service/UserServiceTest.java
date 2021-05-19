package com.fiberhome.filink.userserver.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.device_api.api.DeviceMapConfigFeign;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.MpQueryHelper;
import com.fiberhome.filink.userserver.bean.*;
import com.fiberhome.filink.userserver.dao.DepartmentDao;
import com.fiberhome.filink.userserver.dao.OnlineUserDao;
import com.fiberhome.filink.userserver.dao.RoleDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.exception.FilinkUserException;
import com.fiberhome.filink.userserver.service.impl.UserServiceImpl;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

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
    private OnlineUserDao onlineUserDao;

    @Injectable
    private HttpServletRequest request;

    private List<User> list = new ArrayList<>();


    @Test
    public void queryUserInfoByIdTest(){

        String id = "123";
        new Expectations(){
            {
                userDao.queryUserInfoById(id);
                User user = new User();
                user.setId("1");
                user.setUserName("123");
                result = user;
            }
        };
        /*new MockUp<ResultUtils>() {
          @Mock
          Result success(Object data) {
              Result result = new Result();
              result.setMsg("请求成功");
              return result;
          }
        };*/

        Result result = userService.queryUserInfoById(id);
    }

    @Test
    public void queryUserInfoByIdTestOne(){

        String id = "123";
        new Expectations(){
            {
                userDao.queryUserInfoById((String) any);
                result = null;
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
    }

    @Test
    public void addUser(){
        User user = new User();
        user.setUserName("123");
        user.setUserCode("123");


        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };

        new Expectations(){
            {
                userDao.queryUserByUserCode(user);
                result = null;
            }
        };

        new Expectations(){
            {
                deviceMapConfigFeign.insertConfigBatch((String) any);
                result = true;
            }
        };

        new Expectations(){
            {
                userDao.insert(user);
                result = 1;
            }
        };
        userService.addUser(user);
    }


    @Test
    public void updateUser(){

        User user = new User();
        user.setId("1243");
        user.setUserName("123");

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };

        new Expectations(){
            {
                userDao.updateById(user);
                result = 1;
            }
        };

        new Expectations(){
            {
                userDao.updateUserValidityTime(user);
                result = 1;
            }
        };

        userService.updateUser(user);
    }
    
    @Test
    public void deleteUser(){
        Parameters parameters = new Parameters();
        parameters.setFlag(true);
        String[] userIds = {"123","456"};
        parameters.setFirstArrayParamter(userIds);

        List<Object> userList = new ArrayList<>();
        Set<User> userSet = new HashSet<>();
        Set<String> userIdSet = new HashSet<>();

        User user = new User();
        user.setId("123");
        user.setUserName("123");
        User user1 = new User();
        user1.setId("456");
        user1.setUserName("456");
        userList.add(user);
        userList.add(user1);
        userSet.add(user);
        userSet.add(user1);
        userIdSet.add("123");
        userIdSet.add("456");
        Map<Object, Object> userMap = new HashMap<>();
        Map<Object, Object> userMap1 = new HashMap<>();
        userMap.put("123",user);
        userMap1.put("456",user1);

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };
        new Expectations(){
            {
                userDao.selectBatchIds(Arrays.asList(userIds));
                result = userList;
            }
        };

        new MockUp<Map>() {
            @Mock
            Map<Object, Object> hGetMap(String key) {

                return userMap;
            }
        };

        new Expectations(){
            {
                userDao.deleteUser(userIds,"1");
                result = 2;
            }
        };



        Result result = userService.deleteUser(parameters);
    }



    @Test
    public void deleteUserZero(){
        Parameters parameters = new Parameters();
        parameters.setFlag(true);
        String[] userIds = {"123","456"};
        parameters.setFirstArrayParamter(userIds);

        List<Object> userList = new ArrayList<>();
        Set<User> userSet = new HashSet<>();
        Set<String> userIdSet = new HashSet<>();

        User user = new User();
        user.setId("123");
        user.setUserName("123");
        User user1 = new User();
        user1.setId("456");
        user1.setUserName("456");
        userList.add(user);
        userList.add(user1);
        userSet.add(user);
        userSet.add(user1);
        userIdSet.add("123");
        userIdSet.add("456");
        Map<Object, Object> userMap = new HashMap<>();
        Map<Object, Object> userMap1 = new HashMap<>();
        userMap.put("123",user);
        userMap1.put("456",user1);

        new Expectations(){
            {
                RedisUtils.keys("123*");
                Set<String> keySet = new HashSet<>();
                keySet.add("123");
                result = keySet;
            }
        };


        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };
        new Expectations(){
            {
                userDao.selectBatchIds(Arrays.asList(userIds));
                result = userList;
            }
        };

        new MockUp<Map>() {
            @Mock
            Map<Object, Object> hGetMap(String key) {

                return userMap;
            }
        };

        new Expectations(){
            {
                userDao.deleteUser(userIds,"1");
                result = 2;
            }
        };




        Result result = userService.deleteUser(parameters);
    }




    @Test
    public void deleteUserOne(){
        Parameters parameters = new Parameters();

        Result result = userService.deleteUser(parameters);
    }


    @Test
    public void deleteUserTwo(){
        Parameters parameters = new Parameters();
        parameters.setFlag(true);
        String[] userIds = {"123","456"};
        parameters.setFirstArrayParamter(userIds);

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "123";
            }
        };

        Result result = userService.deleteUser(parameters);
    }


    @Test
    public void deleteUserThree(){
        Parameters parameters = new Parameters();
        parameters.setFlag(false);
        String[] userIds = {"123","456"};
        parameters.setFirstArrayParamter(userIds);

        List<Object> userList = new ArrayList<>();
        Set<User> userSet = new HashSet<>();
        Set<String> userIdSet = new HashSet<>();

        User user = new User();
        user.setId("123");
        user.setUserName("123");
        User user1 = new User();
        user1.setId("456");
        user1.setUserName("456");
        userList.add(user);
        userList.add(user1);
        userSet.add(user);
        userSet.add(user1);
        userIdSet.add("123");
        userIdSet.add("456");
        Map<Object, Object> userMap = new HashMap<>();
        Map<Object, Object> userMap1 = new HashMap<>();
        userMap.put("123",user);
        userMap1.put("456",user1);

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };
        new Expectations(){
            {
                userDao.selectBatchIds(Arrays.asList(userIds));
                result = userList;
            }
        };

        new MockUp<Map>() {
            @Mock
            Map<Object, Object> hGetMap(String key) {

                return userMap;
            }
        };

        new Expectations(){
            {
                userDao.deleteUser(userIds,"1");
                result = 2;
            }
        };



        Result result = userService.deleteUser(parameters);
    }


    @Test
    public void queryUserByNmae(){
        User user = new User();
        user.setId("123");
        user.setUserName("234");
        new Expectations(){
            {
                userDao.queryUserByNmae("123");
                result = user;
            }
        };
        user.setLoginTime(System.currentTimeMillis());
        new Expectations(){
            {
                userDao.updateById(user);
                result = null;
            }
        };
        userService.queryUserByNmae("123", "456","123");
    }

    @Test
    public void updateUserStatus(){
        try {
            int status = 1;
            String[] userIds = {"123","456"};
            new Expectations(){
                {
                    userDao.updateUserStatus(status, userIds);
                    result = 2;
                }
            };

            Result result = userService.updateUserStatus(status, userIds);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FilinkUserException);
        }
    }


    @Test
    public void updateUserStatusOne(){
        try {
            int status = 1;
            String[] userIds = {"123","456"};
            new Expectations(){
                {
                    userDao.updateUserStatus(status, userIds);
                    result = 3;
                }
            };

            new MockUp<I18nUtils>() {
                @Mock
                String getString(String key) {
                    return "更新失败";
                }
            };

            Result result = userService.updateUserStatus(status, userIds);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FilinkUserException);
        }
    }



    @Test
    public void queryUserByField(){
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

        Page page = MpQueryHelper.MyBatiesBuildPage(userQueryCondition);
        EntityWrapper entityWrapper = MpQueryHelper.MyBatiesBuildQuery(userQueryCondition);

        List<User> users = new ArrayList<>();
        User user = new User();
        user.setId("1");
        user.setUserName("403600");
        users.add(user);

        userService.queryUserByField(userQueryCondition);
    }

    @Test
    public void modifyPWD(){

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
                result = user;
            }
        };

        new Expectations(){
            {
                userDao.queryUserById(passwordDto.getUserId());
                result = null;
            }
        };

        new Expectations(){
            {
                userDao.queryUserById(passwordDto.getUserId());
                User user1 = new User();
                user.setId("123234");
                result = user;
            }
        };

        new Expectations(){
            {
                userDao.modifyPWD(passwordDto);
                result = 1;
            }
        };


        userService.modifyPWD(passwordDto);
    }



}
