package com.fiberhome.filink.dump.service;

import com.fiberhome.filink.dump.bean.DumpBean;
import com.fiberhome.filink.dump.bean.DumpData;
import com.fiberhome.filink.dump.bean.ExportDto;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

/**
 * 转储逻辑层接口
 * @author hedongwei@wistronits.com
 * @date 2019/7/6 20:47
 */

public interface DumpService {

    /**
     * 查询转储数据
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 21:03
     * @param dumpBean 转储实体类
     * @param dumpData 转储数据
     * @param exportDto 导出数据
     * @param trigger 数据操作类别
     */
    void searchDumpData(DumpBean dumpBean, DumpData dumpData, ExportDto exportDto, String trigger);

    /**
     * 删除数据
     * @author hedongwei@wistronits.com
     * @date  2019/8/20 14:35
     * @param query 查询删除数据的条件
     * @param mongoTemplate 查询删除数据的内容
     * @param clazz 需要删除的类
     * @param collectionName 集合名称
     */
    void removeInfo(Query query, MongoTemplate mongoTemplate, Class clazz, String collectionName);
}
