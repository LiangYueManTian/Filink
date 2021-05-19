package com.fiberhome.filink.dump.service.impl;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.dump.bean.*;
import com.fiberhome.filink.dump.constant.DumpConstant;
import com.fiberhome.filink.dump.service.TaskInfoService;
import com.fiberhome.filink.dump.utils.CheckUtil;
import com.fiberhome.filink.dump.utils.ZipsUtilTest;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

/**
 * 转储逻辑层
 * @author hedongwei@wistronits.com
 * @date 2019/7/29 14:09
 */
@RunWith(JMockit.class)
public class DumpServiceTest {

    /**
     * 转储数据信息
     */
    @Tested
    private DumpServiceImpl dumpService;

    /**
     * 任务逻辑层
     */
    @Injectable
    private TaskInfoService taskInfoService;

    @Injectable
    private MongoTemplate mongoTemplate;

    @Injectable
    private ZipsUtilTest zipsUtil;


    @Injectable
    private MongoTemplate alarmTemplate;

    /**
     * 日志实体类
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 16:33
     */
    @Test
    public void operateLogEntity() {
        OperateLog operateLog = new OperateLog();
        operateLog.getCreateTime();
        operateLog.getCreateUser();
        operateLog.getDangerLevel();
        operateLog.getDataOptType();
        operateLog.getDetailInfo();
        operateLog.getId();
        operateLog.getOptType();
        operateLog.getOptTerminal();
        operateLog.getOptTime();
        operateLog.getOptResult();
        operateLog.getLogId();
        operateLog.getOptName();
        operateLog.getOptObj();
        operateLog.getOptObjId();
        operateLog.getOptUserCode();
        operateLog.getOptUserName();
        operateLog.getOptUserRole();
        operateLog.getOptUserRoleName();
        operateLog.getCreateUser();
        operateLog.getCreateTime();
        operateLog.getUpdateTime();
        operateLog.getUpdateUser();
    }


    /**
     * 安全日志实体类
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 16:33
     */
    @Test
    public void securityLogEntity() {
        SecurityLog securityLog = new SecurityLog();
        securityLog.getCreateTime();
        securityLog.getCreateUser();
        securityLog.getDangerLevel();
        securityLog.getDataOptType();
        securityLog.getDetailInfo();
        securityLog.getId();
        securityLog.getOptType();
        securityLog.getOptTerminal();
        securityLog.getOptTime();
        securityLog.getOptResult();
        securityLog.getLogId();
        securityLog.getOptName();
        securityLog.getOptObj();
        securityLog.getOptObjId();
        securityLog.getOptUserCode();
        securityLog.getOptUserName();
        securityLog.getOptUserRole();
        securityLog.getOptUserRoleName();
        securityLog.getCreateUser();
        securityLog.getCreateTime();
        securityLog.getUpdateTime();
        securityLog.getUpdateUser();
    }


    /**
     * 系统日志日志实体类
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 16:33
     */
    @Test
    public void systemLogEntity() {
        SystemLog systemLog = new SystemLog();
        systemLog.getCreateTime();
        systemLog.getCreateUser();
        systemLog.getDangerLevel();
        systemLog.getDataOptType();
        systemLog.getDetailInfo();
        systemLog.getId();
        systemLog.getOptType();
        systemLog.getOptTerminal();
        systemLog.getOptTime();
        systemLog.getOptResult();
        systemLog.getLogId();
        systemLog.getOptName();
        systemLog.getOptObj();
        systemLog.getOptObjId();
        systemLog.getOptUserCode();
        systemLog.getOptUserName();
        systemLog.getOptUserRole();
        systemLog.getOptUserRoleName();
        systemLog.getCreateUser();
        systemLog.getCreateTime();
        systemLog.getUpdateTime();
        systemLog.getUpdateUser();
    }

    /**
     * 用户参数
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 16:44
     */
    @Test
    public void UserParam() {
        UserParameter userParameter = new UserParameter();
        userParameter.getAddress();
        userParameter.getArea();
        userParameter.getCountValidityTime();
        userParameter.getCreateTime();
        userParameter.getCreateUser();
        userParameter.getDeleted();
        userParameter.getDepartment();
        userParameter.getDepartmentNameList();
        userParameter.getDeptId();
        userParameter.getEmail();
        userParameter.getId();
        userParameter.getIds();
        userParameter.getLastLoginIp();
        userParameter.getLastLoginTime();
        userParameter.getLastLoginTimeEnd();
        userParameter.getLastLoginTimeRelation();
        userParameter.getLoginIp();
        userParameter.getLoginSourse();
        userParameter.getLoginTime();
        userParameter.getLoginType();
        userParameter.getMaxUsers();
        userParameter.getPage();
        userParameter.getPageSize();
        userParameter.getPassword();
        userParameter.getPermissionId();
        userParameter.getPhonenumber();
        userParameter.getPort();
        userParameter.getPushId();
        userParameter.getRole();
        userParameter.getRoleId();
        userParameter.getRoleIdList();
        userParameter.getRoleNameList();
        userParameter.getSort();
        userParameter.getSortProperties();
        userParameter.getStartNum();
        userParameter.getToken();
        userParameter.getUpdateTime();
        userParameter.getUpdateUser();
        userParameter.getUserCode();
        userParameter.getUserdesc();
        userParameter.getUserName();
        userParameter.getUserNickname();
        userParameter.getUserStatus();
    }

    /**
     * 查询转储数据
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 14:12
     */
    @Test
    public void searchDumpData() {

        TestBean taskBean = new TestBean();
        taskBean.getAge();
        taskBean.getName();
        taskBean.setAge(1);
        taskBean.setName("name");

        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.getId();
        alarmCurrent.getTrapOid();
        alarmCurrent.getAlarmName();
        alarmCurrent.getAlarmNameId();
        alarmCurrent.getAlarmCode();
        alarmCurrent.getAlarmContent();
        alarmCurrent.getAlarmType();
        alarmCurrent.getAlarmSource();
        alarmCurrent.getAlarmSourceType();
        alarmCurrent.getAlarmSourceTypeId();
        alarmCurrent.getAreaId();
        alarmCurrent.getAreaName();
        alarmCurrent.getIsOrder();
        alarmCurrent.getAddress();
        alarmCurrent.getAlarmFixedLevel();
        alarmCurrent.getAlarmObject();
        alarmCurrent.getResponsibleDepartmentId();
        alarmCurrent.getResponsibleDepartment();
        alarmCurrent.getPrompt();
        alarmCurrent.getAlarmBeginTime();
        alarmCurrent.getAlarmNearTime();
        alarmCurrent.getAlarmSystemTime();
        alarmCurrent.getAlarmSystemNearTime();
        alarmCurrent.getAlarmContinousTime();
        alarmCurrent.getAlarmHappenCount();
        alarmCurrent.getAlarmCleanStatus();
        alarmCurrent.getAlarmCleanTime();
        alarmCurrent.getAlarmCleanType();
        alarmCurrent.getAlarmCleanPeopleId();
        alarmCurrent.getAlarmCleanPeopleNickname();
        alarmCurrent.getAlarmConfirmStatus();
        alarmCurrent.getAlarmConfirmTime();
        alarmCurrent.getAlarmConfirmPeopleId();
        alarmCurrent.getAlarmConfirmPeopleNickname();
        alarmCurrent.getExtraMsg();
        alarmCurrent.getAlarmProcessing();
        alarmCurrent.getRemark();
        alarmCurrent.getDoorNumber();
        alarmCurrent.getDoorName();
        alarmCurrent.getIsPicture();
        alarmCurrent.getControlId();


        DeviceLog deviceLog = new DeviceLog();
        deviceLog.getLogId();
        deviceLog.getLogName();
        deviceLog.getType();
        deviceLog.getLogType();
        deviceLog.getDeviceId();
        deviceLog.getDeviceCode();
        deviceLog.getDeviceName();
        deviceLog.getDeviceType();
        deviceLog.getNodeObject();
        deviceLog.getRemarks();
        deviceLog.getAreaId();
        deviceLog.getAreaName();
        deviceLog.getCurrentTime();

        CheckUtil.checkPageConditionNull(new PageCondition());
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(1);
        CheckUtil.checkPageConditionNull(new PageCondition());

        DumpBean dumpBean = new DumpBean();
        dumpBean.setTurnOutNumber("100000");
        DumpData dumpData = new DumpData();
        dumpData.setQueryStr("name");
        dumpData.setMongoTemplate(mongoTemplate);
        dumpBean.setDumpPlace("1");
        ExportDto exportDto = new ExportDto();

        new MockUp<MongoTemplate>(){
            @Mock
            public long count(Query query, Class<?> entityClass) {
                return 10000;
            }
        };

        new Expectations() {
            {

                taskInfoService.insertTask((ExportDto) any, anyString, anyString,
                         anyInt);

                taskInfoService.exportData((Export) any, anyInt, anyString, (Class) any, (MongoTemplate) any, (DumpBean) any, (DumpData) any, anyString);
            }
        };
        String trigger = "1";
        dumpService.searchDumpData(dumpBean, dumpData, exportDto, trigger);
    }


    /**
     * 删除mongo信息
     * @author hedongwei@wistronits.com
     * @date  2019/8/21 14:22
     */
    @Test
    public void removeInfo() {
        Query query = new Query();
        MongoTemplate mongoTemplate = alarmTemplate;
        Class clazz = AlarmHistory.class;
        String collectionName = DumpConstant.ALARM_HISTORY_COLLECTION_NAME;
        dumpService.removeInfo(query, mongoTemplate, clazz, collectionName);
    }
}
