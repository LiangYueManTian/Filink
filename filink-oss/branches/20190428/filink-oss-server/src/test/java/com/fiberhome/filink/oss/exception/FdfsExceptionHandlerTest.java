package com.fiberhome.filink.oss.exception;

import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>
 *     异常捕获类测试类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/3/8
 */
@RunWith(JMockit.class)
public class FdfsExceptionHandlerTest {
    /**
     * 测试对象
     */
    @Tested
    private FdfsExceptionHandler fdfsExceptionHandler;

    @Test
    public void handlerFdfsParamExceptionTest() {
        fdfsExceptionHandler.handlerFdfsParamException(new FilinkFdfsParamException());
    }

    @Test
    public void handlerFdfsFileExceptionTest() {
        fdfsExceptionHandler.handlerFdfsFileException(new FilinkFdfsFileException());
    }

}
