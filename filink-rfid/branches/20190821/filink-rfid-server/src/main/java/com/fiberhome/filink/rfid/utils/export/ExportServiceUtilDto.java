package com.fiberhome.filink.rfid.utils.export;

import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;

/**
 * 导出Dto
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/21
 */
public class ExportServiceUtilDto<T extends AbstractExport> {
    /**
     * 列表导出抽象类
     */
    private T abstractExport;
    /**
     * 导出传输实体
     */
    private ExportDto exportDto;
    /**
     * 导出服务名
     */
    private String exportServerName;

    /**
     * 最大导出条数
     */
    private Integer maxExportDataSize;
    /**
     * 导出文件名
     */
    private String listName;
    /**
     * 系统语言公共类
     */
    private SystemLanguageUtil systemLanguageUtil;
    /**
     * 日志处理类
     */
    private LogProcess logProces;
    /**
     * 危险级别编码
     */
    private String functionCode;

    public ExportServiceUtilDto(T abstractExport, ExportDto exportDto, String exportServerName, Integer maxExportDataSize, String listName, SystemLanguageUtil systemLanguageUtil, LogProcess logProces, String functionCode) {
        this.abstractExport = abstractExport;
        this.exportDto = exportDto;
        this.exportServerName = exportServerName;
        this.maxExportDataSize = maxExportDataSize;
        this.listName = listName;
        this.systemLanguageUtil = systemLanguageUtil;
        this.logProces = logProces;
        this.functionCode = functionCode;
    }

    public T getAbstractExport() {
        return abstractExport;
    }

    public void setAbstractExport(T abstractExport) {
        this.abstractExport = abstractExport;
    }

    public ExportDto getExportDto() {
        return exportDto;
    }

    public void setExportDto(ExportDto exportDto) {
        this.exportDto = exportDto;
    }

    public String getExportServerName() {
        return exportServerName;
    }

    public void setExportServerName(String exportServerName) {
        this.exportServerName = exportServerName;
    }

    public Integer getMaxExportDataSize() {
        return maxExportDataSize;
    }

    public void setMaxExportDataSize(Integer maxExportDataSize) {
        this.maxExportDataSize = maxExportDataSize;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public SystemLanguageUtil getSystemLanguageUtil() {
        return systemLanguageUtil;
    }

    public void setSystemLanguageUtil(SystemLanguageUtil systemLanguageUtil) {
        this.systemLanguageUtil = systemLanguageUtil;
    }

    public LogProcess getLogProces() {
        return logProces;
    }

    public void setLogProces(LogProcess logProces) {
        this.logProces = logProces;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }
}
