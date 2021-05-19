package com.fiberhome.filink.scheduleserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.AreaInfoForeignDto;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.scheduleserver.bean.TaskInfo;
import com.fiberhome.filink.scheduleserver.bean.inspectiontask.AddInspectionTaskBean;
import com.fiberhome.filink.scheduleserver.bean.inspectiontask.InspectionTaskInfo;
import com.fiberhome.filink.scheduleserver.enums.JobGroupEnum;
import com.fiberhome.filink.scheduleserver.enums.JobTriggerEnum;
import com.fiberhome.filink.scheduleserver.job.InspectionTaskPeriodJob;
import com.fiberhome.filink.scheduleserver.service.InspectionTaskService;
import com.fiberhome.filink.scheduleserver.service.JobService;
import com.fiberhome.filink.scheduleserver.utils.InspectionTaskUtil;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.workflowbusinessapi.api.inspectiontask.InspectionTaskFeign;
import com.fiberhome.filink.workflowbusinessapi.api.procinspection.ProcInspectionFeign;
import com.fiberhome.filink.workflowbusinessapi.bean.inspectiontask.InspectionTaskDepartment;
import com.fiberhome.filink.workflowbusinessapi.bean.inspectiontask.InspectionTaskDetailBean;
import com.fiberhome.filink.workflowbusinessapi.bean.inspectiontask.InspectionTaskDevice;
import com.fiberhome.filink.workflowbusinessapi.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessapi.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessapi.req.procinspection.ProcInspectionReq;
import com.fiberhome.filink.workflowbusinessapi.constant.InspectionTaskConstants;
import com.fiberhome.filink.workflowbusinessapi.constant.ProcBaseConstants;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hedongwei@wistronits.com
 * description
 * @date 2019/3/31 8:21
 */
@Service
@Slf4j
public class InspectionTaskServiceImpl implements InspectionTaskService {

    @Autowired
    private JobService jobService;

    @Autowired
    private InspectionTaskService inspectionTaskService;

    @Autowired
    private InspectionTaskFeign inspectionTaskFeign;

    @Autowired
    private ProcInspectionFeign procInspectionFeign;

    @Autowired
    private AreaFeign areaFeign;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private DepartmentFeign departmentFeign;

    @Autowired
    private Scheduler filinkScheduler;

    /**
     * 新增巡检任务定时任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/31 8:22
     * @param quartzTask 巡检任务信息
     */
    @Override
    public void addInspectionTaskJob(InspectionTaskInfo quartzTask) throws SchedulerException {
        String jobName = quartzTask.getJobName();
        String groupName = quartzTask.getJobGroup().getGroupName();
        String triggerGroupName = quartzTask.getTriggerGroup().getGroupName();
        Object data = quartzTask.getData();
        String jobDesc = quartzTask.getJobDesc();
        String triggerName = quartzTask.getTriggerName();
        String cron = quartzTask.getCron();

        if (jobService.checkExists(jobName, quartzTask.getJobGroup())) {
            log.info("Task is exists, not repeat create");
        }

        // TODO: 2019-01-23 获取scheduler  此处获取方式可能存在bug 后续研究
        // 改进版本  注入scheduler
//        StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
//        Scheduler scheduler = stdSchedulerFactory.getScheduler();

        // 构建key值  以各自name + 分组名组成
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, triggerGroupName);
        JobKey jobKey = JobKey.jobKey(jobName, groupName);

        // 构建任务详情
        JobDetail jobDetail = JobBuilder.newJob(quartzTask.getTClass())
                .withIdentity(jobKey)
                .build();

        // 放入任务中需要使用的数据
        jobDetail.getJobDataMap().put(JobServiceImpl.DATA_KEY, data);

        // 构建触发器
        Trigger trigger = TriggerBuilder.newTrigger()
                    .withDescription(jobDesc)
                    .withIdentity(triggerKey)
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();

        // 调度任务
        filinkScheduler.scheduleJob(jobDetail, trigger);
        filinkScheduler.start();
        log.info("task start ：" + jobName);
        log.info("task describe ：" + jobDesc);
        log.info("start time ：" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    }


    /**
     * 新增周期性的任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/31 10:28
     * @param detailBean 详情信息
     * @return 返回新增周期性的任务结果
     * @throws Exception 抛出异常信息
     */
    @Override
    public Result addTaskPeriodJob(InspectionTaskDetailBean detailBean) throws Exception {
        //创建巡检任务周期任务
        TaskInfo addPeriodTaskInfo = this.inspectionTaskDetailCastTaskInfo(detailBean);
        //创建巡检任务周期性任务
        jobService.addSimpleTriggerJob(addPeriodTaskInfo);
        return ResultUtils.success(new Object());
    }

    /**
     * 任务实体
     * @author hedongwei@wistronits.com
     * @date  2019/3/31 10:04
     * @param detailBean 巡检任务详情参数
     * @return 任务实体
     */
    private TaskInfo inspectionTaskDetailCastTaskInfo(InspectionTaskDetailBean detailBean) {
        TaskInfo taskInfo = new TaskInfo();
        Long nowTime = System.currentTimeMillis();

        //巡检任务编号
        String inspectionTaskId = detailBean.getInspectionTaskId();

        //任务名称
        taskInfo.setJobName(inspectionTaskId);

        Integer taskPeriod = detailBean.getTaskPeriod();

        //任务执行情况
        taskInfo.setIntervalSecond(InspectionTaskUtil.getMonthTime(taskPeriod));

        //任务创建时间
        taskInfo.setCreateTime(nowTime);

        //任务分组
        taskInfo.setJobGroup(JobGroupEnum.INSPECTION_TASK);

        //任务触发器分组
        taskInfo.setTriggerGroup(JobTriggerEnum.INSPECTION_TASK);

        //任务对象
        taskInfo.setTClass(InspectionTaskPeriodJob.class);
        int mapInitLength = 64;
        Map<String, Object> data = new HashMap<>(mapInitLength);

        //巡检任务编号
        data.put("inspectionId", inspectionTaskId);

        //任务对象
        taskInfo.setData(JSONObject.toJSONString(data));

        return taskInfo;
    }


    /**
     * 新增巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/31 9:26
     * @param detailBean 巡检任务编号
     * @return 新增巡检工单结果
     */
    @Override
    public Result addInspectionProc(InspectionTaskDetailBean detailBean) {
        Result result = null;
        //获得巡检任务关联设施集合
        if (null != detailBean) {
            //巡检区域编号
            String areaId = detailBean.getInspectionAreaId();
            //巡检区域名称
            String areaName = this.getInspectionAreaName(areaId);

            //获得巡检任务关联设施
            List<InspectionTaskDevice> deviceList = detailBean.getDeviceList();
            //巡检任务关联设施编号
            List<String> deviceIds = this.getDeviceIds(deviceList);
            //查询关联设施信息，包含设施的所有信息
            List<DeviceInfoDto> deviceInfoDtoList = this.getDeviceInfoDtoList(deviceIds);

            //设施名称map
            int mapInitLength = 64;
            Map<String, String> deviceNameMap = new HashMap<>(mapInitLength);
            //设施类型map
            Map<String, String> deviceTypeMap = new HashMap<>(mapInitLength);
            if (null != deviceList && 0 < deviceList.size()) {
                for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList) {
                    String deviceId = deviceInfoDto.getDeviceId();
                    deviceNameMap.put(deviceId, deviceInfoDto.getDeviceName());
                    deviceTypeMap.put(deviceId, deviceInfoDto.getDeviceType());
                }
            }

            //获得巡检任务关联单位
            List<InspectionTaskDepartment> deptList = detailBean.getDeptList();
            List<String> deptIds = this.getDeptIds(deptList);

            //查询巡检任务关联单位
            //部门集合
            List<Department> departmentList = departmentFeign.queryDepartmentFeignById(deptIds);
            //部门map
            Map<String, String> departmentMap = this.getDepartmentMap(departmentList);

            //生成巡检工单 生成一条巡检工单
            AddInspectionTaskBean addInspectionTaskBean = new AddInspectionTaskBean();
            addInspectionTaskBean.setDetailBean(detailBean);
            addInspectionTaskBean.setDeptList(deptList);
            addInspectionTaskBean.setDeviceNameMap(deviceNameMap);
            addInspectionTaskBean.setDeviceTypeMap(deviceTypeMap);
            addInspectionTaskBean.setDeviceList(deviceList);
            //新增巡检任务结果
            result = this.generateInspectionProc(addInspectionTaskBean, areaName);
        }
        return result;
    }

    /**
     * 新增巡检工单过程
     * @author hedongwei@wistronits.com
     * @date  2019/3/31 10:13
     * @param inspectionTaskId 巡检任务编号
     * @return 新增巡检工单结果
     */
    @Override
    public Result addInspectionProcProcess(String inspectionTaskId) {
        Result result = null;
        //根据巡检任务编号查询巡检任务信息
        Result taskDetail = inspectionTaskFeign.getInspectionTaskDetail(inspectionTaskId);
        InspectionTaskDetailBean detailBean = JSONObject.toJavaObject(JSONObject.parseObject(JSONObject.toJSONString(taskDetail.getData())), InspectionTaskDetailBean.class);
        if (InspectionTaskConstants.IS_OPEN.equals(detailBean.getIsOpen())) {
            result = inspectionTaskService.addInspectionProc(detailBean);
        }
        return result;
    }


    /**
     * 获取区域名称
     * @author hedongwei@wistronits.com
     * @date  2019/3/31 9:46
     * @param areaId 区域编号
     * @return 区域名称
     */
    private String getInspectionAreaName(String areaId) {
        String areaName = "";
        List<String> areaIds = new ArrayList<String>();
        areaIds.add(areaId);

        //查询巡检任务关联区域信息
        List<AreaInfoForeignDto> areaInfoList = areaFeign.selectAreaInfoByIds(areaIds);

        //巡检区域名称
        if (null != areaInfoList && 0 < areaInfoList.size()) {
            areaName = areaInfoList.get(0).getAreaName();
        }
        return areaName;
    }

    /**
     * 获得巡检任务关联设施编号集合
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 19:30
     * @param deviceList 设施集合
     * @return 获得巡检任务关联设施编号集合
     */
    private List<String> getDeviceIds(List<InspectionTaskDevice> deviceList) {
        List<String> deviceIds = new ArrayList<>();
        if (null == deviceList && 0 < deviceList.size()) {
            for (InspectionTaskDevice deviceOne : deviceList) {
                //设施编号
                deviceIds.add(deviceOne.getDeviceId());
            }
        }
        return deviceIds;
    }

    /**
     * 查询设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 19:35
     * @param deviceIds 设施编号数组
     * @return 获得设施信息
     */
    private List<DeviceInfoDto> getDeviceInfoDtoList(List<String> deviceIds) {
        List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(deviceInfoDtoList)) {
            String [] deviceListsArray = new String [deviceIds.size()];
            deviceIds.toArray(deviceListsArray);
            //查询巡检任务关联设施根据设施编号
            deviceInfoDtoList = deviceFeign.getDeviceByIds(deviceListsArray);
        }
        return deviceInfoDtoList;
    }

    /**
     * 获取部门编号集合
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 19:39
     * @param deptList 巡检任务关联部门集合
     * @return 获取部门编号集合
     */
    private List<String> getDeptIds(List<InspectionTaskDepartment> deptList) {
        List<String> deptIds = new ArrayList<>();
        if (null != deptList && 0 < deptList.size()) {
            for (InspectionTaskDepartment deptOne : deptList) {
                deptIds.add(deptOne.getInspectionTaskDeptId());
            }
        }
        return deptIds;
    }

    /**
     * 获取部门map
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 19:51
     * @param departmentList 部门集合
     * @return 获取部门map
     */
    private Map<String, String> getDepartmentMap(List<Department> departmentList) {
        int mapInitLength = 64;
        Map<String ,String> departmentMap = new HashMap<>(mapInitLength);
        if (null != departmentList && 0 < departmentList.size()) {
            for (Department departmentOne : departmentList) {
                departmentMap.put(departmentOne.getId(), departmentOne.getDeptName());
            }
        }
        return departmentMap;
    }

    /**
     * 新增巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 20:16
     * @param addInspectionTaskBean 新增巡检任务信息
     * @param areaName 区域名称
     * @return 返回新增巡检任务结果
     */
    private Result generateInspectionProc(AddInspectionTaskBean addInspectionTaskBean, String areaName) {
        //巡检任务详情信息
        InspectionTaskDetailBean detailBean = addInspectionTaskBean.getDetailBean();
        //巡检任务关联设施信息
        List<InspectionTaskDevice> deviceList = addInspectionTaskBean.getDeviceList();
        //设施名称map
        Map<String, String> deviceNameMap = addInspectionTaskBean.getDeviceNameMap();
        //设施类型map
        Map<String, String> deviceTypeMap = addInspectionTaskBean.getDeviceTypeMap();
        //部门集合
        List<InspectionTaskDepartment> deptList = addInspectionTaskBean.getDeptList();

        ProcInspectionReq inspectionReq = new ProcInspectionReq();
        detailBean.setDeptList(null);
        detailBean.setDeviceList(null);
        BeanUtils.copyProperties(detailBean, inspectionReq);

        List<ProcRelatedDevice> relatedDeviceList = new ArrayList<>();
        //关联设施
        if (null != deviceList && 0 < deviceList.size()) {
            ProcRelatedDevice relatedDevice;
            for (InspectionTaskDevice deviceOne : deviceList) {
                relatedDevice = new ProcRelatedDevice();
                BeanUtils.copyProperties(deviceOne, relatedDevice);
                String deviceOneId = deviceOne.getDeviceId();
                relatedDevice.setDeviceName(deviceNameMap.get(deviceOneId));
                relatedDevice.setDeviceType(deviceTypeMap.get(deviceOneId));
                relatedDeviceList.add(relatedDevice);
            }
        }

        List<ProcRelatedDepartment> departmentList = new ArrayList<>();
        //关联部门
        if (null != deptList && 0 < deptList.size()) {
            ProcRelatedDepartment relatedDept;
            for (InspectionTaskDepartment deptOne : deptList) {
                relatedDept = new ProcRelatedDepartment();
                BeanUtils.copyProperties(deptOne, relatedDept);
                departmentList.add(relatedDept);
            }
        }
        //工单标题
        inspectionReq.setTitle(detailBean.getInspectionTaskName());
        //区域名称
        inspectionReq.setInspectionAreaName(areaName);
        inspectionReq.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_2);
        //巡检开始时间
        inspectionReq.setInspectionStartDate(detailBean.getTaskStartTime().getTime());
        //巡检结束时间
        inspectionReq.setInspectionEndDate(detailBean.getTaskEndTime().getTime());
        //关联设施
        inspectionReq.setDeviceList(relatedDeviceList);
        //关联部门
        inspectionReq.setDeptList(departmentList);
        //生成巡检工单
        Result result = procInspectionFeign.addInspectionProc(inspectionReq);
        return result;
    }
}
