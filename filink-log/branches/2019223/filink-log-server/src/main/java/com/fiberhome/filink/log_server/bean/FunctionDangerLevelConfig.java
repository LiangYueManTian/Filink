package com.fiberhome.filink.log_server.bean;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 功能危险级别配置表
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-01-22
 */
@TableName("function_danger_level_config")
public class FunctionDangerLevelConfig extends Model<FunctionDangerLevelConfig> {

    private static final long serialVersionUID = 1L;

    /**
     * 功能编码
     */
    @TableId("function_code")
    private String functionCode;

    /**
     * 功能名称
     */
    @TableField("function_name")
    private String functionName;

    /**
     * 危险级别
     */
    @TableField("danger_level")
    private String dangerLevel;

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }
    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
    public String getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(String dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    @Override
    protected Serializable pkVal() {
        return this.functionCode;
    }

    @Override
    public String toString() {
        return "FunctionDangerLevelConfig{" +
        "functionCode=" + functionCode +
        ", functionName=" + functionName +
        ", dangerLevel=" + dangerLevel +
        "}";
    }
}
