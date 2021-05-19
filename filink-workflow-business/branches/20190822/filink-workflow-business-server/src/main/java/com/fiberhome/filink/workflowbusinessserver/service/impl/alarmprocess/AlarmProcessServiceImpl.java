package com.fiberhome.filink.workflowbusinessserver.service.impl.alarmprocess;

import com.alibaba.fastjson.JSONArray;
import com.fiberhome.filink.alarmcurrentapi.bean.AlarmCurrentInfo;
import com.fiberhome.filink.alarmcurrentapi.bean.OrderDeviceInfo;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseInfoBean;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import com.fiberhome.filink.workflowbusinessserver.dao.procbase.ProcBaseDao;
import com.fiberhome.filink.workflowbusinessserver.service.alarmprocess.AlarmProcessService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcBaseService;
import com.fiberhome.filink.workflowbusinessserver.stream.WorkflowBusinessStreams;
import com.fiberhome.filink.workflowbusinessserver.utils.common.CastMapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 告警处理类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-04-16
 */
@Service
@Slf4j
public class AlarmProcessServiceImpl implements AlarmProcessService {


    @Autowired
    private WorkflowBusinessStreams workflowBusinessStreams;

    @Autowired
    private ProcBaseService procBaseService;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private DepartmentFeign departmentFeign;

    @Autowired
    private ProcBaseDao procBaseDao;

    /**
     * 生成告警数量
     */
    public static final Integer GENERATE_ALARM_COUNT = 20;

    /**
     * 工单超时生成告警信息
     *
     * @param alarmCurrentInfoList 当前告警集合
     * @param alarmCurrentInfo     当前告警信息
     * @return 告警信息
     * @author hedongwei@wistronits.com
     * @date 2019/4/20 19:56
     */
    @Override
    public void procTimeOutGenerateAlarmOne(List<AlarmCurrentInfo> alarmCurrentInfoList, AlarmCurrentInfo alarmCurrentInfo) {
        if (alarmCurrentInfoList.size() < AlarmProcessServiceImpl.GENERATE_ALARM_COUNT) {
            Message msg = MessageBuilder.withPayload(JSONArray.toJSONString(alarmCurrentInfoList)).build();
            //调用生成告警的方法 (使用消息推送)
            workflowBusinessStreams.alarmOutput().send(msg);
        } else {
            List<AlarmCurrentInfo> alarmCurrentAddList = new ArrayList<>();
            for (int i = 0; i < alarmCurrentInfoList.size(); i++) {
                if (!ObjectUtils.isEmpty(alarmCurrentInfoList.get(i))) {
                    int companyCount = i + 1;
                    alarmCurrentAddList.add(alarmCurrentInfoList.get(i));
                    if (AlarmProcessServiceImpl.GENERATE_ALARM_COUNT == alarmCurrentAddList.size() || companyCount == alarmCurrentInfoList.size()) {
                        //发消息给告警，使用工单生成告警信息
                        Message msg = MessageBuilder.withPayload(JSONArray.toJSONString(alarmCurrentAddList)).build();
                        //调用生成告警的方法 (使用消息推送)
                        workflowBusinessStreams.alarmOutput().send(msg);
                        alarmCurrentAddList = new ArrayList<>();
                    }
                }
            }
        }
    }


    /**
     * 工单超时创建告警
     *
     * @return 返回工单超时创建告警
     * @author hedongwei@wistronits.com
     * @date 2019/4/20 20:17
     */
    @Override
    public Result procTimeOutCreateAlarm() {
        String format = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        log.info("begin proc time out cast alarm , the begin time : {}" , simpleDateFormat.format(System.currentTimeMillis()));
        //查询工单信息
        ProcBase procBase = new ProcBase();
        procBase.setExpectedCompletedTime(new Date());
        //查询超时工单信息
        List<ProcBaseInfoBean> procBaseInfoBeanList = procBaseService.queryProcBaseInfoList(procBase);


        //以工单编号为键，值是工单的所有信息
        Map<String, ProcBaseInfoBean> procBaseInfoBeanMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        //所有工单编号
        List<String> procIds = new ArrayList<>();
        //创建人集合
        List<String> createList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(procBaseInfoBeanList)) {
            for (ProcBaseInfoBean procBaseOne : procBaseInfoBeanList) {
                //流程编号
                procIds.add(procBaseOne.getProcId());
                procBaseInfoBeanMap.put(procBaseOne.getProcId(), procBaseOne);

                //查询创建人的责任单位
                if (ProcBaseConstants.PROC_STATUS_ASSIGNED.equals(procBaseOne.getStatus())) {
                    if (!ObjectUtils.isEmpty(procBaseOne.getCreateUser())) {
                        createList.add(procBaseOne.getCreateUser());
                    }
                }
            }
        }

        //获取部门map
        Map<String, Department> createDepartmentMap = this.getDepartmentMap(createList);

        Map<String, String> departmentMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(procIds)) {
            procBaseInfoBeanMap = this.setRelatedInfoToProcBaseInfoMap(procIds, procBaseInfoBeanMap);
            //查询关联部门
            List<ProcRelatedDepartment> procRelatedDepartmentList = procBaseService.queryProcRelateDeptByProcIds(procIds);
            //获取关联部门的map
            departmentMap = this.setDepartmentToDepartmentMap(procRelatedDepartmentList);

        }


        if (!ObjectUtils.isEmpty(procBaseInfoBeanMap)) {
            Date nowDate = new Date();
            List<AlarmCurrentInfo> alarmCurrentInfoList = new ArrayList<>();
            AlarmCurrentInfo alarmCurrentInfo = null;
            for (String procBaseInfoKey : procBaseInfoBeanMap.keySet()) {
                //获得当前告警信息
                alarmCurrentInfo = this.getAlarmCurrentInfoOne(procBaseInfoKey, procBaseInfoBeanMap, departmentMap, createDepartmentMap, nowDate);
                alarmCurrentInfoList.add(alarmCurrentInfo);
            }

            this.procTimeOutGenerateAlarmOne(alarmCurrentInfoList, alarmCurrentInfo);

            //修改巡检工单告警已经生成
            procBaseDao.updateInspectionCreateAlarmInfo(procIds, nowDate);
            //修改销障工单告警已经生成
            procBaseDao.updateClearCreateAlarmInfo(procIds, nowDate);
            log.info("end proc time out cast alarm , the end time : {}" , simpleDateFormat.format(System.currentTimeMillis()));
        }
        return ResultUtils.success();
    }

    /**
     * 组装当前告警的参数
     *
     * @param procBaseInfoKey     工单编号
     * @param procBaseInfoBeanMap 工单map集合
     * @param departmentMap       部门map
     * @param createDepartmentMap 创建人部门map
     * @param nowDate             当前时间
     * @return 组装当前告警参数
     * @author hedongwei@wistronits.com
     * @date 2019/4/20 21:06
     */
    public AlarmCurrentInfo getAlarmCurrentInfoOne(String procBaseInfoKey, Map<String, ProcBaseInfoBean> procBaseInfoBeanMap,
                                                   Map<String, String> departmentMap, Map<String, Department> createDepartmentMap, Date nowDate) {
        AlarmCurrentInfo alarmCurrentInfo;
        //获得工单信息
        ProcBaseInfoBean procBaseInfoOne = procBaseInfoBeanMap.get(procBaseInfoKey);
        //获得设施
        List<ProcRelatedDevice> procRelatedDeviceList = procBaseInfoOne.getProcRelatedDevices();
        //获得单位
        List<ProcRelatedDepartment> departmentList = procBaseInfoOne.getProcRelatedDepartments();
        String procId = procBaseInfoOne.getProcId();
        String title = procBaseInfoOne.getTitle();
        //生成告警的类
        alarmCurrentInfo = this.generateAlarmCurrentInfo(procBaseInfoOne, nowDate);


        OrderDeviceInfo orderDeviceInfoOne = new OrderDeviceInfo();
        if (!ObjectUtils.isEmpty(procRelatedDeviceList)) {
            orderDeviceInfoOne.setAreaId(procRelatedDeviceList.get(0).getDeviceAreaId());
            orderDeviceInfoOne.setAreaName(procRelatedDeviceList.get(0).getDeviceAreaName());
            orderDeviceInfoOne.setAlarmSourceTypeId(procRelatedDeviceList.get(0).getDeviceType());
        }

        StringBuffer departIds = new StringBuffer();
        StringBuffer departNames = new StringBuffer();
        if (!ObjectUtils.isEmpty(departmentList)) {
            for (ProcRelatedDepartment deptOne : departmentList) {
                if (!ObjectUtils.isEmpty(departIds)) {
                    departIds.append(",");
                    departNames.append(",");
                }
                departIds.append(deptOne.getAccountabilityDept());
                departNames.append(departmentMap.get(deptOne.getAccountabilityDept()));
            }
        } else {
            if (!ObjectUtils.isEmpty(procBaseInfoOne.getCreateUser())) {
                departIds.append(createDepartmentMap.get(procBaseInfoOne.getCreateUser()).getId());
                departNames.append(createDepartmentMap.get(procBaseInfoOne.getCreateUser()).getDeptName());
            }
        }

        orderDeviceInfoOne.setResponsibleDepartmentNames(departNames);
        orderDeviceInfoOne.setResponsibleDepartmentIds(departIds);
        List<OrderDeviceInfo> orderDeviceInfoList = new ArrayList<>();
        orderDeviceInfoList.add(orderDeviceInfoOne);
        alarmCurrentInfo.setOrderDeviceInfoList(orderDeviceInfoList);
        return alarmCurrentInfo;
    }

    /**
     * 设置关联信息到工单信息map集合中
     *
     * @param procIds             工单编号
     * @param procBaseInfoBeanMap 工单信息map
     * @return 工单信息map集合
     * @author hedongwei@wistronits.com
     * @date 2019/4/20 20:44
     */
    public Map<String, ProcBaseInfoBean> setRelatedInfoToProcBaseInfoMap(List<String> procIds, Map<String, ProcBaseInfoBean> procBaseInfoBeanMap) {
        //查询关联设施
        List<ProcRelatedDevice> procRelatedDeviceList = procBaseService.queryProcRelateDeviceByProcIds(procIds);
        procBaseInfoBeanMap = this.setDetailRelatedDevice(procRelatedDeviceList, procBaseInfoBeanMap);

        //查询关联部门
        List<ProcRelatedDepartment> procRelatedDepartmentList = procBaseService.queryProcRelateDeptByProcIds(procIds);
        procBaseInfoBeanMap = this.setDetailRelatedDepartment(procRelatedDepartmentList, procBaseInfoBeanMap);
        return procBaseInfoBeanMap;
    }

    /**
     * 生成当前告警基础参数
     *
     * @param procBaseInfoOne 工单基础数据
     * @param nowDate         当前时间
     * @return 当前告警基础参数
     * @author hedongwei@wistronits.com
     * @date 2019/4/20 20:39
     */
    public AlarmCurrentInfo generateAlarmCurrentInfo(ProcBaseInfoBean procBaseInfoOne, Date nowDate) {
        AlarmCurrentInfo alarmCurrentInfo = new AlarmCurrentInfo();
        //工单编号
        alarmCurrentInfo.setOrderId(procBaseInfoOne.getProcId());
        alarmCurrentInfo.setAlarmBeginTime(nowDate.getTime());
        alarmCurrentInfo.setAlarmStatus("1");
        //工单名称
        alarmCurrentInfo.setOrderName(procBaseInfoOne.getTitle());
        alarmCurrentInfo.setIsOrder(true);
        return alarmCurrentInfo;
    }

    /**
     * 工单关联部门集合
     *
     * @param procRelatedDepartmentList 工单关联部门集合
     * @return 设置部门到部门map集合中
     * @author hedongwei@wistronits.com
     * @date 2019/4/20 20:52
     */
    public Map<String, String> setDepartmentToDepartmentMap(List<ProcRelatedDepartment> procRelatedDepartmentList) {
        Map<String, String> departmentMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        Set<String> deptSet = new HashSet<>();
        List<Department> departmentAllSearchList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(procRelatedDepartmentList)) {
            for (ProcRelatedDepartment relatedDepartment : procRelatedDepartmentList) {
                deptSet.add(relatedDepartment.getAccountabilityDept());
            }
            //部门集合
            List<String> searchDeptList = new ArrayList<>();
            searchDeptList.addAll(deptSet);
            //查询部门信息
            departmentAllSearchList = departmentFeign.queryDepartmentFeignById(searchDeptList);
        }
        departmentMap = CastMapUtil.getDepartmentMap(departmentAllSearchList);
        return departmentMap;
    }


    /**
     * 工单列表map
     *
     * @param procRelatedDeviceList 工单关联设施
     * @param procBaseInfoBeanMap   工单列表map
     * @return 工单列表map
     * @author hedongwei@wistronits.com
     * @date 2019/3/27 12:25
     */
    public Map<String, ProcBaseInfoBean> setDetailRelatedDevice(List<ProcRelatedDevice> procRelatedDeviceList, Map<String, ProcBaseInfoBean> procBaseInfoBeanMap) {
        ProcBaseInfoBean procBaseInfoBean = null;
        if (!ObjectUtils.isEmpty(procRelatedDeviceList)) {
            for (ProcRelatedDevice procRelatedDeviceOne : procRelatedDeviceList) {
                String procId = procRelatedDeviceOne.getProcId();
                procBaseInfoBean = procBaseInfoBeanMap.get(procId);
                if (!ObjectUtils.isEmpty(procBaseInfoBean)) {
                    List<ProcRelatedDevice> deviceList = procBaseInfoBean.getProcRelatedDevices();
                    if (ObjectUtils.isEmpty(deviceList)) {
                        deviceList = new ArrayList<>();
                    }
                    deviceList.add(procRelatedDeviceOne);
                    procBaseInfoBean.setProcRelatedDevices(deviceList);
                    procBaseInfoBeanMap.put(procId, procBaseInfoBean);
                }
            }
        }
        return procBaseInfoBeanMap;
    }


    /**
     * 以创建人的用户编号为键，以部门为值获取创建人部门信息
     *
     * @param createList 创建人集合
     * @return 创建人部门信息
     * @author hedongwei@wistronits.com
     * @date 2019/4/20 20:54
     */
    public Map<String, Department> getDepartmentMap(List<String> createList) {
        Map<String, Department> createDepartmentMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(createList)) {
            //根据部门编号查询用户信息
            Object userObject = userFeign.queryUserByIdList(createList);
            List<User> userList = JSONArray.parseArray(JSONArray.toJSONString(userObject), User.class);
            if (!ObjectUtils.isEmpty(userList)) {
                for (User userInfoOne : userList) {
                    createDepartmentMap.put(userInfoOne.getId(), userInfoOne.getDepartment());
                }
            }
        }
        return createDepartmentMap;
    }


    /**
     * 工单列表map
     *
     * @param procRelatedDepartmentList 工单关联部门
     * @param procBaseInfoBeanMap       工单列表map
     * @return 工单列表map
     * @author hedongwei@wistronits.com
     * @date 2019/3/27 12:25
     */
    public Map<String, ProcBaseInfoBean> setDetailRelatedDepartment(List<ProcRelatedDepartment> procRelatedDepartmentList, Map<String, ProcBaseInfoBean> procBaseInfoBeanMap) {
        ProcBaseInfoBean procBaseInfoBean = null;
        if (!ObjectUtils.isEmpty(procRelatedDepartmentList)) {
            for (ProcRelatedDepartment procRelatedDepartmentOne : procRelatedDepartmentList) {
                String procId = procRelatedDepartmentOne.getProcId();
                procBaseInfoBean = procBaseInfoBeanMap.get(procId);
                if (!ObjectUtils.isEmpty(procBaseInfoBean)) {
                    List<ProcRelatedDepartment> departmentList = procBaseInfoBean.getProcRelatedDepartments();
                    if (ObjectUtils.isEmpty(departmentList)) {
                        departmentList = new ArrayList<>();
                    }
                    departmentList.add(procRelatedDepartmentOne);
                    procBaseInfoBean.setProcRelatedDepartments(departmentList);
                    procBaseInfoBeanMap.put(procId, procBaseInfoBean);
                }
            }
        }
        return procBaseInfoBeanMap;
    }

}
