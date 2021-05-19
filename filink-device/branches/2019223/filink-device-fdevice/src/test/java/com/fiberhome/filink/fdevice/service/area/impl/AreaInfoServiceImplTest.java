package com.fiberhome.filink.fdevice.service.area.impl;

import com.fiberhome.filink.alarmCurrent_api.api.AlarmCurrentFeign;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.bean.area.AreaInfoTree;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.dao.area.AreaDeptInfoDao;
import com.fiberhome.filink.fdevice.dao.area.AreaInfoDao;
import com.fiberhome.filink.fdevice.dao.device.DeviceInfoDao;
import com.fiberhome.filink.fdevice.exception.FilinkAreaDateBaseException;
import com.fiberhome.filink.fdevice.exception.FilinkAreaDirtyDataException;
import com.fiberhome.filink.fdevice.exception.FilinkAreaException;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.fdevice.utils.AreaRusultCode;
import com.fiberhome.filink.logapi.bean.XmlParseBean;
import com.fiberhome.filink.logapi.log.LogCastProcess;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.configuration.LanguageConfig;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.SpringUtils;
import com.fiberhome.filink.user_api.api.DepartmentFeign;
import com.fiberhome.filink.user_api.api.UserFeign;
import com.fiberhome.filink.user_api.bean.Department;
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

import javax.servlet.http.HttpServletRequest;
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
    @Mocked
    private RequestContextHolder requestContextHolder;
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
     * queryCondition
     */
    private QueryCondition queryCondition;
    /**
     * map
     */
    private Map map;
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

        map = new HashMap();

        list = new ArrayList<DeviceInfo>();
        list.add(new DeviceInfo());
        list2 = new ArrayList<DeviceInfo>();

        areaIds = new ArrayList<>();
        areaIds.add("1");
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
        Assert.assertTrue(result.getCode() == AreaRusultCode.NAME_IS_EXIST);
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
        Result result = areaInfoService.queryAreaListByItem(queryCondition);
        Assert.assertTrue(result.getCode() != 0);
        queryCondition.setBizCondition(map);
        Result result2 = areaInfoService.queryAreaListByItem(queryCondition);
        Assert.assertTrue(result2.getCode() == 0);
        List<AreaInfoTree> areaInfoTrees = new ArrayList<>();
        AreaInfoTree areaInfoTree = new AreaInfoTree();
        areaInfoTree.setAreaId("a");
        areaInfoTree.setParentId("a");
        areaInfoTree.setLevel(2);
        areaInfoTrees.add(areaInfoTree);

        List<AreaInfoTree> areaInfoTrees2 = new ArrayList<>();
        AreaInfoTree areaInfoTree2 = new AreaInfoTree();
        areaInfoTree2.setAreaId("abc");
        areaInfoTree2.setParentId("a");
        areaInfoTree2.setLevel(3);
        areaInfoTrees2.add(areaInfoTree2);
        new Expectations() {
            {
                areaInfoDao.queryAreaListByItem((Map) any);
                result = areaInfoTrees;
            }

            {
                areaInfoDao.selectAreaInfoByLevel(anyInt);
                result = areaInfoTrees2;
            }
        };
        areaInfoService.queryAreaListByItem(queryCondition);
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

    /**
     * 更新区域测试
     */
    @Test
    public void updateAreaInfo() {
        AreaInfo areaInfo = new AreaInfo();
        new Expectations() {
            {
                areaInfoDao.selectAreaInfoById(anyString);
                result = null;
            }
        };
        Result result = areaInfoService.updateAreaInfo(areaInfo);
        Assert.assertTrue(result.getCode() == AreaRusultCode.THIS_AREA_DOES_NOT_EXIST);
        AreaInfo areaInfo2 = new AreaInfo();
        areaInfo2.setLevel(1);
        new Expectations() {
            {
                areaInfoDao.selectAreaInfoById(anyString);
                result = areaInfo2;
            }
        };
        Result result2 = areaInfoService.updateAreaInfo(areaInfo);
        Assert.assertTrue(result2.getCode() == AreaRusultCode.NAME_IS_EXIST);
        areaInfo.setAreaName("a");
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
                areaInfoDao.updateById((AreaInfo) any);
                result = 1;
            }
        };
        areaInfoService.updateAreaInfo(areaInfo);
    }


    /**
     * setAreaDevice
     */
    @Test
    public void setAreaDevice() throws Exception {
        Map<String, List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("001");
        map.put("001", list);
        new Expectations() {
            {
                areaInfoService.setAreaDevice((Map) any);
                result = true;
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
        Boolean Boolean = areaInfoService.setAreaDevice(map);
        Assert.assertTrue(Boolean);
    }

    /**
     * deleteAreaByIds
     */
    @Test
    public void deleteAreaByIds() {
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
            Assert.assertTrue(e.getClass() == FilinkAreaDirtyDataException.class);
        }
        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setAreaName("aa");
        areaInfo.setLevel(1);
        areaInfoList.add(areaInfo);
        new Expectations() {
            {
                areaInfoDao.selectAreaIdbyParentId(anyString);
                result = areaIds;
            }
        };
        Result result = areaInfoService.deleteAreaByIds(strings);
        Assert.assertTrue(result.getCode() == AreaRusultCode.HAVE_CHILD);
        new Expectations() {
            {
                areaInfoDao.selectAreaIdbyParentId(anyString);
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
        Assert.assertTrue(result2.getCode() == AreaRusultCode.HAVE_DEVICE);
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
    }

    /**
     * 首页获取区域列表
     *
     * @return 区域信息
     */
    @Test
    public void queryAreaListAll() {
        Result result = areaInfoService.queryAreaListAll();
        Assert.assertTrue(result.getCode() == 0);
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
