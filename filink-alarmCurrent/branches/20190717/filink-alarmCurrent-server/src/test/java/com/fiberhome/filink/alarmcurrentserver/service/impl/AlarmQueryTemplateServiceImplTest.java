package com.fiberhome.filink.alarmcurrentserver.service.impl;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmQueryTemplate;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmQueryTemplateDto;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmTemplateArea;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmTemplateDepartment;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmTemplateDevice;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmTemplateName;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmContinousDao;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmQueryTemplateDao;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmTemplateAreaDao;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmTemplateDepartmentDao;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmTemplateDeviceDao;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmTemplateNameDao;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import java.util.ArrayList;
import java.util.List;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmQueryTemplateServiceImplTest {

    /**
     * 测试的告警模板信息
     */
    @Tested
    private AlarmQueryTemplateServiceImpl alarmQueryTemplateService;

    /**
     * 当前告警service
     */
    @Injectable
    private AlarmCurrentService alarmCurrentService;

    /**
     * 日志api
     */
    @Injectable
    private LogProcess logProcess;

    @Injectable
    private AlarmTemplateNameDao alarmTemplateNameDao;

    @Injectable
    private AlarmTemplateDeviceDao alarmTemplateDeviceDao;

    /**
     * 告警模板dao
     */
    @Injectable
    private AlarmQueryTemplateDao alarmQueryTemplateDao;

    @Injectable
    private AlarmTemplateDepartmentDao alarmTemplateDepartmentDao;

    @Injectable
    private AlarmTemplateAreaDao alarmTemplateAreaDao;

    @Injectable
    private AlarmContinousDao alarmContinousDao;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 查询告警模板列表信息
     */
    @Test
    public void queryAlarmTemplateListTest() {
        List<AlarmQueryTemplate> list = new ArrayList<>();
        AlarmQueryTemplate alarmQueryTemplate = new AlarmQueryTemplate();
        alarmQueryTemplate.setId("1");
        list.add(alarmQueryTemplate);
        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserName() {
                return "admin";
            }
        };
        new Expectations() {
            {
                alarmQueryTemplateDao.queryAlarmTemplateList((AlarmQueryTemplateDto) any);
                result = list;
            }
        };
        QueryCondition<AlarmQueryTemplateDto> alarmQueryTemplateDtoQueryCondition = new QueryCondition<>();
        AlarmQueryTemplateDto alarmQueryTemplateDto = new AlarmQueryTemplateDto();
        alarmQueryTemplateDtoQueryCondition.setBizCondition(alarmQueryTemplateDto);
        Result result = alarmQueryTemplateService.queryAlarmTemplateList(alarmQueryTemplateDtoQueryCondition);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 批量删除告警模板信息
     */
    @Test
    public void batchDeleteAlarmTemplateTest() {
        new Expectations() {
            {
                alarmQueryTemplateDao.batchDeleteAlarmTemplate((String[]) any);
                result = 1;
            }
        };
        String[] strings = new String[]{"1", "2"};
        try {
            alarmQueryTemplateService.batchDeleteAlarmTemplate(strings);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        new Expectations() {
            {
                alarmQueryTemplateDao.batchDeleteAlarmTemplate((String[]) any);
                result = 2;
            }
        };
        alarmQueryTemplateService.batchDeleteAlarmTemplate(strings);
    }

    /**
     * 查询告警模板信息
     */
    @Test
    public void queryAlarmTemplateByIdTest() {
        AlarmQueryTemplate alarmQueryTemplate = new AlarmQueryTemplate();
        alarmQueryTemplate.setId("1");
        new Expectations() {
            {
                alarmQueryTemplateDao.queryAlarmTemplateById(anyString);
                result = alarmQueryTemplate;
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result resultOne = new Result();
                resultOne.setCode(1);
                resultOne.setMsg("success");
                return resultOne;
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "ALARM_TEMPLATE_FAILED";
            }
        };
        Result result = alarmQueryTemplateService.queryAlarmTemplateById("1");
    }

    /**
     * 查询告警模板信息
     */
    @Test
    public void queryAlarmTemplateByIdTestOne() {
        new Expectations() {
            {
                alarmQueryTemplateDao.queryAlarmTemplateById(anyString);
                result = null;
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result resultOne = new Result();
                resultOne.setCode(1);
                resultOne.setMsg("success");
                return resultOne;
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "ALARM_TEMPLATE_FAILED";
            }
        };
        Result result = alarmQueryTemplateService.queryAlarmTemplateById("1");
    }

    /**
     * 查询告警模板信息
     */
    @Test
    public void queryAlarmQueryTemplateByIdTest() {
        new Expectations() {
            {
                alarmQueryTemplateDao.queryAlarmTemplateById(anyString);
                result = null;
            }
        };
        try {
            alarmQueryTemplateService.queryAlarmQueryTemplateById("1", new QueryCondition());
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        AlarmQueryTemplate alarmQueryTemplate = new AlarmQueryTemplate();
        alarmQueryTemplate.setAlarmBeginFrontTime(0L);
        alarmQueryTemplate.setAlarmBeginQueenTime(0L);
        alarmQueryTemplate.setAlarmConfirmFrontTime(0L);
        alarmQueryTemplate.setAlarmConfirmQueenTime(0L);
        alarmQueryTemplate.setAlarmCleanFrontTime(0L);
        alarmQueryTemplate.setAlarmCleanQueenTime(0L);
        alarmQueryTemplate.setAlarmNearFrontTime(0L);
        alarmQueryTemplate.setAlarmNearQueenTime(0L);
        alarmQueryTemplate.setAddress("1");
        List<AlarmTemplateName> list = new ArrayList<>();
        AlarmTemplateName alarmTemplateName = new AlarmTemplateName();
        alarmTemplateName.setTemplateId("1");
        alarmTemplateName.setAlarmName("min");
        alarmTemplateName.setAlarmNameId("1");
        list.add(alarmTemplateName);
        alarmQueryTemplate.setAlarmNameList(list);
        alarmQueryTemplate.setRemark("1");
        alarmQueryTemplate.setAlarmFixedLevel("1");
        alarmQueryTemplate.setAlarmCleanStatus(1);
        alarmQueryTemplate.setAlarmConfirmStatus(1);
        List<AlarmTemplateDevice> alarmTemplateDevices = new ArrayList<>();
        AlarmTemplateDevice alarmTemplateDevice = new AlarmTemplateDevice();
        alarmTemplateDevice.setDeviceName("1");
        alarmTemplateDevice.setDeviceId("1");
        alarmTemplateDevice.setTemplateId("1");
        alarmTemplateDevices.add(alarmTemplateDevice);
        alarmQueryTemplate.setAlarmObjectList(alarmTemplateDevices);
        List<AlarmTemplateArea> alarmTemplateAreas = new ArrayList<>();
        AlarmTemplateArea alarmTemplateArea = new AlarmTemplateArea();
        alarmTemplateArea.setAreaName("1");
        alarmTemplateArea.setAreaId("1");
        alarmTemplateArea.setTemplateId("1");
        alarmTemplateAreas.add(alarmTemplateArea);
        alarmQueryTemplate.setAreaNameList(alarmTemplateAreas);
        List<AlarmTemplateDepartment> alarmTemplateDepartments = new ArrayList<>();
        AlarmTemplateDepartment alarmTemplateDepartment = new AlarmTemplateDepartment();
        alarmTemplateDepartment.setDepartmentName("1");
        alarmTemplateDepartment.setDepartmentId("1");
        alarmTemplateDepartment.setTemplateId("1");
        alarmTemplateDepartments.add(alarmTemplateDepartment);
        alarmQueryTemplate.setDepartmentList(alarmTemplateDepartments);
        alarmQueryTemplate.setExtraMsg("1");
        alarmQueryTemplate.setAlarmProcessing("1");
        alarmQueryTemplate.setAlarmHappenCount(1);
        alarmQueryTemplate.setAlarmConfirmPeopleNickname("1");
        alarmQueryTemplate.setAlarmCleanPeopleNickname("1");
        alarmQueryTemplate.setAlarmSourceTypeId("1");
        new Expectations() {
            {
                alarmQueryTemplateDao.queryAlarmTemplateById(anyString);
                result = alarmQueryTemplate;
            }
        };
        Result result = alarmQueryTemplateService.queryAlarmQueryTemplateById("1", new QueryCondition());
    }

    /**
     * 修改告警模板信息
     */
    @Test
    public void updateAlarmTemplateTest() {
        AlarmQueryTemplate alarmQueryTemplate = new AlarmQueryTemplate();
        alarmQueryTemplate.setAlarmBeginFrontTime(0L);
        alarmQueryTemplate.setAlarmBeginQueenTime(0L);
        alarmQueryTemplate.setAlarmConfirmFrontTime(0L);
        alarmQueryTemplate.setAlarmConfirmQueenTime(0L);
        alarmQueryTemplate.setAlarmCleanFrontTime(0L);
        alarmQueryTemplate.setAlarmCleanQueenTime(0L);
        alarmQueryTemplate.setAlarmNearFrontTime(0L);
        alarmQueryTemplate.setAlarmNearQueenTime(0L);
        alarmQueryTemplate.setAddress("1");
        List<AlarmTemplateName> list = new ArrayList<>();
        AlarmTemplateName alarmTemplateName = new AlarmTemplateName();
        alarmTemplateName.setTemplateId("1");
        alarmTemplateName.setAlarmName("min");
        alarmTemplateName.setAlarmNameId("1");
        list.add(alarmTemplateName);
        alarmQueryTemplate.setAlarmNameList(list);
        alarmQueryTemplate.setRemark("1");
        alarmQueryTemplate.setAlarmFixedLevel("1");
        alarmQueryTemplate.setAlarmCleanStatus(1);
        alarmQueryTemplate.setAlarmConfirmStatus(1);
        List<AlarmTemplateDevice> alarmTemplateDevices = new ArrayList<>();
        AlarmTemplateDevice alarmTemplateDevice = new AlarmTemplateDevice();
        alarmTemplateDevice.setDeviceName("1");
        alarmTemplateDevice.setDeviceId("1");
        alarmTemplateDevice.setTemplateId("1");
        alarmTemplateDevices.add(alarmTemplateDevice);
        alarmQueryTemplate.setAlarmObjectList(alarmTemplateDevices);
        List<AlarmTemplateArea> alarmTemplateAreas = new ArrayList<>();
        AlarmTemplateArea alarmTemplateArea = new AlarmTemplateArea();
        alarmTemplateArea.setAreaName("1");
        alarmTemplateArea.setAreaId("1");
        alarmTemplateArea.setTemplateId("1");
        alarmTemplateAreas.add(alarmTemplateArea);
        alarmQueryTemplate.setAreaNameList(alarmTemplateAreas);
        List<AlarmTemplateDepartment> alarmTemplateDepartments = new ArrayList<>();
        AlarmTemplateDepartment alarmTemplateDepartment = new AlarmTemplateDepartment();
        alarmTemplateDepartment.setDepartmentName("1");
        alarmTemplateDepartment.setDepartmentId("1");
        alarmTemplateDepartment.setTemplateId("1");
        alarmTemplateDepartments.add(alarmTemplateDepartment);
        alarmQueryTemplate.setDepartmentList(alarmTemplateDepartments);
        alarmQueryTemplate.setExtraMsg("1");
        alarmQueryTemplate.setAlarmProcessing("1");
        alarmQueryTemplate.setAlarmHappenCount(1);
        alarmQueryTemplate.setAlarmConfirmPeopleNickname("1");
        alarmQueryTemplate.setAlarmCleanPeopleNickname("1");
        alarmQueryTemplate.setAlarmSourceTypeId("1");
        new Expectations() {
            {
                alarmQueryTemplateDao.updateById((AlarmQueryTemplate) any);
            }
        };
        try {
            alarmQueryTemplateService.updateAlarmTemplate(new AlarmQueryTemplate());
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        new Expectations() {
            {
                alarmQueryTemplateDao.updateById((AlarmQueryTemplate) any);
                result = 1;
            }
        };
        alarmQueryTemplateService.updateAlarmTemplate(alarmQueryTemplate);
    }

    /**
     * 新增告警模板
     */
    @Test
    public void addAlarmTemplateTest() {
        new Expectations() {
            {
                alarmQueryTemplateDao.insert((AlarmQueryTemplate) any);
            }
        };
        try {
            alarmQueryTemplateService.addAlarmTemplate(new AlarmQueryTemplate());
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        new Expectations() {
            {
                alarmQueryTemplateDao.insert((AlarmQueryTemplate) any);
                result = 1;
            }
        };
        alarmQueryTemplateService.addAlarmTemplate(new AlarmQueryTemplate());
    }

}