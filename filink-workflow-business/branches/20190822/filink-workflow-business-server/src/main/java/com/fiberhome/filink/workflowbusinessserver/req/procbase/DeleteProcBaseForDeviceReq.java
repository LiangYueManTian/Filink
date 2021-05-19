package com.fiberhome.filink.workflowbusinessserver.req.procbase;

import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 根据设施删除工单信息
 * @author hedongwei@wistronits.com
 * @date 2019/4/23 22:24
 */
@Data
public class DeleteProcBaseForDeviceReq {

    /**
     * 设施编号集合
     */
    private List<String> deviceIdList;

    /**
     * 是否删除
     */
    private String isDeleted;

    /**
     * 获取根据设施删除工单的参数
     * @author hedongwei@wistronits.com
     * @date  2019/5/14 9:43
     * @param req 工单参数
     */
    public static DeleteProcBaseForDeviceReq getDeleteProcBaseForDeviceReq(DeleteProcBaseForDeviceReq req) {
        //没有删除的时候默认状态为删除
        if (ObjectUtils.isEmpty(req.getIsDeleted())) {
            req.setIsDeleted(WorkFlowBusinessConstants.IS_DELETED);
        }
        return req;
    }
}
