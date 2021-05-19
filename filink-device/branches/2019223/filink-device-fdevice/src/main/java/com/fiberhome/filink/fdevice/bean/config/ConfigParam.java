package com.fiberhome.filink.fdevice.bean.config;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 配置策略参数
 * @author CongcaiYu@wistronits.com
 */
@Data
public class ConfigParam implements Serializable {
    /**
     * 唯一标识
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 编码
     */
    private String code;
    /**
     * 输入框类型
     */
    private String type;
    /**
     * 单位
     */
    private String unit;
    /**
     * 占位符
     */
    private String placeholder;
    /**
     * 下拉框值
     */
    private List<SelectParam> selectParams;
    /**
     * 校验规则
     */
    private List<Map<String,Object>> rules;
}
