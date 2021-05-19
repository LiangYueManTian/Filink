package com.fiberhome.filink.logserver.dao;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.fiberhome.filink.logserver.bean.FilterTemplate;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * <p>
 * 查询模板持久层
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/6/4
 */
public interface TemplateDao {
    /**
     * 新增一个查询模板
     *
     * @param template 模板
     */
    void insertOne(FilterTemplate template);

    /**
     * 更新一个查询模板
     *
     * @param template 模板
     */
    void updateOne(FilterTemplate template);

    /**
     * 删除一个查询模板
     *
     * @param template 模板
     */
    void deleteOne(FilterTemplate template);

    /**
     * 查询一个查询模板
     *
     * @param id 模板id
     * @return 查询结果
     */
    FilterTemplate queryOne(String id);

    /**
     * 查询所有模板
     *
     * @param rowBounds 分页查询条件（可以为 RowBounds.DEFAULT）
     * @param wrapper   实体对象封装操作类（可以为 null）
     * @return 分页数据
     */
    List<FilterTemplate> selectPage(RowBounds rowBounds, @Param("ew") EntityWrapper<FilterTemplate> wrapper);

    /**
     * 查询模板总数
     *
     * @param wrapper 实体对象封装操作类（可以为 null）
     * @return 总数
     */
    Integer selectCount(@Param("ew") EntityWrapper<FilterTemplate> wrapper);
}
