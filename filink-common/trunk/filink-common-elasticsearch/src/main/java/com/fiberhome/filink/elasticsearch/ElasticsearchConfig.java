package com.fiberhome.filink.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * es配置类
 *
 * @author yuanyao@wistronits.com
 * create on 2019-07-16 17:03
 */
@Slf4j
@Configuration
public class ElasticsearchConfig implements FactoryBean<RestHighLevelClient>,
        InitializingBean, DisposableBean {

    @Value("${spring.data.elasticsearch.cluster-nodes}")
    private String clusterNodes;

    private RestHighLevelClient restHighLevelClient;

    /**
     * 控制Bean的实例化过程
     *
     * @return
     * @throws Exception
     */
    @Override
    public RestHighLevelClient getObject() throws Exception {
        return restHighLevelClient;
    }

    /**
     * 获取接口返回的实例的class
     *
     * @return
     */
    @Override
    public Class<?> getObjectType() {
        return RestHighLevelClient.class;
    }


    @Override
    public void destroy() throws Exception {
        try {
            if (restHighLevelClient != null) {
                restHighLevelClient.close();
            }
        } catch (final Exception e) {
            log.error("Error closing ElasticSearch client: ", e);
        }
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    /**
     * 设置RestHighLevelClient
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        restHighLevelClient = buildClient();
    }

    /**
     * 配置集群需要修改
     * setMaxRetryTimeoutMillis 是设置超时时间
     *
     * @return
     */
    private RestHighLevelClient buildClient() {
        try {
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(clusterNodes.split(":")[0],
                                    Integer.parseInt(clusterNodes.split(":")[1]),
                                    "http"))
                            .setMaxRetryTimeoutMillis(60*1000));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return restHighLevelClient;
    }


}
