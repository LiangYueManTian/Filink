package com.fiberhome.filink.export_api.bean;

import com.fiberhome.filink.bean.QueryCondition;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * 导出信息实体
 * @author qiqizhu@wistronits.com
 * @Date: 2019/3/14 16:20
 */
@Data
public class Export<R> implements Serializable {
    /**
     * 数据
     */
    private List<List<String>> dataList;
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 查询条件
     */
    private QueryCondition<R> queryCondition;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 列信息集合
     */
    private List<ColumnInfo> columnInfoList;
    /**
     * 方法路径
     */
    private String methodPath;
    /**
     * 需要生成文件总数，比实际多10%用于打包、上传文件服务器时间
     */
    private Integer fileNum;
    /**
     * 已经生成文件数量 初始0
     */
    private Integer fileGeneratedNum = 0;
    /**
     * 文件类型，0为xsl,1为cvs
     */
    private Integer excelType;
    /**
     * 数据总量
     */
    private Integer dateSize;
    /**
     * 存储文件路径
     */
    private String filePath;
    /**
     * 列表名称
     */
    private String listName;
    /**
     * 该任务文件夹
     */
    private String taskFolderPath;
    /**
     * 表格文件文件夹名称
     */
    private String excelFolderName;
    /**
     * 时区
     */
    private String timeZone;
    /**
     * 用户唯一标识
     */
    private String token;
}
