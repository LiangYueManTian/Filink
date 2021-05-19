package com.fiberhome.filink.mongo.configuration;

/**
 * mongo多数据源属性配置类
 *
 * @author yuanyao@wistronits.com
 * create on 2019-05-29 18:06
 */
public class MongoConfig {

    private String host;

    private Integer port;

    private String database;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
