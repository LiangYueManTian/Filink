package com.fiberhome.filink.workflowbusinessserver.vo.statistical.normal;

import com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal.ProcDeviceTypeStatistical;
import com.fiberhome.filink.workflowbusinessserver.utils.common.ProcBaseUtil;
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
public class ProcDeviceTypeStatisticalVo extends ProcStatisticalBaseVo {

    /**
     * 处理方案
     */
    private String deviceType;

    /**
     * 处理方案名称
     */
    private String deviceTypeName;


    /**
     * 将查询到的工单故障原因统计数据转换成工单故障原因统计返回数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/29 15:15
     * @param statisticalList 工单故障原因集合
     * @return 工单故障原因统计返回数据
     */
    public static List<ProcDeviceTypeStatisticalVo> castDeviceTypeVoForProcStatusBean(List<ProcDeviceTypeStatistical> statisticalList) {
        //转换成工单故障原因统计返回类
        List<ProcDeviceTypeStatisticalVo> statisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(statisticalList)) {
            ProcDeviceTypeStatisticalVo statisticalVoOne = null;
            for (ProcDeviceTypeStatistical statisticalOne : statisticalList) {
                statisticalVoOne = new ProcDeviceTypeStatisticalVo();
                BeanUtils.copyProperties(statisticalOne, statisticalVoOne);
                //设施类型名称
                statisticalVoOne.setDeviceTypeName(ProcBaseUtil.getDeviceTypeName(statisticalOne.getDeviceType()));
                statisticalVoList.add(statisticalVoOne);
            }
        }
        return statisticalVoList;
    }

}
