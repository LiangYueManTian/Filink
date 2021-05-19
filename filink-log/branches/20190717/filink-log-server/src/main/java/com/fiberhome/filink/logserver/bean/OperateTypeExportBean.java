package com.fiberhome.filink.logserver.bean;

import lombok.Data;

/**
 * <p>
 * 操作类型统计导出实体
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/6/21
 */
@Data
public class OperateTypeExportBean {
    /**
     * web操作类型
     */
    private String webOperate;
    /**
     * pda操作类型
     */
    private String pdaOperate;
}
