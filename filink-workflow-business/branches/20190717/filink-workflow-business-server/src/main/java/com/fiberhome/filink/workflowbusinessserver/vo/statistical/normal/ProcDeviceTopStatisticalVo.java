package com.fiberhome.filink.workflowbusinessserver.vo.statistical.normal;

import com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal.ProcDeviceTopStatistical;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单设施top统计
 * @author hedongwei@wistronits.com
 * @date 2019/5/29 14:55
 */
@Data
public class ProcDeviceTopStatisticalVo {

    /**
     * 区域编号
     */
    private String areaId;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 设施编号
     */
    private String deviceId;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 设施数量
     */
    private Integer deviceCount;

    /**
     * 将查询到的设施数量统计数据转换成设施数量统计返回数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/29 15:15
     * @param statisticalList 设施数量集合
     * @return 区域设施数量统计返回数据
     */
    public static List<ProcDeviceTopStatisticalVo> castDeviceTopVoForProcStatusBean(List<ProcDeviceTopStatistical> statisticalList) {
        //转换成工单故障原因统计返回类
        List<ProcDeviceTopStatisticalVo> statisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(statisticalList)) {
            ProcDeviceTopStatisticalVo statisticalVoOne = null;
            for (ProcDeviceTopStatistical statisticalOne : statisticalList) {
                statisticalVoOne = new ProcDeviceTopStatisticalVo();
                BeanUtils.copyProperties(statisticalOne, statisticalVoOne);
                statisticalVoList.add(statisticalVoOne);
            }
        }
        return statisticalVoList;
    }

}
