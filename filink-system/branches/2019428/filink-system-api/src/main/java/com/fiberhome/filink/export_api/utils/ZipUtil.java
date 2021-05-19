package com.fiberhome.filink.export_api.utils;

import com.fiberhome.filink.export_api.api.ExportFeign;
import com.fiberhome.filink.export_api.exception.FilinkExportStopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.*;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;


/**
 * @author qiqizhu@wistronits.com
 * @Date: 2019/2/27 11:26
 * 压缩文件工具类
 */
@Component
public class ZipUtil {
    private static final Logger log = LoggerFactory.getLogger(ZipUtil.class);
    @Autowired
    private ExportFeign exportFeign;

    /**
     * 创建ZIP文件
     *
     * @param sourcePath 文件或文件夹路径
     */
    public boolean createZip(String sourcePath, String zipNameAndPath, String taskId) {
        //文件
        String zipPathAndName = zipNameAndPath + ".zip";
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipPathAndName);
            zos = new ZipOutputStream(fos);
            zos.setEncoding("gbk");
            writeZip(new File(sourcePath), "", zos, taskId);
        } catch (FileNotFoundException e) {
            log.error("创建ZIP文件失败", e);
            return false;
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                log.error("创建ZIP文件失败", e);
                return false;
            }

        }
        return true;
    }

    private void writeZip(File file, String parentPath, ZipOutputStream zos, String taskId) {
        if (exportFeign.selectTaskIsStopById(taskId)) {
            throw new FilinkExportStopException();
        }
        if (file.exists()) {
            if (file.isDirectory()) {
                //处理文件夹
                parentPath += file.getName() + File.separator;
                File[] files = file.listFiles();
                if (files.length != 0) {
                    for (File f : files) {
                        writeZip(f, parentPath, zos, taskId);
                    }
                } else {       //空目录则创建当前目录
                    try {
                        zos.putNextEntry(new ZipEntry(parentPath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    byte[] content = new byte[1024];
                    int len;
                    while ((len = fis.read(content)) != -1) {
                        zos.write(content, 0, len);
                        zos.flush();
                    }

                } catch (FileNotFoundException e) {
                    log.error("创建ZIP文件失败", e);
                } catch (IOException e) {
                    log.error("创建ZIP文件失败", e);
                } finally {
                    try {
                        if (fis != null) {
                            fis.close();
                        }
                    } catch (IOException e) {
                        log.error("创建ZIP文件失败", e);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
