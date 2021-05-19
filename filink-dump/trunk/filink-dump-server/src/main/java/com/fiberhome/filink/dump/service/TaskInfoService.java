package com.fiberhome.filink.dump.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.dump.bean.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-02-25
 */
public interface TaskInfoService extends IService<TaskInfo> {
//    /**
//     * 新增任务
//     *
//     * @param exportDto 接收对象
//     * @return 新增结果
//     * @throws Exception 异常
//     */
//    Result<ExportDto> addTask(ExportDto exportDto);



    /**
     * 根据id更新任务
     *
     * @param exportDto 传入参数
     * @return 返回结果
     */
    Boolean updateTaskFileNumById(ExportDto exportDto);

    /**
     * 将任务设置为异常
     *
     * @param taskId 传入参数
     * @return 返回结果
     */
    Boolean changeTaskStatusToUnusual(String taskId);

    /**
     * 根据id查询任务是否被停止
     *
     * @param taskId 任务id
     * @return 返回结果
     */
    Boolean selectTaskIsStopById(String taskId);

    /**
     * 导出数据
     * @param export 导出信息
     * @param queryStr  导出列表
     * @param dumpNum 转储数量
     * @param clazz 类
     * @param mongoTemplate mongo模板
     * @param dumpBean 转储实体类
     * @param dumpData 转储数据
     * @param trigger 操作类型
     */
    void exportData(Export export, int dumpNum, String queryStr, Class clazz, MongoTemplate mongoTemplate, DumpBean dumpBean, DumpData dumpData, String trigger);

    /**
     * 插入任务信息
     * @param exportDto     导出参数
     * @param serverName    服务名
     * @param listName      列表名
     * @param count         数量
     * @return
     */
    Export insertTask(ExportDto exportDto, String serverName, String listName, Integer count);

//    /**
//     * 获取被转储的数据信息
//     * @return
//     */
//    DumpDto queryDumpAlarm();

    /**
     * 获取被转储操作日志的数据信息
     * @param dumpType 转储类型
     * @param mongoTemplate mongo数据库
     * @param clazz 转储的数据类型
     * @param createTime 排序的字段
     * @param listName 列表名称
     * @return
     */
    Result queryDumpData(DumpType dumpType, MongoTemplate mongoTemplate, Class clazz, String createTime, String listName);

    /**
     * 获取被转储操作日志的数据信息
     * @param dumpBean 转储类
     * @param dumpType 转储类型
     * @param mongoTemplate mongo数据库queryDumpDataInfo
     * @param clazz 转储的数据类型
     * @param createTime 排序的字段
     * @param listName 列表名称
     * @param trigger 执行任务的时机
     * @return
     */
    Result queryDumpDataInfo(DumpBean dumpBean, DumpType dumpType, MongoTemplate mongoTemplate, Class clazz, String createTime, String listName, String trigger);

    /**
     * 转储数据信息
     * @param dumpData 需要转储的提示信息
     * @param trigger 执行的时机
     * @return  转储的结果
     */
    Result dumpOperaLogData(DumpData dumpData, String trigger);


    /**
     * 根据id查询转储数据
     * @author hedongwei@wistronits.com
     * @date  2019/8/1 16:24
     * @param taskId 任务编号
     * @return 转储数据结果
     */
    Result queryDumpInfoById(String taskId);

    /**
     * 转储信息
     * @param dumpType 转储类型
     * @param trigger 执行的时机
     * @return 转储的结果
     */
    Result dumpData(Integer dumpType, String trigger);

    /**
     * 查询最新的转储信息
     * @param dumpType 转储类型
     * @return  返回最新的转储信息
     */
    Result queryDumpInfo(String dumpType);


    /**
     * 查询转储数据
     * @author hedongwei@wistronits.com
     * @date  2019/8/5 12:22
     * @param query 查询数据
     * @param className 类名称
     * @param queryStr 查询字段
     * @return 转储数据
     */
    List<Object> queryDumpDataToListObject(Query query, String className, String queryStr);
}
