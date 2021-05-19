package com.fiberhome.filink.bean;

import lombok.Data;

/**
 * 分页查询条件
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/19 17:43
 */
@Data
public class PageCondition {

    /*** 开始页码*/
    private Integer pageNum;

    /**开始条数*/
    private Integer beginNum;

    /**每页条数*/
    private Integer pageSize;
}
