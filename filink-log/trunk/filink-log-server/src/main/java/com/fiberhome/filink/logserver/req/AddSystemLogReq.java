package com.fiberhome.filink.logserver.req;

import com.fiberhome.filink.logserver.bean.SystemLog;
import lombok.Data;

/**
 * @author hedongwei@wistronits.com
 * description 新增系统日志参数
 * date 2019/1/21 12:36
 */
@Data
public class AddSystemLogReq extends SystemLog {
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
