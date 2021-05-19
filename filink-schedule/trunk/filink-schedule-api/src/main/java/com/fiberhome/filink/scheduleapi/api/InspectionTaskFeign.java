package com.fiberhome.filink.scheduleapi.api;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.scheduleapi.bean.InspectionTaskBizBean;
import com.fiberhome.filink.scheduleapi.fallback.InspectionTaskFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 巡检任务接口类
 * @author hedongwei@wistronits.com
 * @date  2019/3/8 16:26
 */
@FeignClient(name = "filink-schedule-server", path = "/inspectionTaskJob",fallback = InspectionTaskFeignFallback.class)
public interface InspectionTaskFeign {

    /**
     * 新增巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 新增巡检任务参数
     * @return 返回新增巡检任务结果
     */
    @PostMapping("/addTaskJob")
    Result addTaskJob(@RequestBody InspectionTaskBizBean req);

    /**
     * 新增二月份的定时任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 10:42
     * @param req 新增定时任务参数
     * @return 新增二月份的定时任务结果
     */
    @PostMapping("/addFebruaryTaskJob")
    Result addFebruaryTaskJob(@RequestBody InspectionTaskBizBean req);


    /**
     * 查询巡检任务是否存在
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 12:29
     * @param req 巡检任务参数
     * @return 查询巡检任务是否存在
     */
    @PostMapping("searchInspectionTaskExists")
    boolean searchInspectionTaskExists(@RequestBody InspectionTaskBizBean req);

    /**
     * 修改巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 修改巡检任务参数
     * @return 返回修改巡检任务结果
     */
    @PostMapping("/updateTaskJob")
    Result updateTaskJob(@RequestBody InspectionTaskBizBean req);


    /**
     * 批量删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 删除巡检任务参数
     * @return 返回删除巡检任务结果
     */
    @PostMapping("/deleteTaskJobList")
    Result deleteTaskJobList(@RequestBody List<String> req);

    /**
     * 删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 删除巡检任务参数
     * @return 返回删除巡检任务结果
     */
    @PostMapping("/deleteTaskJob")
    Result deleteTaskJob(@RequestBody InspectionTaskBizBean req);

    /**
     * 暂停巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 暂停巡检任务参数
     * @return 暂停巡检任务结果
     */
    @PostMapping("/pauseTaskJob")
    Result pauseTaskJob(@RequestBody InspectionTaskBizBean req);

    /**
     * 重新开始巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 重新开始巡检任务参数
     * @return 重新开始巡检任务结果
     */
    @PostMapping("/resumeTaskJob")
    Result resumeTaskJob(@RequestBody InspectionTaskBizBean req);
}
