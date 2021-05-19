package com.fiberhome.filink.rfid.controller.template;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfid.req.template.RealPortReqDto;
import com.fiberhome.filink.rfid.req.template.RealReqDto;
import com.fiberhome.filink.rfid.req.template.TemplateReqDto;
import com.fiberhome.filink.rfid.service.template.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yjli
 * @date 2019/5/28 15:01
 */
@RestController
@RequestMapping("/template")
public class TemplateController {
    /**
     * 模版服务类
     */
    @Autowired
    private TemplateService templateService;

    /**
     * 新建模板
     *
     * @param templateVO 模板实体
     * @return Result 返回结果
     */
    @PostMapping("/createTemplate")
    public Result createTemplate(@RequestBody TemplateReqDto templateVO) {
        return templateService.createTemplate(templateVO);
    }

    /**
     * 查询全部模板
     *
     * @param templateVO 查询条件
     * @return Result
     */
    @PostMapping("/queryAllTemplate")
    public Result queryAllTemplate(@RequestBody TemplateReqDto templateVO) {
        return templateService.queryAllTemplate(templateVO);
    }

    /**
     * 查询实景图坐标
     *
     * @return 实景图坐标
     */
    @PostMapping("/queryRealPosition")
    public Result queryRealPosition(@RequestBody RealReqDto realReqDto) {
        return templateService.queryRealPosition(realReqDto);
    }

    /**
     * 通过框id 查询出放大的实景图信息
     *
     * @param frameId 框id
     * @return Result
     */
    @GetMapping("/queryRealPositionByFrame/{id}")
    public Result queryRealPositionByFrameId(@PathVariable("id") String frameId) {
        return templateService.queryRealPositionByFrameId(frameId, true);
    }

    /**
     * 成端通过 id  查询实景图
     *
     * @param frameId
     * @return
     */
    @GetMapping("/queryFormationByFrameId/{id}")
    public Result queryFormationByFrameId(@PathVariable("id") String frameId) {
        return templateService.queryRealPositionByFrameId(frameId, false);
    }

    /**
     * 通过设施id 查询出箱框的信息
     *
     * @param deviceId 设施id
     * @return Result
     */
    @GetMapping("/queryFormationByDeviceId/{deviceId}")
    public Result queryFormationByDeviceId(@PathVariable("deviceId") String deviceId) {
        return templateService.queryFormationByDeviceId(deviceId);
    }

    /**
     * 保存设施和模板的关系
     *
     * @param reqDto 请求信息
     * @return 保存的信息
     */
    @PostMapping("/saveDeviceAndTempRelation")
    public Result saveDeviceAndTempRelation(@RequestBody TemplateReqDto reqDto) {
        return templateService.saveDeviceAndTempRelation(reqDto);
    }

    /**
     * 查询端口信息
     *
     * @param portId 端口id
     * @return Result
     */
    @GetMapping("/queryPortInfoByPortId/{portId}")
    public Result queryPortInfoByPortId(@PathVariable("portId") String portId) {
        return templateService.queryPortInfoByPortId(portId);
    }

    /**
     * 通过设施id 查询top 数据
     *
     * @param deviceId 设施id
     * @return Result
     */
    @GetMapping("/queryTemplateTop/{deviceId}")
    public Result queryTemplateTop(@PathVariable("deviceId") String deviceId) {
        return templateService.queryTemplateTop(deviceId);
    }

    /**
     * 实景图 盘上下架
     *
     * @param reqDto 请求实际
     * @return Result
     */
    @PostMapping("/updatePortState")
    public Result updatePortState(@RequestBody RealPortReqDto reqDto) {
        return templateService.updatePortState(reqDto);
    }

    /**
     * 修改模板
     *
     * @return Result
     */
    @PostMapping("/updateTemplate")
    public Result updateTemplate(@RequestBody TemplateReqDto templateReqDto) {
        return templateService.updateTemplate(templateReqDto);
    }

    /**
     * 删除模板
     *
     * @return Result
     */
    @PostMapping("/deleteTemplate")
    public Result deleteTemplate(@RequestBody TemplateReqDto templateReqDto) {
        return templateService.deleteTemplate(templateReqDto);
    }

}
