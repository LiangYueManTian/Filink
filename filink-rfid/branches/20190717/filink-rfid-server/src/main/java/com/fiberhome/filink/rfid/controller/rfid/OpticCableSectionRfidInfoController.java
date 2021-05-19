package com.fiberhome.filink.rfid.controller.rfid;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfid.req.rfid.DeleteOpticCableSectionRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.OpticCableSectionRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.UpdateOpticCableSectionRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.app.OpticCableSectionRfidInfoReqApp;
import com.fiberhome.filink.rfid.req.rfid.app.UploadOpticCableSectionRfidInfoReqApp;
import com.fiberhome.filink.rfid.service.rfid.OpticCableSectionRfidInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 光缆段rfId信息表 前端控制器
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-05-30
 */
@RestController
@RequestMapping("/opticCableSectionRfidInfo")
public class OpticCableSectionRfidInfoController {

    /**
     * 光缆段rfid服务类
     */
    @Autowired
    private OpticCableSectionRfidInfoService opticCableSectionRfidInfoService;

    /**
     * 光缆段rfid查询
     *
     * @param queryCondition 查询条件
     * @return Result
     */
    @PostMapping("/OpticCableSectionRfidInfoById")
    public Result opticCableSectionRfidInfoById(@RequestBody QueryCondition<OpticCableSectionRfidInfoReq> queryCondition) {
        return opticCableSectionRfidInfoService.opticCableSectionRfidInfoById(queryCondition);
    }

    /**
     * 根据光缆段id查询光缆段gis信息
     *
     * @param opticCableSectionId 光缆段id
     * @return Result
     */
    @GetMapping("/queryOpticCableSectionRfidInfoByOpticCableSectionId/{opticCableSectionId}")
    public Result queryOpticCableSectionRfidInfoByOpticCableSectionId(@PathVariable("opticCableSectionId") String opticCableSectionId){
        return opticCableSectionRfidInfoService.queryOpticCableSectionRfidInfoByOpticCableSectionId(opticCableSectionId);
    }

    /**
     * 根据光缆id查询光缆段gis信息
     *
     * @param opticCableId 光缆id
     * @return Result
     */
    @GetMapping("/queryOpticCableSectionRfidInfoByOpticCableId/{opticCableId}")
    public Result queryOpticCableSectionRfidInfoByOpticCableId(@PathVariable("opticCableId") String opticCableId){
        return opticCableSectionRfidInfoService.queryOpticCableSectionRfidInfoByOpticCableId(opticCableId);
    }

    /**
     * app光缆段rfid查询(光缆段GIS标签信息)
     *
     * @param opticCableSectionRfidInfoReqApp 查询条件
     * @return Result
     */
    @PostMapping("/queryOpticCableSectionRfidInfo")
    public Result queryOpticCableSectionRfidInfo(@RequestBody OpticCableSectionRfidInfoReqApp opticCableSectionRfidInfoReqApp) {
        return opticCableSectionRfidInfoService.queryOpticCableSectionRfidInfo(opticCableSectionRfidInfoReqApp);
    }

    /**
     * app光缆段rfid上传(光缆段GIS标签信息)
     *
     * @param uploadOpticCableSectionRfidInfoReqApp GIS标签信息
     * @return Result
     */
    @PostMapping("/uploadOpticCableSectionRfidInfo")
    public Result uploadOpticCableSectionRfidInfo(@RequestBody UploadOpticCableSectionRfidInfoReqApp uploadOpticCableSectionRfidInfoReqApp) {
        return opticCableSectionRfidInfoService.uploadOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp);
    }

    /**
     * 根据id光缆段gis坐标微调
     *
     * @param updateOpticCableSectionRfidInfoReqList 光缆段gis列表
     *
     * @return Result
     */
    @PostMapping("/updateOpticCableSectionRfidInfoPositionById")
    public Result updateOpticCableSectionRfidInfoPositionById(@RequestBody List<UpdateOpticCableSectionRfidInfoReq> updateOpticCableSectionRfidInfoReqList) {
        return opticCableSectionRfidInfoService.updateOpticCableSectionRfidInfoPositionById(updateOpticCableSectionRfidInfoReqList);
    }

    /**
     * 删除光缆段rfid信息
     *
     * @param deleteOpticCableSectionRfidInfoReq 删除光缆段rfid请求
     * @return Result
     */
    @PostMapping("/deleteOpticCableSectionRfidInfoById")
    public Result deleteOpticCableSectionRfidInfoById(@RequestBody DeleteOpticCableSectionRfidInfoReq deleteOpticCableSectionRfidInfoReq) {
        return opticCableSectionRfidInfoService.deleteOpticCableSectionRfidInfoById(deleteOpticCableSectionRfidInfoReq);
    }
}
