package com.fiberhome.filink.fdevice.controller.device;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.SortCondition;
import com.fiberhome.filink.fdevice.bean.device.DeviceCollecting;
import com.fiberhome.filink.fdevice.dto.DeviceCollectingForPda;
import com.fiberhome.filink.fdevice.exception.FilinkAttentionRequestParamException;
import com.fiberhome.filink.fdevice.service.device.DeviceCollectingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.fiberhome.filink.fdevice.constant.device.DeviceConstant.*;

/**
 * <p>
 * 我的关注 前端控制器
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-14
 */
@RestController
@RequestMapping("/deviceCollecting")
public class DeviceCollectingController {
    /**
     * 注入我的关注 逻辑层
     */
    @Autowired
    private DeviceCollectingService deviceCollectingService;

    /**
     * 添加关注
     *
     * @param deviceId
     * @return 返回结果
     */
    @GetMapping("/focus/{deviceId}")
    public Result addDeviceCollecting(@PathVariable("deviceId") String deviceId) {
        if (StringUtils.isEmpty(deviceId) || StringUtils.isEmpty(deviceId.trim())) {
            throw new FilinkAttentionRequestParamException();
        }
        return deviceCollectingService.addDeviceCollecting(generateBean(deviceId));
    }

    /**
     * 设施收藏 操作
     * @param deviceCollectingForPda
     * @return 操作结果
     */
    @PostMapping("/operate")
    public Result optDeviceCollecting(@RequestBody DeviceCollectingForPda deviceCollectingForPda) {
        if (deviceCollectingForPda == null
                || StringUtils.isEmpty(deviceCollectingForPda.getOptState())
                || StringUtils.isEmpty(deviceCollectingForPda.getDeviceId())) {
            throw new FilinkAttentionRequestParamException();
        }
        DeviceCollecting deviceCollecting = generateBean(deviceCollectingForPda.getDeviceId());
        Result result = null;
        if (deviceCollectingForPda.getOptState().equals(FOCUS_DEVICE)) {
            //收藏
            result = deviceCollectingService.addDeviceCollecting(deviceCollecting);
        } else if (deviceCollectingForPda.getOptState().equals(UNFOLLOW_DEVICE)) {
            //取消收藏
            result = deviceCollectingService.delDeviceCollecting(deviceCollecting);
        }
        return result;
    }

    /**
     * 取消关注
     *
     * @param deviceId
     * @return 返回结果
     */
    @GetMapping("/unFollow/{deviceId}")
    public Result delDeviceCollecting(@PathVariable("deviceId") String deviceId) {
        //校验参数
        if (StringUtils.isEmpty(deviceId) || StringUtils.isEmpty(deviceId.trim())) {
            throw new FilinkAttentionRequestParamException();
        }
        return deviceCollectingService.delDeviceCollecting(generateBean(deviceId));
    }

    /**
     * 设施/用户删除时，删除关注表的记录
     * 设施id和用户id不可同时存在，但必须存在一个属性
     *
     * @param deviceCollecting
     * @return 取消关注结果
     */
    @PostMapping("/unFollowById")
    public Result delDeviceCollectingById(DeviceCollecting deviceCollecting) {
        //校验参数
        if (null == deviceCollecting) {
            throw new FilinkAttentionRequestParamException();
        }
        //设施id和用户id不可同时存在，但必须存在一个属性
        if (StringUtils.isEmpty(deviceCollecting.getDeviceId()) && StringUtils.isEmpty(deviceCollecting.getUserId())) {
            throw new FilinkAttentionRequestParamException();
        }
        if (StringUtils.isNotEmpty(deviceCollecting.getDeviceId()) && StringUtils.isNotEmpty(deviceCollecting.getUserId())) {
            throw new FilinkAttentionRequestParamException();
        }
        return deviceCollectingService.delDeviceCollecting(deviceCollecting);
    }


    /**
     * 根绝设施类型获取我的关注统计数据
     *
     * @return 统计数据
     */
    @GetMapping("/count")
    public Result queryDeviceCollectingCountByType() {
        return deviceCollectingService.queryDeviceCollectingCountByType();
    }

    /**
     * 获取我的关注列表  分页
     *
     * @param queryCondition
     * @return 关注列表
     */
    @PostMapping("/attentionListByPage")
    public Result attentionListByPage(@RequestBody QueryCondition<DeviceCollecting> queryCondition) {
        //查询条件缺少过滤条件或者缺少分页条件时，报参数错误
        if (queryCondition == null || queryCondition.getFilterConditions() == null || queryCondition.getPageCondition() == null) {
            throw new FilinkAttentionRequestParamException();
        }

        // 没有排序条件的时候，默认排序条件为按创建时间倒序
        if (queryCondition.getSortCondition() == null || StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())) {
            SortCondition sortCondition = new SortCondition();
            //c.create_time是收藏的时间
            sortCondition.setSortField("c.create_time");
            sortCondition.setSortRule("desc");
            queryCondition.setSortCondition(sortCondition);
        }

        return deviceCollectingService.queryDeviceCollectingList(queryCondition);

    }

    /**
     * 获取我的关注列表 无分页
     *
     * @return 关注列表
     */
    @GetMapping("/attentionList")
    public Result attentionList() {
        return deviceCollectingService.queryAttentionList();
    }


    /**
     * 构造DeviceCollecting 实体
     *
     * @param deviceId
     * @return DeviceCollecting 实体
     */
    private DeviceCollecting generateBean(String deviceId) {
        DeviceCollecting deviceCollecting = new DeviceCollecting();
        deviceCollecting.setDeviceId(deviceId.trim());
        //获取当前用户，并赋值与deviceCollecting
        deviceCollecting.setUserId(RequestInfoUtils.getUserId());
        return deviceCollecting;
    }

}
