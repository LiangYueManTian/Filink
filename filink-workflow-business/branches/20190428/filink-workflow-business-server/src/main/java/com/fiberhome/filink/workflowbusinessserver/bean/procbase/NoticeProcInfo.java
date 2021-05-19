package com.fiberhome.filink.workflowbusinessserver.bean.procbase;

import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 提醒的工单信息
 * @author hedongwei@wistronits.com
 * @date 2019/4/11 10:07
 */
@Data
public class NoticeProcInfo {

    /**
     * 工单编号
     */
    private String procId;

    /**
     * 工单名称
     */
    private String title;

    /**
     * 工单类型
     */
    private String procType;


    /**
     * 转换类为提醒工单信息类
     * @author hedongwei@wistronits.com
     * @date  2019/4/20 18:43
     * @param procBase 流程信息
     * @return 提醒工单信息
     */
    public static NoticeProcInfo castProcBaseToNoticeProcInfo(ProcBase procBase) {
        NoticeProcInfo noticeProcInfo = new NoticeProcInfo();
        BeanUtils.copyProperties(procBase, noticeProcInfo);
        return noticeProcInfo;
    }

}
