package com.fiberhome.filink.parts.controller;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.parts.bean.PartInfo;
import com.fiberhome.filink.parts.service.ExportService;
import com.fiberhome.filink.parts.service.PartInfoService;
import com.fiberhome.filink.parts.constant.PartsI18n;
import com.fiberhome.filink.parts.constant.PartsResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gzp
 * @since 2019-02-12
 */
@RestController
@RequestMapping("/partInfo")
public class PartInfoController {
    @Autowired
    private PartInfoService partInfoService;

    @Autowired
    private ExportService exportService;

    /**
     * 校验配件名称是否重复
     *
     * @param partInfo 配件id和配件名称
     * @return Result
     */
    @PostMapping("/queryPartNameIsExisted")
    public Result checkDeviceName(@RequestBody PartInfo partInfo) {
        if (ObjectUtils.isEmpty(partInfo)) {
            return ResultUtils.warn(PartsResultCode.PARTS_PARAM_ERROR,
                    I18nUtils.getSystemString(PartsI18n.PARTS_PARAM_ERROR));
        }
        boolean isExist = partInfoService.checkPartsName(partInfo.getPartId(), partInfo.getPartName());
        if (isExist) {
            return ResultUtils.warn(PartsResultCode.PARTS_NAME_SAME,
                    I18nUtils.getSystemString(PartsI18n.PARTS_PARAM_ERROR));
        }
        return ResultUtils.success();
    }

    /**
     * 新增配件
     *
     * @param partInfo PartInfo
     * @return Result
     */
    @PostMapping("/addPart")
    public Result addPart(@RequestBody PartInfo partInfo) {
        return partInfoService.addPart(partInfo);
    }

    /**
     * 修改配件
     *
     * @param partInfo 配件信息
     * @return Result
     */
    @PostMapping("/updatePartById")
    public Result updateParts(@RequestBody PartInfo partInfo) {
        return partInfoService.updateParts(partInfo);
    }

    /**
     * 批量删除配件
     *
     * @param partsIds 配件id数组
     * @return Result
     */
    @PostMapping("/deletePartByIds")
    public Result deletePartsByIds(@RequestBody String[] partsIds) {
        return partInfoService.deletePartsByIds(partsIds);
    }

    /**
     * 根据id获取配件详情
     *
     * @param partsId 配件id
     * @return Result
     * @throws Exception 异常
     */
    @GetMapping("/findPartById/{id}")
    public Result findPartsById(@PathVariable("id") String partsId) throws Exception {
        return partInfoService.findPartsById(partsId);
    }

    /**
     * 模糊查询配件
     *
     * @param queryCondition QueryCondition
     * @return Result
     * @throws Exception 异常
     */
    @PostMapping("/partListByPage")
    public Result partListByPage(@RequestBody QueryCondition<PartInfo> queryCondition) throws Exception {
        return partInfoService.queryListByPage(queryCondition, null);
    }

    /**
     * 导出操作日志
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @PostMapping("/exportPartList")
    public Result exportPartList(@RequestBody ExportDto exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(PartsResultCode.EXPORT_PARAM_NULL, I18nUtils.getSystemString(PartsI18n.EXPORT_PARAM_NULL));
        }
        return exportService.exportPartList(exportDto);
    }

}
