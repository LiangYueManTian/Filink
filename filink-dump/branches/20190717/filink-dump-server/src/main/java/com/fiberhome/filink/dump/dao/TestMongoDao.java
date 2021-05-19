package com.fiberhome.filink.dump.dao;

import com.fiberhome.filink.dump.bean.TestBean;
import com.fiberhome.filink.mongo.configuration.MongoMultiDataSourceRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author yuanyao@wistronits.com
 * create on 2019-05-29 17:52
 */
@Component
public class TestMongoDao {

    @Qualifier("device_log")
    @Autowired(required = false)
    private MongoTemplate device_log;

    @Qualifier("alarm_current")
    @Autowired(required = false)
    private MongoTemplate alarm_current;

    @Qualifier("alarm_history")
    @Autowired(required = false)
    private MongoTemplate alarm_histroy;


    @PostConstruct
    public void init2() {
//        Map<String, MongoTemplate> mongoTemplateMap = MongoMultiDataSourceRegistrar.mongoTemplateMap;
//        MongoTemplate alarm_current = mongoTemplateMap.get("alarm_current");

//        MongoTemplate device_log = mongoTemplateMap.get("alarm_current");
        TestBean testBean = new TestBean();
        testBean.setName("张三00007777");
        testBean.setAge(0);
//        device_log.insert(testBean);
        testBean.setName("kkkkkk");

//        alarm_histroy.insert(testBean);
//        alarm_current.insert(testBean);
//        alarm_current.insert(testBean);

//        device_log.insert(testBean);
//        alarm_current.insert(testBean);
//        alarm_histroy.insert(testBean);


    }



}
