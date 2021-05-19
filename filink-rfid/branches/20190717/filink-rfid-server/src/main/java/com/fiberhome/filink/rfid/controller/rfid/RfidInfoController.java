package com.fiberhome.filink.rfid.controller.rfid;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfid.req.rfid.InsertRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.QueryRfidInfoReq;
import com.fiberhome.filink.rfid.service.rfid.RfidInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * rfId信息表 前端控制器
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-06-11
 */
@RestController
@RequestMapping("/rfidInfo")
public class RfidInfoController {

    @Autowired
    private RfidInfoService rfidInfoService;

    /**
     * 查询rfid信息
     *
     * @param queryRfidInfoReq 查询条件
     * @return Result
     */
    @PostMapping("/queryRfidInfo")
    public Result queryRfIdInfo(@RequestBody QueryRfidInfoReq queryRfidInfoReq) {
        return rfidInfoService.queryRfidInfo(queryRfidInfoReq);
    }

    /**
     * 保存rfid信息
     *
     * @param insertRfidInfoReqList rfId信息列表
     * @return Result
     */
    @PostMapping("/addRfidInfo")
    public Result addRfIdInfo(@RequestBody List<InsertRfidInfoReq> insertRfidInfoReqList) {
        return rfidInfoService.addRfidInfo(insertRfidInfoReqList);
    }

    /**
     * 删除rfid信息
     *
     * @param rfIdCodeList rfIdCode列表
     * @return Result
     */
    @PostMapping("/deleteRfidInfo")
    public Result deleteRfIdInfo(@RequestBody Set<String> rfIdCodeList) {
        return rfidInfoService.deleteRfidInfo(rfIdCodeList);
    }

    /**
     * 校验rfidCode是否存在
     *
     * @param rfIdCodeList rfIdCode列表
     * @return Boolean
     */
    @PostMapping("/checkRfidCodeListIsExist")
    public Boolean checkRfidCodeListIsExist(@RequestBody Set<String> rfIdCodeList) {
        return rfidInfoService.checkRfidCodeListIsExist(rfIdCodeList);
    }

    /**
     * 根据设施id删除智能标签信息
     *
     * @param deviceId 删除智能id
     * @return Result
     */
    @GetMapping("/deleteRfidInfoByDeviceId/{deviceId}")
    public int deleteRfidInfoByDeviceId(@PathVariable("deviceId") String deviceId) {
        return rfidInfoService.deleteRfidInfoByDeviceId(deviceId);
    }
}
