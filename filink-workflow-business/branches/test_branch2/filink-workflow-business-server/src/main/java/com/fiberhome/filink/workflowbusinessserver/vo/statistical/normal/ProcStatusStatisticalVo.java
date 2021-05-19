package com.fiberhome.filink.workflowbusinessserver.vo.statistical.normal;

import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal.ProcStatusStatistical;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单状态统计返回类
 * @author hedongwei@wistronits.com
 * @date 2019/5/29 14:55
 */
@Data
public class ProcStatusStatisticalVo extends ProcStatisticalBaseVo {

    /**
     * 状态
     */
    private String status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 将查询到的工单状态统计数据转换成工单状态统计返回数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/29 15:15
     * @param statisticalList 工单状态集合
     * @return 工单状态统计返回数据
     */
    public static List<ProcStatusStatisticalVo> castProcStatusVoForProcStatusBean(List<ProcStatusStatistical> statisticalList) {
        //转换成工单状态统计返回类
        List<ProcStatusStatisticalVo> statisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(statisticalList)) {
            ProcStatusStatisticalVo statisticalVoOne = null;
            ProcBase procBase = null;
            for (ProcStatusStatistical statisticalOne : statisticalList) {
                statisticalVoOne = new ProcStatusStatisticalVo();
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
