package com.fiberhome.filink.dump.utils;

import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 导出数据测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/29 15:47
 */
@RunWith(JMockit.class)
public class ExcelListUtilTest {

    @Tested
    private  ExcelListUtil excelListUtil;


    /**
     * 列表导出
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 16:07
     */
    @Test
    public void listExport() {
        List<String> columnNameList = new ArrayList<>();
        columnNameList.add("id");
        columnNameList.add("name");
        List<List<String>> dataList = new ArrayList<>();
        List<String> dataArrayList = new ArrayList<>();
        dataArrayList.add("1");
        dataArrayList.add("2");
        for (int i = 0 ; i < 50001 ; i++) {
            dataList.add(dataArrayList);
        }

        String folderPath = "/path";
        String fileName = "/path";

        try {
            excelListUtil.listExport(columnNameList, dataList, folderPath, fileName);
        } catch (Exception e) {

        }
    }

    /**
     * 导出csv
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 16:06
     */
    @Test
    public void listExportToCsv() {
        List<String> columnNameList = new ArrayList<>();
        columnNameList.add("id");
        columnNameList.add("name");
        List<List<String>> dataList = new ArrayList<>();
        List<String> dataArrayList = new ArrayList<>();
        dataArrayList.add("1");
        dataArrayList.add("2");
        dataList.add(dataArrayList);
        dataList.add(dataArrayList);
        String folderPath = "/path";
        String fileName = "/path";

        try {
            excelListUtil.listExportToCsv(columnNameList, dataList, folderPath, fileName);
        } catch (Exception e) {

        }
    }


    /**
     * 删除文件夹
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 16:04
     */
    @Test
    public void deleteDir() {
        String path = "/info";
        File dir = new File(path);
        excelListUtil.deleteDir(dir);
    }
}
