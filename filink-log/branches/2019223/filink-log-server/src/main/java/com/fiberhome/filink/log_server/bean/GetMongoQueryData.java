package com.fiberhome.filink.log_server.bean;

import com.fiberhome.filink.bean.Result;
import lombok.Data;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @author hedongwei@wistronits.com
 * description mongo查询对象，还有返回类
 * date 2019/1/16 17:19
 */
@Data
public class GetMongoQueryData {

    /**
     * mongo查询对象
     */
    private Query query;

    /**
     * 查询返回类
     */
    private Result result;
}
