package com.fiberhome.filink.dump.controller;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.dump.constant.DumpConstant;
import com.fiberhome.filink.dump.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/dumpData/{dumpType}")
    public Result dumpData(@PathVariable("dumpType") Integer dumpType) {
        return taskInfoService.dumpData(dumpType, DumpConstant.DUMP_TRIGGER_RUN_NOW);
    }


    @GetMapping("/dumpInfo/{taskId}")
    public Result queryDumpInfoById(@PathVariable("taskId") String taskId) {

        return taskInfoService.queryDumpInfoById(taskId);
    }

    @PostMapping("/dumpInfo/{dumpType}")
    public Result queryDumpInfo(@PathVariable("dumpType") String dumpType) {

        return taskInfoService.queryDumpInfo(dumpType);
    }
}
