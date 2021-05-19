package com.fiberhome.filink.fdevice.utils;


import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.PageCondition;

/**
 * @author hedongwei@wistronits.com
 * description 分页帮助类
 * date 2019/1/16 14:36
 */

public class PageBeanHelper {

    /**
     * @author hedongwei@wistronits.com
     * description 生成pagebean对象
     * date 13:51 2019/1/16
     * param [data, totalCount, size, pageNum]
     */
    public static PageBean generatePageBean(Object data, PageCondition pageCondition, Object count) {
        Integer totalCount = Integer.parseInt(String.valueOf(count));
        Integer size = pageCondition.getPageSize();
        Integer pageNum = pageCondition.getPageNum();
        PageBean pageBean = new PageBean();
        pageBean.setData(data);
        pageBean.setTotalCount(totalCount);
        pageBean.setSize(size);
        pageBean.setTotalPage((totalCount - 1) / size + 1);
        pageBean.setPageNum(pageNum);
        return pageBean;
    }
}
