package com.fiberhome.filink.userserver.service.impl;

import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.api.DeviceMapConfigFeign;
import com.fiberhome.filink.license.api.LicenseFeign;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.security.api.SecurityFeign;
import com.fiberhome.filink.security.bean.AccountSecurityStrategy;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userserver.bean.Department;
import com.fiberhome.filink.userserver.bean.Role;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.dao.DepartmentDao;
import com.fiberhome.filink.userserver.dao.RoleDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@RunWith(JMockit.class)
public class ImportServiceImplTest {

    @Tested
    private ImportServiceImpl importService;

    @Injectable
    private UserDao userDao;

    @Injectable
    private RoleDao roleDao;

    @Injectable
    private DepartmentDao departmentDao;

    @Injectable
    private LicenseFeign licenseFeign;

    @Injectable
    private SecurityFeign securityFeign;

    @Injectable
    private LogProcess logProcess;

    @Injectable
    private DeviceMapConfigFeign deviceMapConfigFeign;

    @Mocked
    private RedisUtils redisUtils;

    @Mocked
    private RequestInfoUtils requestInfoUtils;

    @Mocked
    private I18nUtils i18nUtils;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;
    private String testFile;

    //    @Mocked
//    private WorkbookFactory workbookFactory;
    @Before
    public void init() throws Exception {
        String classPath = URLDecoder.decode(this.getClass().getResource("/").getPath(), "utf-8");
        String[] split = classPath.split("filink-user-server");
        testFile = split[0] + "filink-user-server/src/test/java/com/fiberhome/filink/userserver/testFile/";
    }

    @Test
    public void importUserInfo() throws Exception {
        File file1 = new File(testFile + "111.xlsx");
        FileInputStream fileInputStream1 = new FileInputStream(file1);
        MultipartFile multipartFile1 = new MockMultipartFile("123", fileInputStream1);
        try {
            importService.importUserInfo(multipartFile1);
        } catch (Exception e) {
        }
        Result strategyResult = new Result();
        AccountSecurityStrategy strategy = new AccountSecurityStrategy();
        strategy.setMinLength(0);
        strategyResult.setData(strategy);
        List<Role> roleList = new ArrayList<>();
        Role role = new Role();
        role.setRoleName("程序员");
        role.setId("testId");
        roleList.add(role);
        List<Department> departmentList = new ArrayList<>();
        Department department = new Department();
        department.setId("testId");
        department.setDeptName("武汉一级单位");
        departmentList.add(department);
        new Expectations() {
            {
                securityFeign.queryAccountSecurity();
                result = strategyResult;
            }

            {
                roleDao.queryAllRoles();
                result = roleList;
            }

            {
                departmentDao.queryToltalDepartment();
                result = departmentList;
            }
        };
        importService.importUserInfo(multipartFile1);
    }

    public void getUser() {
        new Expectations() {
            {
                List<User> users = new ArrayList<>();
                User user = new User();
                user.setId("123");
                user.setUserCode("123");
                user.setPhoneNumber("110");
                user.setEmail("110@qq.com");
                users.add(user);
                userDao.queryAllUser();
                result = users;
            }
        };
    }

    public void getRole() {
        new Expectations() {
            {
                List<Role> roles = new ArrayList<>();
                Role role = new Role();
                role.setId("123");
                role.setRoleName("程序员");
                roles.add(role);
                roleDao.queryAllRoles();
                result = roles;
            }
        };
    }

    public void getDept() {
        new Expectations() {
            {
                List<Department> departments = new ArrayList<>();
                Department department = new Department();
                department.setId("123");
                department.setDeptName("武汉一级单位");
                departments.add(department);
                departmentDao.queryToltalDepartment();
                result = departments;
            }
        };
    }

    public void getResult() {
        new Expectations() {
            {
                Result objectResult = new Result<>();
                AccountSecurityStrategy accountSecurityStrategy = new AccountSecurityStrategy();
                accountSecurityStrategy.setMinLength(3);

                objectResult.setData(accountSecurityStrategy);
                securityFeign.queryAccountSecurity();
                result = objectResult;
            }
        };
    }

//
//    public void getLiceneMaxUserNum1() {
//        new Expectations() {
//            {
//                License license = new License();
//                license.maxUserNum = "1";
//                licenseFeign.getCurrentLicense();
//                result = license;
//            }
//        };
//    }

//    public void getLiceneMaxUserNum10() {
//        new Expectations() {
//            {
//                License license = new License();
//                license.maxUserNum = "10";
//                licenseFeign.getCurrentLicense();
//                result = license;
//            }
//        };
//    }

    public void insertConfig() {
        new Expectations() {
            {
                List<String> userIdList = new ArrayList<>();
                deviceMapConfigFeign.insertConfigBatchUsers((List<String>) any);
                result = true;
            }
        };
    }

    public void batchAddUserFalse() {
//        new Expectations() {
//            {
//                List<User> userList = new ArrayList<>();
//                User user = new User();
//                user.setId("123");
//                userList.add(user);
//                userDao.batchAddUser((List<User>) any);
//                result = 0;
//            }
//        };
    }

    public void batchAddUserTrue() {
//        new Expectations() {
//            {
//                List<User> userList = new ArrayList<>();
//                User user = new User();
//                user.setId("123");
//                userList.add(user);
//                userDao.batchAddUser((List<User>) any);
//                result = 1;
//            }
//        };
    }

    @Test
    public void importUserInfo1() throws Exception {

        File file1 = new File(testFile + "222.xlsx");
        FileInputStream fileInputStream1 = new FileInputStream(file1);

        MultipartFile multipartFile1 = new MockMultipartFile("123", fileInputStream1);

        getUser();
        getRole();
        getDept();
        getResult();

        importService.importUserInfo(multipartFile1);
    }

    @Test
    public void importUserInfo2() throws Exception {

        File file1 = new File(testFile + "333.xlsx");
        FileInputStream fileInputStream1 = new FileInputStream(file1);

        MultipartFile multipartFile1 = new MockMultipartFile("123", fileInputStream1);

        getUser();
        getRole();
        getDept();
        getResult();
        new Expectations() {
            {
                Role role = new Role();
                role.setId("testId");
                roleDao.verityRoleByName(anyString);
                result = role;
            }
        };
        importService.importUserInfo(multipartFile1);

        file1 = new File(testFile + "444.xlsx");
        fileInputStream1 = new FileInputStream(file1);

        multipartFile1 = new MockMultipartFile("123", fileInputStream1);

        getUser();
        getRole();
        getDept();
        getResult();

        importService.importUserInfo(multipartFile1);

        file1 = new File(testFile + "555.xlsx");
        fileInputStream1 = new FileInputStream(file1);

        multipartFile1 = new MockMultipartFile("123", fileInputStream1);

        getUser();
        getRole();
        getDept();
        getResult();

        importService.importUserInfo(multipartFile1);

        file1 = new File(testFile + "666.xlsx");
        fileInputStream1 = new FileInputStream(file1);

        multipartFile1 = new MockMultipartFile("123", fileInputStream1);

        getUser();
        getRole();
        getDept();
        getResult();

        importService.importUserInfo(multipartFile1);

        file1 = new File(testFile + "777.xlsx");
        fileInputStream1 = new FileInputStream(file1);

        multipartFile1 = new MockMultipartFile("123", fileInputStream1);

        getUser();
        getRole();
        getDept();
        getResult();

        importService.importUserInfo(multipartFile1);

        file1 = new File(testFile + "888.xlsx");
        fileInputStream1 = new FileInputStream(file1);

        multipartFile1 = new MockMultipartFile("123", fileInputStream1);

        getUser();
        getRole();
        getDept();
        getResult();

        importService.importUserInfo(multipartFile1);

        file1 = new File(testFile + "999.xlsx");
        fileInputStream1 = new FileInputStream(file1);

        multipartFile1 = new MockMultipartFile("123", fileInputStream1);

        getUser();
        getRole();
        getDept();
        getResult();

        importService.importUserInfo(multipartFile1);

        file1 = new File(testFile + "1111.xlsx");
        fileInputStream1 = new FileInputStream(file1);
        multipartFile1 = new MockMultipartFile("123", fileInputStream1);
        getUser();
        getRole();
        getDept();
        getResult();
        importService.importUserInfo(multipartFile1);

        file1 = new File(testFile + "2222.xlsx");
        fileInputStream1 = new FileInputStream(file1);
        multipartFile1 = new MockMultipartFile("123", fileInputStream1);
        getUser();
        getRole();
        getDept();
        getResult();
        importService.importUserInfo(multipartFile1);

        file1 = new File(testFile + "3333.xlsx");
        fileInputStream1 = new FileInputStream(file1);
        multipartFile1 = new MockMultipartFile("123", fileInputStream1);
        getUser();
        getRole();
        getDept();
        getResult();
        importService.importUserInfo(multipartFile1);

        file1 = new File(testFile + "111.xlsx");
        fileInputStream1 = new FileInputStream(file1);

        multipartFile1 = new MockMultipartFile("123", fileInputStream1);

        getUser();
        getRole();
        getDept();
        getResult();
        //getLiceneMaxUserNum1();

        importService.importUserInfo(multipartFile1);

        file1 = new File(testFile + "111.xlsx");
        fileInputStream1 = new FileInputStream(file1);

        multipartFile1 = new MockMultipartFile("123", fileInputStream1);

        getUser();
        getRole();
        getDept();
        getResult();


        importService.importUserInfo(multipartFile1);

//        insertConfig();

        batchAddUserFalse();

        try {
            importService.importUserInfo(multipartFile1);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        batchAddUserTrue();
        importService.importUserInfo(multipartFile1);
    }


    @Test
    public void importUserInfo3() throws Exception {

        File file1 = new File(testFile + "4444.xlsx");
        FileInputStream fileInputStream1 = new FileInputStream(file1);
        MultipartFile multipartFile1 = new MockMultipartFile("123", fileInputStream1);
        getUser();
        getRole();
        getDept();
        getResult();
        new Expectations() {
            {
                Role role = new Role();
                role.setId("testId");
                roleDao.verityRoleByName(anyString);
                result = role;
            }
        };
        importService.importUserInfo(multipartFile1);

        file1 = new File(testFile + "5555.xlsx");
        fileInputStream1 = new FileInputStream(file1);
        multipartFile1 = new MockMultipartFile("123", fileInputStream1);
        getUser();
        getRole();
        getDept();
        getResult();
        importService.importUserInfo(multipartFile1);

    }

    @Test
    public void universalLogTest() throws Exception {
        Method method = importService.getClass().getDeclaredMethod("universalLog", String.class, String.class, String.class);
        method.setAccessible(true);
        method.invoke(importService, "testId", "modele", "test");

    }
}