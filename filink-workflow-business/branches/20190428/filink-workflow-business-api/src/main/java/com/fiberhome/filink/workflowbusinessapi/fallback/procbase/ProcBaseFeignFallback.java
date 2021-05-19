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
        String updateStatusMsg = "Update proc status error";
        log.info(INFO);
        return ResultUtils.warn(ProcBaseResultCode.UPDATE_STATUS_ERROR, updateStatusMsg);
    }

    @Override
    public Result deleteProc(List<ProcBase> procBases) {
        String updateStatusMsg = "Delete proc error";
        log.info(INFO);
        return ResultUtils.warn(ProcBaseResultCode.DELETE_PROC_ERROR, updateStatusMsg);
    }

    @Override
    public Result recoverProc(List<ProcBase> procBases) {
        String updateStatusMsg = "Recover proc error";
        log.info(INFO);
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
        log.info(INFO);
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
        log.info(INFO);
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
        log.info(INFO);
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
        log.info(INFO);
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
        log.info(INFO);
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
        log.info(INFO);
        String msg = "Notice download user error";
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
        log.info(INFO);
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
        log.info(INFO);
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
        log.info(INFO);
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
        log.info(INFO);
        return true;
    }
}
