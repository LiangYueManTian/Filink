package com.fiberhome.filink.workflowbusinessserver.vo.statistical.normal;

import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
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
public class ProcProcessingSchemeStatisticalVo extends ProcStatisticalBaseVo {

    /**
     * 处理方案
     */
    private String processingScheme;

    /**
     * 处理方案名称
     */
    private String processingSchemeName;


    /**
     * 将查询到的工单处理方案统计数据转换成工单处理方案统计返回数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/29 15:15
     * @param statisticalList 工单状态集合
     * @return 工单处理方案统计返回数据
     */
    public static List<ProcProcessingSchemeStatisticalVo> castProcessingSchemeVoForProcStatusBean(List<ProcProcessingSchemeStatistical> statisticalList) {
        //转换成工单状态统计返回类
        List<ProcProcessingSchemeStatisticalVo> statisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(statisticalList)) {
            ProcProcessingSchemeStatisticalVo statisticalVoOne = null;
            ProcBase procBase = null;
            for (ProcProcessingSchemeStatistical statisticalOne : statisticalList) {
                statisticalVoOne = new ProcProcessingSchemeStatisticalVo();
                procBase = new ProcBase();
                BeanUtils.copyProperties(statisticalOne, statisticalVoOne);
                procBase.setProcessingScheme(statisticalOne.getProcessingScheme());
                //处理方案名称
                statisticalVoOne.setProcessingSchemeName(procBase.getProcessingSchemeName());
                statisticalVoList.add(statisticalVoOne);
            }
        }
        return statisticalVoList;
    }

}
