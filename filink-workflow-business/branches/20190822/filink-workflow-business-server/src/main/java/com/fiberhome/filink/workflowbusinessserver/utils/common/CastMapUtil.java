package com.fiberhome.filink.workflowbusinessserver.utils.common;

import com.fiberhome.filink.deviceapi.bean.AreaInfoForeignDto;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procclear.ProcClearFailure;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 转换map帮助类
 * @author hedongwei@wistronits.com
 * @date 2019/4/20 16:35
 */

public class CastMapUtil {

    /**
     * 获取部门map
     * @author hedongwei@wistronits.com
     * @date  2019/3/5 22:28
     * @param departments 部门数据
     * @return 返回部门map
     */
    public static Map<String, String> getDepartmentMap(List<Department> departments) {
        Map<String, String> departmentMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(departments)) {
            for (Department departmentOne : departments) {
                departmentMap.put(departmentOne.getId(), departmentOne.getDeptName());
            }
        }
        return departmentMap;
    }


    /**
     * 获取区域map
     * @author hedongwei@wistronits.com
     * @date  2019/3/5 22:28
     * @param areaInfoList 区域编号
     */
    public static Map<String, String> getAreaMap(List<AreaInfoForeignDto> areaInfoList) {
        Map<String, String> areaMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(areaInfoList)) {
            for (AreaInfoForeignDto areaInfo : areaInfoList) {
                if (!ObjectUtils.isEmpty(areaInfo)) {
                    areaMap.put(areaInfo.getAreaId(), areaInfo.getAreaName());
                }
            }
        }
        return areaMap;
    }


    /**
     * 设施map
     * @author hedongwei@wistronits.com
     * @date  2019/3/6 10:43
     * @param devices 设施集合
     */
    public static Map<String, String> getDeviceMap(List<DeviceInfoDto> devices) {
        Map<String, String> deviceMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(devices)) {
            for (DeviceInfoDto deviceOne : devices) {
                String deviceId = deviceOne.getDeviceId();
                deviceMap.put(deviceId, deviceOne.getDeviceName());
            }
        }
        return deviceMap;
    }

    /**
     * 获取工单关联设施map
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 22:42
     * @param procRelatedDeviceList 工单关联设施
     * @return 获取工单关联设施map
     */
    public static Map<String, List<ProcRelatedDevice>> getProcRelatedDeviceMap(List<ProcRelatedDevice> procRelatedDeviceList) {
        //工单设施map集合，已工单编号为键，以关联设施信息为值
        Map<String, List<ProcRelatedDevice>> procDeviceMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(procRelatedDeviceList)) {
            //工单设施map集合，已工单编号为键，以关联设施信息为值
            for (ProcRelatedDevice procRelatedDeviceOne : procRelatedDeviceList) {
                if (!ObjectUtils.isEmpty(procRelatedDeviceOne.getProcId())) {
                    String procId = procRelatedDeviceOne.getProcId();
                    List<ProcRelatedDevice> relatedDeviceList = new ArrayList<>();
                    if (procDeviceMap.containsKey(procId)) {
                        relatedDeviceList = procDeviceMap.get(procId);
                    }
                    relatedDeviceList.add(procRelatedDeviceOne);
                    procDeviceMap.put(procRelatedDeviceOne.getProcId(), relatedDeviceList);
                }
            }
        }
        return procDeviceMap;
    }

    /**
     * 获取工单巡检记录map
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 22:24
     * @param recordList 巡检记录集合
     * @return 获取工单巡检记录map
     */
    public static Map<String, List<ProcInspectionRecord>> getProcInspectionRecord(List<ProcInspectionRecord> recordList) {
        //工单设施map集合，以工单编号为键，以关联设施信息为值
        Map<String, List<ProcInspectionRecord>> procRecordMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(recordList)) {
            //工单设施map集合，以工单编号为键，以关联设施信息为值
            for (ProcInspectionRecord recordOne : recordList) {
                if (!ObjectUtils.isEmpty(recordOne.getProcId())) {
                    String procId = recordOne.getProcId();
                    List<ProcInspectionRecord> inspectionRecordList = new ArrayList<>();
                    if (procRecordMap.containsKey(procId)) {
                        inspectionRecordList = procRecordMap.get(procId);
                    }
                    inspectionRecordList.add(recordOne);
                    procRecordMap.put(procId, inspectionRecordList);
                }
            }
        }
        return procRecordMap;
    }

    /**
     * 获取
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 14:42
     * @param inspectionTaskDeviceList 巡检任务关联设施集合
     */
    public static Map<String, List<InspectionTaskDevice>> getInspectionDeviceMap(List<InspectionTaskDevice> inspectionTaskDeviceList) {
        //巡检任务设施map集合，以巡检任务编号为键，以关联设施信息为值
        Map<String, List<InspectionTaskDevice>> taskDeviceMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(inspectionTaskDeviceList)) {
            //巡检任务设施map集合，以巡检任务编号为键，以关联设施信息为值
            for (InspectionTaskDevice taskDeviceInfo : inspectionTaskDeviceList) {
                if (!ObjectUtils.isEmpty(taskDeviceInfo.getInspectionTaskId())) {
                    //巡检任务编号
                    String inspectionTaskId = taskDeviceInfo.getInspectionTaskId();
                    List<InspectionTaskDevice> taskDeviceList = new ArrayList<>();
                    if (taskDeviceMap.containsKey(inspectionTaskId)) {
                        taskDeviceList = taskDeviceMap.get(inspectionTaskId);
                    }
                    taskDeviceList.add(taskDeviceInfo);
                    taskDeviceMap.put(inspectionTaskId, taskDeviceList);
                }
            }
        }
        return taskDeviceMap;
    }

    /**
     * 获取巡检工单map
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 22:03
     * @param inspectionList 巡检工单集合
     * @return 获取巡检工单map
     */
    public static Map<String, ProcInspection> getProcInspectionMap(List<ProcInspection> inspectionList) {
        Map<String, ProcInspection> map = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(inspectionList)) {
            for (ProcInspection procInspectionOne : inspectionList) {
                if (!ObjectUtils.isEmpty(procInspectionOne.getProcId())) {
                    map.put(procInspectionOne.getProcId(), procInspectionOne);
                }
            }
        }
        return map;
    }

    /**
     * 获取编号map
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 23:43
     * @param ids 编号集合
     * @return 编号map
     */
    public static Map<String, String> getMapForListString(List<String> ids) {
        Map<String, String> map = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(ids)) {
            for (String deviceId : ids) {
                map.put(deviceId, "0");
            }
        }
        return map;
    }

    /**
     * 获取工单map
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 10:32
     * @param procBaseList 工单集合
     * @return 工单map
     */
    public static Map<String, ProcBase> getProcBaseMap(List<ProcBase> procBaseList) {
        Map<String, ProcBase> map = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(procBaseList)) {
            for (ProcBase procBaseOne : procBaseList) {
                if (!ObjectUtils.isEmpty(procBaseOne)) {
                    map.put(procBaseOne.getProcId(), procBaseOne);
                }
            }
        }
        return map;
    }

    /**
     * 获取是否有正在进行的工单告警map信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/6 11:02
     * @param procBaseList 工单集合
     * @param alarmMap 告警map
     * @return 告警map
     */
    public static Map<String, Object> getIsExistsAlarmMap(List<ProcBase> procBaseList, Map<String, Object> alarmMap) {
        if (!ObjectUtils.isEmpty(procBaseList)) {
            for (ProcBase procBaseOne : procBaseList) {
                String existsAlarmId = procBaseOne.getRefAlarm();
                if (!StringUtils.isEmpty(existsAlarmId)) {
                    if (alarmMap.containsKey(existsAlarmId)) {
                        alarmMap.put(existsAlarmId, "1");
                    }
                }
            }
        }
        return alarmMap;
    }

    /**
     * 获取是否有正在进行的工单告警map信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/6 11:02
     * @param procClearFailureList 工单集合
     * @param alarmMap 告警map
     * @return 告警map
     */
    public static Map<String, Object> getIsExistsClearAlarmMap(List<ProcClearFailure> procClearFailureList, Map<String, Object> alarmMap) {
        if (!ObjectUtils.isEmpty(procClearFailureList)) {
            for (ProcClearFailure procClearFailureOne : procClearFailureList) {
                String existsAlarmId = procClearFailureOne.getRefAlarm();
                if (!StringUtils.isEmpty(existsAlarmId)) {
                    if (alarmMap.containsKey(existsAlarmId)) {
                        alarmMap.put(existsAlarmId, "1");
                    }
                }
            }
        }
        return alarmMap;
    }

    /**
     * 获取关联设施map
     * @author hedongwei@wistronits.com
     * @date  2019/5/14 9:54
     * @param type 类型
     * @param procRelatedDevices 关联设施信息
     * @return 获取关联设施map
     */
    public static Map<String,Object> getProcRelatedDeviceMapList(String type, List<ProcRelatedDevice> procRelatedDevices) {
        Map<String,Object> procRelatedDeviceMaps = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(procRelatedDevices)){
            //设施id
            if (ProcBaseConstants.DEVICE_IDS.equals(type)){
                for (ProcRelatedDevice procRelatedDevice : procRelatedDevices){
                    procRelatedDeviceMaps.put(procRelatedDevice.getDeviceId(),procRelatedDevice);
                }
                //区域id
            } else if (ProcBaseConstants.AREA_IDS.equals(type)){
                for (ProcRelatedDevice procRelatedDevice : procRelatedDevices){
                    procRelatedDeviceMaps.put(procRelatedDevice.getDeviceAreaId(),procRelatedDevice);
                }
            }
        }
        return procRelatedDeviceMaps;
    }

    /**
     * 获取销障工单关联设施map
     * @author hedongwei@wistronits.com
     * @date  2019/6/22 17:23
     * @param type 类型
     * @param procClearFailureList 销障工单集合
     * @return 返回销障工单关联设施map
     */
    public static Map<String, ProcClearFailure> getProcClearRelatedDeviceMapList(String type, List<ProcClearFailure> procClearFailureList) {
        Map<String, ProcClearFailure> procRelatedDeviceMaps = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(procClearFailureList)) {
            //设施id
            if (ProcBaseConstants.DEVICE_IDS.equals(type)){
                for (ProcClearFailure procClearFailure : procClearFailureList){
                    procRelatedDeviceMaps.put(procClearFailure.getDeviceId(), procClearFailure);
                }
                //区域id
            } else if (ProcBaseConstants.AREA_IDS.equals(type)){
                for (ProcClearFailure procClearFailure : procClearFailureList){
                    procRelatedDeviceMaps.put(procClearFailure.getDeviceAreaId(), procClearFailure);
                }
            }
        }
        return procRelatedDeviceMaps;
    }

    /**
     * 获取没有巡检的记录map，以工单id为key，巡检记录为值
     * @author hedongwei@wistronits.com
     * @date  2019/5/17 16:51
     * @param inspectionRecordList 巡检记录集合
     * @return  获取没有巡检的记录map
     */
    public static Map<String, List<ProcInspectionRecord>> getNotProcInspectionRecordMap(List<ProcInspectionRecord> inspectionRecordList) {
        Map<String, List<ProcInspectionRecord>> recordMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(inspectionRecordList)) {
            for (ProcInspectionRecord procInspectionRecordOne : inspectionRecordList) {
                List<ProcInspectionRecord> procInspectionRecordList = new ArrayList<>();
                if (!ObjectUtils.isEmpty(procInspectionRecordOne)) {
                    String procId = procInspectionRecordOne.getProcId();
                    if (recordMap.containsKey(procId)) {
                        procInspectionRecordList = recordMap.get(procId);
                    }
                    //巡检结果为空时需要返回下载数据
                    if (ObjectUtils.isEmpty(procInspectionRecordOne.getResult())) {
                        procInspectionRecordList.add(procInspectionRecordOne);
                        recordMap.put(procId, procInspectionRecordList);
                    }
                }
            }
        }
        return recordMap;
    }


    /**
     * 根据工单id获取巡检记录map 以设施id为key
     * @author hedongwei@wistronits.com
     * @date  2019/5/17 17:30
     * @param recordList 巡检记录集合
     * @return 巡检记录map
     */
    public static Map<String, ProcInspectionRecord> getProcInspectionRecordMapForProcId(List<ProcInspectionRecord> recordList) {
        Map<String, ProcInspectionRecord> recordMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(recordList)) {
            for (ProcInspectionRecord procInspectionRecord : recordList) {
                recordMap.put(procInspectionRecord.getDeviceId(), procInspectionRecord);
            }
        }
        return recordMap;
    }


}
