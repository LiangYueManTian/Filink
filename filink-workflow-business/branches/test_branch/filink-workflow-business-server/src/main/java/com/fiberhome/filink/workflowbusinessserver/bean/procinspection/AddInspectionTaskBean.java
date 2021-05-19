package com.fiberhome.filink.workflowbusinessserver.bean.procinspection;

import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDetailBean;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDevice;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 新增巡检任务
 * @author hedongwei@wistronits.com
 * @date 2019/3/28 19:57
 */
@Data
public class AddInspectionTaskBean {

    /**
     * 巡检任务详情信息
     */
    private InspectionTaskDetailBean detailBean;

    /**
     * 巡检任务关联设施信息
     */
    private List<InspectionTaskDevice> deviceList;

    /**
     * 巡检任务关联设施名称map
     */
    private Map<String, String> deviceNameMap;

    /**
     * 巡检任务关联设施类型map
     */
    private Map<String, String> deviceTypeMap;

    /**
     * 巡检任务关联设施对象
     */
    private Map<String, DeviceInfoDto> deviceInfoDtoMap;

    /**
     * 部门集合
     */
    private List<InspectionTaskDepartment> deptList;
}
