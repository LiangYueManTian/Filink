package com.fiberhome.filink.workflowbusinessserver.req.procinspection;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.workflowbusinessserver.utils.common.ProcBaseUtil;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 查询已完成的巡检信息参数类
 * @author hedongwei@wistronits.com
 * @date 2019/3/14 18:54
 */
@Data
public class CompleteInspectionByPageReq {

    /**
     * 工单编号
     */
    private String procId;

    /**
     * 设施编号集合
     */
    private List<String> deviceIds;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 巡检结果
     */
    private List<String> results;

    /**
     * 巡检结果
     */
    private String result;

    /**
     * 异常描述
     */
    private String exceptionDescription;

    /**
     * 责任人
     */
    private List<String> updateUser;

    /**
     * 资源匹配情况
     */
    private String resourceMatching;

    /**
     * 巡检时间集合
     */
    private List<Long> inspectionTimes;

    /**
     * 返回转义后的工单查询条件
     * @author hedongwei@wistronits.com
     * @date  2019/8/12 17:06
     * @param req 工单参数
     * @return 返回工单查询条件
     */
    public static QueryCondition<CompleteInspectionByPageReq> getParsePageReq(QueryCondition<CompleteInspectionByPageReq> req) {
        if (!ObjectUtils.isEmpty(req.getBizCondition())) {
            CompleteInspectionByPageReq pageReq = req.getBizCondition();
            String parseDeviceName = ProcBaseUtil.alterLikeValue(pageReq.getDeviceName());
            String exceptionDescription = ProcBaseUtil.alterLikeValue(pageReq.getExceptionDescription());
            String resourceMatching = ProcBaseUtil.alterLikeValue(pageReq.getResourceMatching());
            pageReq.setDeviceName(parseDeviceName);
            pageReq.setExceptionDescription(exceptionDescription);
            pageReq.setResourceMatching(resourceMatching);
            req.setBizCondition(pageReq);
        }
        return req;
    }

}
