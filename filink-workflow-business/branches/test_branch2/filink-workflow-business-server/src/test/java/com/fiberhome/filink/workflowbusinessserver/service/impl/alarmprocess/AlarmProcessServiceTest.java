package com.fiberhome.filink.workflowbusinessserver.service.impl.alarmprocess;

import com.fiberhome.filink.alarmcurrentapi.bean.AlarmCurrentInfo;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseInfoBean;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.dao.procbase.ProcBaseDao;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcBaseService;
import com.fiberhome.filink.workflowbusinessserver.stream.WorkflowBusinessStreams;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

/**
 * 告警逻辑层测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/6 13:42
 */
@RunWith(JMockit.class)
public class AlarmProcessServiceTest {

    /**
     * 测试对象AlarmProcessServiceImpl
     */
    @Tested
    private AlarmProcessServiceImpl alarmProcessService;

    @Injectable
    private WorkflowBusinessStreams workflowBusinessStreams;

    @Injectable
    private ProcBaseService procBaseService;

    @Injectable
    private UserFeign userFeign;

    @Injectable
    private DepartmentFeign departmentFeign;

    @Injectable
    private ProcBaseDao procBaseDao;

    /**
     * 工单超时生成告警信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 14:44
     */
    @Test
    public void procTimeOutGenerateAlarmOne() {
        List<AlarmCurrentInfo> alarmCurrentInfoList = new ArrayList<>();
        AlarmCurrentInfo alarmCurrentInfoOne = new AlarmCurrentInfo();
        alarmCurrentInfoOne.setOrderId("1");
        alarmCurrentInfoList.add(alarmCurrentInfoOne);
        alarmProcessService.procTimeOutGenerateAlarmOne(alarmCurrentInfoList, alarmCurrentInfoOne);

        for (int i = 0 ; i < 20 ; i++ ) {
            alarmCurrentInfoList.add(alarmCurrentInfoOne);
        }
        alarmProcessService.procTimeOutGenerateAlarmOne(alarmCurrentInfoList, alarmCurrentInfoOne);
    }


    /**
     * 工单超时创建告警
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 14:44
     */
    @Test
    public void procTimeOutCreateAlarm() {
        new Expectations() {
            {
                procBaseService.queryProcBaseInfoList((ProcBase) any);
                List<ProcBaseInfoBean> procBaseInfoBeanList = new ArrayList<>();
                ProcBaseInfoBean procBaseInfoBean = new ProcBaseInfoBean();
                procBaseInfoBean.setAssign("111");
                procBaseInfoBean.setStatus(ProcBaseConstants.PROC_STATUS_ASSIGNED);
                procBaseInfoBean.setCreateUser("1");
                procBaseInfoBean.setProcId("1");

                List<ProcRelatedDevice> procRelatedDevices = new ArrayList<>();
                ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
                procRelatedDevice.setDeviceId("1");
                procRelatedDevice.setDeviceName("name");
                procRelatedDevice.setDeviceType("020");
                procRelatedDevice.setDeviceAreaName("areaName");
                procRelatedDevice.setDeviceAreaId("1");
                procRelatedDevice.setProcId("1");
                procRelatedDevices.add(procRelatedDevice);
                procBaseInfoBean.setProcRelatedDevices(procRelatedDevices);
                List<ProcRelatedDepartment> procRelatedDepartment = new ArrayList<>();
                ProcRelatedDepartment departmentOne = new ProcRelatedDepartment();
                departmentOne.setProcId("1");
                departmentOne.setAccountabilityDept("1");
                procRelatedDepartment.add(departmentOne);
                procRelatedDepartment.add(departmentOne);
                procBaseInfoBean.setProcRelatedDepartments(procRelatedDepartment);
                procBaseInfoBeanList.add(procBaseInfoBean);
                result = procBaseInfoBeanList;
            }
        };
        alarmProcessService.procTimeOutCreateAlarm();
    }

    /**
     * 获取当前告警信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/8 10:40
     */
    @Test
    public void getAlarmCurrentInfoOne() {
        String procBaseInfoKey = "1";
        Map<String, ProcBaseInfoBean> procBaseInfoBeanMap = new HashMap<>();
        ProcBaseInfoBean procBaseInfoBean = new ProcBaseInfoBean();
        procBaseInfoBean.setCreateUser("1");
        procBaseInfoBean.setProcId("1");
        procBaseInfoBeanMap.put("1", procBaseInfoBean);
        Map<String, String> departmentMap = new HashMap<>();
        Map<String, Department> createDepartmentMap = new HashMap<>();
        Department departmentParamInfo = new Department();
        departmentParamInfo.setId("1");
        departmentParamInfo.setDeptName("name");
        createDepartmentMap.put("1", departmentParamInfo);
        Date nowDate = new Date();
        alarmProcessService.getAlarmCurrentInfoOne(procBaseInfoKey, procBaseInfoBeanMap, departmentMap, createDepartmentMap, nowDate);


        Map<String, String> departmentMapInfo = new HashMap<>();
        Map<String, Department> createDepartmentMapInfo = new HashMap<>();
        Map<String, ProcBaseInfoBean> procBaseInfoBeanInfoMap = new HashMap<>();
        procBaseInfoBean = new ProcBaseInfoBean();
        procBaseInfoBean.setProcId("1");
        List<ProcRelatedDevice> procRelatedDevices = new ArrayList<>();
        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
        procRelatedDevice.setDeviceId("1");
        procRelatedDevice.setDeviceName("name");
        procRelatedDevice.setDeviceType("020");
        procRelatedDevice.setDeviceAreaName("areaName");
        procRelatedDevice.setDeviceAreaId("1");
        procRelatedDevice.setProcId("1");
        procRelatedDevices.add(procRelatedDevice);
        procBaseInfoBean.setProcRelatedDevices(procRelatedDevices);
        List<ProcRelatedDepartment> procRelatedDepartment = new ArrayList<>();
        ProcRelatedDepartment departmentOne = new ProcRelatedDepartment();
        departmentOne.setProcId("1");
        departmentOne.setAccountabilityDept("1");
        procRelatedDepartment.add(departmentOne);
        procRelatedDepartment.add(departmentOne);
        procBaseInfoBean.setProcRelatedDepartments(procRelatedDepartment);
        procBaseInfoBeanInfoMap.put("1", procBaseInfoBean);
        alarmProcessService.getAlarmCurrentInfoOne(procBaseInfoKey, procBaseInfoBeanInfoMap, departmentMapInfo, createDepartmentMapInfo, nowDate);
    }


    /**
     * 设置关联工单信息map
     * @author hedongwei@wistronits.com
     * @date  2019/7/8 9:48
     */
    @Test
    public void setRelatedInfoToProcBaseInfoMap() {
        List<String> procIds = new ArrayList<>();
        Map<String, ProcBaseInfoBean> procBaseInfoBeanMap = new HashMap<>();
        alarmProcessService.setRelatedInfoToProcBaseInfoMap(procIds, procBaseInfoBeanMap);
    }


    /**
     * 工单关联部门集合
     * @author hedongwei@wistronits.com
     * @date  2019/7/8 9:48
     */
    @Test
    public void setDepartmentToDepartmentMap() {
        List<ProcRelatedDepartment> procRelatedDepartmentList = new ArrayList<>();
        ProcRelatedDepartment departmentOne = new ProcRelatedDepartment();
        departmentOne.setAccountabilityDept("deptName");
        procRelatedDepartmentList.add(departmentOne);
        alarmProcessService.setDepartmentToDepartmentMap(procRelatedDepartmentList);
    }

    /**
     * 工单关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/7/8 9:48
     */
    @Test
    public void setDetailRelatedDevice() {
        List<ProcRelatedDevice> procRelatedDeviceList = new ArrayList<>();
        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
        procRelatedDevice.setProcId("1");
        procRelatedDeviceList.add(procRelatedDevice);

        Map<String, ProcBaseInfoBean> procBaseInfoBeanMap = new HashMap<>();
        ProcBaseInfoBean procBaseInfoBean = new ProcBaseInfoBean();
        procBaseInfoBean.setProcId("1");
        procBaseInfoBeanMap.put("1", procBaseInfoBean);
        alarmProcessService.setDetailRelatedDevice(procRelatedDeviceList, procBaseInfoBeanMap);
    }


    /**
     * 以创建人的用户编号为键，以部门为值获取创建人部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/8 11:00
     */
    @Test
    public void getDepartmentMap() {
        List<String> createList = new ArrayList<>();
        createList.add("1");
        new Expectations() {
            {
                Object userObject = userFeign.queryUserByIdList(createList);
                List<User> userList = new ArrayList<>();
                User user = new User();
                user.setId("1");
                userList.add(user);
                result = userList;
            }
        };
        alarmProcessService.getDepartmentMap(createList);
    }

    /**
     * 工单列表map
     * @author hedongwei@wistronits.com
     * @date  2019/7/8 11:07
     */
    @Test
    public void setDetailRelatedDepartment() {
        List<ProcRelatedDepartment> procRelatedDepartmentList = new ArrayList<>();
        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
        procRelatedDepartment.setProcId("1");
        procRelatedDepartmentList.add(procRelatedDepartment);

        Map<String, ProcBaseInfoBean> procBaseInfoBeanMap = new HashMap<>();
        ProcBaseInfoBean procBaseInfoBean = new ProcBaseInfoBean();
        procBaseInfoBean.setProcId("1");
        procBaseInfoBeanMap.put("1", procBaseInfoBean);
        alarmProcessService.setDetailRelatedDepartment(procRelatedDepartmentList, procBaseInfoBeanMap);
    }
}
