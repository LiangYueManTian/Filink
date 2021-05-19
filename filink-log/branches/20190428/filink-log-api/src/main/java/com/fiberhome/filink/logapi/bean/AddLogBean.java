package com.fiberhome.filink.logapi.bean;


import lombok.Data;

/**
 * @author hedongwei@wistronits.com
 * 新增操作日志参数
 * 2019/1/21 12:36
 */
@Data
public class AddLogBean {

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
    private String dangerLevel;

    /**
     * 操作用户权限
     */
    private String optUserRole;

    /**
     * 操作用户权限名称
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

    /**
     * 数据编号
     */
    private String dataId;

    /**
     * 数据名称
     */
    private String dataName;
}
