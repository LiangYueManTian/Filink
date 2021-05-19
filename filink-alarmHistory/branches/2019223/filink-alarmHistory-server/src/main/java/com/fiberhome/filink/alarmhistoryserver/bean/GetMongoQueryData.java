package com.fiberhome.filink.alarmhistoryserver.bean;

import com.fiberhome.filink.bean.Result;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @author wtao103@fiberhome.com
 * @Description mongo查询对象，还有返回类
 * @date 2019/1/16 17:19
 */
public class GetMongoQueryData {

    /**
     * mongo查询对象
     */
    private Query query;

    /**
     * 查询返回类
     */
    private Result result;

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
