package com.fiberhome.filink.dump.utils;

import com.fiberhome.filink.dump.constant.ExportApiConstant;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 列表导出工具类
 *
 * @author qiqizhu@wistronits.com
 * @Date: 2019/2/18 10:04
 */
@Component
@Slf4j
public class ExcelListUtil {


    /**
     * 创建表格工具类
     *
     * @param columnNameList 列名集合
     * @param dataList       数据集合
     * @param folderPath     文件夹路径
     * @param fileName       文件名称
     * @return 创建结果
     */
    public static void listExport(List<String> columnNameList, List<List<String>> dataList, String folderPath, String fileName) throws Exception {
        //创建HSSFWorkbook对象(excel的文档对象)
        HSSFWorkbook wkb = new HSSFWorkbook();

        Integer maxSize = ExportApiConstant.MAX_SHEET_ROW_NUM;
        if (dataList.size() <= maxSize) {
            //建立新的sheet对象（excel的表单）
            HSSFSheet sheet = wkb.createSheet();
            //在sheet里创建第二行
            HSSFRow row1 = sheet.createRow(0);
            //获取列的数量
            int columnSize = columnNameList.size();
            //创建单元格并设置单元格内容
            for (int i = 0; i < columnSize; i++) {
                row1.createCell(i).setCellValue(columnNameList.get(i));
            }
            for (int i = 0; i < dataList.size(); i++) {
                HSSFRow row = sheet.createRow(1 + i);
                //当前数据集合
                List<String> data = dataList.get(i);
                for (int j = 0; j < data.size(); j++) {
                    row.createCell(j).setCellValue(data.get(j));
                }
            }
        } else {
            int startList = 0;
            while(startList <= dataList.size()) {
                List<List<String>> subDataList = new ArrayList<List<String>>();
                if(startList + maxSize <= dataList.size()) {
                    subDataList = dataList.subList(startList, startList + maxSize);
                } else {
                    subDataList = dataList.subList(startList, dataList.size());
                }
                startList += maxSize;
                //建立新的sheet对象（excel的表单）
                HSSFSheet sheet = wkb.createSheet();
                //在sheet里创建第二行
                HSSFRow row1 = sheet.createRow(0);
                //获取列的数量
                int columnSize = columnNameList.size();
                //创建单元格并设置单元格内容
                for (int i = 0; i < columnSize; i++) {
                    row1.createCell(i).setCellValue(columnNameList.get(i));
                }
                for (int i = 0; i < subDataList.size(); i++) {
                    HSSFRow row = sheet.createRow(1 + i);
                    //当前数据集合
                    List<String> data = subDataList.get(i);
                    for (int j = 0; j < data.size(); j++) {
                        row.createCell(j).setCellValue(data.get(j));
                    }
                }
            }
        }

        //如果文件夹不存在生成文件夹
        File dirFile = new File(folderPath);
        //如果文件夹不存在，则创建新的文件夹
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        //输出Excel文件
        FileOutputStream output = null;
        output = new FileOutputStream(folderPath + fileName + ".xls");
        wkb.write(output);
        output.close();

    }

    /**
     * 创建表格工具类
     *
     * @param columnNameList 列名集合
     * @param dataList       数据集合
     * @param folderPath     文件夹路径
     * @param fileName       文件名称
     * @return 创建结果
     */
    public static void listExportToCsv(List<String> columnNameList, List<List<String>> dataList, String folderPath, String fileName) throws IOException, IllegalArgumentException {
        //如果文件夹不存在生成文件夹
        File dirFile = new File(folderPath);
        //如果文件夹不存在，则创建新的文件夹
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File file = new File(folderPath + fileName + ".csv");
        //构建输出流，同时指定编码
        OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(file), "gbk");

        //csv文件是逗号分隔，除第一个外，每次写入一个单元格数据后需要输入逗号
        for (String columnName : columnNameList) {
            ow.write(columnName);
            ow.write(",");
        }
        //写完文件头后换行
        ow.write("\r\n");
        //写内容
        for (List<String> data : dataList) {
            for (String property : data) {
                String s = StringEscapeUtils.escapeCsv(property);
                ow.write(s);
                ow.write(",");
                continue;
            }
            //写完一行换行
            ow.write("\r\n");
        }
        ow.flush();
        ow.close();
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
            log.error(dir.getName() + "failed to delete");
        }
        return delete;
    }

}
