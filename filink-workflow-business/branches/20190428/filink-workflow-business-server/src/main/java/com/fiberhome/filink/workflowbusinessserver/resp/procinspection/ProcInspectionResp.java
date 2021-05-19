package com.fiberhome.filink.workflowbusinessserver.resp.procinspection;

import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import lombok.Data;

/**
 * 巡检工单返回数据
 * @author hedongwei@wistronits.com
 * @date 2019/4/26 16:45
 */
@Data
public class ProcInspectionResp extends ProcInspection {

    /**
     * 巡检工单未完成数量
     */
    private Integer inspectionProcessCount;
}
