package com.fiberhome.filink.fdevice.service.device_log.impl;

import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.fdevice.bean.device_log.DeviceLog;
import com.fiberhome.filink.fdevice.utils.DeviceType;
import com.fiberhome.filink.fdevice.utils.PageBeanHelper;
import com.fiberhome.filink.mongo.MongoQueryHelper;
import com.fiberhome.filink.redis.RedisUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 设施日志测试类
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class DeviceLogServiceImplTest {

    private DeviceLog deviceLog;

    private QueryCondition queryCondition;

    private Query query;

    /**
     * 测试对象
     */
    @Tested
    private DeviceLogServiceImpl deviceLogService;

    /**
     * 模拟mongoTemplate
     */
    @Injectable
    private MongoTemplate mongoTemplate;

    /**
     * 初始化参数信息
     */
    @Before
    public void setUp(){
        deviceLog = new DeviceLog();
        deviceLog.setCurrentTime(System.currentTimeMillis());
        deviceLog.setDeviceName("deviceLog001");
        deviceLog.setDeviceCode("deviceCode001");
        deviceLog.setDeviceType(DeviceType.Distribution_Frame.getCode());
        deviceLog.setAreaName("area001");
        queryCondition = new QueryCondition();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(20);
        queryCondition.setPageCondition(pageCondition);
        query = new Query();
    }

    /**
     * 设施日志分页查询测试方法
     */
    @Test
    public void deviceLogListByPage() {
        new Expectations(MongoQueryHelper.class){
            {
                MongoQueryHelper.withPage(query,queryCondition.getPageCondition());
            }
            {
                MongoQueryHelper.buildQuery(query,queryCondition);
                result = query;
            }
        };

        new Expectations(PageBeanHelper.class){
            {
                PageBean pageBean = new PageBean();
                List<DeviceLog> deviceLogList = new ArrayList<>();
                PageBeanHelper.generatePageBean(deviceLogList,queryCondition,100L);
                result = pageBean;
            }
        };

        new Expectations(){
            {
                mongoTemplate.count(query,DeviceLog.class);
                result = 100L;
            }
            {
                List<DeviceLog> deviceLogList = new ArrayList<>();
                mongoTemplate.find(query,DeviceLog.class);
                result = deviceLogList;
            }
        };
        //pageCondition为空
        deviceLogService.deviceLogListByPage(queryCondition);
        //pageCondition不为空
        List<FilterCondition> filterConditions = new ArrayList<>();
        FilterCondition filterCondition = new FilterCondition();
        filterConditions.add(filterCondition);
        queryCondition.setFilterConditions(filterConditions);
        new Expectations(MongoQueryHelper.class){
            {
                MongoQueryHelper.convertFilterConditions(filterConditions);
            }
        };
        deviceLogService.deviceLogListByPage(queryCondition);
    }

    /**
     * 保存设施日志测试方法
     */
    @Test
    public void saveDeviceLog() {
        new Expectations(){
            {
                mongoTemplate.save(any);
            }
        };
        deviceLogService.saveDeviceLog(deviceLog);
    }
}
