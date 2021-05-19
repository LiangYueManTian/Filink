package com.fiberhome.filink.workflowbusinessserver.bean.workflowbusiness;

import lombok.Data;

/**
 * 工单国际化实体类
 *
 * @author hedongwei@wistronits.com
 */
@Data
public class WorkflowBusinessI18n {

    /**
     * 数据错误
     */
    public static final String DATA_ERROR = "DATA_ERROR";

    /**
     * 更新数据成功
     */
    public static final String UPDATE_SUCCESS = "UPDATE_SUCCESS";

    /**
     * 更新数据失败
     */
    public static final String UPDATE_FAIL = "UPDATE_FAILED";

    /**
     * 删除成功
     */
    public static final String DELETE_SUCCESS = "DELETE_SUCCESS";

    /**
     * 导出没有数据
     */
    public static final String EXPORT_NO_DATA = "EXPORT_NO_DATA";
}
