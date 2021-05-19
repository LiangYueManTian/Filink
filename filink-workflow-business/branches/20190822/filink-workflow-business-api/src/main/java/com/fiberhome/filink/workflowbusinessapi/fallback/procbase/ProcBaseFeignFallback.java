package com.fiberhome.filink.workflowbusinessapi.fallback.procbase;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import com.fiberhome.filink.workflowbusinessapi.bean.procbase.*;
import com.fiberhome.filink.workflowbusinessapi.req.procbase.DeleteProcBaseForDeviceReq;
import com.fiberhome.filink.workflowbusinessapi.req.procbase.NoticeDownloadUserReq;
import com.fiberhome.filink.workflowbusinessapi.utils.procbase.ProcBaseResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;


/**
 * @author hedongwei@wistronits.com
 * ferign 熔断
 * 13:45 2019/1/19
 */
@Slf4j
@Component
public class ProcBaseFeignFallback implements ProcBaseFeign {

    private static final String INFO = "Proc feign call fallback";

    /**
     * 根据工单编号修改工单的状态
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param procBase 修改工单的类型
     * @return 修改工单的状态
     */
    @Override
    public Result updateProcBaseStatusById(ProcBase procBase) {
        String updateStatusMsg = ",Update proc status error";
        log.info(INFO + updateStatusMsg + ", procId is " + procBase.getProcId(), "procId");
        return ResultUtils.warn(ProcBaseResultCode.UPDATE_STATUS_ERROR, updateStatusMsg);
    }

    /**
     * 删除工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param procBases 删除工单的信息
     * @return 删除工单
     */
    @Override
    public Result deleteProc(List<ProcBase> procBases) {
        String updateStatusMsg = "Delete proc error";
        log.info(INFO + "," +  updateStatusMsg);
        return ResultUtils.warn(ProcBaseResultCode.DELETE_PROC_ERROR, updateStatusMsg);
    }

    /**
     * 恢复工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param procBases 恢复工单的信息
     * @return 恢复工单
     */
    @Override
    public Result recoverProc(List<ProcBase> procBases) {
        String updateStatusMsg = "Recover proc error";
        log.info(INFO + "," + updateStatusMsg);
        return ResultUtils.warn(ProcBaseResultCode.RECOVER_PROC_ERROR, updateStatusMsg);
    }

    /**
     * 查询流程信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/25 22:32
     * @param procBase 筛选条件
     * @return 查询流程信息
     */
    @Override
    public List<ProcBaseInfoBean> queryProcBaseInfoList(ProcBase procBase) {
        String queryProcBaseError = ",Query proc base info list error";
        log.info(INFO + queryProcBaseError);
        return null;
    }

    /**
     * 查询关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/27 12:56
     * @param procIds 流程编号
     * @return 返回关联设施信息
     */
    @Override
    public List<ProcRelatedDevice> queryProcRelateDeviceByProcIds(List<String> procIds) {
        String procError = ",query proc related device by procIds error";
        log.info(INFO + procError);
        return null;
    }

    /**
     * 查询关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/27 12:56
     * @param procIds 流程编号
     * @return 返回关联部门信息
     */
    @Override
    public List<ProcRelatedDepartment> queryProcRelateDeptByProcIds(List<String> procIds) {
        String procError = ",query proc related dept by procIds error";
        log.info(INFO + procError);
        return null;
    }

    /**
     * 查询设施ids是否有工单信息
     *
     * @since 2019-02-27
     * @param deviceIds 设施id列表
     * @return Result
     *
     */
    @Override
    public Result queryProcExitsForDeviceIds(List<String> deviceIds) {
        String deviceInfo = ",query proc exits for deviceIds error";
        log.info(INFO + deviceInfo);
        return null;
    }

    /**
     * 查询区域ids是否有工单信息
     *
     * @since 2019-02-27
     * @param areaIds 区域id列表
     * @return Result
     *
     */
    @Override
    public Result queryProcExitsForAreaIds(List<String> areaIds) {
        String areaInfo = ",query proc exits for areaIds error";
        log.info(INFO + areaInfo);
        return null;
    }

    /**
     * 通知下载人员
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 9:37
     * @param noticeDownloadUserReq 提醒下载用户参数
     * @return 通知下载人员
     */
    @Override
    public Result noticeDownloadUser(NoticeDownloadUserReq noticeDownloadUserReq) {
        String msg = "Notice download user error";
        log.info(INFO + "," + msg + ",procId is "+ noticeDownloadUserReq.getProcId(), "procId");
        return ResultUtils.warn(ProcBaseResultCode.NOTICE_DOWNLOAD_USER_ERROR, msg);
    }

    /**
     *  更新告警id及工单id
     *
     * @since 2019-04-18
     * @param procRelatedAlarm 工单关联告警
     *
     * @return Result
     *
     */
    @Override
    public Integer addProcRelatedAlarm(ProcRelatedAlarm procRelatedAlarm) {
        String msg = ",Add proc related alarm";
        log.info(INFO + msg + ",procId is" + procRelatedAlarm.getProcId(), "procId");
        return null;
    }

    /**
     * 根据告警查询存在的工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/19 12:49
     * @param alarmProcList 告警编号数组
     * @return 工单结果
     */
    @Override
    public Map<String, Object> queryExistsProcForAlarmList(List<String> alarmProcList) {
        String msg = ",Query exists proc for alarm list error";
        log.info(INFO + msg);
        return null;
    }

    /**
     * 删除工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 17:45
     * @param req 设施编号集合
     * @return 删除工单信息
     */
    @Override
    public Result deleteProcBaseForDeviceList(DeleteProcBaseForDeviceReq req) {
        throw new NullPointerException();
    }

    /**
     * 校验部门信息有无关联工单
     * @author chaofanrong@wistronits.com
     * @param deptIds 部门ids
     *
     * @date  2019/4/26 11:52
     *
     * @return 部门关联工单id
     */
    @Override
    public Object queryProcIdListByDeptIds(List<String> deptIds) {
        String msg = ",Query procId list by dpet id list error";
        log.info(INFO + msg);
        return null;
    }

    /**
     * 查询是否存在正在办理工单的用户
     * @author hedongwei@wistronits.com
     * @date  2019/5/6 10:39
     * @param userIdList 用户编号集合
     * @return 是否存在办理工单的用户
     */
    @Override
    public boolean queryIsExistsAssignUser(List<String> userIdList) {
        String msg = ",query is exists assign user error";
        log.info(INFO + msg);
        return true;
    }
}
