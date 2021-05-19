package com.fiberhome.filink.workflowbusinessserver.vo.statistical.overview;

import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.overview.ProcDeviceTypeOverviewStatistical;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcStatisticalConstants;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.overview.QueryListProcOverviewGroupByDeviceTypeReq;
import com.fiberhome.filink.workflowbusinessserver.utils.common.ProcBaseUtil;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单设施类型概览返回类
 * @author hedongwei@wistronits.com
 * @date 2019/5/30 13:38
 */
@Data
public class ProcDeviceTypeOverviewStatisticalVo extends  ProcOverviewStatisticalBaseVo {

    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 设施类型名称
     */
    private String deviceTypeName;



    /**
     * 获取默认的设施类型统计的信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 10:38
     * @param req 设施类型统计参数
     * @return 获取默认参数信息
     */
    public static List<ProcDeviceTypeOverviewStatisticalVo> getDefaultOverviewDeviceTypeVoList(QueryListProcOverviewGroupByDeviceTypeReq req) {

        //设施类型统计信息
        List<String> deviceTypeList = ProcStatisticalConstants.deviceTypeList;

        //工单设施类型初始化集合
        List<ProcDeviceTypeOverviewStatisticalVo> deviceTypeVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(deviceTypeList)) {
            ProcDeviceTypeOverviewStatisticalVo overViewOverview = null;
            for (String deviceType : deviceTypeList) {
                overViewOverview = new ProcDeviceTypeOverviewStatisticalVo();
                overViewOverview.setDeviceType(deviceType);
                overViewOverview.setOrderCount(0);
                overViewOverview.setDeviceTypeName(ProcBaseUtil.getDeviceTypeName(deviceType));
                deviceTypeVoList.add(overViewOverview);
            }
        }
        return deviceTypeVoList;
    }


    /**
     * 将查询到的工单设施类型统计数据转换成工单设施类型统计返回数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/29 15:15
     * @param statisticalList 工单设施类型统计集合
     * @return 工单设施类型统计返回数据
     */
    public static List<ProcDeviceTypeOverviewStatisticalVo> castDeviceTypeVoForProcDeviceTypeBean(List<ProcDeviceTypeOverviewStatistical> statisticalList) {
        //转换成工单设施类型统计返回类
        List<ProcDeviceTypeOverviewStatisticalVo> statisticalVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(statisticalList)) {
            ProcDeviceTypeOverviewStatisticalVo statisticalVoOne = null;
            for (ProcDeviceTypeOverviewStatistical statisticalOne : statisticalList) {
                statisticalVoOne = new ProcDeviceTypeOverviewStatisticalVo();
                BeanUtils.copyProperties(statisticalOne, statisticalVoOne);
                //设施类型名称
                statisticalVoOne.setDeviceTypeName(ProcBaseUtil.getDeviceTypeName(statisticalOne.getDeviceType()));
                statisticalVoList.add(statisticalVoOne);
            }
        }
        return statisticalVoList;
    }
}
