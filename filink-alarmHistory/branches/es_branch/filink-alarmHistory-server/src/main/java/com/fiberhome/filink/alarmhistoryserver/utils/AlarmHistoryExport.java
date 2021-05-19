package com.fiberhome.filink.alarmhistoryserver.utils;

import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory;
import com.fiberhome.filink.alarmhistoryserver.service.impl.AlarmHistoryServiceImpl;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.userapi.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 导出方法
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Component
public class AlarmHistoryExport extends AbstractExport {

    /**
     * 历史告警service
     */
    @Autowired
    private AlarmHistoryServiceImpl alarmHistoryService;

    /**
     * 查询导出信息
     *
     * @param queryCondition 条件
     * @return 导出数据
     */
    @Override
    protected List queryData(QueryCondition queryCondition) {
        PageBean pageBean = alarmHistoryService.queryAlarmHistoryListExport(queryCondition);
        List<AlarmHistory> alarmHistoryList = (List<AlarmHistory>) pageBean.getData();
        return alarmHistoryList;
    }

    /**
     * 查询count
     *
     * @param queryCondition
     * @return count信息
     */
    @Override
    protected Integer queryCount(QueryCondition queryCondition) {
        User user = alarmHistoryService.getExportUser();
        return alarmHistoryService.getCount(queryCondition, user);
    }
}
