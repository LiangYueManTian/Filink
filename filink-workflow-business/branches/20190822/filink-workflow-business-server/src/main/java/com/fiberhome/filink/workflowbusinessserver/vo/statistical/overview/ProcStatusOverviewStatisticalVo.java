package com.fiberhome.filink.workflowbusinessserver.vo.statistical.overview;

import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.overview.ProcStatusOverviewStatistical;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcStatisticalConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcStatusOverviewConstants;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.overview.QueryListProcOverviewGroupByProcStatusReq;
import com.fiberhome.filink.workflowbusinessserver.utils.common.CalculateUtil;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 工单状态概览返回类
 * @author hedongwei@wistronits.com
 * @date 2019/5/30 13:38
 */
@Data
public class ProcStatusOverviewStatisticalVo extends  ProcOverviewStatisticalBaseVo {

    /**
     * 状态
     */
    private String status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 工单占比
     */
    private double orderPercent;


    /**
     * 将工单总数加入到工单状态返回类
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 14:29
     * @param vo 工单状态返回类
     * @param allCount 统计的总数
     * @return 将工单总数加入到工单状态返回类
     */
    public static ProcStatusOverviewStatisticalVo setOrderPercentToVo(ProcStatusOverviewStatisticalVo vo, int allCount) {
        double orderPercent = 0;
        if (!ObjectUtils.isEmpty(vo.getOrderPercent())) {
            if (!ObjectUtils.isEmpty(allCount) && 0 != allCount) {
                //单个状态的工单数量
                Integer orderCount = vo.getOrderCount();
                //单个状态的工单数量占统计工单数量的百分比
                double percentDouble = CalculateUtil.calculatePercent(orderCount, allCount);
                orderPercent = percentDouble;
            } else {
                orderPercent = 100;
            }
            vo.setOrderPercent(orderPercent);
        }
        return vo;
    }

    /**
     * 获取默认的工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 10:38
     * @param req 参数
     * @return 获取默认参数信息
     */
    public static List<ProcStatusOverviewStatisticalVo> getDefaultOverviewStatusVoList(QueryListProcOverviewGroupByProcStatusReq req) {
        List<String> statusList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(req)) {
            //查询类型
            String statisticalType = req.getStatisticalType();
            if (!ObjectUtils.isEmpty(statisticalType)) {
                if (ProcStatusOverviewConstants.STATISTICAL_TYPE_1.equals(statisticalType)) {
                    //未完工列表
                    statusList = ProcStatisticalConstants.statusProcessingList;
                } else if (ProcStatusOverviewConstants.STATISTICAL_TYPE_2.equals(statisticalType)) {
                    //历史列表
                    statusList = ProcStatisticalConstants.statusCompletedList;
                } else {
                    //全部列表
                    statusList = ProcStatisticalConstants.statusList;
                }
            }
        }

        //工单状态初始化集合
        List<ProcStatusOverviewStatisticalVo> procStatusVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(statusList)) {
            ProcStatusOverviewStatisticalVo overViewOverview = null;
            ProcBase procBase = null;
            for (String status : statusList) {
                overViewOverview = new ProcStatusOverviewStatisticalVo();
                overViewOverview.setStatus(status);
                overViewOverview.setOrderCount(0);
                procBase = new ProcBase();
                procBase.setStatus(status);
                overViewOverview.setStatusName(procBase.getStatusName());
                procStatusVoList.add(overViewOverview);
            }
        }
        return procStatusVoList;
    }


    /**
     * 将查询到的工单状态统计数据转换成工单状态统计返回数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/29 15:15
     * @param statisticalList 工单状态集合
     * @return 工单状态统计返回数据
     */
    public static List<ProcStatusOverviewStatisticalVo> castProcStatusVoForProcStatusBean(List<ProcStatusOverviewStatistical> statisticalList) {
        //转换成工单状态统计返回类
        List<ProcStatusOverviewStatisticalVo> statisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(statisticalList)) {
            ProcStatusOverviewStatisticalVo statisticalVoOne = null;
            ProcBase procBase = null;
            for (ProcStatusOverviewStatistical statisticalOne : statisticalList) {
                statisticalVoOne = new ProcStatusOverviewStatisticalVo();
                procBase = new ProcBase();
                BeanUtils.copyProperties(statisticalOne, statisticalVoOne);
                procBase.setStatus(statisticalOne.getStatus());
                //状态名称
                statisticalVoOne.setStatusName(procBase.getStatusName());
                statisticalVoList.add(statisticalVoOne);
            }
        }
        return statisticalVoList;
    }
}
