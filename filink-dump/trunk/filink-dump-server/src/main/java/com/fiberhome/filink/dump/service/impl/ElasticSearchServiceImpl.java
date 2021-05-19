package com.fiberhome.filink.dump.service.impl;

import com.fiberhome.filink.dump.service.ElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * elasticSearch逻辑实现类
 * @author hedongwei@wistronits.com
 * @date 2019/8/1 10:16
 */
@Service
@Slf4j
public class ElasticSearchServiceImpl implements ElasticSearchService {

    @Autowired
    private RestHighLevelClient client;

    /**
     * 批量删除elasticSearch数据
     * @author hedongwei@wistronits.com
     * @date  2019/8/1 10:15
     */
    @Override
    public boolean deleteIndex(List<String> ids){
        BulkRequest bulkRequest = new BulkRequest();
        String dataBaseName = "filink_alarm";
        String tableName = "alarm_history";
        try {
            for (String id : ids) {
                DeleteRequest request = new DeleteRequest(dataBaseName, tableName,id);
                bulkRequest.add(request);
            }

            client.bulk(bulkRequest);
            return true;
        }catch (Exception e){
            log.error("delete elasticSearch data error", e);
        }
        return  false;
    }
}
