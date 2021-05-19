package com.fiberhome.filink.logserver.req;

import com.fiberhome.filink.logserver.bean.SecurityLog;
import lombok.Data;

/**
 * @author hedongwei@wistronits.com
 * description 新增安全日志参数
 * date 2019/1/21 12:36
 */
@Data
public class AddSecurityLogReq extends SecurityLog {
    /**
     * 是否添加本地文件  0 添加  1 不添加
     */
    private String addLocalFile;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 危险级别编码
     */
    private String functionCode;
}
