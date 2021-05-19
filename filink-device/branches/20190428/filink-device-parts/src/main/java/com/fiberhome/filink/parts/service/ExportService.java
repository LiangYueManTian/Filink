package com.fiberhome.filink.parts.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;

/**
 * 配件导出服务类
 *
 * @Author: zl
 * @Date: 2019/4/28 20:38
 * @Description: com.fiberhome.filink.parts.service
 * @version: 1.0
 */
public interface ExportService {

    /**
     * 导出配件列表
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    Result exportPartList(ExportDto exportDto);
}
