package com.fiberhome.filink.workflowbusinessserver.resp.app.procbase;

import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * app销障工单返回类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-04-15
 */
@Data
public class ProcBaseRespForApp extends ProcBase {

    /**
     * 关联告警时间
     */
    private Long refAlarmTime;

    /**
     * 门编号
     */
    private String doorNo;

    /**
     * 门名称
     */
    private String doorName;

    /**
     * 关联设施
     */
    private List<ProcRelatedDeviceResp> procRelatedDeviceRespList;

    /**
     * 关联未巡检设施信息
     */
    private List<ProcInspectionRecord> procNotInspectionDeviceList;
}
