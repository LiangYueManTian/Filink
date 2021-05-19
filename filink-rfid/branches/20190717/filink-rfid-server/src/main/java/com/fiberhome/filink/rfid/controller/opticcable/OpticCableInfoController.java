package com.fiberhome.filink.rfid.controller.opticcable;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableInfo;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCodeConstant;
import com.fiberhome.filink.rfid.req.opticcable.InsertOpticCableInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.OpticCableInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.UpdateOpticCableInfoReq;
import com.fiberhome.filink.rfid.service.opticcable.OpticCableInfoService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 光缆信息表 前端控制器
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-20
 */
@RestController
@RequestMapping("/opticCableInfo")
public class OpticCableInfoController {

    @Autowired
    private OpticCableInfoService opticCableInfoService;

    /**
     * 分页查询光缆列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @PostMapping("/opticCableListByPage")
    public Result opticCableListByPage(@RequestBody QueryCondition<OpticCableInfoReq> queryCondition) {
        return opticCableInfoService.opticCableListByPage(queryCondition);
    }

    /**
     * 分页查询光缆列表选择器
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @PostMapping("/opticCableListByPageForPageSelection")
    public Result opticCableListByPageForPageSelection(@RequestBody QueryCondition<OpticCableInfoReq> queryCondition) {
        return opticCableInfoService.opticCableListByPage(queryCondition);
    }

    /**
     * 新增光缆
     *
     * @param insertOpticCableInfoReq 新增光缆请求
     * @return Result
     */
    @PostMapping("/addOpticCable")
    public Result addOpticCable(@RequestBody InsertOpticCableInfoReq insertOpticCableInfoReq) {
        return opticCableInfoService.addOpticCable(insertOpticCableInfoReq);
    }

    /**
     * 根据id获取光缆信息
     *
     * @param id 光缆id
     * @return Result
     */
    @GetMapping("/queryOpticCableById/{id}")
    public Result queryOpticCableById(@PathVariable(value = "id") String id) {
        return opticCableInfoService.queryOpticCableById(id);
    }

    /**
     * 修改光缆
     *
     * @param updateOpticCableInfoReq 修改光缆请求
     * @return Result
     */
    @PostMapping("/updateOpticCableById")
    public Result updateOpticCableById(@RequestBody UpdateOpticCableInfoReq updateOpticCableInfoReq) {
        return opticCableInfoService.updateOpticCableById(updateOpticCableInfoReq);
    }

    /**
     * 删除光缆
     *
     * @param id 删除光缆id
     * @return Result
     */
    @GetMapping("/deleteOpticCableById/{id}")
    public Result deleteOpticCableById(@PathVariable(value = "id") String id) {
        return opticCableInfoService.deleteOpticCableById(id);
    }

    /**
     * 校验光缆名字
     *
     * @param opticCableInfo 光缆基础信息
     * @return Result
     */
    @PostMapping("/checkOpticCableName")
    public Result checkOpticCableName(@RequestBody OpticCableInfo opticCableInfo) {
        if (ObjectUtils.isEmpty(opticCableInfo)) {
            return ResultUtils.warn(RfIdResultCodeConstant.OPTIC_CABLE_PARAM_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_PARAM_ERROR));
        }
        boolean isExist = opticCableInfoService.checkOpticCableName(opticCableInfo.getOpticCableId(), opticCableInfo.getOpticCableName());
        if (isExist) {
            return ResultUtils.warn(RfIdResultCodeConstant.OPTIC_CABLE_NAME_SAME, I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_NAME_SAME));
        }
        return ResultUtils.success();
    }

    /**
     * app请求所有光缆的信息
     *
     * @return Result
     */
    @GetMapping("/getOpticCableListForApp")
    public Result getOpticCableListForApp() {
        return opticCableInfoService.getOpticCableListForApp();
    }

    /**
     * 光缆列表导出
     *
     * @param exportDto 光缆列表导出请求
     * @return Result
     */
    @PostMapping("/exportOpticCableList")
    public Result exportOpticCableList(@RequestBody ExportDto<OpticCableInfoReq> exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(RfIdResultCodeConstant.PARAMS_ERROR, I18nUtils.getSystemString(RfIdI18nConstant.PARAMS_ERROR));
        }
        return opticCableInfoService.exportOpticCableList(exportDto);
    }
}
