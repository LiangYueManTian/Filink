package com.fiberhome.filink.export.service;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.export.bean.ExportDto;
import com.fiberhome.filink.export.bean.TaskInfo;
import com.baomidou.mybatisplus.service.IService;

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
    /**
     * 新增任务
     *
     * @param exportDto 接收对象
     * @return 新增结果
     * @throws Exception 异常
     */
    Result<ExportDto> addTask(ExportDto exportDto);

    /**
     * 停止
     *
     * @param taskId 要停止的任务
     * @return 停止结果
     */
    Result stopTask(String taskId);

    /**
     * 分页查询任务列表
     *
     * @param queryCondition 查询条件
     * @return 查询结果
     */
    Result exportTaskList(QueryCondition queryCondition);

    /**
     * 批量删除任务
     *
     * @param taskIdList 传入任务id集合
     * @return 删除结果
     */
    Result deleteTask(List<String> taskIdList);

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
     * 删除过期的到处任务
     */
    void deleteOverdueTask();
}
