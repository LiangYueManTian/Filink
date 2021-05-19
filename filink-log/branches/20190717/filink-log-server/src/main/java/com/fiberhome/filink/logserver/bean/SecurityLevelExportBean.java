package com.fiberhome.filink.logserver.bean;

import lombok.Data;

/**
 * <p>
 * 安全级别统计导出实体
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/6/21
 */
@Data
public class SecurityLevelExportBean {
    /**
     * 危险级别提示
     */
    private String prompt;

    /**
     * 危险级别一般
     */
    private String general;

    /**
     * 危险级别危险
     */
    private String danger;
}
