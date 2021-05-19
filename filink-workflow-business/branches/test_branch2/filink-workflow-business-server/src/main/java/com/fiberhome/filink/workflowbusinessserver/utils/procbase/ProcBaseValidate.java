package com.fiberhome.filink.workflowbusinessserver.utils.procbase;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.utils.common.ValidateUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * @author hedongwei@wistronits.com
 * @date 2019/4/1 16:40
 */

public class ProcBaseValidate {

    /**
     * 校验工单编号
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 20:05
     * @param procId 工单编号
     * @return 校验工单编号
     */
    public static boolean checkProcId(String procId) {
        //巡检工单编号
        String procInspectionId = procId;
        if (!ValidateUtils.validateDataInfoIsEmpty(procInspectionId)) {
            return false;
        }

        //工单编号字段长度
        Integer procIdMinLength = 6;
        Integer procIdMaxLength = 32;
        if (!ValidateUtils.validateDataLength(procId, procIdMinLength, procIdMaxLength)) {
            //工单编号参数不正确
            return false;
        }
        return true;
    }

    /**
     * 校验工单是否能被删除
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 13:16
     * @param ids 工单编号
     * @param index 当前循环的下标
     * @param procBaseMap 工单map
     * @param isDeleted 是否删除
     * @return 工单校验结果
     */
    public static Result checkProcIsAbleDelete(List<String> ids, int index, Map<String, ProcBase> procBaseMap, String isDeleted) {
        //判断是否缺少工单id
        if (StringUtils.isEmpty(ids.get(index))){
            return ResultUtils.warn(ProcBaseResultCode.PROC_ID_LOSE,I18nUtils.getSystemString(ProcBaseI18n.PROC_ID_LOSE));
        }
        //获取工单基础信息
        ProcBase procBase = null;
        if (!ObjectUtils.isEmpty(procBaseMap)) {
            //获取单个工单的详细信息
            procBase = procBaseMap.get(ids.get(index));
        }
        if (ObjectUtils.isEmpty(procBase) || StringUtils.isEmpty(procBase.getProcId())){
            //提示存在已删除的工单
            return ResultUtils.warn(ProcBaseResultCode.EXIST_IS_DELETED_PROC_DATA,I18nUtils.getSystemString(ProcBaseI18n.EXIST_IS_DELETED_PROC_DATA));
        }

        //只有待指派的工单能够删除
        Result checkForStatusResult = ProcBaseValidate.checkProcBaseIsAbleDeleteForStatus(isDeleted, procBase);
        if (null != checkForStatusResult) {
            return checkForStatusResult;
        }
        return null;
    }

    /**
     * 根据状态检查工单是否能够被删除
     * @author hedongwei@wistronits.com
     * @date  2019/5/14 9:27
     * @param isDeleted 是否删除
     * @param procBase 工单
     * @return 返回提示信息
     */
    public static Result checkProcBaseIsAbleDeleteForStatus(String isDeleted, ProcBase procBase) {
        if (ProcBaseConstants.IS_DELETED_1.equals(isDeleted)){
            if (!ObjectUtils.isEmpty(procBase) && !StringUtils.isEmpty(procBase.getProcId())){
                if (!ProcBaseConstants.PROC_STATUS_ASSIGNED.equals(procBase.getStatus())){
                    return ResultUtils.warn(ProcBaseResultCode.PROC_NOT_DELETE,I18nUtils.getSystemString(ProcBaseI18n.PROC_NOT_DELETE));
                }
            }
        }
        return null;
    }
}
