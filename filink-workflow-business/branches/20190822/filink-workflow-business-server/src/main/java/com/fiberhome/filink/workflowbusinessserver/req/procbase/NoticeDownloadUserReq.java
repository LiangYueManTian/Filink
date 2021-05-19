package com.fiberhome.filink.workflowbusinessserver.req.procbase;

import lombok.Data;

/**
 * 提醒下载用户参数
 * @author hedongwei@wistronits.com
 * @date 2019/4/11 12:20
 */
@Data
public class NoticeDownloadUserReq {

    /**
     * 用户编号
     */
    private String procId;

    /**
     * 执行人
     */
    private String assign;
}
