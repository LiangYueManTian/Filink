package com.fiberhome.filink.dump.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.dump.bean.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;

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
     *
     * @param export 导出信息
     * @param list  导出列表
     */
    @Async
    void exportData(Export export, List list);

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
     * @return
     */
    Result queryDumpData(DumpType dumpType, MongoTemplate mongoTemplate, Class clazz, String createTime);

    /**
     * 获取被转储操作日志的数据信息
     * @param dumpType 转储类型
     * @param mongoTemplate mongo数据库
     * @param clazz 转储的数据类型
     * @param createTime 排序的字段
     * @return
     */
    Result queryDumpDataInfo(DumpBean dumpBean ,DumpType dumpType, MongoTemplate mongoTemplate, Class clazz, String createTime);

    /**
     * 转储数据信息
     * @param dumpData 需要转储的提示信息
     * @return  转储的结果
     */
    Result dumpOperaLogData(DumpData dumpData);


    Result queryDumpInfoById(String taskId);

    /**
     * 转储信息
     * @param dumpType 转储类型
     * @return 转储的结果
     */
    Result dumpData(Integer dumpType);

    /**
     * 查询最新的转储信息
     * @return  返回最新的转储信息
     */
    Result queryDumpInfo(String dumpType);
}
