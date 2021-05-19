package com.fiberhome.filink.logserver.bean;

import lombok.Data;

import java.sql.Timestamp;

/**
 * <p>
 *  查询条件模板
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/5/31
 */
@Data
public class FilterTemplate {
    /**
     * 主键
     */
    private String id;
    /**
     * 模板名称
     */
    private String name;
    /**
     * 模板过滤值
     */
    private String filterValue;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */
    private Timestamp createTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 是否删除 0-否 1-是
     */
    private String isDeleted;
}
