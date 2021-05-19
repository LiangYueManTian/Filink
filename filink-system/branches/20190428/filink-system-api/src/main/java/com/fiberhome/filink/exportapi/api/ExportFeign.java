package com.fiberhome.filink.exportapi.api;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.fallback.ExportFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



/**
 * 导出api
 * @author qiqizhu@wistronits.com
 * @Date: 2019/3/13 13:48
 */
@FeignClient(name = "filink-system-server", path = "/taskInfo", fallback = ExportFeignFallback.class)
public interface ExportFeign {
    /**
     * 对外暴露创建任务api
     *
     * @param  exportDto 传入导出数据
     * @return 返回结果
     */
    @PostMapping("/addTask")
    Result addTask(@RequestBody ExportDto exportDto);
    /**
     * 根据任务id更新任务进度
     * @param exportDto
     * @return
     */
    @PostMapping("/updateTaskFileNumById")
    Boolean updateTaskFileNumById(@RequestBody ExportDto exportDto);
    /**
     * 停止任务
     *
     * @param taskId 要停止的任务
     * @return 停止结果
     */
    @GetMapping("/stopExport/{taskId}")
    Result stopExport(@PathVariable(value = "taskId") String taskId);
    /**
     * 将任务设置为异常
     * @param taskId 任务id
     * @return 设置结果
     */
    @GetMapping("/changeTaskStatusToUnusual/{taskId}")
    Boolean changeTaskStatusToUnusual(@PathVariable(value = "taskId") String taskId);
    /**
     * 根据任务id查找任务是否被停止
     * @param taskId
     * @return
     */
    @GetMapping("/selectTaskIsStopById/{taskId}")
    Boolean selectTaskIsStopById(@PathVariable(value = "taskId")String taskId);
}
