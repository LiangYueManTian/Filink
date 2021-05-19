package com.fiberhome.filink.alarmcurrentserver.utils;


import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;

/**
 * <p>
 * 分页帮助类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */

public class PageBeanHelper {


    public static PageBean generatePageBean(Object data, QueryCondition queryCondition, Object count) {
        Integer totalCount = Integer.parseInt(String.valueOf(count));
        Integer size = queryCondition.getPageCondition().getPageSize();
        Integer pageNum = queryCondition.getPageCondition().getPageNum();
        PageBean pageBean = new PageBean();
        pageBean.setData(data);
        pageBean.setTotalCount(totalCount);
        pageBean.setSize(size);
        pageBean.setTotalPage((totalCount - 1) / size + 1);
        pageBean.setPageNum(pageNum);
        return pageBean;
    }
}
