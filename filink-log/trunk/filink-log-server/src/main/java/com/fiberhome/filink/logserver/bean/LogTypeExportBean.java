package com.fiberhome.filink.logserver.bean;

import lombok.Data;

/**
 * <p>
 *  日志类型统计导出实体
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/6/21
 */
@Data
public class LogTypeExportBean {
    /**
     * 操作日志
     */
   private String  operateLog;
    /**
     * 系统日志
     */
   private String  systemLog;
    /**
     * 安全日志
     */
   private String  securityLog;
}
