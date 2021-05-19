package com.fiberhome.filink.export.bean;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 任务传输实体
 *
 * @Author: qiqizhu@wistronits.com
 * Date:2019/6/22
 */
@Data
public class TaskInfoDto {
    /**
     * UUID
     */
    private String taskInfoId;

    /**
     * 任务类型
     */
    private String taskInfoType;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 生成文件总数量
     */
    private Integer fileNum;
    /**
     * 已生成数量
     */
    private Integer fileGeneratedNum;

    /**
     * 任务状态
     */
    private Integer taskStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateUser;
    /**
     * 列表名称
     */
    private String listName;
    /**
     * 查询条件
     */
    private String queryCondition;
    /**
     * 创建时间时间戳
     */
    private long tsCreateTime;
    /**
     * 创建时间时间戳
     */
    private Long tsUpDateTime;
    /**
     * 删除标记 默认为0
     */
    private String isDeleted;
    /**
     * 列明信息
     */
    private String columnInfos;
    /**
     * 方法路径
     */
    private String methodPath;
    /**
     * 文件类型，0为xsl,1为cvs
     */
    private Integer excelType;
    /**
     * 数据集合 用于统计的导出
     */
    private List objectList;

    /**
     * 获取时间戳创建时间
     *
     * @return 创建时间
     */
    public long getTsCreateTime() {
        return this.createTime.getTime();
    }

    /**
     * 获取更新时间时间戳
     *
     * @return 更新时间
     */
    public Long getTsUpDateTime() {
        if (this.getUpdateTime() == null) {
            return null;
        }
        return this.updateTime.getTime();
    }

}
