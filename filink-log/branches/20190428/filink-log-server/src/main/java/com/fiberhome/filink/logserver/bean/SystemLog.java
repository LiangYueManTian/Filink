package com.fiberhome.filink.logserver.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import lombok.Data;



/**
 * @author hedongwei@wistronits.com
 * description 系统日志表
 * date 2019/1/14 14:32
 */
@Data
public class SystemLog {

    /**
     * 日志编号(UUID)
     */
    private String logId;

    /**
     * 操作名称
     */
    private String optName;

    /**
     * 数据操作类型
     */
    private String dataOptType;

    /**
     * 操作类型
     */
    private String optType;

    /**
     * 危险级别
     */
    private Integer dangerLevel;

    /**
     * 操作用户权限
     */
    private String optUserRole;

    /**
     * 操作用户权限
     */
    private String optUserRoleName;


    /**
     * 操作用户编码
     */
    private String optUserCode;

    /**
     * 操作用户名称
     */
    private String optUserName;

    /**
     * 操作终端
     */
    private String optTerminal;

    /**
     * 操作时间
     */
    private Long optTime;

    /**
     * 操作对象
     */
    private String optObj;

    /**
     * 操作对象编号
     */
    private String optObjId;

    /**
     * 操作结果
     */
    private String optResult;

    /**
     * 详细信息
     */
    private String detailInfo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 修改时间
     */
    private Long updateTime;

    @JsonIgnore
    public String getTranslationOptTime() {
        return ExportApiUtils.getZoneTime(this.optTime);
    }
}
