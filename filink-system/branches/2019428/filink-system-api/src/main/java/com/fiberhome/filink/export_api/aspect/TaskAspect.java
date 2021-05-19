package com.fiberhome.filink.export_api.aspect;


import com.fiberhome.filink.export_api.api.ExportFeign;
import com.fiberhome.filink.export_api.bean.Export;
import com.fiberhome.filink.export_api.utils.ExportApiUtils;
import com.fiberhome.filink.export_api.utils.ListExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author qiqizhu@wistronits.com
 * @Date: 2019/3/20 9:41
 */
@Component
@Aspect
@Slf4j
public class TaskAspect {
    /**
     * 临时文件的路径
     */
    @Value("${readListExcelPath}")
    private String listExcelFilePath;
    @Autowired
    private ExportFeign exportFeign;

    /**
     * 切点路径
     */
    @Pointcut("@annotation(com.fiberhome.filink.export_api.annotation.addExportTaskAnnotation)")
    public void addExport() {
    }

    /**
     * description 方法后删除文件，清除缓存，清除ThreadLocal
     * date 9:22 2019/1/22
     * param [joinPoint]
     */
    @After("addExport()")
    public void around(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Export export = (Export) args[0];
        String folderPath = export.getTaskFolderPath();
        ListExcelUtil.deleteDir(new File(folderPath));
        ExportApiUtils.RESOURCE.remove();
        //如果任务未完成
        if (export.getFileNum() != 0 && !export.getFileGeneratedNum().equals(export.getFileNum())) {
            exportFeign.changeTaskStatusToUnusual(export.getTaskId());
        }
    }
}
