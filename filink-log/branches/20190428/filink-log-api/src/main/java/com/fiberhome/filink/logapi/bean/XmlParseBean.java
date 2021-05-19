package com.fiberhome.filink.logapi.bean;

import lombok.Data;

/**
 * @author hedongwei@wistronits.com
 * description 配置文件解析类
 * date 2019/1/25 14:05
 */
@Data
public class XmlParseBean {

    /**
     * 功能模板
     */
    private String detailInfoTemplate;

    /**
     * 操作名称
     */
    private String optName;
}
