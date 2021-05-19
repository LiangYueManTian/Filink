package com.fiberhome.filink.parts.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.parts.bean.PartInfo;
import com.fiberhome.filink.parts.dao.PartInfoDao;
import com.fiberhome.filink.parts.exception.FilinkPartsException;
import com.fiberhome.filink.parts.exception.FilinkPartsNameSameException;
import com.fiberhome.filink.parts.constant.PartType;
import com.fiberhome.filink.parts.constant.PartsResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.bean.Department;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @Author: zl
 * @Date: 2019/3/13 16:52
 * @Description: com.fiberhome.filink.parts.service.impl
 * @version: 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class PartInfoServiceImplTest {

    @InjectMocks
    private PartInfoServiceImpl partInfoService;

    @Mock
    private PartInfoDao partInfoDao;

    @Mock
    private LogProcess logProcess;

    @Mock
    private DepartmentFeign departmentFeign;

    @Mocked
    private ServletRequestAttributes servletRequestAttributes;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    public static final String DEFAULT_RESULT = "i18n_result";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void addPart() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString);
                result = DEFAULT_RESULT;
            }
        };
        PartInfo partInfo = new PartInfo();

        //校验必填参数异常
        try {
            partInfoService.addPart(partInfo);
            Assert.fail();
        } catch (FilinkPartsException e) {
            Assert.assertTrue(e.getMessage().contains(DEFAULT_RESULT));
        }

        //校验设施名异常
        partInfo.setPartName("!@#$%");  //设施名填入非法字符
        partInfo.setPartType(PartType.BLUETOOTHKEY.getCode());
        partInfo.setTrustee("admin");
        List<String> unitList = new ArrayList<>();
        unitList.add("8e486021c980403f926c9523895e5f10");
        partInfo.setAccountabilityUnit(unitList);
        try {
            partInfoService.addPart(partInfo);
            Assert.fail();
        } catch (FilinkPartsException e) {
            Assert.assertTrue(e.getMessage().contains(DEFAULT_RESULT));
        }

        // 校验配件名的唯一性
        partInfo.setPartName("abc123456");
        when(partInfoDao.selectByName("abc123456")).thenReturn(partInfo);
        try {
            partInfoService.addPart(partInfo);
            Assert.fail();
        } catch (FilinkPartsNameSameException e) {
            Assert.assertTrue(e.getMessage().contains(DEFAULT_RESULT));
        }

        partInfo.setPartName("abc12345678");
        when(partInfoDao.checkPartsCode(anyString())).thenReturn(null);
        new Expectations(RequestContextHolder.class) {
            {
                RequestContextHolder.getRequestAttributes();
                result = servletRequestAttributes.getRequest();
            }
        };
        when(partInfoDao.insertParts(partInfo)).thenReturn(1);
        Result r = partInfoService.addPart(partInfo);
        Assert.assertTrue(r.getCode() == ResultUtils.success().getCode());

        when(partInfoDao.insertParts(partInfo)).thenReturn(-1);
        Result r1 = partInfoService.addPart(partInfo);
        Assert.assertTrue(r1.getCode() == ResultCode.FAIL);
    }

    @Test
    public void serialNumber() {
        String unitCode = "010101";
        String partsType = "180";
        String s = partInfoService.serialNumber(unitCode, partsType);
        String comb = unitCode + partsType;
        //序列号由unitCode + unitCode + 7位数字组成
        Assert.assertTrue(s.startsWith(comb) && s.length() == comb.length() + 7);
    }

    @Test
    public void checkPartsName() {
        PartInfo partInfo = new PartInfo();
        partInfo.setPartId("id1");
        partInfo.setPartName("name1");

        String partId = "";
        when(partInfoDao.selectByName("name1")).thenReturn(partInfo);
        Assert.assertTrue(partInfoService.checkPartsName(partId, "name1"));

        String partId1 = "id1";
        Assert.assertTrue(!partInfoService.checkPartsName(partId1, "name1"));

        String partId2 = "id2";
        Assert.assertTrue(partInfoService.checkPartsName(partId2, "name1"));
    }

    @Test
    public void updateParts() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString);
                result = DEFAULT_RESULT;
            }
        };
        PartInfo partInfo = new PartInfo();
        partInfo.setPartId("id123456");

        //校验必填参数异常
        try {
            partInfoService.updateParts(partInfo);
            Assert.fail();
        } catch (FilinkPartsException e) {
            Assert.assertTrue(e.getMessage().contains(DEFAULT_RESULT));
        }

        //校验设施名异常
        partInfo.setPartName("!@#$%");  //设施名填入非法字符
        partInfo.setPartType(PartType.BLUETOOTHKEY.getCode());
        partInfo.setTrustee("admin");
        List<String> unitList = new ArrayList<>();
        unitList.add("8e486021c980403f926c9523895e5f10");
        partInfo.setAccountabilityUnit(unitList);
        try {
            partInfoService.updateParts(partInfo);
            Assert.fail();
        } catch (FilinkPartsException e) {
            Assert.assertTrue(e.getMessage().contains(DEFAULT_RESULT));
        }

        // 校验配件名重复异常
        partInfo.setPartName("name123");
        PartInfo partInfo1 = new PartInfo();
        partInfo1.setPartId("id123");
        partInfo1.setPartName("name123");
        when(partInfoDao.selectByName("name123")).thenReturn(partInfo1);
        try {
            partInfoService.updateParts(partInfo);
            Assert.fail();
        } catch (FilinkPartsNameSameException e) {
            Assert.assertTrue(e.getMessage().contains(DEFAULT_RESULT));
        }

        // 校验配件ID不存在
        partInfo.setPartName("name123456");
        when(partInfoDao.selectPartsById(partInfo.getPartId())).thenReturn(null);
        Result r1 = partInfoService.updateParts(partInfo);
        Assert.assertTrue(r1.getCode() == PartsResultCode.PARTS_NOT_EXIST);

        when(partInfoDao.selectPartsById(partInfo.getPartId())).thenReturn(partInfo);

//        when(partInfoDao.checkPartsCode(anyString())).thenReturn(null);
        new Expectations(RequestContextHolder.class) {
            {
                RequestContextHolder.getRequestAttributes();
                result = servletRequestAttributes.getRequest();
            }
        };
        when(partInfoDao.updateById(partInfo)).thenReturn(1);
        Result r2 = partInfoService.updateParts(partInfo);
        Assert.assertTrue(r2.getCode() == ResultUtils.success().getCode());

        when(partInfoDao.updateById(partInfo)).thenReturn(-1);
        Result r3 = partInfoService.updateParts(partInfo);
        Assert.assertTrue(r3.getCode() == ResultCode.FAIL);
    }

//    @Test
//    public void deletePartsByIds() {
//        // 校验设施是不存在
//        String[] dummyIds = {"dummyId"};
//        when(partInfoDao.selectPartsById("dummyId")).thenReturn(null);
//        Result r = partInfoService.deletePartsByIds(dummyIds);
//        Assert.assertTrue(r.getCode() == PartsResultCode.PARTS_NOT_EXIST);
//
//        PartInfo partInfo = new PartInfo();
//        partInfo.setPartId("partsId1");
//        List<PartInfo> partInfos = new ArrayList<>();
//        partInfos.add(partInfo);
//
//        String[] partsIds = {"partsId1"};
//        when(partInfoDao.selectPartsById("partsId1")).thenReturn(partInfo);
//        when(partInfoDao.selectPartsByIds(partsIds)).thenReturn(partInfos);
//        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(new AddLogBean());
//
//        Result r1 = partInfoService.deletePartsByIds(partsIds);
//        Assert.assertTrue(r1.getCode() == ResultUtils.success().getCode());
//    }

//    @Test
//    public void findPartsById() throws Exception {
//        // 校验设施是不存在
//        String dummyId = "dummyId";
//        when(partInfoDao.selectPartsById(dummyId)).thenReturn(null);
//        Result r = partInfoService.findPartsById(dummyId);
//        Assert.assertTrue(r.getCode() == PartsResultCode.PARTS_NOT_EXIST);
//
//        String partId = "partId";
//        mockGetPartInfoDtoById(partId);
//        Result r1 = partInfoService.findPartsById(partId);
//        Assert.assertTrue(r1.getCode() == ResultUtils.success().getCode());
//    }

    @Test
    public void queryListByPage() throws Exception {
        QueryCondition<PartInfo> queryCondition = new QueryCondition<>();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
        queryCondition.setPageCondition(pageCondition);

        queryCondition.setFilterConditions(new ArrayList<>());

        PartInfo partInfo = new PartInfo();
        partInfo.setPartId("partsId");
        EntityWrapper<PartInfo> wrapper = new EntityWrapper();
        List<PartInfo> list = new ArrayList<>();
        list.add(partInfo);
        when(partInfoDao.selectPage(anyObject(), anyObject())).thenReturn(list);

        mockGetPartInfoDtoById(partInfo.getPartId());
        when(partInfoDao.selectCount(wrapper)).thenReturn(1);
        Result r = partInfoService.queryListByPage(queryCondition,null);
        Assert.assertTrue(r.getCode() == ResultUtils.success().getCode());
    }

    private void mockGetPartInfoDtoById(String partsId) {
        PartInfo partInfo = new PartInfo();
        partInfo.setPartId(partsId);
        partInfo.setPartName("partName");
        partInfo.setPartType(PartType.BLUETOOTHKEY.getCode());
        partInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
        partInfo.setUpdateTime(new Timestamp(new Date().getTime()));

        List<String> deptIds = new ArrayList<>();
        deptIds.add("deptId1");
        deptIds.add("deptId2");

        List<Department> allDepartmentList = new ArrayList<>();
        Department department1 = new Department();
        department1.setId("deptId1");
        Department department2 = new Department();
        department2.setId("deptId2");
        Department department3 = new Department();
        department3.setId("deptId3");
        allDepartmentList.add(department1);
        allDepartmentList.add(department2);
        allDepartmentList.add(department3);

        when(partInfoDao.selectPartsById(partsId)).thenReturn(partInfo);
        when(partInfoDao.getDeptId(partsId)).thenReturn(deptIds);
        when(departmentFeign.queryAllDepartment()).thenReturn(allDepartmentList);
    }
}
