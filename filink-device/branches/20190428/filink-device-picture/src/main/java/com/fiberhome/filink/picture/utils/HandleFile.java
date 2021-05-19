package com.fiberhome.filink.picture.utils;

import com.fiberhome.filink.picture.annotation.AddExportPicTaskAnnotation;
import com.fiberhome.filink.picture.constant.PicRelationI18nConstants;
import com.fiberhome.filink.picture.constant.TaskStatusConstant;
import com.fiberhome.filink.picture.exception.ExportPicDataBaseException;
import com.fiberhome.filink.picture.exception.ExportPicStopException;
import com.fiberhome.filink.picture.resp.DevicePicResp;
import com.fiberhome.filink.exportapi.api.ExportFeign;
import com.fiberhome.filink.exportapi.bean.Export;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.utils.ListExcelUtil;
import com.fiberhome.filink.ossapi.upload.UploadFile;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.fiberhome.filink.ossapi.api.FdfsFeign;


/**
 * @author chaofanrong@wistronits.com
 * @Date: 2019/3/4 10:10
 */
@Component
@Slf4j
@RefreshScope
public class HandleFile {

    /**
     * 临时文件的路径
     */
    @Value("${readListExcelPath}")
    private String listExcelFilePath;

    /**
     * 上传文件服务类实体
     */
    @Autowired
    private UploadFile uploadFile;

    /**
     * 导出服务feign
     */
    @Autowired
    private ExportFeign exportFeign;

    /**
     * 远程调用oss服务
     */
    @Autowired
    private FdfsFeign fdfsFeign;

    /**
     * 生成图片
     *
     * @param devicePicRespList 导出数据
     * @param export            导出类
     * @param exportDto         导出请求
     * @return Boolean
     * @throws Exception
     */
    @Async
    @AddExportPicTaskAnnotation
    public Boolean generatedPic(List<DevicePicResp> devicePicRespList, Export export, ExportDto exportDto) throws Exception {
        //设置redis中的key值
        String key = "task" + export.getTaskId();
        RedisUtils.set(key, TaskStatusConstant.START);
        //当前任务文件夹
        String taskFolderPath = listExcelFilePath + export.getTaskId();
        export.setTaskFolderPath(taskFolderPath);
        //临时文件夹
        String excelFolderName = export.getListName() + System.currentTimeMillis();
        export.setExcelFolderName(excelFolderName);
        //下载图片基础路径
        String basePath = fdfsFeign.getBasePath();
        log.info("-----------basePath-----------" + basePath);
        if (StringUtils.isEmpty(basePath)) {
            return false;
        }
        //循环生成本地文件
        for (DevicePicResp devicePicResp : devicePicRespList) {
            //获取图片url,name,type信息
            boolean b = generatePicInfo(basePath + devicePicResp.getPicUrlBase(), devicePicResp.getPicName(), devicePicResp.getType(), export, exportDto);
            if (!b) {
                //如果文件生成失败数据库中该任务的状态为异常
                changeTaskStatusToUnusual(export.getTaskId());
            }
        }
        //生成压缩文件并更新进度
        generatedZip(key, export, exportDto);
        //上传到文件服务器并保存到数据库
        uploadToFastDfs(key, export, exportDto);
        return true;
    }

    /**
     * 上传到文件服务器并保存路径到数据库
     *
     * @param exportDto 导出数据请求类
     * @param key       redis缓存key
     * @return void
     */
    private void uploadToFastDfs(String key, Export export, ExportDto exportDto) {
        String path = export.getTaskFolderPath() + "/" + export.getExcelFolderName() + ".zip";
        File file = new File(path);
        if (!file.exists() || !TaskStatusConstant.START.equals(RedisUtils.get(key))) {
            throw new ExportPicStopException();
        }

        //上传zip压缩包至文件服务器
        String zipUrl = uploadFile.uploadFile(file);
        if (StringUtils.isEmpty(zipUrl)) {
            //远程更新任务状态为异常
            this.changeTaskStatusToUnusual(exportDto.getTaskId());
            throw new ExportPicStopException(I18nUtils.getString(PicRelationI18nConstants.FAILED_TO_UPLOAD_FILE_TO_FILE_SERVER));
        } else {
            //远程更新任务状态为已完成
            exportDto.setFileGeneratedNum(exportDto.getFileNum() - 1);
            exportDto.setFilePath(zipUrl);
            //如果更新失败，清除缓存
            if (!this.updateTaskFileNumById(exportDto, key)) {
                //清除缓存
                RedisUtils.remove(key);
                throw new ExportPicStopException();
            }

            //删除本地缓存
            ListExcelUtil.deleteDir(new File(export.getTaskFolderPath()));
        }
    }

    /**
     * 设置任务状态为异常
     *
     * @param taskInfoId 任务id
     */
    private void changeTaskStatusToUnusual(String taskInfoId) {
        //将任务状态设置为异常状态
        boolean updateTaskStatusBoolean = exportFeign.changeTaskStatusToUnusual(taskInfoId);
        if (!updateTaskStatusBoolean) {
            throw new ExportPicDataBaseException();
        }
    }

    /**
     * 更新任务进度
     *
     * @param exportDto 导出数据请求类
     * @param key       缓存中key值
     * @return Boolean
     */
    private Boolean updateTaskFileNumById(ExportDto exportDto, String key) {
        //已经生成文件数量递增并保存到数据库
        int fileGeneratedNum = exportDto.getFileGeneratedNum();
        fileGeneratedNum++;
        exportDto.setFileGeneratedNum(fileGeneratedNum);

        //更新任务进度
        Boolean updateTaskFileNumBoolean = exportFeign.updateTaskFileNumById(exportDto);
        log.info("---------------------远程更新生成文件进度--------------------return-----" + !updateTaskFileNumBoolean);
        if (ObjectUtils.isEmpty(updateTaskFileNumBoolean)) {
            //清除缓存
            RedisUtils.remove(key);
            //更新任务状态为异常
            changeTaskStatusToUnusual(exportDto.getTaskId());
            throw new ExportPicDataBaseException();
        }
        return updateTaskFileNumBoolean;
    }

    /**
     * 生成图片本地文件
     *
     * @param readDevicePicPath 读取文件路径
     * @param fileName          新文件名
     * @param export            导出类
     * @return Boolean
     * @throws Exception
     * @author chaofanrong@wistronits.com
     * @date 14:01 2019/3/30
     */
    public Boolean generatePicInfo(String readDevicePicPath, String fileName, String type, Export export, ExportDto exportDto) throws Exception {
        //临时文件夹路径
        File dirFile = new File(export.getTaskFolderPath() + "/" + export.getExcelFolderName());
        //如果文件夹不存在，则创建新的文件夹
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        //从缓存中取出标识符，判断任务是否停止
        String key = "task" + export.getTaskId();
        String redisMark = (String) RedisUtils.get(key);
        //如果停止了
        if (!TaskStatusConstant.START.equals(redisMark)) {
            return false;
        }

        //文件路径+名称
        String filenameTemp = export.getTaskFolderPath() + "/" + export.getExcelFolderName() + "/" + fileName + type;
        File file = new File(filenameTemp);

        //如果文件存在，则重命名文件为追加时间戳后创建新的文件
        if (file.exists()) {
            filenameTemp = export.getTaskFolderPath() + "/" + export.getExcelFolderName() + "/" + fileName + "-" + System.currentTimeMillis() + type;
            file = new File(filenameTemp);
        }
        file.createNewFile();
        System.out.println("success create file,the file is " + filenameTemp);
        //创建文件成功后，写入内容到文件里
        this.writeFileContent(readDevicePicPath, file);

        //如果更新失败，清除缓存
        if (this.updateTaskFileNumById(exportDto, key)) {
            //清除缓存
            RedisUtils.remove(key);
            throw new ExportPicStopException();
            //否则更新成功
        } else {
            return true;
        }
    }

    /**
     * @param readDevicePicPath 读取文件路径
     * @param file              生成文件
     * @author chaofanrong@wistronits.com
     * @description 向文件中写入内容
     * @date 19:36 2019/3/30
     */
    public void writeFileContent(String readDevicePicPath, File file) {
        URL url;
        try {
            //生成图片链接的url类
            url = new URL(readDevicePicPath);
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();

            //为file生成对应的文件输出流
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            //定义字节数组大小
            byte[] buffer = new byte[1024];
            //从所包含的输入流中读取[buffer.length()]的字节，并将它们存储到缓冲区数组buffer 中。
            //inputStream.read()会返回写入到buffer的实际长度,若已经读完 则返回-1
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                //将buffer中的字节写入文件中区
                fileOutputStream.write(buffer, 0, len);
            }
            fileOutputStream.close();//关闭输出流
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建ZIP文件
     *
     * @param key       缓存key
     * @param exportDto 导出数据请求类
     * @return Boolean
     */
    public Boolean generatedZip(String key, Export export, ExportDto exportDto) {
        //压缩文件夹名为当前任务文件夹+临时文件夹名
        String sourcePath = export.getTaskFolderPath() + "/" + export.getExcelFolderName();
        String zipPathAndName = export.getTaskFolderPath() + "/" + export.getExcelFolderName() + ".zip";
        FileOutputStream fos;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipPathAndName);
            zos = new ZipOutputStream(fos);
            //解决打包时文件夹乱码
            zos.setEncoding("gbk");
            writeZip(new File(sourcePath), "", zos, key, exportDto);
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

    /**
     * 生成ZIP文件
     *
     * @param file       待写入文件
     * @param parentPath 文件夹路径
     * @param zos        zip输出流
     * @param key        缓存key
     * @param exportDto  导出数据请求类
     * @return void
     */
    private void writeZip(File file, String parentPath, ZipOutputStream zos, String key, ExportDto exportDto) {
        if (!TaskStatusConstant.START.equals(RedisUtils.get(key))) {
            throw new ExportPicStopException();
        }
        if (file.exists()) {
            if (file.isDirectory()) {
                //处理文件夹
                parentPath += file.getName() + File.separator;
                File[] files = file.listFiles();
                if (files.length != 0) {
                    for (File f : files) {
                        writeZip(f, parentPath, zos, key, exportDto);
                        //更新任务进度
                        this.updateProgressByZip(exportDto);

                        System.out.println("success compression file,the file is " + f.getName());
                    }
                } else {
                    //空目录则创建当前目录
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

    /**
     * 更新压缩包生成进度
     *
     * @param exportDto 导出数据请求类
     * @return Boolean
     */
    public Boolean updateProgressByZip(ExportDto exportDto) {
        //从缓存中取出标识符，判断任务是否停止
        String key = "task" + exportDto.getTaskId();
        String redisMark = (String) RedisUtils.get(key);
        //本地临时文件夹
        File file = new File(listExcelFilePath + exportDto.getTaskId());
        //如果停止了
        if (!TaskStatusConstant.START.equals(redisMark)) {
            HandleFile.deleteDir(file);
            throw new ExportPicStopException();
        }

        //如果更新失败，清除缓存
        if (this.updateTaskFileNumById(exportDto, key)) {
            //清除缓存
            RedisUtils.remove(key);
            throw new ExportPicStopException();
        }
        return true;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        boolean delete = dir.delete();
        if (!delete) {
            log.error(dir.getName() + "删除失败");
        }
        return delete;
    }
}
