//<<<<<<< .mine
//||||||| .r14863
//package com.fiberhome.filink.workflowbusinessserver.service.impl;
//
//import com.fiberhome.filink.bean.PageCondition;
//import com.fiberhome.filink.bean.QueryCondition;
//import com.fiberhome.filink.bean.Result;
//import com.fiberhome.filink.logapi.log.LogProcess;
//import com.fiberhome.filink.server_common.utils.I18nUtils;
//import com.fiberhome.filink.user_api.api.DepartmentFeign;
//import com.fiberhome.filink.user_api.bean.Department;
//import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
//import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcClearFailure;
//import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
//import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
//import com.fiberhome.filink.workflowbusinessserver.dao.procbase.ProcBaseDao;
//import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
//import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
//import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
//import com.fiberhome.filink.workflowbusinessserver.service.impl.procbase.ProcBaseServiceImpl;
//import com.fiberhome.filink.workflowbusinessserver.service.procclear.ProcClearFailureService;
//import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionRecordService;
//import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionService;
//import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
//import com.fiberhome.filink.workflowbusinessserver.utils.procbase.ProcBaseResultCode;
//import mockit.Expectations;
//import mockit.Injectable;
//import mockit.Mocked;
//import mockit.Tested;
//import mockit.integration.junit4.JMockit;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//@RunWith(JMockit.class)
//public class ProcBaseServiceTests {
//
//    /**
//     * 测试对象 ProcBaseServiceImpl
//     */
//    @Tested
//    private ProcBaseServiceImpl procBaseServiceImpl;
//
//    /**
//     * 自动注入 ProcBaseDao
//     */
//    @Injectable
//    private ProcBaseDao procBaseDao;
//
//    /**
//     * 自动注入 ProcClearFailureService
//     */
//    @Injectable
//    private ProcClearFailureService procClearFailureService;
//
//    /**
//     * 自动注入 ProcInspectionService
//     */
//    @Injectable
//    private ProcInspectionService procInspectionService;
//
//    /**
//     * 自动注入 ProcInspectionRecordService
//     */
//    @Injectable
//    private ProcInspectionRecordService procInspectionRecordService;
//
//    /**
//     * 自动注入 LogProcess
//     */
//    @Injectable
//    private LogProcess logProcess;
//
//    /**
//     * 自动注入 DepartmentFeign
//     */
//    @Injectable
//    private DepartmentFeign departmentFeign;
//
//    /**
//     * Mock RequestContextHolder
//     */
//    @Mocked
//    private RequestContextHolder requestContextHolder;
//    /**
//     * Mock ServletRequestAttributes
//     */
//    @Mocked
//    private ServletRequestAttributes servletRequestAttributes;
//
//
//    /**
//     * Mock I18nUtils
//     */
//    @Mocked
//    private I18nUtils I18nUtils;
//
//    /**
//     * Mock ProcBaseResultCode
//     */
//    @Mocked
//    private ProcBaseResultCode procBaseResultCode;
//
//    /**
//     * 初始化
//     */
//    @Before
//    public void setUp() {
//    }
//
//    /**
//     * queryTitleIsExists()
//     */
//    @Test
//    public void queryTitleIsExists() {
//        ProcBase procbase = new ProcBase();
//        procbase.setProcId("fdsfadfsafdsa");
//        procbase.setTitle("fdsafasfdasfdsaffsad");
//        new Expectations() {
//            {
//                procBaseDao.queryTitleIsExists(anyString);
//                result = procbase;
//            }
//        };
//        //返回值
//        Boolean b = procBaseServiceImpl.queryTitleIsExists("fdsfadfsafdsa","fdsafasfdasfdsaffsad");
//        Assert.assertTrue(b == false);
//
//        procbase.setProcId("fdsfadfsafdsa");
//        procbase.setTitle("fdsafasfdasfdsaffsad");
//        new Expectations() {
//            {
//                procBaseDao.queryTitleIsExists(anyString);
//                result = procbase;
//            }
//        };
//        //返回值
//        b = procBaseServiceImpl.queryTitleIsExists(null,"fdsafasfdasfdsaffsad");
//        Assert.assertTrue(b == true);
//
//        procbase.setProcId("fdsafdsad");
//        procbase.setTitle("fdsafasfdasfdsaffsad");
//        new Expectations() {
//            {
//                procBaseDao.queryTitleIsExists(anyString);
//                result = procbase;
//            }
//        };
//        //返回值
//        b = procBaseServiceImpl.queryTitleIsExists("fdsfadfsafdsa","fdsafasfdasfdsaffsad");
//        Assert.assertTrue(b == true);
//    }
//
//    /**
//     * addProcBase()
//     */
//    @Test
//    public void addProcBase() {
//
//        ProcessInfo processInfo = new ProcessInfo();
//
//        ProcBaseReq procBaseReq = new ProcBaseReq();
//        procBaseReq.setTitle("测试工单110");
//        procBaseReq.setProcType("clear_failure");
//        procBaseReq.setRemark("测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109");
//        procBaseReq.setRefAlarm("3");
//        procBaseReq.setProcResourceType("1");
//        processInfo.setProcBaseReq(procBaseReq);
//
//        ProcBase procbase = new ProcBase();
//        procbase.setTitle("测试工单110");
//        procbase.setProcType("clear_failure");
//        procbase.setRemark("测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109");
//        procbase.setRefAlarm("3");
//        procbase.setProcResourceType("1");
//        processInfo.setProcBase(procbase);
//
//        ProcClearFailure procClearFailure = new ProcClearFailure();
//        processInfo.setProcClearFailure(procClearFailure);
//
//        List<ProcRelatedDevice> procBaseServices = new ArrayList<>();
//        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
//        procBaseServices.add(procRelatedDevice);
//        processInfo.setProcRelatedDevices(procBaseServices);
//
//        List<ProcRelatedDepartment> procRelatedDepartments = new ArrayList<>();
//        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
//        procRelatedDepartments.add(procRelatedDepartment);
//        processInfo.setProcRelatedDepartments(procRelatedDepartments);
//
//        new Expectations() {
//            {
//                //保存主表信息
//                procBaseDao.addProcBase((ProcBaseReq) any);
//                result = 1;
//            }
//        };
//
//        //返回值
//        Result s = procBaseServiceImpl.addProcBase(processInfo);
//        Assert.assertTrue(s.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//
//    /**
//     * addProcRelate()
//     */
//    @Test
//    public void addProcRelate() {
//
//        ProcessInfo processInfo = new ProcessInfo();
//        ProcBase procbase = new ProcBase();
//
//        List<ProcRelatedDevice> deviceCounts = new ArrayList<>();
//        List<ProcRelatedDepartment> unitCounts = new ArrayList<>();
//        processInfo.setProcBase(procbase);
//
//        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
//        deviceCounts.add(procRelatedDevice);
//        processInfo.setProcRelatedDevices(deviceCounts);
//
//        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
//        unitCounts.add(procRelatedDepartment);
//        processInfo.setProcRelatedDepartments(unitCounts);
//
//        new Expectations() {
//            {
//                // 修改工单关联设施信息
//                procBaseDao.deleteProcRelateDeviceByProcId((ProcBase) any);
//                result = 1;
//                procBaseDao.addProcRelateDevice((ProcessInfo) any);
//                result = 1;
//
//                // 保存工单关联部门信息
//                procBaseDao.deleteProcRelateDeptByProcId((ProcBaseReq) any);
//                result = 1;
//                procBaseDao.addProcRelateDept((ProcessInfo) any);
//                result = 1;
//            }
//        };
//
//        //返回值
//        procBaseServiceImpl.saveProcRelate(processInfo);
//        Assert.assertTrue(true);
//    }
//
//
//    /**
//     * updateProcessById()
//     */
//    @Test
//    public void updateProcessById() {
//
//        Date date = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            date = sdf.parse("2019-03-04 10:53:49");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        ProcessInfo processInfo = new ProcessInfo();
//        ProcBase procbase = new ProcBase();
//        procbase.setProcId("fdsafdsafdsafds");
//        procbase.setTitle("测试工单110");
//        procbase.setProcType("clear_failure");
//        procbase.setRemark("测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109");
//        procbase.setRefAlarm("3");
//        procbase.setProcResourceType("1");
//        processInfo.setProcBase(procbase);
//
//        ProcBaseResp procBaseResp = new ProcBaseResp();
//        procBaseResp.setProcId("fdsafdsafdsafds");
//        processInfo.setProcBaseResp(procBaseResp);
//
//        List<ProcRelatedDevice> procBaseServices = new ArrayList<>();
//        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
//        procBaseServices.add(procRelatedDevice);
//        processInfo.setProcRelatedDevices(procBaseServices);
//
//        List<ProcRelatedDepartment> procRelatedDepartments = new ArrayList<>();
//        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
//        procRelatedDepartments.add(procRelatedDepartment);
//        processInfo.setProcRelatedDepartments(procRelatedDepartments);
//
//        //预言不能使用具体值，只能使用泛型，不然会拿不到值
//        new Expectations() {
//            {
//                //获取工单基础信息
//                procBaseDao.queryProcessByProcId(anyString);
//                result = procBaseResp;
//
//                // 修改基础信息
//                procBaseDao.updateProcBaseById((ProcBaseReq) any);
//                result = 1;
//            }
//        };
//
//        //返回值
//        Result s = procBaseServiceImpl.updateProcessById(processInfo);
//        Assert.assertTrue(s.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//
//    /**
//     * deleteProcessByIds()
//     */
//    @Test
//    public void deleteProcessByIds() {
//
//        ProcBase procbase = new ProcBase();
//        procbase.setProcId("fdsafdsafdsafds");
//        procbase.setStatus("assigned");
//
//        List<ProcBase> procBases = new ArrayList<>();
//        procBases.add(procbase);
//
//        List<String> ids = new ArrayList<>();
//        ids.add("fdsafdsafdsafds");
//        ids.add("fdsafdsafdsafds");
//
//        //此预言只能测同一个方法，方法内引用方法只能分开测
//        new Expectations() {
//            {
//                //获取工单基础信息
//                procBaseDao.queryProcessByProcId(anyString);
//                result = procbase;
//
//                // 删除基础信息
//                procBaseDao.updateProcBaseIsDeletedById((ProcBaseReq) any);
//                result = 1;
//            }
//        };
//
//        //返回值
//        Result s = procBaseServiceImpl.updateProcessIsDeletedByIds(ids,"1");
//        Assert.assertTrue(s.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//    /**
//     * deleteProcRelate()
//     */
//    @Test
//    public void deleteProcRelate(){
//
//        ProcBase procbase = new ProcBase();
//
//        List<ProcRelatedDevice> deviceCounts = new ArrayList<>();
//        List<ProcRelatedDepartment> unitCounts = new ArrayList<>();
//
//        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
//        deviceCounts.add(procRelatedDevice);
//
//        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
//        unitCounts.add(procRelatedDepartment);
//
//        new Expectations() {
//            {
//                // 获取工单关联设施信息
//                procBaseDao.queryProcRelateDevice((ProcBaseReq) any);
//                result = deviceCounts;
//
//                // 删除工单关联设施信息
//                procBaseDao.updateProcRelateDeviceIsDeletedByProcId((ProcBase) any);
//                result = 1;
//
//                //获取工单关联部门信息
//                procBaseDao.queryProcRelateDept((ProcBaseReq) any);
//                result = unitCounts;
//
//                // 删除工单关联部门信息
//                procBaseDao.updateProcRelateDeptIsDeletedByProcId((ProcBase) any);
//                result = 1;
//            }
//        };
//
//        //返回值
//        procBaseServiceImpl.updateProcRelateIsDeleted(procbase);
//        Assert.assertTrue(true);
//    }
//
//
//    /**
//     * getProcessByProcId()
//     */
//    @Test
//    public void getProcessByProcId() {
//
//        ProcessInfo processInfo = new ProcessInfo();
//        ProcBaseReq procBaseReq = new ProcBaseReq();
//        ProcBaseResp procBaseResp = new ProcBaseResp();
//        procBaseResp.setProcId("fdsafdsafdsafdsafdsa");
//        ProcBase procbase = new ProcBase();
//        procBaseReq.setProcId("fdsafdsafdsafdsafdsa");
//        processInfo.setProcBaseReq(procBaseReq);
//        processInfo.setProcBase(procbase);
//        processInfo.setProcBaseResp(procBaseResp);
//
//        new Expectations() {
//            {
//                //获取工单基础信息
//                procBaseDao.queryProcessByProcId(anyString);
//                result = procBaseResp;
//            }
//        };
//
//        //返回值
//        Result s = procBaseServiceImpl.queryProcessById("000d03f33fdf11e99b3adc5360c0ce1f");
//        Assert.assertTrue(s.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//    /**
//     * queryProcRelate()
//     */
//    @Test
//    public void queryProcRelate() {
//
//        ProcessInfo processInfo = new ProcessInfo();
//        ProcBaseReq procBaseReq = new ProcBaseReq();
//        ProcBaseResp procBaseResp = new ProcBaseResp();
//        ProcBase procbase = new ProcBase();
//
//        List<ProcRelatedDevice> deviceCounts = new ArrayList<>();
//        List<ProcRelatedDepartment> unitCounts = new ArrayList<>();
//        List<Department> departmentList = new ArrayList<>();
//
//        procBaseReq.setProcId("b9d27a0d3e2811e9b61adc5360c0ce1f");
//        procBaseReq.setProcType("clear_failure");
//        processInfo.setProcBaseReq(procBaseReq);
//        processInfo.setProcBase(procbase);
//        processInfo.setProcBaseResp(procBaseResp);
//        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
//        deviceCounts.add(procRelatedDevice);
//
//        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
//        unitCounts.add(procRelatedDepartment);
//
//        new Expectations() {
//            {
//                //获取工单关联设施信息
//                procBaseDao.queryProcRelateDevice(processInfo.getProcBaseReq());
//                result = deviceCounts;
//
//                //获取工单关联部门信息
//                procBaseDao.queryProcRelateDept(processInfo.getProcBaseReq());
//                result = unitCounts;
//            }
//        };
//
////        procBaseServiceImpl.queryProcRelate(processInfo,departmentList, ProcBaseConstants.PROC_METHOD_DETAIL);
////        Assert.assertTrue(true);
//    }
//
//
//    /**
//     * queryListProcUnfinishedProcByPage()
//     */
//    @Test
//    public void queryListProcUnfinishedProcByPage(){
//        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
//
//        List<ProcBase> procBaselist = new ArrayList<>();
//        List<ProcBaseResp> resultList = new ArrayList<>();
//        ProcBaseReq procBaseReq = new ProcBaseReq();
//        queryCondition.setBizCondition(procBaseReq);
//        ProcBase procbase = new ProcBase();
//        procBaselist.add(procbase);
//
//        PageCondition pageCondition = new PageCondition();
//        pageCondition.setPageNum(1);
//        pageCondition.setPageSize(200);
//        queryCondition.setPageCondition(pageCondition);
//
//        new Expectations() {
//            {
//                procBaseDao.queryCountListProcUnfinishedProc((QueryCondition<ProcBaseReq>) any);
//                result = 1;
//
//                procBaseDao.queryListProcUnfinishedProcByPage((QueryCondition<ProcBaseReq>) any);
//                result = procBaselist;
//            }
//        };
//        Result result = procBaseServiceImpl.queryListProcUnfinishedProcByPage(queryCondition);
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//    /**
//     * queryListProcHisProcByPage()
//     */
//    @Test
//    public void queryListProcHisProcByPage(){
//        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
//
//        ProcessInfo processInfo = new ProcessInfo();
//        List<ProcRelatedDepartment> procRelatedDepartments = new ArrayList<>();
//        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
//        procRelatedDepartments.add(procRelatedDepartment);
//        processInfo.setProcRelatedDepartments(procRelatedDepartments);
//
//        List<ProcRelatedDevice> procRelatedDevices = new ArrayList<>();
//        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
//        procRelatedDevices.add(procRelatedDevice);
//        processInfo.setProcRelatedDevices(procRelatedDevices);
//
//        List<Department> departmentList = new ArrayList<>();
//        Department department = new Department();
//        departmentList.add(department);
//
//        List<ProcBase> procBaselist = new ArrayList<>();
//        List<ProcBaseResp> resultList = new ArrayList<>();
//        ProcBaseReq procBaseReq = new ProcBaseReq();
//        queryCondition.setBizCondition(procBaseReq);
//        ProcBase procbase = new ProcBase();
//        procBaselist.add(procbase);
//
//        ProcBaseResp procBaseResp = new ProcBaseResp();
//        resultList.add(procBaseResp);
//
//        PageCondition pageCondition = new PageCondition();
//        pageCondition.setPageNum(1);
//        pageCondition.setPageSize(200);
//        queryCondition.setPageCondition(pageCondition);
//
//        new Expectations() {
//            {
//                procBaseDao.queryCountListProcHisProc((QueryCondition<ProcBaseReq>) any);
//                result = 1;
//
//                procBaseDao.queryListProcHisProcByPage((QueryCondition<ProcBaseReq>) any);
//                result = resultList;
//
//            }
//        };
//        Result result = procBaseServiceImpl.queryListProcHisProcByPage(queryCondition);
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//    /**
//     * queryProcSpecific()
//     */
//    @Test
//    public void queryProcSpecific(){
//        ProcessInfo processInfo = new ProcessInfo();
//        ProcBase procbase = new ProcBase();
//        procbase.setProcType("clear_failure");
//        processInfo.setProcBase(procbase);
//        new Expectations() {
//            {
//                //获取工单特性信息
////                procClearFailureService.queryProcClearFailureSpecific(anyString);
//            }
//        };
//
////        procBaseServiceImpl.queryProcSpecific(processInfo);
////        Assert.assertTrue(true);
//    }
//
//    /**
//     * deleteProcSpecific()
//     */
//    @Test
//    public void deleteProcSpecific(){
//        ProcBase procbase = new ProcBase();
//        procbase.setProcType("clear_failure");
//        new Expectations() {
//            {
//                //删除工单特性信息
//                procClearFailureService.updateProcClearFailureSpecificIsDeleted(anyString,"1");
//            }
//        };
//
//        procBaseServiceImpl.updateProcSpecificIsDeletd(procbase);
//        Assert.assertTrue(true);
//    }
//
//    /**
//     * queryCountListProcUnfinishedProcStatus()
//     */
//    @Test
//    public void queryCountListProcUnfinishedProcStatus(){
//        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
//        new Expectations() {
//            {
//                procBaseDao.queryCountListProcUnfinishedProc(queryCondition);
//                result = 1;
//            }
//        };
//        Result result = procBaseServiceImpl.queryCountListProcUnfinishedProcStatus(queryCondition);
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//    /**
//     * queryCountProcByStatus()
//     */
//    @Test
//    public void queryCountProcByStatus(){
//        ProcBaseReq procBaseReq = new ProcBaseReq();
//        new Expectations() {
//            {
//                procBaseDao.queryCountProcByStatus((ProcBaseReq) any);
//                result = 1;
//            }
//        };
//        Result result = procBaseServiceImpl.queryCountProcByStatus(procBaseReq);
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//    /**
//     * queryCountProcByStatus()
//     */
//    @Test
//    public void queryCountProcByGroup(){
//        ProcBaseReq procBaseReq = new ProcBaseReq();
//        List<Map<String,Object>> maps = new ArrayList<>();
//        new Expectations() {
//            {
//                procBaseDao.queryCountProcByGroup((ProcBaseReq) any);
//                result = maps;
//            }
//        };
//        Result result = procBaseServiceImpl.queryCountProcByGroup(procBaseReq);
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//    /**
//     * queryListGroupByDeviceType()
//     */
//    @Test
//    public void queryListGroupByDeviceType(){
//        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
//        List<Map<String,Object>> maps = new ArrayList<>();
//        Map<String,Object> map = new HashMap<>();
//        map.put("001","222222222");
//        maps.add(map);
//        new Expectations() {
//            {
//                procBaseDao.queryListGroupByDeviceType((ProcBaseReq) any);
//                result = maps;
//            }
//        };
//        Result result = procBaseServiceImpl.queryListGroupByDeviceType(queryCondition);
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//
//    /**
//     * queryCountListProcHisProc()
//     */
//    @Test
//    public void queryCountListProcHisProc(){
//        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
//        new Expectations() {
//            {
//                procBaseDao.queryCountListProcHisProc(queryCondition);
//                result = 1;
//            }
//        };
//        Result result = procBaseServiceImpl.queryCountListProcHisProc(queryCondition);
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//    /**
//     * saveProcRelate()
//     */
//    @Test
//    public void saveProcRelate(){
//        ProcessInfo processInfo = new ProcessInfo();
//        List<ProcRelatedDepartment> procRelatedDepartments = new ArrayList<>();
//        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
//        procRelatedDepartments.add(procRelatedDepartment);
//        processInfo.setProcRelatedDepartments(procRelatedDepartments);
//
//        List<ProcRelatedDevice> procRelatedDevices = new ArrayList<>();
//        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
//        procRelatedDevices.add(procRelatedDevice);
//        processInfo.setProcRelatedDevices(procRelatedDevices);
//
//        new Expectations() {
//            {
//                procBaseDao.deleteProcRelateDeviceByProcId((ProcBase) any);
//                result = 1;
//                procBaseDao.addProcRelateDevice((ProcessInfo) any);
//                result = 1;
//
//                procBaseDao.deleteProcRelateDeptByProcId((ProcBase) any);
//                result = 1;
//                procBaseDao.addProcRelateDept((ProcessInfo) any);
//                result = 1;
//            }
//        };
//        procBaseServiceImpl.saveProcRelate(processInfo);
//        Assert.assertTrue(true);
//    }
//
//    /**
//     * queryProcExitsForIds()
//     */
//    @Test
//    public void queryProcExitsForIds(){
//        List<String> ids = new ArrayList<>();
//        ids.add("dsafdsfdsafdafdsafd");
//        List<ProcRelatedDevice> procRelatedDevices = new ArrayList<>();
//        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
//        procRelatedDevice.setProcId("dsafdsafdsafd");
//        procRelatedDevice.setDeviceId("dsafdsfdsafdafdsafd");
//        procRelatedDevices.add(procRelatedDevice);
//        new Expectations() {
//            {
//                procBaseDao.queryProcRelateDevice((ProcBaseReq) any);
//                result = procRelatedDevices;
//            }
//        };
//
//        Result result = procBaseServiceImpl.queryProcExitsForIds(ids,"device");
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//
//        procRelatedDevice.setProcId("dsafdsafdsafd");
//        procRelatedDevice.setDeviceAreaId("dsafdsfdsafdafdsafd");
//        new Expectations() {
//            {
//                procBaseDao.queryProcRelateDevice((ProcBaseReq) any);
//                result = procRelatedDevices;
//            }
//        };
//
//        result = procBaseServiceImpl.queryProcExitsForIds(ids,"area");
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//}
//
//=======
//package com.fiberhome.filink.workflowbusinessserver.service.impl;
//
//import com.fiberhome.filink.bean.PageCondition;
//import com.fiberhome.filink.bean.QueryCondition;
//import com.fiberhome.filink.bean.Result;
//import com.fiberhome.filink.logapi.log.LogProcess;
//import com.fiberhome.filink.server_common.utils.I18nUtils;
//import com.fiberhome.filink.user_api.api.DepartmentFeign;
//import com.fiberhome.filink.user_api.bean.Department;
//import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
//import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcClearFailure;
//import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
//import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
//import com.fiberhome.filink.workflowbusinessserver.dao.procbase.ProcBaseDao;
//import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
//import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
//import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
//import com.fiberhome.filink.workflowbusinessserver.service.impl.procbase.ProcBaseServiceImpl;
//import com.fiberhome.filink.workflowbusinessserver.service.procclear.ProcClearFailureService;
//import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionRecordService;
//import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionService;
//import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
//import com.fiberhome.filink.workflowbusinessserver.utils.procbase.ProcBaseResultCode;
//import mockit.Expectations;
//import mockit.Injectable;
//import mockit.Mocked;
//import mockit.Tested;
//import mockit.integration.junit4.JMockit;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//@RunWith(JMockit.class)
//public class ProcBaseServiceTests {
//
//    /**
//     * 测试对象 ProcBaseServiceImpl
//     */
//    @Tested
//    private ProcBaseServiceImpl procBaseServiceImpl;
//
//    /**
//     * 自动注入 ProcBaseDao
//     */
//    @Injectable
//    private ProcBaseDao procBaseDao;
//
//    /**
//     * 自动注入 ProcClearFailureService
//     */
//    @Injectable
//    private ProcClearFailureService procClearFailureService;
//
//    /**
//     * 自动注入 ProcInspectionService
//     */
//    @Injectable
//    private ProcInspectionService procInspectionService;
//
//    /**
//     * 自动注入 ProcInspectionRecordService
//     */
//    @Injectable
//    private ProcInspectionRecordService procInspectionRecordService;
//
//    /**
//     * 自动注入 LogProcess
//     */
//    @Injectable
//    private LogProcess logProcess;
//
//    /**
//     * 自动注入 DepartmentFeign
//     */
//    @Injectable
//    private DepartmentFeign departmentFeign;
//
//    /**
//     * Mock RequestContextHolder
//     */
//    @Mocked
//    private RequestContextHolder requestContextHolder;
//    /**
//     * Mock ServletRequestAttributes
//     */
//    @Mocked
//    private ServletRequestAttributes servletRequestAttributes;
//
//
//    /**
//     * Mock I18nUtils
//     */
//    @Mocked
//    private I18nUtils I18nUtils;
//
//    /**
//     * Mock ProcBaseResultCode
//     */
//    @Mocked
//    private ProcBaseResultCode procBaseResultCode;
//
//    /**
//     * 初始化
//     */
//    @Before
//    public void setUp() {
//    }
//
//    /**
//     * queryTitleIsExists()
//     */
//    @Test
//    public void queryTitleIsExists() {
//        ProcBase procbase = new ProcBase();
//        procbase.setProcId("fdsfadfsafdsa");
//        procbase.setTitle("fdsafasfdasfdsaffsad");
//        new Expectations() {
//            {
//                procBaseDao.queryTitleIsExists(anyString);
//                result = procbase;
//            }
//        };
//        //返回值
//        Boolean b = procBaseServiceImpl.queryTitleIsExists("fdsfadfsafdsa","fdsafasfdasfdsaffsad");
//        Assert.assertTrue(b == false);
//
//        procbase.setProcId("fdsfadfsafdsa");
//        procbase.setTitle("fdsafasfdasfdsaffsad");
//        new Expectations() {
//            {
//                procBaseDao.queryTitleIsExists(anyString);
//                result = procbase;
//            }
//        };
//        //返回值
//        b = procBaseServiceImpl.queryTitleIsExists(null,"fdsafasfdasfdsaffsad");
//        Assert.assertTrue(b == true);
//
//        procbase.setProcId("fdsafdsad");
//        procbase.setTitle("fdsafasfdasfdsaffsad");
//        new Expectations() {
//            {
//                procBaseDao.queryTitleIsExists(anyString);
//                result = procbase;
//            }
//        };
//        //返回值
//        b = procBaseServiceImpl.queryTitleIsExists("fdsfadfsafdsa","fdsafasfdasfdsaffsad");
//        Assert.assertTrue(b == true);
//    }
//
//    /**
//     * addProcBase()
//     */
//    @Test
//    public void addProcBase() {
//
//        ProcessInfo processInfo = new ProcessInfo();
//
//        ProcBaseReq procBaseReq = new ProcBaseReq();
//        procBaseReq.setTitle("测试工单110");
//        procBaseReq.setProcType("clear_failure");
//        procBaseReq.setRemark("测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109");
//        procBaseReq.setRefAlarm("3");
//        procBaseReq.setProcResourceType("1");
//        processInfo.setProcBaseReq(procBaseReq);
//
//        ProcBase procbase = new ProcBase();
//        procbase.setTitle("测试工单110");
//        procbase.setProcType("clear_failure");
//        procbase.setRemark("测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109");
//        procbase.setRefAlarm("3");
//        procbase.setProcResourceType("1");
//        processInfo.setProcBase(procbase);
//
//        ProcClearFailure procClearFailure = new ProcClearFailure();
//        processInfo.setProcClearFailure(procClearFailure);
//
//        List<ProcRelatedDevice> procBaseServices = new ArrayList<>();
//        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
//        procBaseServices.add(procRelatedDevice);
//        processInfo.setProcRelatedDevices(procBaseServices);
//
//        List<ProcRelatedDepartment> procRelatedDepartments = new ArrayList<>();
//        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
//        procRelatedDepartments.add(procRelatedDepartment);
//        processInfo.setProcRelatedDepartments(procRelatedDepartments);
//
//        new Expectations() {
//            {
//                //保存主表信息
//                procBaseDao.addProcBase((ProcBaseReq) any);
//                result = 1;
//            }
//        };
//
//        //返回值
//        Result s = procBaseServiceImpl.addProcBase(processInfo);
//        Assert.assertTrue(s.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//
//    /**
//     * addProcRelate()
//     */
//    @Test
//    public void addProcRelate() {
//
//        ProcessInfo processInfo = new ProcessInfo();
//        ProcBase procbase = new ProcBase();
//
//        List<ProcRelatedDevice> deviceCounts = new ArrayList<>();
//        List<ProcRelatedDepartment> unitCounts = new ArrayList<>();
//        processInfo.setProcBase(procbase);
//
//        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
//        deviceCounts.add(procRelatedDevice);
//        processInfo.setProcRelatedDevices(deviceCounts);
//
//        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
//        unitCounts.add(procRelatedDepartment);
//        processInfo.setProcRelatedDepartments(unitCounts);
//
//        new Expectations() {
//            {
//                // 修改工单关联设施信息
//                procBaseDao.deleteProcRelateDeviceByProcId((ProcBase) any);
//                result = 1;
//                procBaseDao.addProcRelateDevice((ProcessInfo) any);
//                result = 1;
//
//                // 保存工单关联部门信息
//                procBaseDao.deleteProcRelateDeptByProcId((ProcBaseReq) any);
//                result = 1;
//                procBaseDao.addProcRelateDept((ProcessInfo) any);
//                result = 1;
//            }
//        };
//
//        //返回值
//        procBaseServiceImpl.saveProcRelate(processInfo);
//        Assert.assertTrue(true);
//    }
//
//
//    /**
//     * updateProcessById()
//     */
//    @Test
//    public void updateProcessById() {
//
//        Date date = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            date = sdf.parse("2019-03-04 10:53:49");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        ProcessInfo processInfo = new ProcessInfo();
//        ProcBase procbase = new ProcBase();
//        procbase.setProcId("fdsafdsafdsafds");
//        procbase.setTitle("测试工单110");
//        procbase.setProcType("clear_failure");
//        procbase.setRemark("测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109测试工单109");
//        procbase.setRefAlarm("3");
//        procbase.setProcResourceType("1");
//        processInfo.setProcBase(procbase);
//
//        ProcBaseResp procBaseResp = new ProcBaseResp();
//        procBaseResp.setProcId("fdsafdsafdsafds");
//        processInfo.setProcBaseResp(procBaseResp);
//
//        List<ProcRelatedDevice> procBaseServices = new ArrayList<>();
//        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
//        procBaseServices.add(procRelatedDevice);
//        processInfo.setProcRelatedDevices(procBaseServices);
//
//        List<ProcRelatedDepartment> procRelatedDepartments = new ArrayList<>();
//        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
//        procRelatedDepartments.add(procRelatedDepartment);
//        processInfo.setProcRelatedDepartments(procRelatedDepartments);
//
//        //预言不能使用具体值，只能使用泛型，不然会拿不到值
//        new Expectations() {
//            {
//                //获取工单基础信息
//                procBaseDao.queryProcessByProcId(anyString);
//                result = procBaseResp;
//
//                // 修改基础信息
//                procBaseDao.updateProcBaseById((ProcBaseReq) any);
//                result = 1;
//            }
//        };
//
//        //返回值
//        Result s = procBaseServiceImpl.updateProcessById(processInfo);
//        Assert.assertTrue(s.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//
//    /**
//     * deleteProcessByIds()
//     */
//    @Test
//    public void deleteProcessByIds() {
//
//        ProcBase procbase = new ProcBase();
//        procbase.setProcId("fdsafdsafdsafds");
//        procbase.setStatus("assigned");
//
//        List<ProcBase> procBases = new ArrayList<>();
//        procBases.add(procbase);
//
//        List<String> ids = new ArrayList<>();
//        ids.add("fdsafdsafdsafds");
//        ids.add("fdsafdsafdsafds");
//
//        //此预言只能测同一个方法，方法内引用方法只能分开测
//        new Expectations() {
//            {
//                //获取工单基础信息
//                procBaseDao.queryProcessByProcId(anyString);
//                result = procbase;
//
//                // 删除基础信息
////                procBaseDao.updateProcBaseIsDeletedById((ProcBaseReq) any);
//                result = 1;
//            }
//        };
//
//        //返回值
//        Result s = procBaseServiceImpl.updateProcessIsDeletedByIds(ids,"1");
//        Assert.assertTrue(s.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//    /**
//     * deleteProcRelate()
//     */
//    @Test
//    public void deleteProcRelate(){
//
//        ProcBase procbase = new ProcBase();
//
//        List<ProcRelatedDevice> deviceCounts = new ArrayList<>();
//        List<ProcRelatedDepartment> unitCounts = new ArrayList<>();
//
//        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
//        deviceCounts.add(procRelatedDevice);
//
//        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
//        unitCounts.add(procRelatedDepartment);
//
//        new Expectations() {
//            {
//                // 获取工单关联设施信息
//                procBaseDao.queryProcRelateDevice((ProcBaseReq) any);
//                result = deviceCounts;
//
//                // 删除工单关联设施信息
//                procBaseDao.updateProcRelateDeviceIsDeletedByProcId((ProcBase) any);
//                result = 1;
//
//                //获取工单关联部门信息
//                procBaseDao.queryProcRelateDept((ProcBaseReq) any);
//                result = unitCounts;
//
//                // 删除工单关联部门信息
//                procBaseDao.updateProcRelateDeptIsDeletedByProcId((ProcBase) any);
//                result = 1;
//            }
//        };
//
//        //返回值
////        procBaseServiceImpl.updateProcRelateIsDeleted(procbase);
//        Assert.assertTrue(true);
//    }
//
//
//    /**
//     * getProcessByProcId()
//     */
//    @Test
//    public void getProcessByProcId() {
//
//        ProcessInfo processInfo = new ProcessInfo();
//        ProcBaseReq procBaseReq = new ProcBaseReq();
//        ProcBaseResp procBaseResp = new ProcBaseResp();
//        procBaseResp.setProcId("fdsafdsafdsafdsafdsa");
//        ProcBase procbase = new ProcBase();
//        procBaseReq.setProcId("fdsafdsafdsafdsafdsa");
//        processInfo.setProcBaseReq(procBaseReq);
//        processInfo.setProcBase(procbase);
//        processInfo.setProcBaseResp(procBaseResp);
//
//        new Expectations() {
//            {
//                //获取工单基础信息
//                procBaseDao.queryProcessByProcId(anyString);
//                result = procBaseResp;
//            }
//        };
//
//        //返回值
//        Result s = procBaseServiceImpl.queryProcessById("000d03f33fdf11e99b3adc5360c0ce1f");
//        Assert.assertTrue(s.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//    /**
//     * queryProcRelate()
//     */
//    @Test
//    public void queryProcRelate() {
//
//        ProcessInfo processInfo = new ProcessInfo();
//        ProcBaseReq procBaseReq = new ProcBaseReq();
//        ProcBaseResp procBaseResp = new ProcBaseResp();
//        ProcBase procbase = new ProcBase();
//
//        List<ProcRelatedDevice> deviceCounts = new ArrayList<>();
//        List<ProcRelatedDepartment> unitCounts = new ArrayList<>();
//        List<Department> departmentList = new ArrayList<>();
//
//        procBaseReq.setProcId("b9d27a0d3e2811e9b61adc5360c0ce1f");
//        procBaseReq.setProcType("clear_failure");
//        processInfo.setProcBaseReq(procBaseReq);
//        processInfo.setProcBase(procbase);
//        processInfo.setProcBaseResp(procBaseResp);
//        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
//        deviceCounts.add(procRelatedDevice);
//
//        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
//        unitCounts.add(procRelatedDepartment);
//
//        new Expectations() {
//            {
//                //获取工单关联设施信息
//                procBaseDao.queryProcRelateDevice(processInfo.getProcBaseReq());
//                result = deviceCounts;
//
//                //获取工单关联部门信息
//                procBaseDao.queryProcRelateDept(processInfo.getProcBaseReq());
//                result = unitCounts;
//            }
//        };
//
////        procBaseServiceImpl.queryProcRelate(processInfo,departmentList, ProcBaseConstants.PROC_METHOD_DETAIL);
////        Assert.assertTrue(true);
//    }
//
//
//    /**
//     * queryListProcUnfinishedProcByPage()
//     */
//    @Test
//    public void queryListProcUnfinishedProcByPage(){
//        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
//
//        List<ProcBase> procBaselist = new ArrayList<>();
//        List<ProcBaseResp> resultList = new ArrayList<>();
//        ProcBaseReq procBaseReq = new ProcBaseReq();
//        queryCondition.setBizCondition(procBaseReq);
//        ProcBase procbase = new ProcBase();
//        procBaselist.add(procbase);
//
//        PageCondition pageCondition = new PageCondition();
//        pageCondition.setPageNum(1);
//        pageCondition.setPageSize(200);
//        queryCondition.setPageCondition(pageCondition);
//
//        new Expectations() {
//            {
//                procBaseDao.queryCountListProcUnfinishedProc((QueryCondition<ProcBaseReq>) any);
//                result = 1;
//
//                procBaseDao.queryListProcUnfinishedProcByPage((QueryCondition<ProcBaseReq>) any);
//                result = procBaselist;
//            }
//        };
//        Result result = procBaseServiceImpl.queryListProcUnfinishedProcByPage(queryCondition);
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//    /**
//     * queryListProcHisProcByPage()
//     */
//    @Test
//    public void queryListProcHisProcByPage(){
//        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
//
//        ProcessInfo processInfo = new ProcessInfo();
//        List<ProcRelatedDepartment> procRelatedDepartments = new ArrayList<>();
//        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
//        procRelatedDepartments.add(procRelatedDepartment);
//        processInfo.setProcRelatedDepartments(procRelatedDepartments);
//
//        List<ProcRelatedDevice> procRelatedDevices = new ArrayList<>();
//        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
//        procRelatedDevices.add(procRelatedDevice);
//        processInfo.setProcRelatedDevices(procRelatedDevices);
//
//        List<Department> departmentList = new ArrayList<>();
//        Department department = new Department();
//        departmentList.add(department);
//
//        List<ProcBase> procBaselist = new ArrayList<>();
//        List<ProcBaseResp> resultList = new ArrayList<>();
//        ProcBaseReq procBaseReq = new ProcBaseReq();
//        queryCondition.setBizCondition(procBaseReq);
//        ProcBase procbase = new ProcBase();
//        procBaselist.add(procbase);
//
//        ProcBaseResp procBaseResp = new ProcBaseResp();
//        resultList.add(procBaseResp);
//
//        PageCondition pageCondition = new PageCondition();
//        pageCondition.setPageNum(1);
//        pageCondition.setPageSize(200);
//        queryCondition.setPageCondition(pageCondition);
//
//        new Expectations() {
//            {
//                procBaseDao.queryCountListProcHisProc((QueryCondition<ProcBaseReq>) any);
//                result = 1;
//
//                procBaseDao.queryListProcHisProcByPage((QueryCondition<ProcBaseReq>) any);
//                result = resultList;
//
//            }
//        };
//        Result result = procBaseServiceImpl.queryListProcHisProcByPage(queryCondition);
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//    /**
//     * queryProcSpecific()
//     */
//    @Test
//    public void queryProcSpecific(){
//        ProcessInfo processInfo = new ProcessInfo();
//        ProcBase procbase = new ProcBase();
//        procbase.setProcType("clear_failure");
//        processInfo.setProcBase(procbase);
//        new Expectations() {
//            {
//                //获取工单特性信息
////                procClearFailureService.queryProcClearFailureSpecific(anyString);
//            }
//        };
//
////        procBaseServiceImpl.queryProcSpecific(processInfo);
////        Assert.assertTrue(true);
//    }
//
//    /**
//     * deleteProcSpecific()
//     */
//    @Test
//    public void deleteProcSpecific(){
//        ProcBase procbase = new ProcBase();
//        procbase.setProcType("clear_failure");
//        new Expectations() {
//            {
//                //删除工单特性信息
//                procClearFailureService.updateProcClearFailureSpecificIsDeleted(anyString,"1");
//            }
//        };
//
////        procBaseServiceImpl.updateProcSpecificIsDeletd(procbase);
//        Assert.assertTrue(true);
//    }
//
//    /**
//     * queryCountListProcUnfinishedProcStatus()
//     */
//    @Test
//    public void queryCountListProcUnfinishedProcStatus(){
//        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
//        new Expectations() {
//            {
//                procBaseDao.queryCountListProcUnfinishedProc(queryCondition);
//                result = 1;
//            }
//        };
//        Result result = procBaseServiceImpl.queryCountListProcUnfinishedProcStatus(queryCondition);
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//    /**
//     * queryCountProcByStatus()
//     */
//    @Test
//    public void queryCountProcByStatus(){
//        ProcBaseReq procBaseReq = new ProcBaseReq();
//        new Expectations() {
//            {
//                procBaseDao.queryCountProcByStatus((ProcBaseReq) any);
//                result = 1;
//            }
//        };
//        Result result = procBaseServiceImpl.queryCountProcByStatus(procBaseReq);
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//    /**
//     * queryCountProcByStatus()
//     */
//    @Test
//    public void queryCountProcByGroup(){
//        ProcBaseReq procBaseReq = new ProcBaseReq();
//        List<Map<String,Object>> maps = new ArrayList<>();
//        new Expectations() {
//            {
//                procBaseDao.queryCountProcByGroup((ProcBaseReq) any);
//                result = maps;
//            }
//        };
//        Result result = procBaseServiceImpl.queryCountProcByGroup(procBaseReq);
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//    /**
//     * queryListGroupByDeviceType()
//     */
//    @Test
//    public void queryListGroupByDeviceType(){
//        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
//        List<Map<String,Object>> maps = new ArrayList<>();
//        Map<String,Object> map = new HashMap<>();
//        map.put("001","222222222");
//        maps.add(map);
//        new Expectations() {
//            {
//                procBaseDao.queryListGroupByDeviceType((ProcBaseReq) any);
//                result = maps;
//            }
//        };
//        Result result = procBaseServiceImpl.queryListGroupByDeviceType(queryCondition);
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//
//    /**
//     * queryCountListProcHisProc()
//     */
//    @Test
//    public void queryCountListProcHisProc(){
//        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
//        new Expectations() {
//            {
//                procBaseDao.queryCountListProcHisProc(queryCondition);
//                result = 1;
//            }
//        };
//        Result result = procBaseServiceImpl.queryCountListProcHisProc(queryCondition);
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//
//    /**
//     * saveProcRelate()
//     */
//    @Test
//    public void saveProcRelate(){
//        ProcessInfo processInfo = new ProcessInfo();
//        List<ProcRelatedDepartment> procRelatedDepartments = new ArrayList<>();
//        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
//        procRelatedDepartments.add(procRelatedDepartment);
//        processInfo.setProcRelatedDepartments(procRelatedDepartments);
//
//        List<ProcRelatedDevice> procRelatedDevices = new ArrayList<>();
//        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
//        procRelatedDevices.add(procRelatedDevice);
//        processInfo.setProcRelatedDevices(procRelatedDevices);
//
//        new Expectations() {
//            {
//                procBaseDao.deleteProcRelateDeviceByProcId((ProcBase) any);
//                result = 1;
//                procBaseDao.addProcRelateDevice((ProcessInfo) any);
//                result = 1;
//
//                procBaseDao.deleteProcRelateDeptByProcId((ProcBase) any);
//                result = 1;
//                procBaseDao.addProcRelateDept((ProcessInfo) any);
//                result = 1;
//            }
//        };
//        procBaseServiceImpl.saveProcRelate(processInfo);
//        Assert.assertTrue(true);
//    }
//
//    /**
//     * queryProcExitsForIds()
//     */
//    @Test
//    public void queryProcExitsForIds(){
//        List<String> ids = new ArrayList<>();
//        ids.add("dsafdsfdsafdafdsafd");
//        List<ProcRelatedDevice> procRelatedDevices = new ArrayList<>();
//        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
//        procRelatedDevice.setProcId("dsafdsafdsafd");
//        procRelatedDevice.setDeviceId("dsafdsfdsafdafdsafd");
//        procRelatedDevices.add(procRelatedDevice);
//        new Expectations() {
//            {
//                procBaseDao.queryProcRelateDevice((ProcBaseReq) any);
//                result = procRelatedDevices;
//            }
//        };
//
//        Result result = procBaseServiceImpl.queryProcExitsForIds(ids,"device");
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//
//        procRelatedDevice.setProcId("dsafdsafdsafd");
//        procRelatedDevice.setDeviceAreaId("dsafdsfdsafdafdsafd");
//        new Expectations() {
//            {
//                procBaseDao.queryProcRelateDevice((ProcBaseReq) any);
//                result = procRelatedDevices;
//            }
//        };
//
//        result = procBaseServiceImpl.queryProcExitsForIds(ids,"area");
//        Assert.assertTrue(result.getCode() == ProcBaseResultCode.SUCCESS);
//    }
//}
//
//>>>>>>> .r14867
