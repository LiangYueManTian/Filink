package com.fiberhome.filink.alarmcurrentserver.bean;

import com.fiberhome.filink.bean.Result;
import org.springframework.data.mongodb.core.query.Query;

/**
 * <p>
 * mongo查询对象，还有返回类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
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
