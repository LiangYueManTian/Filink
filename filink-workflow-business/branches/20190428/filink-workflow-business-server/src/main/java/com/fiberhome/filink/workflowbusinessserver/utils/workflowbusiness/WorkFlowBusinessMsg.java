package com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskI18n;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.utils.inspectiontask.InspectionTaskResultCode;
import com.fiberhome.filink.workflowbusinessserver.utils.procbase.ProcBaseResultCode;

import java.text.MessageFormat;

/**
 * 通用提示
 * @author hedongwei@wistronits.com
 * @date 2019/3/11 17:20
 */

public class WorkFlowBusinessMsg {

    /**
     * 参数异常提示
     * @author hedongwei@wistronits.com
     * @date  2019/3/6 17:09
     * @return 提示信息
     */
    public static Result paramErrorMsg() {
        //不满足参数条件返回提示信息
        //参数异常i18N的名称
        String paramError = I18nUtils.getString(InspectionTaskI18n.PARAM_ERROR);
        //参数异常的返回code
        Integer paramErrorResultCode = InspectionTaskResultCode.PARAM_ERROR;
        //不满足参数条件返回提示信息
        return ResultUtils.warn(paramErrorResultCode, paramError);
    }


    /**
     * 设置导出数据超长异常提示语
     *
     * @param fe 导出数据超长异常
     *
     * @param maxExportDataSize 最长导出长度
     *
     * @return Result
     */
    public static Result getExportToLargeMsg(FilinkExportDataTooLargeException fe,Integer  maxExportDataSize){
        fe.printStackTrace();
        String string = I18nUtils.getString(ProcBaseI18n.EXPORT_DATA_TOO_LARGE);
        String dataCount = fe.getMessage();
        Object[] params = {dataCount, maxExportDataSize};
        String msg = MessageFormat.format(string, params);
        return ResultUtils.warn(ProcBaseResultCode.EXPORT_DATA_TOO_LARGE, msg);
    }

}
