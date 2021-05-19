package com.fiberhome.filink.workflowbusinessserver.resp;

import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procclear.ProcClearFailure;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 流程信息汇总类
 * </p>
 *
 * @author choafanrong@wistronits.com
 * @since 2019-02-18
 */
@Data
public class ProcessInfo {
    private ProcBase procBase;
    private ProcBaseReq procBaseReq;
    private ProcBaseResp procBaseResp;
    private ProcClearFailure procClearFailure;
    private ProcInspection procInspection;
    private List<ProcRelatedDevice> procRelatedDevices;
    private List<ProcRelatedDepartment> procRelatedDepartments;
    private List<ProcInspectionRecord> procInspectionRecords;

    /**
     * 流程名称（用于保存操作日志）
     */
    private String title;

    /**
     * 流程id（用于保存操作日志）
     */
    private String procId;

    /**
     * 操作类型（重新生成）
     */
    private String operaType;

}