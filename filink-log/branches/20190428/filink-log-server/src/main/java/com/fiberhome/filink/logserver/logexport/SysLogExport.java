package com.fiberhome.filink.logserver.logexport;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.logserver.bean.GetMongoQueryData;
import com.fiberhome.filink.logserver.bean.SystemLog;
import com.fiberhome.filink.logserver.bean.SystemLogExportBean;
import com.fiberhome.filink.logserver.constant.LogConstants;
import com.fiberhome.filink.logserver.service.impl.LogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 系统日志列表导出类
 *
 * @author qiqizhu@wistronits.com
 */
@Component
public class SysLogExport extends AbstractExport {
    @Autowired
    private LogServiceImpl logService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    protected List queryData(QueryCondition condition) {
        Query query = conditionToQuery(condition);
        List<SystemLog> logList = mongoTemplate.find(query, SystemLog.class);
        //将查询的系统日志的结果转换成导出的数据
        List<SystemLogExportBean> exportLogList = SystemLogExportBean.getSystemLogExportBeanForSystemLog(logList);
        return exportLogList;
    }

    @Override
    protected Integer queryCount(QueryCondition condition) {
        Query query = conditionToQuery(condition);
        long count = mongoTemplate.count(query, LogConstants.SYSTEM_LOG_TABLE_NAME);
        return (int) count;
    }

    private Query conditionToQuery(QueryCondition condition) {
        Query query = null;
        //将查询条件创建成query对象
        GetMongoQueryData queryResult = logService.castToMongoQuery(condition, query);
        query = queryResult.getQuery();
        return query;
    }
}
