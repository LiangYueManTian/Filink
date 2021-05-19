package com.fiberhome.filink.workflowbusinessserver.service.inspectiontask;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskRecord;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 巡检任务记录表 服务类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-26
 */
public interface InspectionTaskRecordService extends IService<InspectionTaskRecord> {

    /**
     * 巡检记录
     * @author hedongwei@wistronits.com
     * @date  2019/3/10 22:27
     * @param taskRecord 巡检记录
     * @return 新增巡检记录
     */
    Result addInspectionTaskRecord(InspectionTaskRecord taskRecord);
}
