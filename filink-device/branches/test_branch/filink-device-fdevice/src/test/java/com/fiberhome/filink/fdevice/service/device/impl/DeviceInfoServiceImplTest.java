package com.fiberhome.filink.fdevice.service.device.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.fdevice.async.DeviceInfoAsync;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.bean.device.RetryDeleteInfo;
import com.fiberhome.filink.fdevice.constant.device.DeviceConstant;
import com.fiberhome.filink.fdevice.constant.device.DeviceResultCode;
import com.fiberhome.filink.fdevice.constant.device.DeviceRetryDeleteConstant;
import com.fiberhome.filink.fdevice.dao.area.AreaInfoDao;
import com.fiberhome.filink.fdevice.dao.device.DeviceCollectingDao;
import com.fiberhome.filink.fdevice.dao.device.DeviceInfoDao;
import com.fiberhome.filink.fdevice.dao.device.RetryDeleteInfoDao;
import com.fiberhome.filink.fdevice.dto.*;
import com.fiberhome.filink.fdevice.exception.FiLinkAreaDateBaseException;
import com.fiberhome.filink.fdevice.exception.FiLinkAreaDirtyDataException;
import com.fiberhome.filink.fdevice.exception.FiLinkDeviceException;
import com.fiberhome.filink.fdevice.export.DeviceExport;
import com.fiberhome.filink.fdevice.service.area.AreaInfoService;
import com.fiberhome.filink.fdevice.service.device.DeviceConfigService;
import com.fiberhome.filink.fdevice.service.devicelog.DeviceLogService;
import com.fiberhome.filink.filinklockapi.bean.Lock;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.filinklockapi.feign.LockFeign;
import com.fiberhome.filink.license.api.LicenseFeign;
import com.fiberhome.filink.license.bean.License;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.mysql.MpQueryHelper;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.rfidapi.api.DeleteDeviceFeign;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.Role;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import mockit.Expectations;
import mockit.Mocked;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildQuery;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * @author WH1707069 create on 2019/1/12
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceInfoServiceImplTest {

    @InjectMocks
    private DeviceInfoServiceImpl deviceInfoService;

    @Mock
    private DeviceInfoDao deviceInfoDao;

    @Mock
    private DeviceCollectingDao deviceCollectingDao;

    @Mocked
    private ServletRequestAttributes servletRequestAttributes;

    @Mock
    private AreaInfoService areaInfoService;

    @Mock
    private AlarmCurrentFeign alarmCurrentFeign;

    @Mock
    private ProcBaseFeign procBaseFeign;

    @Mock
    private LicenseFeign licenseFeign;

    @Mock
    private LogProcess logProcess;

    @Mock
    private HttpServletRequest request;

    @Mock
    AreaInfoDao areaInfoDao;

    private List<DeviceInfo> deviceInfos;

    @Mock
    private SystemLanguageUtil systemLanguageUtil;

    @Mock
    private UserFeign userFeign;

    @Mock
    private LockFeign lockFeign;

    @Mock
    private DeleteDeviceFeign deleteDeviceFeign;

    @Mock
    private DeviceLogService deviceLogService;

    @Mock
    private ControlFeign controlFeign;

    @Mock
    private DeviceInfoAsync deviceInfoAsync;

    @Mock
    private RetryDeleteInfoDao retryDeleteInfoDao;

    @Mock
    private DeviceExport deviceExport;
    @Mock
    private RedisTemplate redisTemplate;
    @Mock
    private ParameterFeign parameterFeign;

    @Mock
    private DeviceConfigService deviceConfigService;

    public static final String DEFAULT_RESULT = "result";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Result result = new Result();

    @Before
    public void setUp() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceName("aaa");
        deviceInfos = new ArrayList<>();
        deviceInfos.add(deviceInfo);
    }

    @Test
    public void checkDeviceNameTest() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceName("device001");
        deviceInfo.setDeviceId("1");
        when(deviceInfoDao.selectByName("device001")).thenReturn(deviceInfo);
        boolean result1 = deviceInfoService.checkDeviceName("", "device001");
        boolean result2 = deviceInfoService.checkDeviceName("1", "device001");
        boolean result3 = deviceInfoService.checkDeviceName("2", "device001");
        Assert.assertEquals(result1, true);
        Assert.assertEquals(result2, false);
        Assert.assertEquals(result3, true);
    }

    @Test
    public void updateDeviceTest() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hHasKey(DeviceConstant.DEVICE_GIS_MAP, "areaId001");
                result = true;
            }

            {
                RedisUtils.hGet(DeviceConstant.DEVICE_GIS_MAP, "areaId001");
                result = new HashMap<>();
            }
        };
        DeviceInfo deviceInfo = new DeviceInfo();

        //校验设施id为空
        try {
            deviceInfoService.updateDevice(deviceInfo);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
            Assert.assertEquals(e.getMessage(), "demo");
        }

        deviceInfo.setDeviceId("id001");
        // 校验必填参数
        try {
            deviceInfoService.updateDevice(deviceInfo);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
            Assert.assertEquals(e.getMessage(), "demo");
        }

        deviceInfo.setDeviceName("name001");
        deviceInfo.setDeviceType("type001");
        deviceInfo.setAreaId("areaId001");
        deviceInfo.setPositionGps("123,123");
        deviceInfo.setAddress("address001");
        deviceInfo.setRemarks("remarks001");

        deviceInfo.setPositionBase("132,123,123");
        try {
            deviceInfoService.updateDevice(deviceInfo);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
            Assert.assertEquals(e.getMessage(), "demo");
        }
        deviceInfo.setPositionBase("132,123");

        //测试非法字符
        deviceInfo.setDeviceName("!@#$%");
        try {
            deviceInfoService.updateDevice(deviceInfo);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
            Assert.assertEquals(e.getMessage(), "demo");
        }
        deviceInfo.setDeviceName("testName");

        String randomString = RandomStringUtils.randomAlphanumeric(201);
        deviceInfo.setRemarks(randomString);
        try {
            deviceInfoService.updateDevice(deviceInfo);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
            Assert.assertEquals(e.getMessage(), "demo");
        }
        deviceInfo.setRemarks("remarks");

        when(deviceInfoDao.selectDeviceById(deviceInfo.getDeviceId())).thenReturn(null);
        try {
            deviceInfoService.updateDevice(deviceInfo);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
            Assert.assertEquals(e.getMessage(), "demo");
        }
        when(deviceInfoDao.selectDeviceById(deviceInfo.getDeviceId())).thenReturn(deviceInfo);

        new Expectations(RequestContextHolder.class) {
            {
                RequestContextHolder.getRequestAttributes();
                result = servletRequestAttributes.getRequest();
            }
        };
        when(deviceInfoDao.updateById(deviceInfo)).thenReturn(-1);
        when(areaInfoDao.selectAreaInfoById(deviceInfo.getAreaId())).thenReturn(new AreaInfo());
        createUser(any(), any());
        Result s1 = deviceInfoService.updateDevice(deviceInfo);
        Assert.assertTrue(s1.getCode() == ResultCode.FAIL);
        when(deviceInfoDao.updateById(deviceInfo)).thenReturn(1);

        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setAreaName("testArea");
        when(request.getHeader("userId")).thenReturn("111");
        when(areaInfoDao.selectAreaInfoById(deviceInfo.getAreaId())).thenReturn(areaInfo);
        HomeDeviceInfoDto homeDeviceInfoDto = new HomeDeviceInfoDto();
        homeDeviceInfoDto.setAreaId("areaId002");
        when(deviceInfoDao.queryDeviceAreaById(deviceInfo.getDeviceId())).thenReturn(homeDeviceInfoDto);
        Result r = deviceInfoService.updateDevice(deviceInfo);
        Assert.assertTrue(r.getCode() == ResultUtils.success().getCode());

        //修改时缓存错误
        when(deviceInfoDao.queryDeviceAreaById(deviceInfo.getDeviceId())).thenThrow(Exception.class);
        r = deviceInfoService.updateDevice(deviceInfo);
        Assert.assertTrue(r.getCode() == ResultUtils.success().getCode());

        when(logProcess.generateAddLogToCallParam(anyString())).thenReturn(new AddLogBean());
        r = deviceInfoService.updateDeviceForPda(deviceInfo);
        Assert.assertTrue(r.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void listDeviceTest() {
        QueryCondition<DeviceInfo> queryCondition = new QueryCondition<>();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("device_name");
        filterCondition.setOperator("like");
        filterCondition.setFilterValue("1");
        List<FilterCondition> filterConditions = new ArrayList<>();
        filterConditions.add(filterCondition);
        queryCondition.setPageCondition(pageCondition);
        queryCondition.setFilterConditions(filterConditions);
        queryCondition.setSortCondition(new SortCondition());
        EntityWrapper wrapper = myBatiesBuildQuery(queryCondition);
        Page page = myBatiesBuildPage(queryCondition);
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId("1");
        deviceInfo.setDeviceName("test");
        deviceInfo.setDeviceType("001");
        deviceInfo.setAreaId("123");
        deviceInfo.setPositionBase("132,123");
        deviceInfo.setPositionGps("123,123");
        List list = new ArrayList();
        list.add(deviceInfo);
        list.add(deviceInfo);
        when(deviceInfoDao.selectPage(page, wrapper)).thenReturn(list);
        when(deviceInfoDao.selectCount(wrapper)).thenReturn(2);
        List<DeviceInfoDto> list1 = new ArrayList<>();
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        deviceInfoDto.setDeviceId("deviceId");
        deviceInfoDto.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        deviceInfoDto.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
        list1.add(deviceInfoDto);
        when(deviceInfoDao.selectDevicePage(any(), any(), any())).thenReturn(list1);
        createUser(anyString(), anyString());
        List<Lock> lockList = new ArrayList<>();
        Lock lock = new Lock();
        lock.setDeviceId("deviceId");
        lockList.add(lock);
        Result r = deviceInfoService.listDevice(queryCondition);
        Assert.assertTrue(r.getCode() == ResultUtils.success().getCode());

        when(lockFeign.lockListByDeviceIds(any())).thenReturn(lockList);
        r = deviceInfoService.listDevice(queryCondition);
        Assert.assertTrue(r.getCode() == ResultUtils.success().getCode());

        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };
        try {
            deviceInfoService.listDevice(null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
            Assert.assertEquals(e.getMessage(), "demo");
        }

        filterCondition.setFilterField("device_name");
        filterCondition.setOperator("in");
        filterCondition.setFilterValue(new ArrayList<>());
        r = deviceInfoService.listDevice(queryCondition);
        Assert.assertTrue(r.getCode() == ResultUtils.success().getCode());
    }

    private void createUser(String userId, String token) {
        User user = new User();
        user.setId(DeviceConstant.ADMIN);
        when(userFeign.queryCurrentUser(userId, token)).thenReturn(JSONObject.toJSON(user));
    }

    @Test
    public void getDeviceByIdTest() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };
        //校验设施id为空
        try {
            deviceInfoService.getDeviceById("", "");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
            Assert.assertEquals(e.getMessage(), "demo");
        }

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId("id1");
        deviceInfo.setDeviceName("test");
        deviceInfo.setAreaId("123");
        deviceInfo.setDeviceType("001");
        when(deviceInfoDao.selectDeviceById("123")).thenReturn(deviceInfo);
        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setAreaName("testArea");
        when(areaInfoDao.selectAreaInfoById("123")).thenReturn(areaInfo);
        Result result = new Result();
        result.setData(areaInfo);
        result.setMsg("123");
        when(areaInfoService.queryAreaById("123")).thenReturn(result);
        when(deviceCollectingDao.selectAttentionDeviceCount("id1", "userId")).thenReturn(1);
        Result s = deviceInfoService.getDeviceById("123", DeviceConstant.ADMIN);
        Assert.assertTrue(s.getCode() == ResultUtils.success().getCode());

        when(deviceInfoDao.selectDeviceById("123")).thenReturn(null);
        try {
            deviceInfoService.getDeviceById("123", DeviceConstant.ADMIN);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
        }
    }

    /***
     * queryDeviceByAreaId
     */
    @Test
    public void queryDeviceByAreaId() {
        List<DeviceInfo> a = deviceInfoService.queryDeviceByAreaId("a");
        Assert.assertTrue(a != null);
    }

    /**
     * queryDeviceAreaIdIsNull
     */
    @Test
    public void queryHomeDeviceById() {
        String deviceId = "XX";
        HomeDeviceInfoDto homeDeviceInfoDto = new HomeDeviceInfoDto();
        homeDeviceInfoDto.setAreaId("x");
        when(deviceInfoDao.queryDeviceAreaById(deviceId)).thenReturn(homeDeviceInfoDto);
        Assert.assertEquals(0, deviceInfoService.queryHomeDeviceById(deviceId).getCode());
    }

    @Test
    public void queryHomeDeviceByIds() {
        List<String> deviceIds = new ArrayList<>();
        when(deviceInfoDao.queryDeviceAreaByIds(deviceIds)).thenReturn(new ArrayList<>());
        Assert.assertEquals(0, deviceInfoService.queryHomeDeviceByIds(deviceIds).getCode());
    }

    @Test
    public void updateHomeDeviceLimit() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(anyString, any);
            }
        };
        when(redisTemplate.opsForValue()).thenCallRealMethod();
        deviceInfoService.updateHomeDeviceLimit(0);
    }

    @Test
    public void addDevice() {
        DeviceInfo deviceInfo = new DeviceInfo();
        try {
            deviceInfoService.addDevice(deviceInfo);
        } catch (Exception e) {

        }

    }

    @Test
    public void queryCurrentDeviceCount() {
        new Expectations(MpQueryHelper.class) {
            {
                MpQueryHelper.myBatiesBuildQuery((QueryCondition) any);
                result = new EntityWrapper<>();
            }
        };
        when(deviceInfoDao.selectCount(any())).thenReturn(1);
        Assert.assertTrue(1 == deviceInfoService.queryCurrentDeviceCount());

    }

    @Test
    public void queryDeviceAreaAll() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hasKey(DeviceConstant.DEVICE_GIS_MAP);
                result = false;
            }

            {
                RedisUtils.hSetMap(anyString, (Map) any);

            }
        };
        List<HomeDeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
        when(deviceInfoDao.queryDeviceAreaList()).thenReturn(deviceInfoDtoList);
        Assert.assertEquals(0, deviceInfoService.queryDeviceAreaAll().size());

        HomeDeviceInfoDto homeDeviceInfoDto = new HomeDeviceInfoDto();
        homeDeviceInfoDto.setAreaId("ww");
        deviceInfoDtoList.add(homeDeviceInfoDto);
        when(deviceInfoDao.queryDeviceAreaList()).thenReturn(deviceInfoDtoList);
        when(redisTemplate.opsForHash()).thenCallRealMethod();
        Assert.assertEquals(1, deviceInfoService.queryDeviceAreaAll().size());
    }

    @Test
    public void queryHomeDeviceArea() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.get(DeviceConstant.HOME_DEVICE_LIMIT);
                result = null;
            }

            {
                RedisUtils.set(anyString, any);
            }
        };
        new Expectations(RequestInfoUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "1";
            }
        };
        queryDeviceAreaAll();
        List<AreaInfoForeignDto> areaInfoForeignDtoList = new ArrayList<>();
        AreaInfoForeignDto areaInfoForeignDto = new AreaInfoForeignDto();
        areaInfoForeignDto.setAreaId("ww");
        areaInfoForeignDtoList.add(areaInfoForeignDto);
        when(areaInfoService.getAreaInfoFromRedis()).thenReturn(areaInfoForeignDtoList);
        when(parameterFeign.queryHomeDeviceLimit()).thenReturn(1);
        Assert.assertEquals(0, deviceInfoService.queryHomeDeviceArea().getCode());
    }

    @Test
    public void refreshHomeDeviceArea() {
        new Expectations(RequestInfoUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "2";
            }
        };
        User user = new User();
        Department department = new Department();
        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("wuhan");
        department.setAreaIdList(areaIdList);
        user.setDepartment(department);

        Role role = new Role();
        user.setRole(role);
        when(userFeign.queryCurrentUser(anyString(), anyString())).thenReturn(user);
        queryDeviceAreaAll();
        Assert.assertEquals(0, deviceInfoService.refreshHomeDeviceArea().getCode());
        List<RoleDeviceType> roleDeviceTypeList = new ArrayList<>();
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setDeviceTypeId("c");
        roleDeviceTypeList.add(roleDeviceType);
        role.setRoleDevicetypeList(roleDeviceTypeList);
        user.setRole(role);
        when(userFeign.queryCurrentUser(anyString(), anyString())).thenReturn(user);
        Assert.assertEquals(0, deviceInfoService.refreshHomeDeviceArea().getCode());
    }


    @Test
    public void queryDeviceAreaList() {
        new Expectations(RequestInfoUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "2";
            }
        };
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hasKey(anyString);
                result = true;
            }

            {
                RedisUtils.hGetMap(anyString);
                result = new HashMap<>();
            }
        };
        User user = new User();
        Department department = new Department();
        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("wuhan");
        department.setAreaIdList(areaIdList);
        user.setDepartment(department);

        Role role = new Role();
        List<RoleDeviceType> roleDeviceTypeList = new ArrayList<>();
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setDeviceTypeId("c");
        roleDeviceTypeList.add(roleDeviceType);
        role.setRoleDevicetypeList(roleDeviceTypeList);
        user.setRole(role);
        when(userFeign.queryCurrentUser(anyString(), anyString())).thenReturn(user);
        Assert.assertEquals(0, deviceInfoService.queryDeviceAreaList().getCode());
        new Expectations(RequestInfoUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "1";
            }
        };
        Assert.assertEquals(0, deviceInfoService.queryDeviceAreaList().getCode());

    }

    @Test
    public void refreshHomeDeviceAreaHuge() {
        List<String> areaIdList = new ArrayList<>();
        refreshHomeDeviceArea();
        Assert.assertEquals(0, deviceInfoService.refreshHomeDeviceAreaHuge(areaIdList).getCode());
    }

    @Test
    public void refreshDeviceAreaRedis() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hasKey(anyString);
                result = true;
            }

            {
                RedisUtils.hHasKey(anyString, anyString);
                result = true;
            }

            {
                RedisUtils.hGet(anyString, anyString);
                result = new HashMap<>();
            }

            {
                RedisUtils.hSet(anyString, anyString, any);
            }
        };
        String areaId = "X";
        List<HomeDeviceInfoDto> homeDeviceInfoDtoList = new ArrayList<>();
        HomeDeviceInfoDto homeDeviceInfoDto = new HomeDeviceInfoDto();
        homeDeviceInfoDto.setDeviceId("xx");
        homeDeviceInfoDtoList.add(homeDeviceInfoDto);
        when(deviceInfoDao.queryDeviceAreaByAreaId(areaId)).thenReturn(homeDeviceInfoDtoList);
        deviceInfoService.refreshDeviceAreaRedis(areaId);
    }

    @Test
    public void addDeviceInfo() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hHasKey(DeviceConstant.DEVICE_GIS_MAP, anyString);
                result = true;
            }

            {
                RedisUtils.hGet(DeviceConstant.DEVICE_GIS_MAP, anyString);
                result = new HashMap<>();
            }

            {
                RedisUtils.hSet(anyString, anyString, any);

            }
        };
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "ok";
            }
        };
        when(systemLanguageUtil.querySystemLanguage()).thenReturn("zh");
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId("x");
        deviceInfo.setDeviceName("device");
        deviceInfo.setAreaId("wuhan");
        deviceInfo.setRemarks("remark");
        deviceInfo.setDeviceType("030");
        deviceInfo.setPositionBase("15,20");
        deviceInfo.setPositionGps("16,19");
        deviceInfo.setAddress("wuhan");
        User user = new User();
        Role role = new Role();
        user.setRole(role);
        user.setId("1");
        when(userFeign.queryCurrentUser(anyString(), anyString())).thenReturn(user);
        when(areaInfoDao.selectAreaInfoById(anyString())).thenReturn(new AreaInfo());
        License license = new License();
        license.maxDeviceNum = "3";
        when(licenseFeign.getCurrentLicense()).thenReturn(license);
        when(deviceInfoDao.selectCount(any())).thenReturn(2);
        when(deviceInfoDao.insertDevice(deviceInfo)).thenReturn(1);
        HomeDeviceInfoDto homeDeviceInfoDto = new HomeDeviceInfoDto();
        homeDeviceInfoDto.setAreaId("wuhan");
        when(deviceInfoDao.queryDeviceAreaById(anyString())).thenReturn(homeDeviceInfoDto);
        when(licenseFeign.synchronousLicenseThreshold(any())).thenReturn(true);
        Assert.assertEquals(0, deviceInfoService.addDeviceInfo(deviceInfo).getCode());

    }

    @Test
    public void addDeviceForPda() {
        saveOperatorLog();
        addDeviceInfo();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId("x");
        deviceInfo.setDeviceName("device");
        deviceInfo.setAreaId("wuhan");
        deviceInfo.setRemarks("remark");
        deviceInfo.setDeviceType("030");
        deviceInfo.setPositionBase("15,20");
        deviceInfo.setPositionGps("16,19");
        deviceInfo.setAddress("wuhan");
        when(deviceInfoDao.insertDevice(deviceInfo)).thenReturn(1);
        Assert.assertEquals(0, deviceInfoService.addDeviceForPda(deviceInfo).getCode());
    }

    /**
     * 日志
     */
    public void saveOperatorLog() {
        AddLogBean addLogBean = new AddLogBean();
        addLogBean.setTableName("ww");
        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(addLogBean);
        when(systemLanguageUtil.querySystemLanguage()).thenReturn("CN");
        when(logProcess.addOperateLogInfoToCall(any(), any())).thenCallRealMethod();
    }

    /**
     * 根据用户设施类型权限获取区域下有权限的设施信息、计算设施数量
     */
    public void getHomeDeviceAndNumForUser() {


    }

    /**
     * 关联设施
     */
    @Test
    public void setAreaDevice() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = DEFAULT_RESULT;
            }
        };
        Map<String, List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        map.put("m1", list);
        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setAreaId("areaId");

        when(procBaseFeign.queryProcExitsForDeviceIds(list)).thenReturn(ResultUtils.success());
        boolean b1 = deviceInfoService.setAreaDevice(map, areaInfo);
        Assert.assertTrue(b1);

        list.add("s1");
        //返回List有值
        when(alarmCurrentFeign.queryAlarmSourceForFeign(list)).thenReturn(list);
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId("s1");
        when(deviceInfoDao.selectDeviceById(list.get(0))).thenReturn(deviceInfo);

        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = DEFAULT_RESULT;
            }
        };
        try {
            deviceInfoService.setAreaDevice(map, areaInfo);
            Assert.fail();
        } catch (FiLinkAreaDirtyDataException e) {
            Assert.assertTrue(e.getMessage().contains(DEFAULT_RESULT));
        }

        deviceInfo.setDeviceName("name1");
        try {
            deviceInfoService.setAreaDevice(map, areaInfo);
            Assert.fail();
        } catch (FiLinkAreaDirtyDataException e) {
            Assert.assertTrue(e.getMessage().contains(deviceInfo.getDeviceName() + DEFAULT_RESULT));
        }

        //List没有值
        when(alarmCurrentFeign.queryAlarmSourceForFeign(list)).thenReturn(new ArrayList<>());
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        when(deviceInfoDao.selectByIds(list)).thenReturn(deviceInfoList);
        boolean b2 = deviceInfoService.setAreaDevice(map, areaInfo);
        Assert.assertTrue(!b2);

        deviceInfoList.add(deviceInfo);
        when(deviceInfoDao.setAreaDevice("m1", list)).thenReturn(1);
        HomeDeviceInfoDto homeDeviceInfoDto = new HomeDeviceInfoDto();
        when(deviceInfoDao.queryDeviceAreaById(list.get(0))).thenReturn(homeDeviceInfoDto);
        Map map1 = new HashMap();
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hasKey(anyString);
                result = true;
            }

            {
                RedisUtils.hGetMap(anyString);
                result = map1;
            }

            {
                RedisUtils.hSetMap(DeviceConstant.DEVICE_GIS_MAP, map1);
                result = null;
            }
        };
        List<HomeDeviceInfoDto> homeDeviceInfoDtoList = new ArrayList<>();
        homeDeviceInfoDtoList.add(homeDeviceInfoDto);
        when(deviceInfoDao.queryDeviceAreaByIds(list)).thenReturn(homeDeviceInfoDtoList);
        boolean b3 = deviceInfoService.setAreaDevice(map, areaInfo);
        Assert.assertTrue(b3);

        when(deviceInfoDao.setAreaDevice("m1", list)).thenReturn(0);
        try {
            deviceInfoService.setAreaDevice(map, areaInfo);
            Assert.fail();
        } catch (FiLinkAreaDateBaseException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void deleteDeviceByIdsTest() throws Exception {
        String[] str = {"00000de5373611e9aaf5f48e38f46893"};
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceName("test");
        deviceInfo.setAreaId("123");
        deviceInfo.setDeviceType("001");
        when(deviceInfoDao.selectDeviceById("00000de5373611e9aaf5f48e38f46893")).thenReturn(deviceInfo);
        List list = new ArrayList();
        list.add("00000de5373611e9aaf5f48e38f46893");
        when(alarmCurrentFeign.queryAlarmSourceForFeign(list)).thenReturn(new ArrayList());
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        deviceInfoList.add(deviceInfo);
        when(deviceInfoDao.selectDeviceByIds(str)).thenReturn(deviceInfoList);
        when(logProcess.generateAddLogToCallParam("1")).thenReturn(new AddLogBean());
        Map map = new HashMap();
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hasKey(DeviceConstant.DEVICE_GIS_MAP);
                result = true;
            }

            {
                RedisUtils.hGetMap(DeviceConstant.DEVICE_GIS_MAP);
                result = map;
            }

            {
                RedisUtils.hSetMap(DeviceConstant.DEVICE_GIS_MAP, map);
            }
        };
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };
        when(deleteDeviceFeign.checkDevice(list)).thenReturn(true);
        Result result = deviceInfoService.deleteDeviceByIds(str);
        Assert.assertTrue(result.getCode() == DeviceResultCode.DEVICE_NOT_DELETE_WITH_LABEL);

        when(deleteDeviceFeign.checkDevice(list)).thenReturn(false);

        when(controlFeign.deleteControlByDeviceIds(list)).thenReturn(null);
        try {
            deviceInfoService.deleteDeviceByIds(str);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
            e.getMessage().equals("demo");
        }

        when(controlFeign.deleteControlByDeviceIds(list)).thenReturn(ResultUtils.success());
        result = deviceInfoService.deleteDeviceByIds(str);
        Assert.assertTrue(result.getCode() == DeviceResultCode.FAIL);

        when(licenseFeign.updateRedisLicenseThreshold(any())).thenReturn(true);
        result = deviceInfoService.deleteDeviceByIds(str);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        //设施号不存在
        when(deviceInfoDao.selectDeviceById(anyString())).thenReturn(null);
        try {
            deviceInfoService.deleteDeviceByIds(str);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
        }
    }

    @Test
    public void deviceCanChangeDetailTest() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };

        List list = new ArrayList();
        list.add("deviceId001");
        when(alarmCurrentFeign.queryAlarmSourceForFeign(list)).thenReturn(null);
        try {
            deviceInfoService.deviceCanChangeDetail("deviceId001");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
            Assert.assertTrue(e.getMessage().equalsIgnoreCase("demo"));
        }

        when(alarmCurrentFeign.queryAlarmSourceForFeign(list)).thenReturn(list);
        try {
            deviceInfoService.deviceCanChangeDetail("deviceId001");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
            Assert.assertTrue(e.getMessage().equalsIgnoreCase("demo"));
        }

        List procBaseList = new ArrayList();
        when(procBaseFeign.queryProcExitsForDeviceIds(list)).thenReturn(ResultUtils.success(procBaseList));
        Boolean b = deviceInfoService.deviceCanChangeDetail("deviceId001");
        Assert.assertEquals(b.toString(), "false");
    }

    @Test
    public void queryDeviceCount() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };
        QueryCondition<DeviceInfo> queryCondition = new QueryCondition<>();
        User user = new User();
        user.setId(DeviceConstant.ADMIN);
        try {
            deviceInfoService.queryDeviceCount(queryCondition, user);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
            Assert.assertTrue(e.getMessage().equalsIgnoreCase("demo"));
        }

        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("device_name");
        filterCondition.setOperator("like");
        filterCondition.setFilterValue("1");
        FilterCondition filterCondition1 = new FilterCondition();
        filterCondition1.setFilterField("device_name");
        filterCondition1.setOperator("in");
        List<String> filterString = new ArrayList<>();
        filterCondition1.setFilterValue(filterString);
        List<FilterCondition> filterConditions = new ArrayList<>();
        filterConditions.add(filterCondition);
        filterConditions.add(filterCondition1);
        queryCondition.setPageCondition(pageCondition);
        queryCondition.setFilterConditions(filterConditions);
        when(deviceInfoDao.selectDeviceCount(filterConditions)).thenReturn(2);
        Integer integer = deviceInfoService.queryDeviceCount(queryCondition, user);
        Assert.assertTrue(integer == 0);

        filterString.add("name1");
        integer = deviceInfoService.queryDeviceCount(queryCondition, user);
        Assert.assertTrue(integer == 2);
    }


    private User createUser() {
        User user = new User();
        user.setId(DeviceConstant.ADMIN);
        user.setDepartment(new Department());
        user.getDepartment().setAreaIdList(new ArrayList<>());

        user.setRole(new Role());
        user.getRole().setRoleDevicetypeList(new ArrayList<>());

        return user;
    }

    private User fillUser(User user) {
        user.getDepartment().getAreaIdList().add("areaId001");
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setDeviceTypeId("type001");
        user.getRole().getRoleDevicetypeList().add(roleDeviceType);
        return user;
    }

    @Test
    public void getUserAuth() {
        User user = createUser();
        DeviceParam deviceParam = deviceInfoService.getUserAuth(user);
        Assert.assertTrue(deviceParam != null);
    }

    @Test
    public void queryDeviceTypesByAreaIds() {
        User user = createUser();
        user.setId("userId");
        when(userFeign.queryCurrentUser(anyString(), anyString())).thenReturn(JSONObject.toJSON(user));
        List<String> deviceTypeList = deviceInfoService.queryDeviceTypesByAreaIds(null);
        Assert.assertTrue(deviceTypeList.size() == 0);

        List<String> areaIds = new ArrayList<>();
        areaIds.add("areaId001");
        when(deviceInfoDao.queryDeviceTypesByAreaIds(areaIds)).thenReturn(new ArrayList<>());
        deviceTypeList = deviceInfoService.queryDeviceTypesByAreaIds(areaIds);
        Assert.assertTrue(deviceTypeList.size() == 0);
    }

    @Test
    public void queryDeviceInfoBaseByParam() {
        DeviceParam deviceParam = new DeviceParam();
        List<DeviceInfoBase> deviceInfoBases = deviceInfoService.queryDeviceInfoBaseByParam(deviceParam);
        Assert.assertTrue(deviceInfoBases == null);

        deviceParam.setAreaIds(new ArrayList<>());
        deviceParam.getAreaIds().add("areaId001");
        when(deviceInfoDao.queryDeviceInfoBaseByParam(deviceParam)).thenReturn(new ArrayList<>());
        deviceInfoBases = deviceInfoService.queryDeviceInfoBaseByParam(deviceParam);
        Assert.assertTrue(deviceInfoBases.size() == 0);
    }

    @Test
    public void retryDelete() {
        List<RetryDeleteInfo> retryDeleteInfos = new ArrayList<>();
        RetryDeleteInfo retryDeleteInfo = new RetryDeleteInfo();
        retryDeleteInfo.setFunctionCode(DeviceRetryDeleteConstant.PROC_BASE);
        retryDeleteInfos.add(retryDeleteInfo);

        RetryDeleteInfo retryDeleteInfo2 = new RetryDeleteInfo();
        retryDeleteInfo2.setFunctionCode(DeviceRetryDeleteConstant.ALARM_CURRENT);
        retryDeleteInfos.add(retryDeleteInfo2);

        RetryDeleteInfo retryDeleteInfo3 = new RetryDeleteInfo();
        retryDeleteInfo3.setFunctionCode(DeviceRetryDeleteConstant.DEVICE_COLLECTING);
        retryDeleteInfos.add(retryDeleteInfo3);

        RetryDeleteInfo retryDeleteInfo4 = new RetryDeleteInfo();
        retryDeleteInfo4.setFunctionCode(DeviceRetryDeleteConstant.DEVICE_PIC);
        retryDeleteInfos.add(retryDeleteInfo4);

        RetryDeleteInfo retryDeleteInfo5 = new RetryDeleteInfo();
        retryDeleteInfo5.setFunctionCode(DeviceRetryDeleteConstant.INSPECTION_TASK);
        retryDeleteInfos.add(retryDeleteInfo5);

        RetryDeleteInfo retryDeleteInfo6 = new RetryDeleteInfo();
        retryDeleteInfo6.setFunctionCode(890);
        retryDeleteInfos.add(retryDeleteInfo6);
        when(retryDeleteInfoDao.selectAllRetryDeleteInfo()).thenReturn(retryDeleteInfos);
        deviceInfoService.retryDelete();

    }

    @Test
    public void convertFilterConditionsToMap() {
        List<FilterCondition> filterConditions = new ArrayList<>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("field001");
        filterCondition.setFilterValue("value001");
        filterConditions.add(filterCondition);
        Map map = deviceInfoService.convertFilterConditionsToMap(filterConditions);
        Assert.assertTrue(map.size() == 1);
    }


    @Test
    public void queryDeviceDtoByAreaId() {
        new Expectations(RequestInfoUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "userId";
            }
        };
        List<DeviceInfoDto> deviceInfoDtoList = deviceInfoService.queryDeviceDtoByAreaId("areaId001");
        Assert.assertTrue(deviceInfoDtoList.size() == 0);

        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId("deviceId001");
        deviceInfo.setAreaId("areaId001");
        deviceInfoList.add(deviceInfo);
        when(deviceInfoDao.queryDeviceByAreaId("areaId001")).thenReturn(deviceInfoList);
        List<AreaInfo> areaInfoList = new ArrayList<>();
        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setAreaId("areaId001");
        areaInfoList.add(areaInfo);
        when(areaInfoService.queryAreaInfoByIds(anyList())).thenReturn(areaInfoList);

        deviceInfoDtoList = deviceInfoService.queryDeviceDtoByAreaId("areaId001");
        Assert.assertTrue(deviceInfoDtoList.size() == 1);
    }

    @Test
    public void queryDeviceDtoByAreaIds() {
        List<DeviceInfoDto> deviceInfoDtoList = deviceInfoService.queryDeviceDtoByAreaIds(anyList());
        Assert.assertTrue(deviceInfoDtoList.size() == 0);
    }

    @Test
    public void queryDeviceByBean() throws Exception {
        DeviceReq deviceReq = new DeviceReq();
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId("deviceId001");
        deviceInfo.setAreaId("areaId001");
        deviceInfoList.add(deviceInfo);
        when(deviceInfoDao.selectDeviceByBean(deviceReq)).thenReturn(deviceInfoList);
        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setAreaId("areaId001");
        areaInfo.setAreaName("areaName001");
        when(areaInfoService.queryAreaByIdForPda("areaId001")).thenReturn(areaInfo);
        when(controlFeign.getControlParams("deviceId001")).thenReturn(new ArrayList<>());
        deviceReq.setDeviceId("deviceId001");
        deviceReq.setAreaId("areaId001");
        Result result = deviceInfoService.queryDeviceByBean(deviceReq);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void getDeviceByIds() throws Exception {
        i18nUtilsExpectation();
        try {
            deviceInfoService.getDeviceByIds(null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
            e.getMessage().equals("demo");
        }

        List<DeviceInfoDto> deviceByIds = deviceInfoService.getDeviceByIds(new String[0]);
        Assert.assertTrue(deviceByIds.size() == 0);

        try {
            deviceInfoService.getDeviceByIds(new String[]{null});
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
            e.getMessage().equals("demo");
        }

        String[] ids = new String[]{"1", "2"};
        deviceByIds = deviceInfoService.getDeviceByIds(ids);
        Assert.assertTrue(deviceByIds.size() == 0);
    }

    private void i18nUtilsExpectation() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };
    }

    @Test
    public void getDeviceResultByIds() {
        i18nUtilsExpectation();
        Result r = deviceInfoService.getDeviceResultByIds(null);
        Assert.assertTrue(r.getCode() == DeviceResultCode.DEVICE_PARAM_ERROR);

        r = deviceInfoService.getDeviceResultByIds(new String[0]);
        Assert.assertTrue(r.getCode() == ResultUtils.success().getCode());

        String[] ids = new String[]{"1", "2"};
        r = deviceInfoService.getDeviceResultByIds(ids);
        Assert.assertTrue(r.getCode() == ResultUtils.success().getCode());

        r = deviceInfoService.getDeviceResultByIds(new String[]{null});
        Assert.assertTrue(r.getCode() == DeviceResultCode.FAIL);
    }


    @Test
    public void exportDeviceList() {
        i18nUtilsExpectation();
        ExportDto exportDto = new ExportDto();
        when(deviceExport.insertTask(exportDto, "filink-device-server", "demo"))
                .thenThrow(FilinkExportNoDataException.class)
                .thenThrow(FilinkExportDataTooLargeException.class)
                .thenThrow(FilinkExportTaskNumTooBigException.class)
                .thenThrow(Exception.class)
                .thenReturn(new ExportRequestInfo());
        Result result = deviceInfoService.exportDeviceList(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.EXPORT_NO_DATA);

        result = deviceInfoService.exportDeviceList(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.EXPORT_DATA_TOO_LARGE);

        result = deviceInfoService.exportDeviceList(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS);

        result = deviceInfoService.exportDeviceList(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.FAILED_TO_CREATE_EXPORT_TASK);

        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(new AddLogBean());
        result = deviceInfoService.exportDeviceList(exportDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }


    @Test
    public void queryNearbyDeviceListForPda() {
        i18nUtilsExpectation();
        try {
            deviceInfoService.queryNearbyDeviceListForPda(null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
        }

        User user = mockGettingUser();
        DeviceReqPda deviceReqPda = new DeviceReqPda();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        deviceReqPda.setPageCondition(pageCondition);
        Result result = deviceInfoService.queryNearbyDeviceListForPda(deviceReqPda);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        deviceReqPda.setDeviceType(new ArrayList<>());
        deviceReqPda.getDeviceType().add("type001");
        deviceReqPda.setAreaId(new ArrayList<>());
        deviceReqPda.getAreaId().add("areaId001");
        result = deviceInfoService.queryNearbyDeviceListForPda(deviceReqPda);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        user = fillUser(user);
        when(userFeign.queryCurrentUser("userId", "token")).thenReturn(JSONObject.toJSON(user));
        List<DeviceInfoForPda> devicePdaList = new ArrayList<>();
        devicePdaList.add(new DeviceInfoForPda());
        when(deviceInfoDao.queryNearbyDeviceList(deviceReqPda, user.getId())).thenReturn(devicePdaList);
        List<DevicePicDto> devicePicDtoList = new ArrayList<>();
        devicePicDtoList.add(new DevicePicDto());
        when(deviceInfoDao.queryPicInfoByDeviceIds(any())).thenReturn(devicePicDtoList);

        List<Lock> lockList = new ArrayList<>();
        lockList.add(new Lock());
        when(lockFeign.lockListByDeviceIds(anyList())).thenReturn(lockList);
        result = deviceInfoService.queryNearbyDeviceListForPda(deviceReqPda);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

    }

    private User mockGettingUser() {
        new Expectations(RequestInfoUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "userId";
            }

            {
                RequestInfoUtils.getToken();
                result = "token";
            }
        };
        User user = createUser();
        user.setId("userId");
        when(userFeign.queryCurrentUser("userId", "token")).thenReturn(JSONObject.toJSON(user));
        return user;
    }

    @Test
    public void updateDeviceStatus() throws Exception {
        i18nUtilsExpectation();
        try {
            deviceInfoService.updateDeviceStatus(null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
        }

        when(deviceInfoDao.selectDeviceById("deviceId")).thenReturn(null);
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        deviceInfoDto.setDeviceId("deviceId");
        try {
            deviceInfoService.updateDeviceStatus(deviceInfoDto);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
        }

        when(deviceInfoDao.selectDeviceById("deviceId")).thenReturn(new DeviceInfo());
        deviceInfoDto.setDeployStatus("deployStatus");
        deviceInfoDto.setDeviceStatus("deviceStatus");
        Result result = deviceInfoService.updateDeviceStatus(deviceInfoDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void updateDeviceListStatus() {
        i18nUtilsExpectation();
        UpdateDeviceStatusPda updateDeviceStatusPda = new UpdateDeviceStatusPda();
        updateDeviceStatusPda.setDeviceIdList(new ArrayList<>());
        updateDeviceStatusPda.getDeviceIdList().add("deviceId001");
        Result result = deviceInfoService.updateDeviceListStatus(updateDeviceStatusPda);
        Assert.assertTrue(result.getCode() == DeviceResultCode.DEVICE_PARAM_ERROR);

        updateDeviceStatusPda.setDeployStatus("deployStatus001");
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        when(deviceInfoDao.selectDeviceByIds(new String[]{"deviceId001"})).thenReturn(deviceInfoList);
        result = deviceInfoService.updateDeviceListStatus(updateDeviceStatusPda);
        Assert.assertTrue(result.getCode() == DeviceResultCode.DEVICE_ID_LOSE);

        deviceInfoList.add(new DeviceInfo());
        when(deviceInfoDao.selectDeviceByIds(new String[]{"deviceId001"})).thenReturn(deviceInfoList);
        result = deviceInfoService.updateDeviceListStatus(updateDeviceStatusPda);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void getDefaultParamsByDeviceType() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId("deviceId001");
        deviceInfo.setDeviceType("001");
        when(deviceInfoDao.selectDeviceById("deviceId001")).thenReturn(deviceInfo);
        Map<String, String> deviceMap = deviceInfoService.getDefaultParamsByDeviceType("deviceId001");
        Assert.assertTrue(deviceMap.size() == 0);
    }


    @Test
    public void queryDeviceDtoForPageSelection() {
        i18nUtilsExpectation();
        DeviceParam deviceParam = new DeviceParam();
        Result result = deviceInfoService.queryDeviceDtoForPageSelection(deviceParam);
        Assert.assertTrue(result.getCode() == DeviceResultCode.DEVICE_PARAM_ERROR);

        deviceParam.setAreaIds(new ArrayList<>());
        deviceParam.getAreaIds().add("areaId001");
        deviceParam.setDeviceTypes(new ArrayList<>());
        deviceParam.getDeviceTypes().add("001");

        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        when(deviceInfoDao.selectList(any())).thenReturn(deviceInfoList);
        result = deviceInfoService.queryDeviceDtoForPageSelection(deviceParam);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }
}