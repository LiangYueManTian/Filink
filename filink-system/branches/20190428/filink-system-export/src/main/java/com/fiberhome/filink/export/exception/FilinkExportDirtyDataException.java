package com.fiberhome.filink.export.exception;

/**
 * <p>
 * FilinkExportDirtyDataException 数据异常
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-23
 */
public class FilinkExportDirtyDataException extends RuntimeException {
    public FilinkExportDirtyDataException() {
    }

    public FilinkExportDirtyDataException(String msg) {
        super(msg);
    }
}
