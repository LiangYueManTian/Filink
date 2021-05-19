package com.fiberhome.filink.rfid.controller.opticcable;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCode;
import com.fiberhome.filink.rfid.req.opticcable.OpticCableSectionInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.app.OperatorOpticCableSectionInfoReqForApp;
import com.fiberhome.filink.rfid.req.opticcable.app.OpticCableSectionInfoReqForApp;
import com.fiberhome.filink.rfid.service.opticcable.OpticCableSectionInfoService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 光缆段信息表 前端控制器
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-20
 */
@RestController
@RequestMapping("/opticCableSectionInfo")
public class OpticCableSectionInfoController {

    @Autowired
    private OpticCableSectionInfoService opticCableSectionInfoService;

    /**
     * 查询光缆段列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @PostMapping("/opticCableSectionById")
    public Result opticCableSectionById(@RequestBody QueryCondition<OpticCableSectionInfoReq> queryCondition) {
        return opticCableSectionInfoService.selectOpticCableSection(queryCondition);
    }

    /**
     * 拓扑根据光缆id查询光缆段列表
     *
     * @param opticCableSectionInfoReq 查询光缆段请求
     * @return Result
     */
    @PostMapping("/opticCableSectionByIdForTopology")
    public Result opticCableSectionByIdForTopology(@RequestBody OpticCableSectionInfoReq opticCableSectionInfoReq) {
        return opticCableSectionInfoService.opticCableSectionByIdForTopology(opticCableSectionInfoReq);
    }

    /**
     * 查询光缆段列表选择器
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @PostMapping("/opticCableSectionByIdForPageSelection")
    public Result opticCableSectionByIdForPageSelection(@RequestBody QueryCondition<OpticCableSectionInfoReq> queryCondition) {
        return opticCableSectionInfoService.selectOpticCableSection(queryCondition);
    }

    /**
     * 根据设施id查询光缆段列表
     *
     * @param deviceId 设备id
     * @return Result
     */
    @GetMapping("/selectOpticCableSectionByDeviceId/{deviceId}")
    public Result selectOpticCableSectionByDeviceId(@PathVariable(value = "deviceId") String deviceId) {
        return opticCableSectionInfoService.selectOpticCableSectionByDeviceId(deviceId);
    }

    /**
     * 通过光缆id查询所有设施信息
     *
     * @param opticCableId 光缆id
     * @return Result
     */
    @GetMapping("/queryDeviceInfoListByOpticCableId/{opticCableId}")
    public Result queryDeviceInfoListByOpticCableId(@PathVariable(value = "opticCableId") String opticCableId) {
        return opticCableSectionInfoService.queryDeviceInfoListByOpticCableId(opticCableId);
    }

    /**
     * 通过光缆段id删除光缆段
     *
     * @param opticCableSectionId 光缆段id
     *
     * @return Result
     */
    @GetMapping("/deleteOpticCableSectionByOpticCableSectionId/{opticCableSectionId}")
    public Result deleteOpticCableSectionByOpticCableSectionId(@PathVariable(value = "opticCableSectionId") String opticCableSectionId) {
        return opticCableSectionInfoService.deleteOpticCableSectionByOpticCableSectionId(opticCableSectionId);
    }

    /**
     * app请求光缆段基础信息
     *
     * @return Result
     */
    @PostMapping("/queryOpticCableSectionListForApp")
    public Result queryOpticCableSectionListForApp(@RequestBody OpticCableSectionInfoReqForApp opticCableSectionInfoReqForApp) {
        return opticCableSectionInfoService.queryOpticCableSectionListForApp(opticCableSectionInfoReqForApp);
    }

    /**
     * app光缆段基础信息上传
     *
     * @return Result
     */
    @PostMapping("/uploadOpticCableSectionInfoForApp")
    public Result uploadOpticCableSectionInfoForApp(@RequestBody OperatorOpticCableSectionInfoReqForApp operatorOpticCableSectionInfoReqForApp) {
        return opticCableSectionInfoService.uploadOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp);
    }

    /**
     * 光缆段列表导出
     *
     * @param exportDto 光缆段列表导出请求
     * @return Result
     */
    @PostMapping("/exportOpticCableSectionList")
    public Result exportOpticCableList(@RequestBody ExportDto<OpticCableSectionInfoReq> exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
        return opticCableSectionInfoService.exportOpticCableSectionList(exportDto);
    }

    /**
     * app校验光缆段名字
     *
     * @param operatorOpticCableSectionInfoReqForApp 光缆段基础信息
     * @return Result
     */
    @PostMapping("/checkOpticCableSectionNameForApp")
    public Result checkOpticCableSectionNameForApp(@RequestBody OperatorOpticCableSectionInfoReqForApp operatorOpticCableSectionInfoReqForApp) {
        if (ObjectUtils.isEmpty(operatorOpticCableSectionInfoReqForApp)) {
            return ResultUtils.warn(RfIdResultCode.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
        boolean isExist = opticCableSectionInfoService.checkOpticCableSectionNameForApp(operatorOpticCableSectionInfoReqForApp);
        if (isExist) {
            return ResultUtils.warn(RfIdResultCode.OPTIC_CABLE_SECTION_NAME_SAME, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_SECTION_NAME_SAME));
        }
        return ResultUtils.success();
    }


    /**
     * 校验设施是否有权限
     * @author hedongwei@wistronits.com
     * @date  2019/7/30 14:16
     * @param deviceIdList 设施编号集合
     * @return 设施信息
     */
    @PostMapping("/checkIsPermissionDeviceByDeviceIdList")
    public Result checkIsPermissionDeviceByDeviceIdList(@RequestBody List<String> deviceIdList) {
        return opticCableSectionInfoService.checkIsPermissionDeviceByDeviceIdList(deviceIdList);
    }


    /**
     * 校验用户同时拥有需要查询所有设施的权限
     * @author hedongwei@wistronits.com
     * @date  2019/7/30 14:16
     * @param deviceIdList 设施编号集合
     * @return 设施信息
     */
    @PostMapping("/existIsPermissionDeviceByDeviceIdList")
    public Result existIsPermissionDeviceByDeviceIdList(@RequestBody List<String> deviceIdList) {
        return opticCableSectionInfoService.existIsPermissionDeviceByDeviceIdList(deviceIdList);
    }

}
