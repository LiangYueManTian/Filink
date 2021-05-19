package com.fiberhome.filink.workflowbusinessserver.req.procbase;

import com.fiberhome.filink.workflowbusinessserver.utils.common.ValidateUtils;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * 转办的参数
 * @author hedongwei@wistronits.com
 * @date 2019/3/27 20:28
 */
@Data
public class TurnProcReq {

    /**
     * 流程编号
     */
    private String procId;

    /**
     * 用户编号
     */
    private String userId;

    /**
     * 转派原因
     */
    private String turnReason;


    /**
     * 检查转派原因
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 13:36
     * @param req 转派原因
     * @return 返回转派原因校验结果
     */
    public static boolean checkTurnReason(TurnProcReq req) {
        //转派原因
        if (!StringUtils.isEmpty(req.getTurnReason())) {
            String turnReason = req.getTurnReason();
            Integer turnReasonMinLength = 0;
            Integer turnReasonMaxLength = 200;
            if (!ValidateUtils.validateDataLength(turnReason, turnReasonMinLength, turnReasonMaxLength)) {
                return false;
            }
        } else {
            //转派原因必填
            return false;
        }
        return true;
    }
}
