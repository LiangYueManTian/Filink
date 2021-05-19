package com.fiberhome.filink.oss.exception;

import com.fiberhome.filink.oss.constant.FdfsResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <P>
 *     文件服务器全局捕获
 * </P>
 * @author chaofang@wistronits.com
 * @since \2019/1/17
 */
@Slf4j
@RestControllerAdvice
public class FdfsExceptionHandler {

    /**
     * 捕获文件服务器自定义请求参数异常
     *
     * @param ex 异常
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(FilinkFdfsParamException.class)
    @ResponseBody
    public void handlerFdfsParamException(FilinkFdfsParamException ex) {
        log.error("ErrorCode: " + FdfsResultCode.OSS_PARAM_ERROR + "******parameter error");
        ex.printStackTrace();
    }

    /**
     * 捕获文件服务器自定义文件流异常
     *
     * @param ex 异常
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(FilinkFdfsFileException.class)
    @ResponseBody
    public void handlerFdfsFileException(FilinkFdfsFileException ex) {
        log.error("ErrorCode: " + FdfsResultCode.OSS_FILE_ERROR + "********IOException");
        ex.printStackTrace();
    }

}
