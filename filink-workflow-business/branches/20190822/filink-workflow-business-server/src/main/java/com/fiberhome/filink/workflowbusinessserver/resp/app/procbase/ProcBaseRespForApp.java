package com.fiberhome.filink.workflowbusinessserver.resp.app.procbase;

import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    /**
     * 菜单关联设施信息加入到下载返回数据中
     * @author hedongwei@wistronits.com
     * @date  2019/6/25 13:48
     * @param procRelatedDeviceRespList 工单关联设施返回集合
     * @param procBaseRespForApps 工单主信息返回集合
     * @return 将菜单关联设施信息加入到下载返回数据中
     */
    public static List<ProcBaseRespForApp> getProcBaseRespForAppsListForProcRelatedDeviceRespList(List<ProcRelatedDeviceResp> procRelatedDeviceRespList, List<ProcBaseRespForApp> procBaseRespForApps) {
        //组装主表及关系表信息
        Map<String, List<ProcRelatedDeviceResp>> procRelatedDeviceListMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        List<ProcRelatedDeviceResp> procRelatedDeviceRespListTemp;
        if (!ObjectUtils.isEmpty(procRelatedDeviceRespList)) {
            for (ProcRelatedDeviceResp procRelatedDeviceResp : procRelatedDeviceRespList) {
                if (procRelatedDeviceListMap.containsKey(procRelatedDeviceResp.getProcId())) {
                    procRelatedDeviceRespListTemp = procRelatedDeviceListMap.get(procRelatedDeviceResp.getProcId());
                } else {
                    procRelatedDeviceRespListTemp = new ArrayList<>();
                }
                procRelatedDeviceRespListTemp.add(procRelatedDeviceResp);
                procRelatedDeviceListMap.put(procRelatedDeviceResp.getProcId(), procRelatedDeviceRespListTemp);
            }
        }

        for (ProcBaseRespForApp procBaseRespForApp : procBaseRespForApps) {
            procBaseRespForApp.setProcRelatedDeviceRespList(procRelatedDeviceListMap.get(procBaseRespForApp.getProcId()));
        }
        return procBaseRespForApps;
    }
}
