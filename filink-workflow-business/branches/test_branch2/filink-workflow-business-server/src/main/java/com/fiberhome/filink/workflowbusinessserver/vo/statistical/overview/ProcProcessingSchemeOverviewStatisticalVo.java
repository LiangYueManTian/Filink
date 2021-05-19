package com.fiberhome.filink.workflowbusinessserver.vo.statistical.overview;

import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.overview.ProcProcessingSchemeOverviewStatistical;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcStatisticalConstants;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.overview.QueryListProcOverviewGroupByProcessingSchemeReq;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单处理方案概览返回类
 * @author hedongwei@wistronits.com
 * @date 2019/5/30 13:38
 */
@Data
public class ProcProcessingSchemeOverviewStatisticalVo extends  ProcOverviewStatisticalBaseVo {

    /**
     * 处理方案
     */
    private String processingScheme;

    /**
     * 处理方案名称
     */
    private String processingSchemeName;



    /**
     * 获取默认的处理方案统计的信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 10:38
     * @param req 参数
     * @return 获取默认参数信息
     */
    public static List<ProcProcessingSchemeOverviewStatisticalVo> getDefaultOverviewProcessingSchemeVoList(QueryListProcOverviewGroupByProcessingSchemeReq req) {

        //处理方案统计信息
        List<String> processingSchemeList = ProcStatisticalConstants.processingSchemeList;

        //工单处理方案初始化集合
        List<ProcProcessingSchemeOverviewStatisticalVo> processingSchemeVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(processingSchemeList)) {
            ProcProcessingSchemeOverviewStatisticalVo overViewOverview = null;
            ProcBase procBase = null;
            for (String processingScheme : processingSchemeList) {
                overViewOverview = new ProcProcessingSchemeOverviewStatisticalVo();
                overViewOverview.setProcessingScheme(processingScheme);
                overViewOverview.setOrderCount(0);
                procBase = new ProcBase();
                procBase.setProcessingScheme(processingScheme);
                overViewOverview.setProcessingSchemeName(procBase.getProcessingSchemeName());
                processingSchemeVoList.add(overViewOverview);
            }
        }
        return processingSchemeVoList;
    }


    /**
     * 将查询到的工单处理方案统计数据转换成工单处理方案统计返回数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/29 15:15
     * @param statisticalList 工单处理方案集合
     * @return 工单处理方案统计返回数据
     */
    public static List<ProcProcessingSchemeOverviewStatisticalVo> castProcessingSchemeVoForProcErrorReasonBean(List<ProcProcessingSchemeOverviewStatistical> statisticalList) {
        //转换成工单处理方案统计返回类
        List<ProcProcessingSchemeOverviewStatisticalVo> statisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(statisticalList)) {
            ProcProcessingSchemeOverviewStatisticalVo statisticalVoOne = null;
            ProcBase procBase = null;
            for (ProcProcessingSchemeOverviewStatistical statisticalOne : statisticalList) {
                statisticalVoOne = new ProcProcessingSchemeOverviewStatisticalVo();
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
