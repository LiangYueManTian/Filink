package com.fiberhome.filink.dump.service.impl;

import com.fiberhome.filink.dump.bean.DumpBean;
import com.fiberhome.filink.dump.bean.DumpData;
import com.fiberhome.filink.dump.bean.Export;
import com.fiberhome.filink.dump.bean.ExportDto;
import com.fiberhome.filink.dump.constant.ExportApiConstant;
import com.fiberhome.filink.dump.service.DumpService;
import com.fiberhome.filink.dump.service.TaskInfoService;
import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * 转储逻辑层
 *
 * @author hedongwei@wistronits.com
 * @date 2019/7/6 20:51
 */
@Service
@Slf4j
public class DumpServiceImpl implements DumpService {


    @Autowired
    private TaskInfoService taskInfoService;

    /**
     * 查询转储数据
     *
     * @param dumpBean  转储实体类
     * @param dumpData  转储数据
     * @param exportDto 导出数据
     * @param trigger 操作类别
     * @author hedongwei@wistronits.com
     * @date 2019/7/6 21:03
     */
    @Override
    public void searchDumpData(DumpBean dumpBean, DumpData dumpData, ExportDto exportDto, String trigger) {
        MongoTemplate mongoTemplate = dumpData.getMongoTemplate();
        Class clazz = dumpData.getClazz();
        String queryStr = dumpData.getQueryStr();
        int dumpNum = Integer.parseInt(dumpBean.getTurnOutNumber());
        Sort begin = new Sort(Sort.Direction.ASC, queryStr);
        Query query = new Query().with(begin).limit(dumpNum);
        long count = mongoTemplate.count(query, clazz);
        String dumpPlace = dumpBean.getDumpPlace();
        if (dumpNum > count) {
            dumpNum = (int) count;
        }
        exportDto.setDumpSite(Integer.parseInt(dumpPlace));
        Export export = taskInfoService.insertTask(exportDto, ExportApiConstant.SERVER_NAME,
                dumpData.getListName(), dumpNum);

        taskInfoService.exportData(export, dumpNum, queryStr, clazz, mongoTemplate, dumpBean, dumpData, trigger);
    }

    /**
     * 删除数据
     * @author hedongwei@wistronits.com
     * @date  2019/8/20 14:35
     * @param query 查询删除数据的条件
     * @param mongoTemplate 查询删除数据的内容
     * @param collectionName 集合名称
     */
    @Override
    public void removeInfo(Query query, MongoTemplate mongoTemplate, Class clazz, String collectionName) {
        DBObject dbObject = query.getQueryObject();
        mongoTemplate.getCollection(collectionName).remove(dbObject);
    }
}
