package com.fiberhome.filink.fdevice.service.device_log.impl;

import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.fdevice.bean.device_log.DeviceLog;
import com.fiberhome.filink.fdevice.service.device_log.DeviceLogService;
import com.fiberhome.filink.fdevice.utils.PageBeanHelper;
import com.fiberhome.filink.mongo.MongoQueryHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 设施日志实现类
 * @author CongcaiYu@wistronits.com
 */
@Service
public class DeviceLogServiceImpl implements DeviceLogService {


    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 分页查询设施日志
     *
     * @param queryCondition 查询条件
     * @return 分页数据
     */
    @Override
    public PageBean deviceLogListByPage(QueryCondition queryCondition) {
        //若排序条件为空,默认时间排序
        SortCondition sortCondition = queryCondition.getSortCondition();
        if(sortCondition == null || StringUtils.isEmpty(sortCondition.getSortField())){
            SortCondition condition = new SortCondition();
            condition.setSortField("currentTime");
            condition.setSortRule("desc");
            queryCondition.setSortCondition(condition);
        }
        Query query = new Query();
        //判断filterCondition对象是否为空
        if (null != queryCondition.getFilterConditions()) {
            //不为空的情况需要添加过滤条件到MongoQuery对象中
            MongoQueryHelper.convertFilterConditions(queryCondition.getFilterConditions());
        }
        //添加分页条件到MonogoQuery条件
        MongoQueryHelper.withPage(query, queryCondition.getPageCondition());
        Query buildQuery = MongoQueryHelper.buildQuery(query, queryCondition);
        //查询设施日志条数
        long count = mongoTemplate.count(buildQuery, DeviceLog.class);
        //查询设施日志列表信息
        List<DeviceLog> deviceLogList = mongoTemplate.find(buildQuery, DeviceLog.class);
        //返回分页对象信息，组装分页对象
        PageBean pageBean = PageBeanHelper.generatePageBean(deviceLogList, queryCondition, count);
        return pageBean;
    }

    /**
     * 保存设施日志
     *
     * @param deviceLog 设施日志对象
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result saveDeviceLog(DeviceLog deviceLog) {
        //根据序列号查询设施信息
        mongoTemplate.save(deviceLog);
        return ResultUtils.success();
    }
}
