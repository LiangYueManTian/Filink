package com.fiberhome.filink.workflowbusinessserver.vo.statistical.normal;

import com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal.ProcDepartmentStatistical;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 工单部门统计返回类
 * @author hedongwei@wistronits.com
 * @date 2019/5/29 14:55
 */
@Data
public class ProcDepartmentStatisticalVo {

    /**
     * 责任部门id
     */
    private String departmentId;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 责任部门数量
     */
    private Integer departmentCount;

    /**
     * 将查询到的工单部门统计数据转换成工单部门统计返回数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/29 15:15
     * @param statisticalList 工单部门集合
     * @return 工单部门统计返回数据
     */
    public static List<ProcDepartmentStatisticalVo> castDepartmentVoForProcStatusBean(List<ProcDepartmentStatistical> statisticalList, Map<String, String> departmentMap) {
        //转换成工单工单部门统计返回类
        List<ProcDepartmentStatisticalVo> statisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(statisticalList)) {
            ProcDepartmentStatisticalVo statisticalVoOne = null;
            for (ProcDepartmentStatistical statisticalOne : statisticalList) {
                statisticalVoOne = new ProcDepartmentStatisticalVo();
                BeanUtils.copyProperties(statisticalOne, statisticalVoOne);
                if (!ObjectUtils.isEmpty(departmentMap)) {
                    String departmentName = departmentMap.get(statisticalOne.getDepartmentId());
                    if (!ObjectUtils.isEmpty(departmentName)) {
                        statisticalVoOne.setDepartmentName(departmentName);
                    }
                }
                statisticalVoList.add(statisticalVoOne);
            }
        }
        return statisticalVoList;
    }

}
