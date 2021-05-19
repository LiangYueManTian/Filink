package com.fiberhome.filink.workflowbusinessserver.req.procbase;

import com.fiberhome.filink.workflowbusinessserver.utils.common.ValidateUtils;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * 退单
 * @author hedongwei@wistronits.com
 * @date 2019/3/22 15:57
 */
@Data
public class SingleBackProcReq {

    /**
     * 流程编号
     */
    private String procId;

    /**
     * 退单原因
     */
    private String singleBackReason;

    /**
     * 退单自定义原因
     */
    private String singleBackUserDefinedReason;


    /**
     * 检查退单原因
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 13:36
     * @param req 退单参数
     * @return 返回退单原因检查结果
     */
    public static boolean checkSingleBackReason(SingleBackProcReq req) {
        //退单原因
        if (!StringUtils.isEmpty(req.getSingleBackReason())) {
            String singleBackReason = req.getSingleBackReason();
            Integer singleBackMinLength = 0;
            Integer singleBackMaxLength = 200;
            if (!ValidateUtils.validateDataLength(singleBackReason, singleBackMinLength, singleBackMaxLength)) {
                return false;
            }
        } else {
            //退单原因必填
            return false;
        }
        return true;
    }
}
