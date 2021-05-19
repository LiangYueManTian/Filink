package com.fiberhome.filink.workflowbusinessserver.service.impl.inspectiontask;

import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDevice;
import com.fiberhome.filink.workflowbusinessserver.dao.inspectiontask.InspectionTaskDeviceDao;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontask.taskrelated.InspectionTaskDeviceReq;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * 巡检任务关联设施测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/6 13:42
 */
@RunWith(JMockit.class)
public class InspectionTaskDeviceServiceTest {

    /**
     * 被测试的对象巡检任务关联设施
     */
    @Tested
    private InspectionTaskDeviceServiceImpl inspectionTaskDeviceService;


    /**
     * 巡检任务关联设施持久层
     */
    @Injectable
    private InspectionTaskDeviceDao inspectionTaskDeviceDao;


    /**
     * 删除巡检关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/7/8 20:30
     */
    @Test
    public void deleteInspectionTaskDevice() {
        InspectionTaskDevice inspectionTaskDevice = new InspectionTaskDevice();
        inspectionTaskDeviceService.deleteInspectionTaskDevice(inspectionTaskDevice);

        InspectionTaskDevice inspectionTaskDeviceParam = new InspectionTaskDevice();
        inspectionTaskDeviceParam.setInspectionTaskId("1");
        inspectionTaskDeviceService.deleteInspectionTaskDevice(inspectionTaskDeviceParam);
    }


    /**
     * 批量逻辑删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/8 20:30
     */
    @Test
    public void deleteInspectionTaskDeviceBatch() {
        List<String> inspectionTaskIds = new ArrayList<>();
        String isDelete = "0";
        inspectionTaskDeviceService.deleteInspectionTaskDeviceBatch(inspectionTaskIds, isDelete);

        List<String> inspectionTaskIdList = new ArrayList<>();
        String inspectionTaskId = "1";
        inspectionTaskIdList.add(inspectionTaskId);
        inspectionTaskDeviceService.deleteInspectionTaskDeviceBatch(inspectionTaskIdList, isDelete);
    }


    /**
     * 批量逻辑删除设施
     * @author hedongwei@wistronits.com
     * @date  2019/7/8 20:30
     */
    @Test
    public void logicDeleteTaskDeviceBatch() {
        InspectionTaskDeviceReq req = new InspectionTaskDeviceReq();
        inspectionTaskDeviceService.logicDeleteTaskDeviceBatch(req);
    }


    /**
     * 批量新增巡检任务关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/8 20:30
     */
    @Test
    public void insertInspectionTaskDeviceBatch() {
        List<InspectionTaskDevice> inspectionTaskDeviceList = new ArrayList<>();
        String inspectionTaskId = "1";
        InspectionTaskDevice inspectionTaskDevice = new InspectionTaskDevice();
        inspectionTaskDevice.setDeviceId("1");
        inspectionTaskDeviceList.add(inspectionTaskDevice);
        inspectionTaskDeviceService.insertInspectionTaskDeviceBatch(inspectionTaskDeviceList, inspectionTaskId);
    }

    /**
     * 查询巡检任务关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/7/8 20:30
     */
    @Test
    public void queryInspectionTaskDeviceByTaskId() {
        String inspectionTaskId = "1";
        inspectionTaskDeviceService.queryInspectionTaskDeviceByTaskId(inspectionTaskId);
    }

    /**
     * 根据巡检编号集合查询巡检任务设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/8 20:30
     */
    @Test
    public void queryInspectionTaskDeviceForDeviceIdList() {
        InspectionTaskDeviceReq req = new InspectionTaskDeviceReq();
        inspectionTaskDeviceService.queryInspectionTaskDeviceForDeviceIdList(req);
    }


}
