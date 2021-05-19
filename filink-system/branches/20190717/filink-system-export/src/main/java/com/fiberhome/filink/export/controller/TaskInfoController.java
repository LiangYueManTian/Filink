package com.fiberhome.filink.export.controller;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.export.bean.ExportDto;
import com.fiberhome.filink.export.constant.ExportI18nConstant;
import com.fiberhome.filink.export.constant.ExportResultCodeConstant;
import com.fiberhome.filink.export.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-02-25
 */
@RestController
@RequestMapping("/taskInfo")
public class TaskInfoController {
    /**
     * 注入导出服务
     */
    @Autowired
    private TaskInfoService taskInfoService;

    /**
     * 新增任务
     *
     * @param exportDto 传入数据
     * @return 新增结果
     */
    @PostMapping("/addTask")
    public Result addTask(@RequestBody ExportDto exportDto) {
        return taskInfoService.addTask(exportDto);
    }

    /**
     * 停止任务
     *
     * @param taskId 要停止的任务
     * @return 停止结果
     */
    @GetMapping("/stopExportForPageSelection/{taskId}")
    public Result stopExportForPageSelection(@PathVariable String taskId) {
        return taskInfoService.stopTask(taskId);
    }

    /**
     * 分页查询任务列表
     *
     * @param queryCondition 查询条件
     * @return 查询结果
     */
    @PostMapping("/exportTaskListForPageSelection")
    public Result exportTaskListForPageSelection(@RequestBody QueryCondition queryCondition) {
        return taskInfoService.exportTaskList(queryCondition);
    }

    /**
     * 批量删除任务
     *
     * @param taskIdList 传入id集合
     * @return 删除结果
     */
    @PostMapping("/deleteTaskForPageSelection")
    public Result deleteTaskForPageSelection(@RequestBody List<String> taskIdList) {
        if(taskIdList.size()==0){
           return ResultUtils.warn(ExportResultCodeConstant.PARAM_NULL, ExportI18nConstant.PARAM_NULL);
        }
        return taskInfoService.deleteTask(taskIdList);
    }

    /**
     * 根据任务id更新任务进度
     *
     * @param exportDto
     * @return
     */
    @PostMapping("/updateTaskFileNumById")
    public Boolean updateTaskFileNumById(@RequestBody ExportDto exportDto) {
        return taskInfoService.updateTaskFileNumById(exportDto);
    }

    /**
     * 将任务设置为异常
     *
     * @param taskId 任务id
     * @return 设置结果
     */
    @GetMapping("/changeTaskStatusToUnusual/{taskId}")
    public Boolean changeTaskStatusToUnusual(@PathVariable String taskId) {
        return taskInfoService.changeTaskStatusToUnusual(taskId);
    }

    /**
     * 根据任务id查找任务是否被停止
     *
     * @param taskId
     * @return
     */
    @GetMapping("/selectTaskIsStopById/{taskId}")
    public Boolean selectTaskIsStopById(@PathVariable String taskId) {
        return taskInfoService.selectTaskIsStopById(taskId);
    }
}
