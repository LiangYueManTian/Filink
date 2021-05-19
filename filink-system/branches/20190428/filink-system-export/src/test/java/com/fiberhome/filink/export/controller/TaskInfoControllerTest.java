package com.fiberhome.filink.export.controller;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.export.bean.ExportDto;
import com.fiberhome.filink.export.service.TaskInfoService;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:qiqizhu@wistronits.com
 * Date:2019/4/15
 */
@RunWith(JMockit.class)
public class TaskInfoControllerTest {
    /**
     * 注入导出服务
     */
    @Injectable
    private TaskInfoService taskInfoService;
    /**
     * 要测试的
     */
    @Tested
    private TaskInfoController taskInfoController;

    /**
     * 测试新增任务方法
     */
    @Test
    public void addTask() {
        Result<ExportDto> exportDtoResult = taskInfoController.addTask(new ExportDto());
        Assert.assertTrue(exportDtoResult.getCode() == 0);
    }

    /**
     * 测试停止任务方法
     */
    @Test
    public void stopExportForPageSelection() {
        Result result = taskInfoController.stopExportForPageSelection("aa");
        Assert.assertTrue(result.getCode() == 0);
    }

    /**
     * 测试任务列表方法
     */
    @Test
    public void exportTaskListForPageSelection() {
        Result result = taskInfoController.exportTaskListForPageSelection(new QueryCondition());
        Assert.assertTrue(result.getCode() == 0);
    }

    /**
     * 测试删除任务方法
     */
    @Test
    public void deleteTaskForPageSelection() {
        List<String> taskIds = new ArrayList<>();
        Result result = taskInfoController.deleteTaskForPageSelection(taskIds);
        Assert.assertTrue(result.getCode() != 0);
        taskIds.add("1");
        Result result1 = taskInfoController.deleteTaskForPageSelection(taskIds);
        Assert.assertTrue(result1.getCode() == 0);
    }

    /**
     * 测试删除任务方法
     */
    @Test
    public void updateTaskFileNumById() {
        Boolean aBoolean = taskInfoController.updateTaskFileNumById(new ExportDto());
        Assert.assertTrue(!aBoolean);
    }
    /**
     * 测试将任务设置为异常方法
     */
    @Test
    public void changeTaskStatusToUnusual() {
        Boolean aBoolean = taskInfoController.changeTaskStatusToUnusual("a");
        Assert.assertTrue(!aBoolean);
    }
    /**
     * 测试根据任务id查找任务是否被停止方法
     */
    @Test
    public void selectTaskIsStopById() {
        Boolean aBoolean = taskInfoController.selectTaskIsStopById("a");
        Assert.assertTrue(!aBoolean);
    }
}
