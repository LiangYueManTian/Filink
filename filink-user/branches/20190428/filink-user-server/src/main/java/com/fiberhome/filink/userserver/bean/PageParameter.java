package com.fiberhome.filink.userserver.bean;

import lombok.Data;


/**
 * 条件查询中通用的分页信息
 * @author xgong
 */

@Data
public class PageParameter {


    /**
     * 每页大小
     */
    private int pageSize = 10;

    /**
     * 页数
     */
    private int page = 0;

    /**
     * 排序的字段
     */
    private String sortProperties;

    /**
     * 降序还是升序
     */
    private String sort;

    /**
     * 开始条数
     */
    private int startNum;
}
