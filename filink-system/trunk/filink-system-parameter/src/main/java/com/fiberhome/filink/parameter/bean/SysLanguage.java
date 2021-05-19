package com.fiberhome.filink.parameter.bean;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * <p>
 *  系统语言实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-11
 */
@Data
@TableName("sys_language")
public class SysLanguage {

    /**
     * 主键ID
     */
    @TableId("language_id")
    private String languageId;

    /**
     * 系统语言
     */
    @TableField("language_type")
    private String languageType;

    /**
     * 语言名称
     */
    @TableField("language_name")
    private String languageName;
}
