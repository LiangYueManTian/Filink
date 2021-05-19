package com.fiberhome.filink.dump.bean;

import com.fiberhome.filink.bean.QueryCondition;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
 * 列信息传输实体
 *
 * @author qiqizhu@wistronits.com
 * @Date: 2019/3/14 16:20
 */
@Data
public class ExportDto implements Serializable {
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 查询条件
     */
    private QueryCondition queryCondition;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 方法路径
     */
    private String methodPath;
    /**
     * 需要生成文件总数，比实际多10%用于打包、上传文件服务器时间
     */
    private Integer fileNum;
    /**
     * 已经生成文件总数
     */
    private  Integer fileGeneratedNum;
    /**
     * 文件类型，0为xsl,1为cvs
     */
    private Integer excelType;
    /**
     * 列信息集合
     */
    private List<ColumnInfo> columnInfoList;
    /**
     * 存储文件路径
     */
    private String filePath;
    /**
     * 列表名称
     */
    private String listName;
    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 存储的位置0：本地，1：文件服务器
     */
    private Integer dumpSite;

    /**
     * 转储类型，11：告警  13：日志  12：设施
     */
    private String dumpType;
}

