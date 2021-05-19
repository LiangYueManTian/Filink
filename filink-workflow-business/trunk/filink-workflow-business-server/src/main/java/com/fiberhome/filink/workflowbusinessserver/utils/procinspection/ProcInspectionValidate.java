package com.fiberhome.filink.workflowbusinessserver.utils.procinspection;

import com.fiberhome.filink.workflowbusinessserver.utils.procbase.ProcBaseValidate;

/**
 * 巡检工单校验参数
 * @author hedongwei@wistronits.com
 * @date 2019/4/1 16:14
 */

public class ProcInspectionValidate {

    /**
     * 校验修改操作
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 16:16
     * @param procId 流程编号
     * @return 校验结果
     */
    public static boolean checkOperateUpdate(String procId) {
        if (!ProcBaseValidate.checkProcId(procId)) {
            return false;
        }
        return true;
    }


}
