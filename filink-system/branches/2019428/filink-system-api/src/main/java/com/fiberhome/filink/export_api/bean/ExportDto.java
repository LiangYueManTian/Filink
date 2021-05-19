package com.fiberhome.filink.export_api.bean;

import com.fiberhome.filink.bean.QueryCondition;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
 * 导出传输实体
 * @author qiqizhu@wistronits.com
 * @Date: 2019/3/14 14:15
 */
@Data
public class ExportDto<R> implements Serializable {
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
    private Integer fileGeneratedNum = 0;
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
     * 检测必填参数
     * @return 检测结果
     */
    public boolean checkParam() {
        if (this.queryCondition == null || queryCondition.getPageCondition() == null || queryCondition.getFilterConditions() == null || columnInfoList == null || columnInfoList.size() == 0 || excelType == null) {
            return false;
        }
        return true;
    }
}
