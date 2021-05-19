package com.fiberhome.filink.userserver.bean;

import lombok.Data;

/**
 * 分页列表
 * @author xuangong
 */
@Data
public class PageEntity {

    /**
     * 分页查询出的数据信息
     */
    private Object object;

    /**
     * 页号
     */
    private int page;

    /**
     * 每页大小
     */
    private int pageSize;
    /**
     * 总条数信息
     */
    private Long total;

    public PageEntity(Object objects, Long total) {
        object = objects;
        this.total = total;
    }

    public PageEntity(Object object, int page, int pageSize, Long total) {
        this.object = object;
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
    }
}
