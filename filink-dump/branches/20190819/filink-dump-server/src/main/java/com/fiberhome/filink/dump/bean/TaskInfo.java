package com.fiberhome.filink.dump.bean;

import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 任务实体
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-02-25
 */
@Data
@TableName("task_info")
public class TaskInfo extends Model<TaskInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * UUID
     */
    @TableId("task_info_id")
    private String taskInfoId;

    /**
     * 任务类型
     */
    @TableField("task_info_type")
    private String taskInfoType;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;

    @TableField("file_num")
    private Integer fileNum;

    @TableField("file_generated_num")
    private Integer fileGeneratedNum;

    /**
     * 任务状态
     */
    @TableField("task_status")
    private Integer taskStatus;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 更新人
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * 列表名称
     */
    @TableField("list_name")
    private String listName;
    /**
     * 查询条件
     */
    @TableField("query_condition")
    private String queryCondition;
    /**
     * 创建时间时间戳
     */
    @TableField(exist = false)
    private Long tsCreateTime;
    /**
     * 创建时间时间戳
     */
    @TableField(exist = false)
    private Long tsUpDateTime;
    /**
     * 删除标记 默认为0
     */
    @TableField("is_deleted")
    private String isDeleted;
    /**
     * 列明信息
     */
    @TableField("column_infos")
    private String columnInfos;
    /**
     * 方法路径
     */
    @TableField("method_path")
    private String methodPath;
    /**
     * 文件类型，0为xsl,1为cvs
     */
    @TableField("excel_type")
    private Integer excelType;

    /**
     * 转储数据的总条数
     */
    @TableField(exist = false)
    private Long dumpAllNumber;

    /**
     * 下一次执行时间
     */
    @TableField(exist = false)
    private Long nextExecutionTime;

    public Long getNextExecutionTime() {
        return nextExecutionTime;
    }

    public void setNextExecutionTime(Long nextExecutionTime) {
        this.nextExecutionTime = nextExecutionTime;
    }

    public Long getTsCreateTime() {
        return this.createTime;
    }

    public Long getTsUpDateTime() {
        if (this.getUpdateTime() == null) {
            return null;
        }
        return this.updateTime.getTime();
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
