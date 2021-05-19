package com.fiberhome.filink.dump.service;

import java.util.List;

/**
 * elasticSearch接口类
 * @author hedongwei@wistronits.com
 * @date 2019/8/1 10:13
 */
public interface ElasticSearchService {

    /**
     * 批量删除elasticSearch数据
     * @author hedongwei@wistronits.com
     * @date  2019/8/1 10:15
     * @param ids 数据集合
     * @return 删除数据结果
     */
    boolean deleteIndex(List<String> ids);
}
