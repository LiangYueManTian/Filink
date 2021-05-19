package com.fiberhome.filink.logserver.logexport;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.logserver.bean.GetMongoQueryData;
import com.fiberhome.filink.logserver.bean.OperateLog;
import com.fiberhome.filink.logserver.bean.OperateLogExportBean;
import com.fiberhome.filink.logserver.constant.LogConstants;
import com.fiberhome.filink.logserver.service.impl.LogServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 操作日志列表导出类
 *
 * @author qiqizhu@wistronits.com
 */
@Component
@Slf4j
public class OperateLogExport extends AbstractExport {
    @Autowired
    private LogServiceImpl logService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    protected List queryData(QueryCondition condition) {
        Query query = conditionToQuery(condition);
        Long nowDate = System.currentTimeMillis();
        log.info("查询操作日志导出数据开始:" );
        List<OperateLog> logList = mongoTemplate.find(query, OperateLog.class);
        Long beforeDate = System.currentTimeMillis();
        log.info("查询操作日志导出数据结束，耗时"  + ((beforeDate - nowDate)/1000L) + "秒" );
        //将查询到的操作日志的结果转换成导出的数据
        List<OperateLogExportBean> exportLogList = OperateLogExportBean.getOperateLogExportBeanForOperateLog(logList);
        return exportLogList;
    }

    @Override
    protected Integer queryCount(QueryCondition condition) {
        Query query = conditionToQuery(condition);
        long count = mongoTemplate.count(query, LogConstants.OPERATE_LOG_TABLE_NAME);
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
