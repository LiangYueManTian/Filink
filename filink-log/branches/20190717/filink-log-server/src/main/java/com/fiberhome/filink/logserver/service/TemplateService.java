package com.fiberhome.filink.logserver.service;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.logserver.bean.FilterTemplate;

/**
 * <p>
 * 查询模板逻辑层接口
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/5/31
 */
public interface TemplateService {
    /**
     * 插入模板
     *
     * @param filterTemplate 模板对象
     * @return 插入结果
     */
    Result insertTemplate(FilterTemplate filterTemplate);

    /**
     * 更新模板
     *
     * @param filterTemplate 模板对象
     * @return 更新结果
     */
    Result updateTemplate(FilterTemplate filterTemplate);

    /**
     * 删除模板
     *
     * @param filterTemplate 模板对象
     * @return 删除结果
     */
    Result deleteTemplate(FilterTemplate filterTemplate);

    /**
     * 查询模板
     *
     * @param id 模板id
     * @return 查询结果
     */
    Result queryTemplate(String id);

    /**
     * 查询模板列表
     *
     * @param queryCondition 查询条件实体
     * @return 查询结果
     */
    Result queryList(QueryCondition queryCondition);
}
