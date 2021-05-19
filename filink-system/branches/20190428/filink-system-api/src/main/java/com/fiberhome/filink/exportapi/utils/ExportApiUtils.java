package com.fiberhome.filink.exportapi.utils;

import com.fiberhome.filink.exportapi.bean.ColumnInfo;
import com.fiberhome.filink.exportapi.bean.Export;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 列表导出工具类
 *
 * @author qiqizhu@wistronits.com
 * @Date: 2019/3/14 14:12
 */
@Slf4j
public class ExportApiUtils {
    /**
     * ThreadLocal 用于存储当前用户id 时区
     */
    public static final ThreadLocal<Map<String, String>> RESOURCE = new ThreadLocal<Map<String, String>>();

    /**
     * 处理导出数据
     *
     * @param export     传入导出信息
     * @param objectList 传入导出数据
     * @throws Exception 方法未找到异常
     */
    public static void handelExportDto(Export export, List<Object> objectList) throws Exception {
        //列信息集合
        List<ColumnInfo> columnInfoList = export.getColumnInfoList();
        //根据属性名拿到数据
        List<List<String>> dataList = ExportApiUtils.findValueByName(columnInfoList, objectList);
        //拿到列名称并放入dataList
        List<String> nameList = new ArrayList<>();
        for (ColumnInfo columnInfo : columnInfoList) {
            nameList.add(columnInfo.getColumnName());
        }
        dataList.add(0, nameList);
        export.setDataList(dataList);
    }

    /**
     * 根据属性名称获取值
     *
     * @param columnInfoList 列名信息集合
     * @param objectList
     * @return 分装好的导出数据
     * @throws Exception 方法未找到异常等
     */
    private static List<List<String>> findValueByName(List<ColumnInfo> columnInfoList, List<Object> objectList) throws Exception {
        List<List<String>> dataList = new ArrayList<>();
        Class<?> aClass = objectList.get(0).getClass();
        List<Method> methods = new ArrayList<>();
        for (ColumnInfo columnInfo : columnInfoList) {
            char[] chars = columnInfo.getPropertyName().toCharArray();
            if (chars[0] >= 'a' && chars[0] <= 'z') {
                chars[0] = (char) (chars[0] - 32);
            }
            String methodName;
            if (1 == columnInfo.getIsTranslation()) {
                methodName = "get" + "Translation" + new String(chars);
            } else {
                methodName = "get" + new String(chars);
            }
            Method method = aClass.getMethod(methodName);
            methods.add(method);
        }
        for (Object o : objectList) {
            List<String> valueList = new ArrayList<>(columnInfoList.size());
            for (Method method : methods) {
                Object invoke = method.invoke(o);
                if (invoke != null) {
                    valueList.add(String.valueOf(invoke));
                } else {
                    valueList.add("");
                }
            }
            dataList.add(valueList);
        }
        return dataList;
    }

    /**
     * 从请求头获取时区
     *
     * @return
     */
    public static String getZone() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String zone = request.getHeader("zone");
        return zone;
    }

    /**
     * 从请求头获取相关信息
     *
     * @return
     */
    public static String getRequestUri() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request.getRequestURI();
    }

    /**
     * 获取时区时间
     *
     * @param time 时间戳
     * @return 时间
     */
    public static String getZoneTime(Long time) {
        if (time == null) {
            return null;
        }
        Map<String, String> map = RESOURCE.get();
        if (map == null) {
            log.error("timeZone is null >>>");
            return null;
        }
        String timeZone = map.get("timeZone");
        if (timeZone == null) {
            timeZone = "GMT+08:00";
        }
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(tz);
        return sdf.format(date);
    }

    /**
     * 获取当前系统时区时间
     * @return
     */
    public static String getCurrentZoneTime() {
        Map<String, String> map = RESOURCE.get();
        if (map == null) {
            log.error("timeZone is null >>>");
            return null;
        }
        String timeZone = map.get("timeZone");
        if (timeZone == null) {
            timeZone = "GMT+08:00";
        }
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(tz);
        return sdf.format(date);
    }

    /**
     * 获取当前用户id
     *
     * @return
     */
    public static String getCurrentUserId() {
        Map<String, String> map = RESOURCE.get();
        if (map == null) {
            log.error("userId is null >>>");
            return null;
        }
        return map.get("userId");
    }
}
