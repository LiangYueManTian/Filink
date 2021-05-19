package com.fiberhome.filink.workflowbusinessserver.req.procinspection;

import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import lombok.Data;

import java.util.List;

/**
 *
 * @author hedongwei@wistronits.com
 * @date 2019/3/27 19:03
 */
@Data
public class ProcInspectionRecordReq extends ProcInspectionRecord {

    /**
     * 图片集合
     */
    private List<String> imgInfoList;
}
