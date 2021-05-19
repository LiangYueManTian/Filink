package com.fiberhome.filink.workflowbusinessserver.exception.procbase.procoperate;

/**
 * <p>
 * DownloadProcErrorException 下载流程异常
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-27
 */
public class DownLoadProcErrorException extends RuntimeException {

    public DownLoadProcErrorException() {
    }

    public DownLoadProcErrorException(String msg) {
        super(msg);
    }

}
