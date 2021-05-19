package com.fiberhome.filink.workflowbusinessserver.service.impl.inspectiontask;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskI18n;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskRecord;
import com.fiberhome.filink.workflowbusinessserver.dao.inspectiontask.InspectionTaskRecordDao;
import com.fiberhome.filink.workflowbusinessserver.exception.FilinkWorkflowBusinessDataException;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskRecordService;
import com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness.WorkFlowBusinessMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * <p>
 * 巡检任务记录表 服务实现类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-26
 */
@Service
public class InspectionTaskRecordServiceImpl extends ServiceImpl<InspectionTaskRecordDao, InspectionTaskRecord> implements InspectionTaskRecordService {

    @Autowired
    private InspectionTaskRecordDao inspectionTaskRecordDao;

    /**
     * 巡检记录
     * @author hedongwei@wistronits.com
     * @date  2019/3/10 22:27
     * @param taskRecord 巡检记录
     * @return 新增巡检记录
     */
    @Override
    public Result addInspectionTaskRecord(InspectionTaskRecord taskRecord) {
        //巡检任务编号不能为空
        if (StringUtils.isEmpty(taskRecord.getInspectionTaskId())) {
            return WorkFlowBusinessMsg.paramErrorMsg();
        }
        //巡检记录编号
        taskRecord.setInspectionTaskRecordId(NineteenUUIDUtils.uuid());
        //创建时间
        taskRecord.setCreateTime(new Date());

        //成功修改行数为1
        int successRowCount = 1;
        //新增巡检记录成功提示
        String successMsg = I18nUtils.getString(InspectionTaskI18n.SUCCESS_INSERT_TASK_RECORD);
        //新增巡检记录信息
        if (successRowCount == inspectionTaskRecordDao.insert(taskRecord)) {
            return ResultUtils.success(ResultCode.SUCCESS, successMsg);
        } else {
            throw new FilinkWorkflowBusinessDataException();
        }
    }
}
