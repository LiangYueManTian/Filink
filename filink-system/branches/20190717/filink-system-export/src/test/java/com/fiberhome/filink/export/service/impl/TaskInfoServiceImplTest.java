package com.fiberhome.filink.export.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.export.bean.ExportDto;
import com.fiberhome.filink.export.bean.TaskInfo;
import com.fiberhome.filink.export.constant.ExportResultCodeConstant;
import com.fiberhome.filink.export.dao.TaskInfoDao;
import com.fiberhome.filink.export.exception.FilinkExportDateBaseException;
import com.fiberhome.filink.export.exception.FilinkExportDirtyDataException;
import com.fiberhome.filink.export.stream.ExportStreams;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:qiqizhu@wistronits.com
 * Date:2019/4/15
 */
@RunWith(JMockit.class)

@TestPropertySource("classpath:application.properties.properties")
public class TaskInfoServiceImplTest {
    /**
     * 自动注入taskInfoDao
     */
    @Injectable
    private TaskInfoDao taskInfoDao;
    /**
     * 远程调用文件服务器实体
     */
    @Injectable
    private FdfsFeign fdfsFeign;
    /**
     * 发送消息类
     */
    @Injectable
    private ExportStreams exportStreams;
    /**
     * 要测试的
     */
    @Tested
    private TaskInfoServiceImpl taskInfoServiceImpl;
    /**
     * 过期天数
     */
    @Injectable
    private Integer daysOverdue;
    /**
     * 用户最大导出任务数量
     */
    @Injectable
    private Integer maxTaskNum;
    /**
     * I18n
     */
    @Mocked
    private I18nUtils i18nUtils;
    /**
     * requestContextHolder
     */
    @Mocked
    private RequestContextHolder requestContextHolder;
    /**
     * servletRequestAttributes
     */
    @Mocked
    private ServletRequestAttributes servletRequestAttributes;
    /**
     * redisUtils
     */
    @Mocked
    private RedisUtils redisUtils;

    /**
     * 测试新增任务方法
     */
    @Before
    public void initData() {
        new Expectations() {
            {
                daysOverdue = 7;
                maxTaskNum = 3;
            }
        };

    }

    @Test
    public void addTask() {
        ExportDto exportDto = new ExportDto();
        try {
            taskInfoServiceImpl.addTask(exportDto);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkExportDateBaseException.class);
        }
        new Expectations() {
            {
                taskInfoDao.insert((TaskInfo) any);
                result = 1;
            }
        };

        Result result = taskInfoServiceImpl.addTask(exportDto);
        Assert.assertTrue(result.getCode() == 0);
        exportDto.setTaskId("aa");
        try {
            taskInfoServiceImpl.addTask(exportDto);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkExportDateBaseException.class);
        }
    }

    @Test
    public void stopTaskTest() {
        new Expectations() {
            {
                taskInfoDao.selectById(anyString);
                result = null;
            }
        };
        Result result = taskInfoServiceImpl.stopTask("aa");
        Assert.assertTrue(result.getCode() != 0);
        TaskInfo taskInfo = new TaskInfo();
        new Expectations() {
            {
                taskInfoDao.selectById(anyString);
                result = taskInfo;
            }
        };
        try {
            taskInfoServiceImpl.stopTask("aa");
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkExportDateBaseException.class);
        }
        new Expectations() {
            {
                taskInfoDao.updateById((TaskInfo) any);
                result = 1;
            }
        };
        Result result2 = taskInfoServiceImpl.stopTask("aa");
        System.out.println(result2.getCode());
        Assert.assertTrue(result2.getCode() == 0);
        taskInfo.setFilePath("aaa");
        new Expectations() {
            {
                taskInfoDao.selectById(anyString);
                result = taskInfo;
            }
        };
        Result result3 = taskInfoServiceImpl.stopTask("aa");
        System.out.println(result3.getCode());
        Assert.assertTrue(result3.getCode() == 0);
    }

    @Test
    public void exportTaskListTest() {
        QueryCondition queryCondition = new QueryCondition();
        Result result = taskInfoServiceImpl.exportTaskList(queryCondition);
        Assert.assertTrue(result.getCode() == ExportResultCodeConstant.PARAM_NULL);
        queryCondition.setFilterConditions(new ArrayList<>());
        Result result2 = taskInfoServiceImpl.exportTaskList(queryCondition);
        Assert.assertTrue(result2.getCode() == ExportResultCodeConstant.PARAM_NULL);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(1);
        queryCondition.setPageCondition(pageCondition);
        List<TaskInfo> taskInfoList = new ArrayList<>();
        TaskInfo taskInfo = new TaskInfo();
        taskInfoList.add(taskInfo);
        new Expectations(){
            {
                taskInfoDao.selectPage((Page)any, (EntityWrapper)any);
                result = taskInfoList;
            }
        };
        Result result3 = taskInfoServiceImpl.exportTaskList(queryCondition);
        Assert.assertTrue(result3.getCode() == ResultCode.SUCCESS);

    }

    @Test
    public void deleteTaskTest() {
        ArrayList<String> taskIdList = new ArrayList<>();
        Result result = taskInfoServiceImpl.deleteTask(taskIdList);
        Assert.assertTrue(result == null);
        taskIdList.add("11");
        try {
            taskInfoServiceImpl.deleteTask(taskIdList);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkExportDirtyDataException.class);
        }
        ArrayList<TaskInfo> taskInfos = new ArrayList<>();
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setFilePath("aaa");
        taskInfos.add(taskInfo);
        new Expectations() {
            {
                taskInfoDao.selectBatchIds(taskIdList);
                result = taskInfos;
            }
        };
        Result result1 = taskInfoServiceImpl.deleteTask(taskIdList);
        Assert.assertTrue(result1.getCode() == ResultCode.SUCCESS);
    }

    @Test
    public void selectTaskIsStopByIdTest() {
        Boolean result = taskInfoServiceImpl.selectTaskIsStopById("aa");
        Assert.assertTrue(result);
        new Expectations() {
            {
                RedisUtils.get(anyString);
                result = 1;
            }
        };
        Assert.assertTrue(!taskInfoServiceImpl.selectTaskIsStopById("aa"));
    }

    @Test
    public void deleteOverdueTaskTest() {
        taskInfoServiceImpl.deleteOverdueTask();
    }

    @Test
    public void updateTaskFileNumByIdTest() {
        ExportDto exportDto = new ExportDto();
        exportDto.setTaskId("aa");
        exportDto.setFileGeneratedNum(1);
        exportDto.setFileNum(1);
        new Expectations() {
            {
                taskInfoDao.updateById((TaskInfo) any);
                result = 1;
            }
        };
        taskInfoServiceImpl.updateTaskFileNumById(exportDto);
        exportDto.setFileNum(2);
        taskInfoServiceImpl.updateTaskFileNumById(exportDto);
    }

    @Test
    public void changeTaskStatusToUnusual() {
        new Expectations() {
            {
                taskInfoDao.updateById((TaskInfo) any);
                result = 1;
            }
        };
        Assert.assertTrue(taskInfoServiceImpl.changeTaskStatusToUnusual("aa"));
        new Expectations() {
            {
                taskInfoDao.updateById((TaskInfo) any);
                result = 2;
            }
        };
        try {
            taskInfoServiceImpl.changeTaskStatusToUnusual("aa");
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkExportDateBaseException.class);
        }
        new Expectations() {
            {
                taskInfoDao.selectById(anyString);
                result = null;
            }
        };
        Assert.assertTrue(taskInfoServiceImpl.changeTaskStatusToUnusual("aa"));
    }
}
