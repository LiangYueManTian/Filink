package com.fiberhome.filink.workflowbusinessserver.req.inspectiontask;

import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 修改巡检任务请求参数信息
 * @author hedongwei@wistronits.com
 * @date 2019/2/26 11:32
 */
@Data
public class DeleteInspectionTaskByIdsReq {

    /**
     * 巡检任务名称 6-32位字符
     */
    private List<String> inspectionTaskIds;

    /**
     * 巡检任务名称 6-32位字符
     */
    private String isDeleted;

    /**
     * 修改巡检任务校验
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 17:26
     * @param req 修改巡检任务参数
     * @return Result 返回值
     */
    public static boolean validateDeleteInspectionTaskByIdsReq(DeleteInspectionTaskByIdsReq req) {
        //获取巡检任务编号集合
        if (!ObjectUtils.isEmpty(req)) {
            List<String> inspectionTaskIds = req.getInspectionTaskIds();
            if (ObjectUtils.isEmpty(inspectionTaskIds)) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
}
