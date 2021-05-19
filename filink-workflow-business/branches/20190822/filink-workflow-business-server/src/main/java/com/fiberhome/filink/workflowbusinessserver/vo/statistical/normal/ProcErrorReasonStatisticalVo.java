package com.fiberhome.filink.workflowbusinessserver.vo.statistical.normal;

import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal.ProcErrorReasonStatistical;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal.ProcProcessingSchemeStatistical;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单处理方案统计返回类
 * @author hedongwei@wistronits.com
 * @date 2019/5/29 14:55
 */
@Data
public class ProcErrorReasonStatisticalVo extends ProcStatisticalBaseVo {

    /**
     * 处理方案
     */
    private String errorReason;

    /**
     * 处理方案名称
     */
    private String errorReasonName;


    /**
     * 将查询到的工单故障原因统计数据转换成工单故障原因统计返回数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/29 15:15
     * @param statisticalList 工单故障原因集合
     * @return 工单故障原因统计返回数据
     */
    public static List<ProcErrorReasonStatisticalVo> castErrorReasonVoForProcStatusBean(List<ProcErrorReasonStatistical> statisticalList) {
        //转换成工单故障原因统计返回类
        List<ProcErrorReasonStatisticalVo> statisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(statisticalList)) {
            ProcErrorReasonStatisticalVo statisticalVoOne = null;
            ProcBase procBase = null;
            for (ProcErrorReasonStatistical statisticalOne : statisticalList) {
                statisticalVoOne = new ProcErrorReasonStatisticalVo();
                procBase = new ProcBase();
                BeanUtils.copyProperties(statisticalOne, statisticalVoOne);
                procBase.setErrorReason(statisticalOne.getErrorReason());
                //故障原因名称
                statisticalVoOne.setErrorReasonName(procBase.getProcessingSchemeName());
                statisticalVoList.add(statisticalVoOne);
            }
        }
        return statisticalVoList;
    }

}
