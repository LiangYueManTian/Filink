package com.fiberhome.filink.fdevice.service.area.impl;

import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.fdevice.async.AreaAsync;
import com.fiberhome.filink.fdevice.bean.area.AreaDeptInfo;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.bean.area.AreaInfoTree;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.constant.area.AreaI18nConstant;
import com.fiberhome.filink.fdevice.constant.area.AreaResultCodeConstant;
import com.fiberhome.filink.fdevice.dao.device.DeviceInfoDao;
import com.fiberhome.filink.fdevice.dao.area.AreaDeptInfoDao;
import com.fiberhome.filink.fdevice.dao.area.AreaInfoDao;
import com.fiberhome.filink.fdevice.dto.AreaInfoDto;
import com.fiberhome.filink.fdevice.dto.AreaInfoForeignDto;
import com.fiberhome.filink.fdevice.exception.*;
import com.fiberhome.filink.fdevice.export.AreaExport;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.fdevice.stream.DeviceStreams;
import com.fiberhome.filink.logapi.bean.XmlParseBean;
import com.fiberhome.filink.logapi.log.LogCastProcess;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.configuration.LanguageConfig;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.SpringUtils;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

/**
 * AreaInfoServiceImplTest
 */
@RunWith(JMockit.class)
public class AreaInfoServiceImplTest {
    /**
     * 要测试的类
     */
    @Tested
    private AreaInfoServiceImpl areaInfoService;
    /**
     * 自动注入区域dao对象
     */
    @Injectable
    private AreaInfoDao areaInfoDao;
    /**
     * 自动注入关联部门dao对象
     */
    @Injectable
    private AreaDeptInfoDao areaDeptInfoDao;
    /**
     * 自动注入关联部门dao对象
     */
    @Injectable
    private AlarmCurrentFeign alarmCurrentFeign;
    /**
     * 用于请求头mock
     */
    @Mocked
    private RequestContextHolder requestContextHolder;
    /**
     * 用于请求头mock
     */
    @Mocked
    private ServletRequestAttributes servletRequestAttributes;
    /**
     * 自动注入设施service
     */
    @Injectable
    private DeviceInfoService deviceInfoService;
    /**
     * Mock i18nUtils
     */
    @Mocked
    private I18nUtils i18nUtils;
    /**
     * Mock springUtils
     */
    @Mocked
    private SpringUtils springUtils;
    /**
     * areaInfo
     */
    private AreaInfo areaInfo;
    /**
     * 自动注入设施dao对象
     */
    @Injectable
    private DeviceInfoDao deviceInfoDao;
    /**
     * 自动注入日志服务
     */
    @Injectable
    private LogProcess logProcess;
    /**
     * 自动注入日志服务
     */
    @Injectable
    private LogCastProcess logCastProcess;
    /**
     * 部门service
     */
    @Injectable
    private DepartmentFeign departmentFeign;
    /**
     * 用户feign
     */
    @Injectable
    private UserFeign userFeign;
    /**
     * 导出类
     */
    @Injectable
    private AreaExport areaExport;
    /**
     * 设施推消息实体
     */
    @Injectable
    private DeviceStreams deviceStreams;
    /**
     * 最大导出条数
     */
    @Injectable
    private Integer maxExportDataSize;
    /**
     * 区域异步线程类
     */
    @Injectable
    private AreaAsync areaAsync;
    /**
     * 工单feign
     */
    @Injectable
    private ProcBaseFeign procBaseFeign;
    /**
     * redis mock
     */
    @Mocked
    private RedisUtils redisUtils;
    /**
     * queryCondition
     */
    private QueryCondition queryCondition;
    /**
     * list
     */
    private List<DeviceInfo> list;
    /**
     * list2
     */
    private List<DeviceInfo> list2;
    /**
     * areaIds
     */
    private List<String> areaIds;
    /**
     * departmentList
     */
    private List<Department> departmentList = new ArrayList<>();

    /**
     * 初始化
     */
    @Before
    public void setUp() {
        areaInfo = new AreaInfo();
        queryCondition = new QueryCondition();


        list = new ArrayList<DeviceInfo>();
        list.add(new DeviceInfo());
        list2 = new ArrayList<DeviceInfo>();

        areaIds = new ArrayList<>();
        areaIds.add("1");
        new Expectations() {
            {
                maxExportDataSize = 3000;
            }
        };
    }

    /**
     * queryAreaNameIsExist
     */
    @Test
    public void queryAreaNameIsExist() {
        new Expectations() {
            {
                areaInfoDao.selectAreaInfoByName((AreaInfo) any);
                result = null;
            }
        };
        Result result = areaInfoService.queryAreaNameIsExist(new AreaInfo());
        Assert.assertTrue(result.getCode() == 0);
        new Expectations() {
            {
                areaInfoDao.selectAreaInfoByName((AreaInfo) any);
                result = new AreaInfo();
            }
        };
        Result result2 = areaInfoService.queryAreaNameIsExist(new AreaInfo());
        Assert.assertTrue(result2.getCode() != 0);
    }

    /**
     * addArea
     */
    @Test
    public void addArea() {
        Result result = areaInfoService.addArea(new AreaInfo());
        Assert.assertTrue(result.getCode() == AreaResultCodeConstant.NAME_IS_EXIST);
        new Expectations() {
            {
                areaInfoDao.selectAreaInfoByName((AreaInfo) any);
                result = null;
            }
        };
        AreaInfo areaInfo2 = new AreaInfo();
        areaInfo2.setAreaName("湖北");
        try {
            areaInfoService.addArea(areaInfo2);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAreaDateBaseException.class);
        }
        new Expectations() {
            {
                areaInfoDao.addAreaInfo((AreaInfo) any);
                result = 1;
            }
        };
        areaInfoService.addArea(areaInfo2);
        Set<String> list = new HashSet<>();
        list.add("001");
        list.add("002");
        areaInfo2.setAccountabilityUnit(list);
        new Expectations() {
            {
                departmentFeign.queryAllDepartment();
                result = null;
            }
        };
        try {
            areaInfoService.addArea(areaInfo2);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAreaException.class);
        }
        new Expectations() {
            {
                departmentFeign.queryAllDepartment();
                result = departmentList;
            }
        };
        try {
            areaInfoService.addArea(areaInfo2);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAreaDirtyDataException.class);
        }
        Department department = new Department();
        department.setId("001");
        department.setDeptName("纬创");

        Department department2 = new Department();
        department2.setId("002");
        department2.setDeptName("烽火");

        departmentList.add(department);
        departmentList.add(department2);
        try {
            areaInfoService.addArea(areaInfo2);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAreaDateBaseException.class);
        }
        new Expectations() {
            {
                areaDeptInfoDao.addAreaDeptInfoBatch((List) any);
                result = 2;
            }
        };
        areaInfo2.setParentId("002");
        areaInfo.setLevel(1);
        areaInfo.setAreaName("中国");
        new Expectations() {
            {
                areaInfoDao.selectAreaInfoById(anyString);
                result = areaInfo;
            }
        };
        areaInfoService.addArea(areaInfo2);
    }

    /**
     * queryAreaListByItem
     */
    @Test
    public void queryAreaListByItem() {
        User user = new User();
        Department department = new Department();
        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("001");
        department.setAreaIdList(areaIdList);
        user.setDepartment(department);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        new Expectations() {
            {
                userFeign.queryUserByIdList((List) any);
                result = userList;
            }
        };
        QueryCondition queryCondition = new QueryCondition<>();
        Result result = areaInfoService.queryAreaListByItem(queryCondition);
        Assert.assertTrue(result.getCode() == AreaResultCodeConstant.PARAM_NULL);
        AreaInfoDto areaInfoDto = new AreaInfoDto();
        queryCondition.setBizCondition(areaInfoDto);
        Result result1 = areaInfoService.queryAreaListByItem(queryCondition);
        Assert.assertTrue(result1.getCode() == ResultCode.SUCCESS);
    }

    /**
     * queryAreaById
     */
    @Test
    public void queryAreaById() {
        new Expectations() {
            {
                areaInfoDao.selectAreaInfoById(anyString);
                result = null;
            }
        };
        try {
            areaInfoService.queryAreaById("a");
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAreaException.class);
        }
        AreaInfo areaInfo = new AreaInfo();
        Set<String> list = new HashSet<>();
        list.add("001");
        list.add("002");
        areaInfo.setAccountabilityUnit(list);
        Department department = new Department();
        department.setId("001");
        department.setDeptName("纬创");

        Department department2 = new Department();
        department2.setId("002");
        department2.setDeptName("烽火");

        departmentList.add(department);
        departmentList.add(department2);
        new Expectations() {
            {
                areaInfoDao.selectAreaInfoById(anyString);
                result = areaInfo;
            }
        };
        new Expectations() {
            {
                departmentFeign.queryAllDepartment();
                result = departmentList;
            }
        };
        areaInfoService.queryAreaById("a");
    }

    @Test
    public void queryAreaByIdForPdaTest() {
        AreaInfo areaInfo = areaInfoService.queryAreaByIdForPda("aa");
        Assert.assertTrue(areaInfo != null);
        new Expectations() {
            {
                areaInfoDao.selectAreaInfoById(anyString);
                result = null;
            }
        };
        try {
            areaInfoService.queryAreaByIdForPda("aa");
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAreaDoesNotExistException.class);
        }
    }

    /**
     * 更新区域测试
     */
    @Test
    public void updateAreaInfoTest() {
        User user = new User();
        Department department = new Department();
        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("001");
        department.setAreaIdList(areaIdList);
        user.setDepartment(department);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        new Expectations() {
            {
                userFeign.queryUserByIdList((List) any);
                result = userList;
            }
        };
        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setAreaName("aa");
        try {
            areaInfoService.updateAreaInfo(areaInfo);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAreaNoDataPermissionsException.class);
        }
        areaInfo.setAreaId("001");
        new Expectations() {
            {
                areaInfoDao.selectAreaInfoById(anyString);
                result = null;
            }
        };
        try {
            areaInfoService.updateAreaInfo(areaInfo);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAreaDoesNotExistException.class);
        }
        areaInfo.setLevel(1);
        new Expectations() {
            {
                areaInfoDao.selectAreaInfoById(anyString);
                result = areaInfo;
            }
        };
        Result result = areaInfoService.updateAreaInfo(areaInfo);
        Assert.assertTrue(result.getCode() == AreaResultCodeConstant.NAME_IS_EXIST);
        new Expectations() {
            {
                areaInfoDao.selectAreaInfoByName((AreaInfo) any);
                result = null;
            }
        };
        try {
            areaInfoService.updateAreaInfo(areaInfo);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAreaDateBaseException.class);
        }
        new Expectations() {
            {
                areaInfoDao.updateAreaInfoById((AreaInfo) any);
                result = 1;
            }
        };
        Result result1 = areaInfoService.updateAreaInfo(areaInfo);
        Assert.assertTrue(result1.getCode() == ResultCode.SUCCESS);
        List<String> list = new ArrayList<>();
        list.add("aa");
        new Expectations() {
            {
                alarmCurrentFeign.queryAreaForFeign((List) any);
                result = list;
            }
        };
        try {
            areaInfoService.updateAreaInfo(areaInfo);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAreaDirtyDataException.class);
        }
        new Expectations() {
            {
                areaInfoDao.updateAreaNameById((AreaInfo) any);
                result = true;
            }
        };
        Result result2 = areaInfoService.updateAreaInfo(areaInfo);
        Assert.assertTrue(result2.getCode() == ResultCode.SUCCESS);
    }


    /**
     * setAreaDevice
     */
    @Test
    public void setAreaDevice() throws Exception {
        User user = new User();
        Department department = new Department();
        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("001");
        department.setAreaIdList(areaIdList);
        user.setDepartment(department);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        new Expectations() {
            {
                userFeign.queryUserByIdList((List) any);
                result = userList;
            }
        };
        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setAreaName("aa");
        areaInfo.setLevel(1);
        new Expectations() {
            {
                areaInfoDao.selectAreaInfoById(anyString);
                result = areaInfo;
            }

            {
                deviceInfoService.setAreaDevice((Map) any, (AreaInfo) any);
                result = true;
            }
        };
        new Expectations() {
            {
                SpringUtils.getBean(LanguageConfig.class).getEnvironment();
                result = "aa";
            }
        };
        XmlParseBean xmlParseBean = new XmlParseBean();
        new Expectations() {
            {
                logCastProcess.dom4jParseXml(anyString, anyString);
                result = xmlParseBean;
            }
        };
        Map<String, List<String>> map2 = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("001");
        map2.put("001", list);
        Boolean Boolean = areaInfoService.setAreaDevice(map2);
        Assert.assertTrue(Boolean);
    }

    /**
     * deleteAreaByIds
     */
    @Test
    public void deleteAreaByIds() {
        User user = new User();
        Department department = new Department();
        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("001");
        department.setAreaIdList(areaIdList);
        user.setDepartment(department);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        new Expectations() {
            {
                userFeign.queryUserByIdList((List) any);
                result = userList;
            }
        };
        List<AreaInfo> areaInfoList = new ArrayList<>();
        new Expectations() {
            {
                areaInfoDao.selectBatchIds((List) any);
                result = areaInfoList;
            }
        };
        ArrayList<String> strings = new ArrayList<>();
        strings.add("001");
        try {
            areaInfoService.deleteAreaByIds(strings);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAreaDoesNotExistException.class);
        }
        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setAreaName("aa");
        areaInfo.setLevel(1);
        areaInfoList.add(areaInfo);
        new Expectations() {
            {
                areaInfoDao.selectAreaIdByParentId(anyString);
                result = areaIds;
            }
        };
        Result result = areaInfoService.deleteAreaByIds(strings);
        Assert.assertTrue(result.getCode() == AreaResultCodeConstant.HAVE_CHILD);
        new Expectations() {
            {
                areaInfoDao.selectAreaIdByParentId(anyString);
                result = strings;
            }
        };
        new Expectations() {
            {
                deviceInfoService.queryDeviceByAreaId(anyString);
                result = list;
            }
        };
        Result result2 = areaInfoService.deleteAreaByIds(strings);
        Assert.assertTrue(result2.getCode() == AreaResultCodeConstant.HAVE_DEVICE);
        list.clear();
        try {
            Result result3 = areaInfoService.deleteAreaByIds(strings);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAreaDateBaseException.class);
        }
        new Expectations() {
            {
                areaInfoDao.deleteAreaInfoByIds((List) any);
                result = 1;
            }
        };
        Result result4 = areaInfoService.deleteAreaByIds(strings);
        Assert.assertTrue(result4.getCode() == ResultCode.SUCCESS);
    }

    /**
     * 首页获取区域列表
     *
     * @return 区域信息
     */
    @Test
    public void selectAreaInfoByIdsTest() {
        List<String> areaIds = new ArrayList<>();
        AreaInfoForeignDto areaInfoForeignDto = new AreaInfoForeignDto();
        areaIds.add("11");
        new Expectations() {
            {
                RedisUtils.hGet(anyString, anyString);
                result = areaInfoForeignDto;
            }
        };
        List<AreaInfoForeignDto> areaInfoForeignDtos = areaInfoService.selectAreaInfoByIds(areaIds);
        Assert.assertTrue(areaInfoForeignDtos != null);

    }

    @Test
    public void selectAreaInfoByDeptIdsTest() {
        List<String> deptIds = new ArrayList<>();
        deptIds.add("001");
        List<AreaInfo> areaInfoList = areaInfoService.selectAreaInfoByDeptIds(deptIds);
        Assert.assertTrue(areaInfoList != null);
    }

    @Test
    public void exportAreaTest() {
        ExportDto<AreaInfoDto> exportDto = new ExportDto<>();
        Result result = areaInfoService.exportArea(exportDto);
        Assert.assertTrue(result.getCode() == ResultCode.SUCCESS);
        new Expectations() {
            {
                areaExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportNoDataException();
            }
        };
        Result result2 = areaInfoService.exportArea(exportDto);
        Assert.assertTrue(result2.getCode() == AreaResultCodeConstant.EXPORT_NO_DATA);
        new Expectations() {
            {
                I18nUtils.getString(AreaI18nConstant.EXPORT_DATA_TOO_LARGE);
                result = "导出数据{0}条，超过最大限制{1}条";
            }

            {
                areaExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportDataTooLargeException("1000");
            }
        };
        Result result1 = areaInfoService.exportArea(exportDto);
        Assert.assertTrue(result1.getCode() == AreaResultCodeConstant.EXPORT_DATA_TOO_LARGE);
        new Expectations() {
            {
                areaExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportTaskNumTooBigException();
            }
        };
        Result result3 = areaInfoService.exportArea(exportDto);
        Assert.assertTrue(result3.getCode() == AreaResultCodeConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS);
        new Expectations() {
            {
                areaExport.insertTask((ExportDto) any, anyString, anyString);
                result = new NullPointerException();
            }
        };
        Result result4 = areaInfoService.exportArea(exportDto);
        Assert.assertTrue(result4.getCode() == AreaResultCodeConstant.FAILED_TO_CREATE_EXPORT_TASK);
    }

    @Test
    public void selectForeignAreaInfoTest() {
        List<String> userIds = new ArrayList<>();
        Result result = areaInfoService.selectForeignAreaInfo(userIds);
        Assert.assertTrue(result.getCode() == ResultCode.SUCCESS);
        Map<String, AreaInfoForeignDto> redisMap = new HashMap<>();
        AreaInfoForeignDto areaInfoForeignDto = new AreaInfoForeignDto();
        areaInfoForeignDto.setAreaLevel(1);
        redisMap.put("001", areaInfoForeignDto);
        new Expectations() {
            {
                RedisUtils.hasKey(anyString);
                result = true;
            }

            {
                RedisUtils.hGetMap(anyString);
                result = redisMap;
            }
        };
        User user = new User();
        Department department = new Department();
        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("001");
        department.setAreaIdList(areaIdList);
        user.setDepartment(department);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        new Expectations() {
            {
                userFeign.queryUserByIdList((List) any);
                result = userList;
            }
        };
        Result result1 = areaInfoService.selectForeignAreaInfo(userIds);
        Assert.assertTrue(result1.getCode() == ResultCode.SUCCESS);
        userIds.add("11");
        userIds.add("1");
        Result result2 = areaInfoService.selectForeignAreaInfo(userIds);
        Assert.assertTrue(result2.getCode() == ResultCode.SUCCESS);
    }

    @Test
    public void selectSimultaneousForeignAreaInfoTest() {
        User user = new User();
        Department department = new Department();
        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("001");
        department.setAreaIdList(areaIdList);
        user.setDepartment(department);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        new Expectations() {
            {
                userFeign.queryUserByIdList((List) any);
                result = userList;
            }
        };
        Result result = areaInfoService.selectSimultaneousForeignAreaInfo();
        Assert.assertTrue(result.getCode() == ResultCode.SUCCESS);
    }

    @Test
    public void selectAreaIdsByDeptIdsTest() {
        ArrayList<String> deptIds = new ArrayList<>();
        deptIds.add("001");
        List<String> list = areaInfoService.selectAreaIdsByDeptIds(deptIds);
        Assert.assertTrue(list != null);
    }

    @Test
    public void selectAreaDeptInfosByDeptIdsTest() {
        ArrayList<String> deptIds = new ArrayList<>();
        deptIds.add("001");
        List<AreaDeptInfo> areaDeptInfos = areaInfoService.selectAreaDeptInfosByDeptIds(deptIds);
        Assert.assertTrue(areaDeptInfos != null);
    }

    @Test
    public void selectAreaDeptInfoByAreaIdsTest() {
        ArrayList<String> areaIds = new ArrayList<>();
        areaIds.add("001");
        Result result = areaInfoService.selectAreaDeptInfoByAreaIds(areaIds);
        Assert.assertTrue(result.getCode() == ResultCode.SUCCESS);
    }

    @Test
    public void selectChildAreaInfoTest() {
        AreaInfoTree areaInfoTree = new AreaInfoTree();
        areaInfoTree.setLevel(1);
        areaInfoTree.setAreaId("1");
        List<AreaInfoTree> areaInfoTreeList = new ArrayList<>();
        areaInfoTreeList.add(areaInfoTree);
        User user = new User();
        Department department = new Department();
        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("001");
        department.setAreaIdList(areaIdList);
        user.setDepartment(department);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        new Expectations() {
            {
                userFeign.queryUserByIdList((List) any);
                result = userList;
            }
        };
        areaInfoService.selectChildAreaInfo(areaInfoTreeList);
    }

    @Test
    public void queryAreaListAllTest() {
        Result result = areaInfoService.queryAreaListAll();
        Assert.assertTrue(result.getCode() == ResultCode.SUCCESS);
        Map<String, AreaInfoForeignDto> redisMap = new HashMap<>();
        AreaInfoForeignDto areaInfoForeignDto = new AreaInfoForeignDto();
        areaInfoForeignDto.setAreaLevel(1);
        redisMap.put("001", areaInfoForeignDto);
        new Expectations() {
            {
                RedisUtils.hasKey(anyString);
                result = true;
            }

            {
                RedisUtils.hGetMap(anyString);
                result = redisMap;
            }
        };
        User user = new User();
        Department department = new Department();
        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("1");
        department.setAreaIdList(areaIdList);
        user.setDepartment(department);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        new Expectations() {
            {
                userFeign.queryUserByIdList((List) any);
                result = userList;
            }
        };
        Result result1 = areaInfoService.queryAreaListAll();
        Assert.assertTrue(result1.getCode() == ResultCode.SUCCESS);
    }

    @Test
    public void updateRedisAreaTest() {
        areaInfoService.updateRedisArea(new AreaInfoForeignDto());

    }

    @Test
    public void sendUpdateUserInfoTest() {
        areaInfoService.sendUpdateUserInfo();
    }

    @Test
    public void deleteRedisAreaTest() {
        List<String> areaIds = new ArrayList<>();
        areaIds.add("001");
        areaInfoService.deleteRedisArea(areaIds);
    }

    /**
     * 删除区域部门关系测试
     */
    @Test
    public void deleteAreaDeptRelation() {
        List<String> list = new ArrayList<>();
        list.add("aa");
        areaInfoService.deleteAreaDeptRelation(list);
    }
}
