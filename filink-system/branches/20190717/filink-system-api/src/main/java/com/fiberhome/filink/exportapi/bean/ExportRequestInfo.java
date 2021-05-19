package com.fiberhome.filink.exportapi.bean;

import lombok.Data;

import java.util.Map;

/**
 * 导出信息和request信息实体
 *
 * @Author: qiqizhu@wistronits.com
 * Date:2019/7/3
 */
@Data
public class ExportRequestInfo {
    /**
     * 导出对象
     */
    private Export export;
    /**
     * request信息map
     */
    private Map<String, Object> map;
}
