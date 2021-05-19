package com.fiberhome.filink.dump.bean;

import lombok.Data;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * 导出数据类
 * @author hedongwei@wistronits.com
 * @date  2019/8/1 14:34
 */
@Data
public class DumpData {

    /**
     * 导出的类型
     */
    private DumpType dumpType;

    /**
     * 使用哪个mongo库
     */
    private MongoTemplate mongoTemplate;

    /**
     * 导出哪个类型
     */
    private Class clazz;

    /**
     * 根据哪个字段进行筛选
     */
    private String queryStr;

    /**
     * 导出列表的名称
     */
    private String listName;
}
