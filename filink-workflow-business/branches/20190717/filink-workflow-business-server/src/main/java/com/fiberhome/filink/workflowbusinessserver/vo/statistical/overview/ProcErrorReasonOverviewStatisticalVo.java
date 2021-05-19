package com.fiberhome.filink.workflowbusinessserver.vo.statistical.overview;

import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.overview.ProcErrorReasonOverviewStatistical;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.overview.ProcProcessingSchemeOverviewStatistical;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcStatisticalConstants;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.overview.QueryListProcOverviewGroupByErrorReasonReq;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.overview.QueryListProcOverviewGroupByProcessingSchemeReq;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单故障原因概览返回类
 * @author hedongwei@wistronits.com
 * @date 2019/5/30 13:38
 */
@Data
public class ProcErrorReasonOverviewStatisticalVo extends  ProcOverviewStatisticalBaseVo {

    /**
     * 故障原因
     */
    private String errorReason;

    /**
     * 故障原因名称
     */
    private String errorReasonName;



    /**
     * 获取默认的故障原因统计的信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 10:38
     * @param req 故障原因统计参数
     * @return 获取默认参数信息
     */
    public static List<ProcErrorReasonOverviewStatisticalVo> getDefaultOverviewErrorReasonVoList(QueryListProcOverviewGroupByErrorReasonReq req) {

        //故障原因统计信息
        List<String> errorReasonList = ProcStatisticalConstants.errorReasonList;

        //工单故障原因初始化集合
        List<ProcErrorReasonOverviewStatisticalVo> processingSchemeVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(errorReasonList)) {
            ProcBase procBase = null;
            ProcErrorReasonOverviewStatisticalVo overViewOverview = null;
            for (String errorReason : errorReasonList) {
                overViewOverview = new ProcErrorReasonOverviewStatisticalVo();
                overViewOverview.setErrorReason(errorReason);
                overViewOverview.setOrderCount(0);
                procBase = new ProcBase();
                procBase.setErrorReason(errorReason);
                overViewOverview.setErrorReasonName(procBase.getErrorReasonName());
                processingSchemeVoList.add(overViewOverview);
            }
        }
        return processingSchemeVoList;
    }


    /**
     * 将查询到的工单故障原因统计数据转换成工单故障原因统计返回数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/29 15:15
     * @param statisticalList 工单故障原因集合
     * @return 工单故障原因统计返回数据
     */
    public static List<ProcErrorReasonOverviewStatisticalVo> castErrorReasonVoForErrorReasonBean(List<ProcErrorReasonOverviewStatistical> statisticalList) {
        //转换成工单故障原因统计返回类
        List<ProcErrorReasonOverviewStatisticalVo> statisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(statisticalList)) {
            ProcErrorReasonOverviewStatisticalVo statisticalVoOne = null;
            ProcBase procBase = null;
            for (ProcErrorReasonOverviewStatistical statisticalOne : statisticalList) {
                statisticalVoOne = new ProcErrorReasonOverviewStatisticalVo();
                procBase = new ProcBase();
                BeanUtils.copyProperties(statisticalOne, statisticalVoOne);
                procBase.setErrorReason(statisticalOne.getErrorReason());
                //故障原因名称
                statisticalVoOne.setErrorReasonName(procBase.getErrorReasonName());
                statisticalVoList.add(statisticalVoOne);
            }
        }
        return statisticalVoList;
    }
}
