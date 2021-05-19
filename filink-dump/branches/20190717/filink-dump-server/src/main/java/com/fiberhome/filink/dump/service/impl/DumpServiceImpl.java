package com.fiberhome.filink.dump.service.impl;

import com.fiberhome.filink.dump.bean.*;
import com.fiberhome.filink.dump.constant.ExportApiConstant;
import com.fiberhome.filink.dump.service.DumpService;
import com.fiberhome.filink.dump.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 转储逻辑层
 * @author hedongwei@wistronits.com
 * @date 2019/7/6 20:51
 */
@Service
public class DumpServiceImpl implements DumpService {


    @Autowired
    private TaskInfoService taskInfoService;

    /**
     * 查询转储数据
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 21:03
     * @param dumpBean 转储实体类
     * @param dumpData 转储数据
     * @param exportDto 导出数据
     *
     */
    @Override
    @Async
    public void searchDumpData(DumpBean dumpBean, DumpData dumpData, ExportDto exportDto) {
        MongoTemplate mongoTemplate = dumpData.getMongoTemplate();
        Class clazz = dumpData.getClazz();
        String queryStr = dumpData.getQueryStr();
        int dumpNum = Integer.parseInt(dumpBean.getTurnOutNumber());
        Sort beginTime = new Sort(Sort.Direction.ASC, queryStr);
        Query query = new Query().with(beginTime).limit(dumpNum);
        List list = mongoTemplate.find(query, clazz);

        String dumpPlace = dumpBean.getDumpPlace();

        exportDto.setDumpSite(Integer.parseInt(dumpPlace));
        Export export = taskInfoService.insertTask(exportDto, ExportApiConstant.SERVER_NAME,
                dumpData.getListName(), dumpNum);
        taskInfoService.exportData(export, list);

        //导出数据以后将数据删除
        if (ExportApiConstant.DUMP_OPERATION_DELETE.equals(dumpBean.getDumpOperation())) {
            mongoTemplate.findAllAndRemove(query, clazz);
        }
    }
}
