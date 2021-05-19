package com.fiberhome.filink.workflowbusinessserver.controller.procbase;


import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedAlarm;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.*;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcBaseService;
import com.fiberhome.filink.workflowbusinessserver.service.procclear.ProcClearFailureService;
import com.fiberhome.filink.workflowbusinessserver.service.procnotice.ProcNoticeService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 工单控制层测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/3 20:23
 */
@RunWith(JMockit.class)
public class ProcBaseControllerTest {

    @Tested
    private ProcBaseController procBaseController;

    /**
     * 工单逻辑层
     */
    @Injectable
    private ProcBaseService procBaseService;

    /**
     * 工单提醒逻辑层
     */
    @Injectable
    private ProcNoticeService procNoticeService;

    /**
     * 销障工单逻辑层
     */
    @Injectable
    private ProcClearFailureService procClearFailureService;


    /**
     * 查询工单标题是否重复测试方法
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 14:45
     */
    @Test
    public void queryTitleIsExists() {
        ProcBase procBase = new ProcBase();
        procBase.setProcId("1");
        procBase.setProcType("inspection");
        procBase.setAccountabilityDeptName("");
        procBase.setProcResourceType("");
        procBase.setExpectedCompletedTime(new Date());
        procBase.setTitle("工单测试信息");

        new Expectations() {
            {
                procBaseService.queryTitleIsExists(anyString, anyString);
                boolean retFlag = true;
                result = retFlag;
            }
        };


        //查询工单名称是否存在
        procBaseController.queryTitleIsExists(procBase);

        ProcBase procBaseParam = new ProcBase();
        procBaseParam.setProcId("1");
        procBaseParam.setTitle("工单测试信息");
        new Expectations() {
            {
                procBaseService.queryTitleIsExists(anyString, anyString);
                boolean retFlag = false;
                result = retFlag;
            }
        };
        //查询工单名称是否存在
        procBaseController.queryTitleIsExists(procBaseParam);

        ProcBase procBaseParamInfo = new ProcBase();
        //查询工单名称是否存在
        procBaseController.queryTitleIsExists(procBaseParamInfo);
    }

    /**
     * 删除工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 14:56
     */
    @Test
    public void deleteProc() {
        List<String> procIdList = new ArrayList<>();
        procIdList.add("1");
        procBaseController.deleteProc(procIdList);
    }

    /**
     * 恢复工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 14:56
     */
    @Test
    public void recoverProc() {
        List<String> procIdList = new ArrayList<>();
        procIdList.add("1");
        procBaseController.recoverProc(procIdList);
    }

    /**
     * 恢复工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 14:56
     */
    @Test
    public void updateProcBaseStatusById() {
        ProcBase procBase = new ProcBase();
        procBase.setTitle("工单标题");
        procBaseController.updateProcBaseStatusById(procBase);
    }

    /**
     * 查看工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 14:56
     */
    @Test
    public void getProcessByProcId() {
        String procId = "status";
        procBaseController.getProcessByProcId(procId);
    }

    /**
     * 根据工单id获取工单类型
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 14:56
     */
    @Test
    public void getProcTypeByProcId() {
        String procId = "status";
        procBaseController.getProcTypeByProcId(procId);
    }

    /**
     * 查询设施ids是否有工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 14:56
     */
    @Test
    public void queryProcExitsForDeviceIds() {
        List<String> deviceIds = new ArrayList<>();
        deviceIds.add("1");
        procBaseController.queryProcExitsForDeviceIds(deviceIds);
    }

    /**
     * 查询区域ids是否有工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 14:56
     */
    @Test
    public void queryProcExitsForAreaIds() {
        List<String> areaIds = new ArrayList<>();
        areaIds.add("1");
        procBaseController.queryProcExitsForAreaIds(areaIds);
    }

    /**
     * 下载工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 14:56
     */
    @Test
    public void updateProcByUserForApp() {
        UpdateProcByUserReq req = new UpdateProcByUserReq();
        procBaseController.updateProcByUserForApp(req);
    }

    /**
     * 通知下载人员
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 14:56
     */
    @Test
    public void noticeDownloadUser() {
        NoticeDownloadUserReq req = new NoticeDownloadUserReq();
        procBaseController.noticeDownloadUser(req);
    }


    /**
     * 指派工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 14:56
     */
    @Test
    public void assignProc() {
        AssignProcReq req = new AssignProcReq();
        procBaseController.assignProc(req);
    }

    /**
     * app转派工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 14:56
     */
    @Test
    public void turnProcForApp() {
        TurnProcReq req = new TurnProcReq();
        procBaseController.turnProcForApp(req);
    }

    /**
     * 退单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:12
     */
    @Test
    public void singleBackProcForApp() {
        SingleBackProcReq req = new SingleBackProcReq();
        procBaseController.singleBackProcForApp(req);
    }

    /**
     * 撤销单据
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:12
     */
    @Test
    public void revokeProc() {
        RevokeProcReq req = new RevokeProcReq();
        procBaseController.revokeProc(req);
    }

    /**
     * 退单确认
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:12
     */
    @Test
    public void checkSingleBack() {
        CheckSingleBackReq req = new CheckSingleBackReq();
        procBaseController.checkSingleBack(req);
    }

    /**
     * 获取转派用户
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:12
     */
    @Test
    public void getTurnUserForApp() {
        GetTurnUserReq req = new GetTurnUserReq();
        procBaseController.getTurnUserForApp(req);
    }

    /**
     * 工单关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:12
     */
    @Test
    public void procRelationDeviceList() {
        ProcRelationDeviceListReq req = new ProcRelationDeviceListReq();
        procBaseController.procRelationDeviceList(req);
    }

    /**
     * 查询工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:12
     */
    @Test
    public void queryProcBaseInfoList() {
        ProcBase req = new ProcBase();
        procBaseController.queryProcBaseInfoList(req);
    }


    /**
     * 查询关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:12
     */
    @Test
    public void queryProcRelateDeviceByProcIds() {
        List<String> procIds = new ArrayList<>();
        procIds.add("1");
        procBaseController.queryProcRelateDeviceByProcIds(procIds);
    }


    /**
     * 查询关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:12
     */
    @Test
    public void queryProcRelateDeptByProcIds() {
        List<String> procIds = new ArrayList<>();
        procIds.add("1");
        procBaseController.queryProcRelateDeptByProcIds(procIds);
    }


    /**
     * 新增告警id及工单id
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:12
     */
    @Test
    public void addProcRelatedAlarm() {
        ProcRelatedAlarm procRelatedAlarm = new ProcRelatedAlarm();
        procBaseController.addProcRelatedAlarm(procRelatedAlarm);
    }

    /**
     * app下载工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:12
     */
    @Test
    public void downLoadProcForApp() {
        ProcBaseReq req = new ProcBaseReq();
        procBaseController.downLoadProcForApp(req);
    }


    /**
     * 根据告警查询存在的工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:12
     */
    @Test
    public void queryExistsProcForAlarmList() {
        List<String> alarmProcList = new ArrayList<>();
        alarmProcList.add("1");
        procBaseController.queryExistsProcForAlarmList(alarmProcList);

        List<String> alarmProcListInfo = null;
        procBaseController.queryExistsProcForAlarmList(alarmProcListInfo);
    }


    /**
     * 根据设施删除工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:12
     */
    @Test
    public void deleteProcBaseForDeviceList() {
        DeleteProcBaseForDeviceReq req = new DeleteProcBaseForDeviceReq();
        procBaseController.deleteProcBaseForDeviceList(req);
    }


    /**
     * 校验部门有无关联工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:12
     */
    @Test
    public void queryProcIdListByDeptIds() {
        List<String> deptIds = new ArrayList<>();
        deptIds.add("1");
        procBaseController.queryProcIdListByDeptIds(deptIds);
    }


    /**
     * 查询是否存在正在办理工单的用户
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:12
     */
    @Test
    public void queryIsExistsAssignUser() {
        List<String> userIdList = new ArrayList<>();
        userIdList.add("1");
        procBaseController.queryIsExistsAssignUser(userIdList);
    }





}