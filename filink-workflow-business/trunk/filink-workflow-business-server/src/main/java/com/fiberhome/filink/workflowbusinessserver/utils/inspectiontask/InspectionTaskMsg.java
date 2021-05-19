package com.fiberhome.filink.workflowbusinessserver.utils.inspectiontask;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskI18n;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkflowBusinessResultCode;

/**
 * 巡检任务提示信息
 * @author hedongwei@wistronits.com
 * @date 2019/3/6 17:12
 */

public class InspectionTaskMsg {

    /**
     * 存在开启的巡检任务数据
     * @author hedongwei@wistronits.com
     * @date  2019/3/6 17:09
     * @return 提示信息
     */
    public static Result existsOpenDataMsg() {
        //存在开启的巡检任务i18N的名称
        String openExists = I18nUtils.getSystemString(InspectionTaskI18n.EXISTS_OPEN_DATA);
        //存在开启的巡检任务的返回code
        Integer openExistsResultCode = WorkflowBusinessResultCode.PARAM_ERROR;
        //存在开启的巡检任务返回提示信息
        return ResultUtils.warn(openExistsResultCode, openExists);
    }

    /**
     * 存在关闭的巡检任务数据
     * @author hedongwei@wistronits.com
     * @date  2019/3/6 17:09
     * @return 提示信息
     */
    public static Result existsCloseDataMsg() {
        //存在关闭的巡检任务i18N的名称
        String closeExists = I18nUtils.getSystemString(InspectionTaskI18n.EXISTS_CLOSE_DATA);
        //存在关闭的巡检任务的返回code
        Integer closeExistsResultCode = WorkflowBusinessResultCode.PARAM_ERROR;
        //存在关闭的巡检任务返回提示信息
        return ResultUtils.warn(closeExistsResultCode, closeExists);
    }

    /**
     * 返回巡检任务名称重复提示信息
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 19:00
     * @return result 返回提示信息
     */
    public static Result getInspectionNameIsRepeatMsg() {
        //巡检名称重复返回code
        Integer inspectionNameResultCode = WorkflowBusinessResultCode.INSPECTION_NAME_IS_REPEAT;
        //巡检名称重复提示消息
        String inspectionNameMsg = I18nUtils.getSystemString(InspectionTaskI18n.INSPECTION_NAME_IS_REPEAT);
        //有重复的名称提示名称重复
        return ResultUtils.warn(inspectionNameResultCode, inspectionNameMsg);
    }
}
