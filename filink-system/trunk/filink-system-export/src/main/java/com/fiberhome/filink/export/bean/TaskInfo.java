package com.fiberhome.filink.export.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

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
    /**
     * 生成文件总数量
     */
    @TableField("file_num")
    private Integer fileNum;
    /**
     * 生成文件总数量
     */
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
    private Date createTime;

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
     * 数据集合 用于统计的导出
     */
    @TableField("object_list")
    private String objectList;


    @Override
    protected Serializable pkVal() {
        return null;
    }
}
