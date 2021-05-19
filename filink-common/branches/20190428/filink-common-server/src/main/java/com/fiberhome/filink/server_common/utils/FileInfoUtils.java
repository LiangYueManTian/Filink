package com.fiberhome.filink.server_common.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hedongwei@wistronits.com
 * description 文件工具类
 * date 2019/1/17 9:47
 */
@Slf4j
public class FileInfoUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileInfoUtils.class);

    /**
     * @author hedongwei@wistronits.com
     * description 生成日志本地文件
     * date 14:01 2019/1/17
     * param [fileName, jsonObject, path]
     */
    public static boolean generateFileInfo(String fileName, String data, String path) throws Exception{
        boolean bool = false;
        //文件路径+名称+文件类型
        String filenameTemp = path + fileName;
        File file = new File(filenameTemp);
        String fileDir = path;
        File dirFile = new File(fileDir);
        //如果文件夹不存在，则创建新的文件夹
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        //如果文件不存在，则创建新的文件
        if (!file.exists()) {
            file.createNewFile();
            bool = true;
            System.out.println("success create file,the file is "+filenameTemp);
            //创建文件成功后，写入内容到文件里
            writeFileContent(filenameTemp, data);
        }
        return bool;
    }



    /**
     * @author hedongwei@wistronits.com
     * description 向文件中写入内容
     * date 19:36 2019/1/17
     * param [filepath, newstr]
     */
    public static boolean writeFileContent(String filepath, String newstr) throws Exception {
        boolean bool = false;
        //新写入的行，换行
        String filein = newstr+"\r\n";
        String temp  = "";
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        FileOutputStream fos  = null;
        PrintWriter pw = null;
        try {
            //文件路径(包括文件名称)
            File file = new File(filepath);
            //将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();

            //文件原有内容
            for (int i = 0 ; (temp =br.readLine()) != null ; i++){
                buffer.append(newstr);
            }
            buffer.append(filein);

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buffer.toString().toCharArray());
            pw.flush();
            bool = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //不要忘记关闭
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return bool;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 删除文件
     * date 19:37 2019/1/17
     * param [fileName, path]
     */
    public static boolean deleteFile(String fileName, String path) throws Exception{
        boolean bool = false;
        String filenameTemp = path + fileName;
        File file  = new File(filenameTemp);
        if (file.exists()){
            file.delete();
            bool = true;
        }
        return bool;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 查找一个文件夹下的所有文件
     * date 19:16 2019/1/17
     * param [fileDirectoryPath]
     */
    public static List<String> getFiles(String fileDirectoryPath) {
        //文件名称集合
        List<String> fileNameList = new ArrayList<String>();
        //获得该文件夹信息
        File file = new File(fileDirectoryPath);
        //判断是否是文件夹
        if (file.isDirectory()) {
            //获得文件夹下的所有文件
            File[] files = file.listFiles();
            if (null != files) {
                if (0 < files.length) {
                    //遍历所有文件，将文件名称加到文件名称集合中
                    for (int i = 0; i < files.length; i++) {
                        File fileOne = files[i];
                        if (null != fileOne) {
                            fileNameList.add(fileOne.getName());
                        }
                    }
                }
            }
        }
        return fileNameList;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 读取文件信息
     * date 19:07 2019/1/17
     * param [fileName, path]
     */
    public static JSONObject readTxt(String fileName, String path) throws Exception{
        String filePath = path + fileName;
        File file = new File(filePath);
        StringBuilder result = new StringBuilder();
        //构造一个BufferedReader类来读取文件
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s = null;
        //使用readLine方法，一次读一行
        while ((s = br.readLine()) != null) {
            result.append(s);
        }
        br.close();
        JSONObject json = JSONObject.parseObject(result.toString());
        return json;
    }

}
