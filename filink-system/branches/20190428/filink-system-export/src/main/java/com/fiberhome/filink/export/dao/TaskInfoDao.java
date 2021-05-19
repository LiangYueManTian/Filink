package com.fiberhome.filink.export.dao;

import com.fiberhome.filink.export.bean.TaskInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-02-25
 */
public interface TaskInfoDao extends BaseMapper<TaskInfo> {
    /**
     * 根据id更新任务状态
     *
     * @param taskInfo 任务实体
     * @return 任务状态
     */
    boolean updateTaskStatus(TaskInfo taskInfo);

    /**
     * 根据id集合批量删除任务
     *
     * @param taskIds 任务id集合
     * @return 删除结果
     */
    Integer deleteTaskByTaskIds(List<String> taskIds);

    /**
     * 删除过期任务
     * @param time 时间
     * @return 删除结果
     */
    List<String> selectOverdueTask(Date time);
    /**
     * 查询当前用户正在准备、执行的任务数量
     * @param userId 用户id
     * @return 查询结果
     */
    int selectOngoingTaskCountByUserId(String userId);
}
