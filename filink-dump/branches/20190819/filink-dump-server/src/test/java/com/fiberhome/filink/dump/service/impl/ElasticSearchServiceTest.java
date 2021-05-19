package com.fiberhome.filink.dump.service.impl;


import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * es逻辑层测试类
 * @author hedongwei@wistronits.com
 * @date  2019/8/1 13:13
 *
 */
@RunWith(JMockit.class)
public class ElasticSearchServiceTest {

    /**
     * 测试对象
     */
    @Tested
    private ElasticSearchServiceImpl elasticSearchService;

    /**
     * es删除类
     */
    @Injectable
    private RestHighLevelClient client;

    /**
     * 批量删除elasticSearch数据
     * @author hedongwei@wistronits.com
     * @date  2019/8/1 13:18
     */
    @Test
    public void deleteIndex() {
        List<String> ids = new ArrayList<>();
        ids.add("1");
        elasticSearchService.deleteIndex(ids);

        new Expectations()  {
            {
                try {
                    client.bulk((BulkRequest) any);
                } catch (Exception e) {

                }
                result = new Exception();
            }
        };
        elasticSearchService.deleteIndex(ids);
    }
}
