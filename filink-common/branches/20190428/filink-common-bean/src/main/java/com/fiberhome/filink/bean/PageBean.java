package com.fiberhome.filink.bean;

import lombok.Data;

/**
 * 分页数据封装  出自ptn项目
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/19 16:58
 */
@Data
public class PageBean {

    /*** 当前页*/
    private Integer pageNum;

    /*** 总页数*/
    private Integer totalPage;

    /*** 总记录数*/
    private Integer totalCount;

    /*** 每页显示记录数*/
    private Integer size;

    /*** 数据信息*/
    private Object data ;
}
