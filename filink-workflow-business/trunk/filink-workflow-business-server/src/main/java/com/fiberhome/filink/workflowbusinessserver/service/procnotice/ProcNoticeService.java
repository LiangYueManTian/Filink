package com.fiberhome.filink.workflowbusinessserver.service.procnotice;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.NoticeDownloadUserReq;

/**
 * <p>
 * 工单推送 服务类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-03-11
 */
public interface ProcNoticeService {

    /**
     * 工单编号
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 9:37
     * @param noticeDownloadUserReq 提醒下载人员参数
     * @return 通知下载人员
     */
    Result noticeDownloadUser(NoticeDownloadUserReq noticeDownloadUserReq);

    /**
     * 消息推送
     * @author hedongwei@wistronits.com
     * @date  2019/4/25 11:11
     */
    void noticeInfo();

}
