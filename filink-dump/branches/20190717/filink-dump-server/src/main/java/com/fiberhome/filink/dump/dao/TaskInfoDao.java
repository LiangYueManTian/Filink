package com.fiberhome.filink.dump.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.dump.bean.TaskInfo;
import org.apache.ibatis.annotations.Param;

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
     * 查询当前用户正在准备、执行的任务数量
     * @param userId 用户id
     * @return 查询结果
     */
    int selectOngoingTaskCountByUserId(String userId);

    /**
     * 查询在指定时间后创建的任务
     * @param time
     * @return
     */
    List<String> queryTaskByCreateTime(@Param("type") String type,@Param("time") Long time);

    /**
     * 查询最新的任务信息
     * @return  最新的任务信息
     */
    TaskInfo queryLastTaskInfo(@Param("dumpType") String dumpType);

    /**
     * 查询总的转储数量
     * @return
     */
    Long queryTotalTaskNum(String dumpType);
}
