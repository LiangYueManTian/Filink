package com.fiberhome.filink.workflowbusinessserver.utils.common;

import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTask;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontask.DeleteInspectionTaskForDeviceReq;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.DeleteProcBaseForDeviceReq;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 转换list工具类
 *
 * @author hedongwei@wistronits.com
 * @date 2019/4/23 22:45
 */

public class CastListUtil {


    /**
     * 获取巡检任务编号集合
     *
     * @param inspectionRecordList 巡检任务记录集合
     * @return 获取巡检任务编号集合
     * @author hedongwei@wistronits.com
     * @date 2019/4/23 22:49
     */
    public static List<String> getInspectionRecordIdList(List<ProcInspectionRecord> inspectionRecordList) {
        List<String> recordIdList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(inspectionRecordList)) {
            for (ProcInspectionRecord procInspectionRecord : inspectionRecordList) {
                recordIdList.add(procInspectionRecord.getProcInspectionRecordId());
            }
        }
        return recordIdList;
    }

    /**
     * 获取巡检任务编号集合
     *
     * @param procRelatedDeviceList 巡检任务记录集合
     * @return 获取巡检任务编号集合
     * @author hedongwei@wistronits.com
     * @date 2019/4/23 22:49
     */
    public static List<String> getRelatedDeviceIdList(List<ProcRelatedDevice> procRelatedDeviceList) {
        List<String> relatedIdList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(procRelatedDeviceList)) {
            for (ProcRelatedDevice procInspectionRecord : procRelatedDeviceList) {
                relatedIdList.add(procInspectionRecord.getProcRelatedDeviceId());
            }
        }
        return relatedIdList;
    }

    /**
     * 获取巡检任务设施表编号集合
     *
     * @param deviceList 设施类型集合
     * @return 获取巡检任务设施表编号
     * @author hedongwei@wistronits.com
     * @date 2019/4/26 19:33
     */
    public static List<String> getTaskDeviceIdList(List<InspectionTaskDevice> deviceList) {
        List<String> taskDeviceIdList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(deviceList)) {
            for (InspectionTaskDevice taskDevice : deviceList) {
                taskDeviceIdList.add(taskDevice.getInspectionTaskDeviceId());
            }
        }
        return taskDeviceIdList;
    }

    /**
     * 获取能够删除的工单编号集合
     *
     * @param procDeviceMap 工单设施map
     * @param req           删除工单参数
     * @return 删除工单编号信息
     * @author hedongwei@wistronits.com
     * @date 2019/4/23 23:16
     */
    public static List<String> getAbleDeleteProcIdList(Map<String, List<ProcRelatedDevice>> procDeviceMap, DeleteProcBaseForDeviceReq req) {
        List<String> ableProcId = new ArrayList<>();
        if (!ObjectUtils.isEmpty(procDeviceMap) && !ObjectUtils.isEmpty(req.getDeviceIdList())) {
            List<String> procDeviceIdList = req.getDeviceIdList();
            Map<String, String> procDeviceIdMap = CastMapUtil.getMapForListString(procDeviceIdList);
            for (String procId : procDeviceMap.keySet()) {
                if (!ObjectUtils.isEmpty(procDeviceMap.get(procId))) {
                    List<ProcRelatedDevice> procRelatedDeviceList = procDeviceMap.get(procId);
                    boolean flag = false;
                    int sum = procRelatedDeviceList.size();
                    int count = 0;
                    for (ProcRelatedDevice procRelatedDeviceOne : procRelatedDeviceList) {
                        if (procDeviceIdMap.containsKey(procRelatedDeviceOne.getDeviceId())) {
                            count++;
                            if (count == sum) {
                                flag = true;
                            }
                        }
                    }
                    //删除了全部设施的工单需要删除工单信息
                    if (flag) {
                        ableProcId.add(procId);
                    }
                }
            }
        } else {
            return new ArrayList<>();
        }
        return ableProcId;
    }

    /**
     * 获得能够删除的巡检任务信息
     *
     * @param taskDeviceMap 任务设施map
     * @param req           删除巡检任务的参数
     * @return 获得能够删除的巡检任务信息
     * @author hedongwei@wistronits.com
     * @date 2019/4/26 18:01
     */
    public static List<String> getAbleDeleteInspectionTaskIdList(Map<String, List<InspectionTaskDevice>> taskDeviceMap, DeleteInspectionTaskForDeviceReq req) {
        List<String> ableInspectionTaskId = new ArrayList<>();
        if (!ObjectUtils.isEmpty(taskDeviceMap) && !ObjectUtils.isEmpty(req.getDeviceIdList())) {
            List<String> taskDeviceIdList = req.getDeviceIdList();
            Map<String, String> taskDeviceIdMap = CastMapUtil.getMapForListString(taskDeviceIdList);
            for (String taskId : taskDeviceMap.keySet()) {
                if (!ObjectUtils.isEmpty(taskDeviceMap.get(taskId))) {
                    List<InspectionTaskDevice> relatedDeviceList = taskDeviceMap.get(taskId);
                    boolean flag = false;
                    int sum = relatedDeviceList.size();
                    int count = 0;
                    for (InspectionTaskDevice inspectionTaskDeviceOne : relatedDeviceList) {
                        if (taskDeviceIdMap.containsKey(inspectionTaskDeviceOne.getDeviceId())) {
                            count++;
                            if (count == sum) {
                                flag = true;
                            }
                        }
                    }
                    //删除了全部设施的工单需要删除工单信息
                    if (flag) {
                        ableInspectionTaskId.add(taskId);
                    }
                }
            }
        } else {
            return new ArrayList<>();
        }
        return ableInspectionTaskId;
    }


    /**
     * 获取没有删除的工单编号
     *
     * @param procDeviceMap 设施map
     * @param ableProcIds   能够删除的工单
     * @return 获取没有删除的工单编号
     * @author hedongwei@wistronits.com
     * @date 2019/4/24 19:14
     */
    public static List<String> getNotDeletedProcId(Map<String, List<ProcRelatedDevice>> procDeviceMap, List<String> ableProcIds) {
        List<String> retList = new ArrayList<>();
        Map<String, String> procIdMap = CastMapUtil.getMapForListString(ableProcIds);
        if (!ObjectUtils.isEmpty(procIdMap) && !ObjectUtils.isEmpty(ableProcIds)) {
            for (String procDeviceKey : procDeviceMap.keySet()) {
                if (!procIdMap.containsKey(procDeviceKey)) {
                    //添加工单编号
                    retList.add(procDeviceKey);
                }
            }
        } else if (!ObjectUtils.isEmpty(procDeviceMap)) {
            for (String procDeviceKey : procDeviceMap.keySet()) {
                //添加工单编号
                retList.add(procDeviceKey);
            }
        }
        return retList;
    }

    /**
     * 获取提醒用户编号
     *
     * @param userList 用户集合
     * @author hedongwei@wistronits.com
     * @date 2019/5/5 22:03
     */
    public static List<String> getNoticeUserId(List<User> userList) {
        List<String> userIdList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(userList)) {
            for (User userOne : userList) {
                userIdList.add(userOne.getId());
            }
        }
        return userIdList;
    }

    /**
     * 获取不用删除的巡检任务编号
     *
     * @param deviceMap   设施map
     * @param ableTaskIds 需要删除的巡检任务
     * @return 获取不用删除的巡检任务
     * @author hedongwei@wistronits.com
     * @date 2019/4/26 18:21
     */
    public static List<String> getNotDeletedTaskId(Map<String, List<InspectionTaskDevice>> deviceMap, List<String> ableTaskIds) {
        List<String> retInspectionIdList = new ArrayList<>();
        Map<String, String> ableTaskMap = CastMapUtil.getMapForListString(ableTaskIds);
        if (!ObjectUtils.isEmpty(ableTaskMap) && !ObjectUtils.isEmpty(ableTaskIds)) {
            for (String deviceKey : deviceMap.keySet()) {
                if (!ableTaskMap.containsKey(deviceKey)) {
                    //添加巡检任务编号
                    retInspectionIdList.add(deviceKey);
                }
            }
        } else if (!ObjectUtils.isEmpty(deviceMap)) {
            for (String deviceKey : deviceMap.keySet()) {
                //添加巡检任务编号
                retInspectionIdList.add(deviceKey);
            }
        }
        return retInspectionIdList;
    }


    /**
     * 获取没有删除的工单的设施总数
     *
     * @param procDeviceMap      工单设施map
     * @param procInspectionList 工单巡检集合
     * @return 获取没有删除的工单的设施总数
     * @author hedongwei@wistronits.com
     * @date 2019/4/24 20:13
     */
    public static List<ProcInspection> getNotDeleteDeviceCount(Map<String, List<ProcRelatedDevice>> procDeviceMap, List<ProcInspection> procInspectionList) {
        if (!ObjectUtils.isEmpty(procDeviceMap) && !ObjectUtils.isEmpty(procInspectionList)) {
            for (ProcInspection procInspectionOne : procInspectionList) {
                if (procDeviceMap.containsKey(procInspectionOne.getProcId())) {
                    List<ProcRelatedDevice> procRelatedDeviceList = procDeviceMap.get(procInspectionOne.getProcId());
                    int deviceCount = procInspectionOne.getInspectionDeviceCount();
                    deviceCount = deviceCount - procRelatedDeviceList.size();
                    procInspectionOne.setInspectionDeviceCount(deviceCount);
                }
            }
            return procInspectionList;
        }
        return new ArrayList<>();
    }


    /**
     * 获取没有删除的巡检任务设施总数
     *
     * @param taskDeviceMap      巡检任务map
     * @param inspectionTaskList 巡检任务集合
     * @return 巡检任务集合
     * @author hedongwei@wistronits.com
     * @date 2019/4/26 18:53
     */
    public static List<InspectionTask> getNotDeleteTaskDeviceCount(Map<String, List<InspectionTaskDevice>> taskDeviceMap, List<InspectionTask> inspectionTaskList) {
        if (!ObjectUtils.isEmpty(taskDeviceMap) && !ObjectUtils.isEmpty(inspectionTaskList)) {
            for (InspectionTask inspectionTaskOne : inspectionTaskList) {
                if (taskDeviceMap.containsKey(inspectionTaskOne.getInspectionTaskId())) {
                    List<InspectionTaskDevice> procRelatedDeviceList = taskDeviceMap.get(inspectionTaskOne.getInspectionTaskId());
                    int deviceCount = inspectionTaskOne.getInspectionDeviceCount();
                    deviceCount = deviceCount - procRelatedDeviceList.size();
                    inspectionTaskOne.setInspectionDeviceCount(deviceCount);
                }
            }
            return inspectionTaskList;
        }
        return new ArrayList<>();
    }

    /**
     * 根据map获取巡检工单集合
     *
     * @param procInspectionMap 巡检工单map
     * @return 根据map获取巡检工单集合
     * @author hedongwei@wistronits.com
     * @date 2019/4/24 22:36
     */
    public static List<ProcInspection> getProcInspectionListForMap(Map<String, ProcInspection> procInspectionMap) {
        List<ProcInspection> procInspectionList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(procInspectionMap)) {
            for (String procInspectionKey : procInspectionMap.keySet()) {
                procInspectionList.add(procInspectionMap.get(procInspectionKey));
            }
        }
        return procInspectionList;
    }

    /**
     * 获取已完成的巡检记录信息
     *
     * @param inspectionRecordList 巡检记录信息
     * @param procInspectionList   巡检信息
     * @return 获取已完成的巡检记录信息
     * @author hedongwei@wistronits.com
     * @date 2019/4/24 20:22
     */
    public static List<ProcInspection> getNotDeleteCompleteDeviceCount(List<ProcInspectionRecord> inspectionRecordList, List<ProcInspection> procInspectionList) {
        List<ProcInspection> inspectionList = new ArrayList<>();
        Map<String, ProcInspection> procInspectionMap = CastMapUtil.getProcInspectionMap(procInspectionList);
        if (!ObjectUtils.isEmpty(inspectionRecordList) && !ObjectUtils.isEmpty(procInspectionMap)) {
            for (ProcInspectionRecord procInspectionRecordOne : inspectionRecordList) {
                //有巡检结果的数据
                if (!ObjectUtils.isEmpty(procInspectionRecordOne.getResult())) {
                    if (procInspectionMap.containsKey(procInspectionRecordOne.getProcId())) {
                        ProcInspection procInspection = procInspectionMap.get(procInspectionRecordOne.getProcId());
                        int completedCount = procInspection.getInspectionCompletedCount();
                        //每次完成巡检个数减一
                        completedCount = completedCount - 1;
                        procInspection.setInspectionCompletedCount(completedCount);
                        procInspectionMap.put(procInspectionRecordOne.getProcId(), procInspection);
                    }
                }
            }
        }

        if (!ObjectUtils.isEmpty(procInspectionMap)) {
            inspectionList = CastListUtil.getProcInspectionListForMap(procInspectionMap);
        }
        return inspectionList;
    }


    /**
     * 过滤掉能够删除的信息
     *
     * @param procRelatedDeviceList 关联设施集合
     * @param procMap               需要过滤掉的工单
     * @return 过滤之后的信息
     * @author hedongwei@wistronits.com
     * @date 2019/4/24 0:06
     */
    public static List<ProcRelatedDevice> filterAbleProc(List<ProcRelatedDevice> procRelatedDeviceList,
                                                         Map<String, String> procMap) {
        List<ProcRelatedDevice> returnRelatedDeviceList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(procRelatedDeviceList) && !ObjectUtils.isEmpty(procMap)) {
            for (ProcRelatedDevice procRelatedOne : procRelatedDeviceList) {
                if (!ObjectUtils.isEmpty(procRelatedOne.getProcId())) {
                    String procId = procRelatedOne.getProcId();
                    if (!procMap.containsKey(procId)) {
                        returnRelatedDeviceList.add(procRelatedOne);
                    }
                }
            }
        } else if (!ObjectUtils.isEmpty(procRelatedDeviceList)) {
            return procRelatedDeviceList;
        }
        return returnRelatedDeviceList;
    }

    /**
     * 过滤掉能够删除的信息
     *
     * @param taskDeviceList 任务关联设施信息
     * @param taskMap        巡检任务编号map
     * @return 过滤掉能够删除的信息
     * @author hedongwei@wistronits.com
     * @date 2019/4/26 19:02
     */
    public static List<InspectionTaskDevice> filterAbleTask(List<InspectionTaskDevice> taskDeviceList,
                                                            Map<String, String> taskMap) {
        List<InspectionTaskDevice> returnDeviceList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(taskDeviceList) && !ObjectUtils.isEmpty(taskMap)) {
            for (InspectionTaskDevice inspectionTaskOne : taskDeviceList) {
                if (!ObjectUtils.isEmpty(inspectionTaskOne.getInspectionTaskId())) {
                    String inspectionTaskId = inspectionTaskOne.getInspectionTaskId();
                    if (!taskMap.containsKey(inspectionTaskId)) {
                        returnDeviceList.add(inspectionTaskOne);
                    }
                }
            }
        } else if (!ObjectUtils.isEmpty(taskDeviceList)) {
            return taskDeviceList;
        }
        return returnDeviceList;
    }

    /**
     * 过滤掉能够删除的信息
     *
     * @param procDeviceRecordList 关联设施集合
     * @param procMap              需要过滤掉的工单
     * @return 过滤之后的信息
     * @author hedongwei@wistronits.com
     * @date 2019/4/24 0:06
     */
    public static List<ProcInspectionRecord> filterAbleProcToRecord(List<ProcInspectionRecord> procDeviceRecordList,
                                                                    Map<String, String> procMap) {
        List<ProcInspectionRecord> returnRecordList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(procDeviceRecordList) && !ObjectUtils.isEmpty(procMap)) {
            for (ProcInspectionRecord recordOne : procDeviceRecordList) {
                if (!ObjectUtils.isEmpty(recordOne.getProcId())) {
                    String procId = recordOne.getProcId();
                    if (!procMap.containsKey(procId)) {
                        returnRecordList.add(recordOne);
                    }
                }
            }
        } else if (!ObjectUtils.isEmpty(procDeviceRecordList)) {
            return procDeviceRecordList;
        }
        return returnRecordList;
    }


    /**
     * 获取删除/恢复的工单信息
     *
     * @param procList    工单编号集合
     * @param procBaseMap 工单map
     * @param isDeleted   是否删除
     * @return 获取删除/恢复的工单信息
     * @author hedongwei@wistronits.com
     * @date 2019/4/24 14:13
     */
    public static List<ProcBaseReq> getDeleteProcBaseReqList(List<String> procList, Map<String, ProcBase> procBaseMap, String isDeleted) {
        List<ProcBaseReq> procBaseReqList = new ArrayList<>();
        for (String procIdOne : procList) {
            if (!ObjectUtils.isEmpty(procBaseMap)) {
                ProcBase procBaseOne = procBaseMap.get(procIdOne);
                if (!ObjectUtils.isEmpty(procBaseOne)) {
                    //转换持久类对象
                    ProcBaseReq procBaseReqInfo = new ProcBaseReq();
                    BeanUtils.copyProperties(procBaseOne, procBaseReqInfo);
                    procBaseReqInfo.setIsDeleted(isDeleted);
                    procBaseReqInfo.setUpdateUser(ProcBaseUtil.getUserId());
                    procBaseReqInfo.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
                    procBaseReqList.add(procBaseReqInfo);
                }
            }
        }
        return procBaseReqList;
    }


    /**
     * 获取工单编号
     * @author hedongwei@wistronits.com
     * @date  2019/5/14 9:38
     * @param procIds 工单编号集合
     * @param procBaseRespList 工单返回信息集合
     * @return 工单编号集合
     */
    public static List<String> getProcIdsForProcBaseRespList(List<String> procIds, List<ProcBaseResp> procBaseRespList) {
        if (!ObjectUtils.isEmpty(procBaseRespList)) {
            for (ProcBaseResp procBaseResp : procBaseRespList){
                if (!procIds.contains(procBaseResp.getProcId())){
                    procIds.add(procBaseResp.getProcId());
                }
            }
        }
        return procIds;
    }

    /**
     * 没有巡检的设施加入到工单返回类中
     * @author hedongwei@wistronits.com
     * @date  2019/5/17 16:35
     * @param procBaseRespForApps 工单返回类
     * @param inspectionRecordList 巡检记录集合
     * @return 工单返回类
     */
    public static List<ProcBaseRespForApp> notInspectionDeviceToProcBaseResp(List<ProcBaseRespForApp> procBaseRespForApps,
                                                                             List<ProcInspectionRecord> inspectionRecordList) {
        if (!ObjectUtils.isEmpty(inspectionRecordList) && !ObjectUtils.isEmpty(procBaseRespForApps)) {
            Map<String, List<ProcInspectionRecord>> notInspectionRecordMap = CastMapUtil.getNotProcInspectionRecordMap(inspectionRecordList);
            if (!ObjectUtils.isEmpty(notInspectionRecordMap)) {
                for (ProcBaseRespForApp respOne : procBaseRespForApps) {
                    if (!ObjectUtils.isEmpty(respOne)) {
                        //工单编号
                        String procId = respOne.getProcId();
                        if (notInspectionRecordMap.containsKey(procId)) {
                            List<ProcInspectionRecord> recordList = notInspectionRecordMap.get(procId);
                            respOne.setProcNotInspectionDeviceList(recordList);
                        }
                    }
                }
            }
        }
        return procBaseRespForApps;
    }

    /**
     * 获取部门编号集合
     * @author hedongwei@wistronits.com
     * @date  2019/6/24 16:46
     * @param departmentList 部门集合
     * @return 获取部门编号集合
     */
    public static List<String> getDepartmentIdList(List<Department> departmentList) {
        List<String> departmentIdList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(departmentList)) {
            for (Department department : departmentList) {
                departmentIdList.add(department.getId());
            }
        }
        return departmentIdList;
    }

    /**
     * 获取部门编号集合
     * @author hedongwei@wistronits.com
     * @date  2019/6/24 16:46
     * @param departmentList 部门集合
     * @return 获取部门编号集合
     */
    public static List<String> getRelatedDepartmentIdList(List<ProcRelatedDepartment> departmentList) {
        List<String> departmentIdList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(departmentList)) {
            for (ProcRelatedDepartment department : departmentList) {
                departmentIdList.add(department.getAccountabilityDept());
            }
        }
        return departmentIdList;
    }
}
