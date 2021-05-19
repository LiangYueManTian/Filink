package com.fiberhome.filink.workflowbusinessserver.vo.statistical.normal;

import com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal.ProcAreaPercentStatistical;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单区域工单比统计返回类
 * @author hedongwei@wistronits.com
 * @date 2019/5/29 14:55
 */
@Data
public class ProcAreaPercentStatisticalVo extends ProcStatisticalBaseVo {


    /**
     * 将查询到的工单区域工单比统计数据转换成工单区域工单比统计返回数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/29 15:15
     * @param statisticalList 区域工单比集合
     * @return 区域工单比统计返回数据
     */
    public static List<ProcAreaPercentStatisticalVo> castAreaPercentVoForProcStatusBean(List<ProcAreaPercentStatistical> statisticalList) {
        //转换成工单故障原因统计返回类
        List<ProcAreaPercentStatisticalVo> statisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(statisticalList)) {
            ProcAreaPercentStatisticalVo statisticalVoOne = null;
            for (ProcAreaPercentStatistical statisticalOne : statisticalList) {
                statisticalVoOne = new ProcAreaPercentStatisticalVo();
                BeanUtils.copyProperties(statisticalOne, statisticalVoOne);
                statisticalVoList.add(statisticalVoOne);
            }
        }
        return statisticalVoList;
    }

}
