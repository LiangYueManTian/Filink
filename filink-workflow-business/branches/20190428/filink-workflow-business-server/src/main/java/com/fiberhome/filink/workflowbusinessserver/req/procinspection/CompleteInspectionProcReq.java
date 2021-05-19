package com.fiberhome.filink.workflowbusinessserver.req.procinspection;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionI18n;
import com.fiberhome.filink.workflowbusinessserver.utils.procinspection.ProcInspectionResultCode;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 完成巡检工单
 * @author hedongwei@wistronits.com
 * @date 2019/3/23 12:20
 */
@Data
public class CompleteInspectionProcReq {

    /**
     * 用户编号
     */
    private String userId;

    /**
     * 流程编号
     */
    private String procId;

    /**
     * 操作
     */
    private String operate;

    /**
     * 流程巡检任务记录
     */
    private List<ProcInspectionRecordReq> procInspectionRecords;

    /**
     *
     * @author hedongwei@wistronits.com
     * @date  2019/3/27 17:57
     * @param req 查询完成
     */
    public static Result checkCompleteInspectionProc(CompleteInspectionProcReq req) {
        //办理用户不能为空
        String userId =  req.getUserId();
        if (ObjectUtils.isEmpty(userId)) {
            return ResultUtils.warn(ProcInspectionResultCode.USER_ID_IS_EMPTY, I18nUtils.getString(ProcInspectionI18n.USER_ID_IS_EMPTY));
        }

        return null;
    }
}


