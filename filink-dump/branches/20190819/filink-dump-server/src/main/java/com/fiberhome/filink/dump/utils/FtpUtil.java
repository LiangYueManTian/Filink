package com.fiberhome.filink.dump.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

/**
 * ftp工具类
 * @author hedongwei@wistronits.com
 * @date 2019/8/20 16:14
 */
@Slf4j
public class FtpUtil {

    /**
     * 获取FTPClient对象
     *
     * @param ftpHost     FTP主机服务器
     * @param ftpPassword FTP 登录密码
     * @param ftpUserName FTP登录用户名
     * @param ftpPort     FTP端口 默认为21
     * @param timeout     超时时间
     * @return ftp客户端
     */
    public static FTPClient getFTPClient(String ftpHost, String ftpUserName,
                                         String ftpPassword, int ftpPort, int timeout) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient = new FTPClient();
            // 连接FTP服务器
            ftpClient.setConnectTimeout(timeout);
            ftpClient.connect(ftpHost, ftpPort);
            // 登陆FTP服务器
            ftpClient.login(ftpUserName, ftpPassword);
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                ftpClient.disconnect();
            } else {
                log.info("ftp connect success>>>");
            }
        } catch (SocketException e) {
            e.printStackTrace();
            log.info("ftp ip is error>>>");
        } catch (IOException e) {
            e.printStackTrace();
            log.info("ftp port is error>>>");
        }
        return ftpClient;
    }


    /**
     * 上传文件
     *
     * @param ftpHost     ftp服务器地址
     * @param ftpUserName anonymous匿名用户登录，不需要密码。administrator指定用户登录
     * @param ftpPassword 指定用户密码
     * @param ftpPort     ftp服务员器端口号
     * @param ftpPath     ftp文件存放物理路径
     * @param fileName    文件路径
     * @param input       文件输入流，即从本地服务器读取文件的IO输入流
     * @param timeout     超时时间
     */
    public static boolean uploadFile(String ftpHost, String ftpUserName,
                                  String ftpPassword, int ftpPort, String ftpPath,
                                  String fileName, InputStream input, int timeout) {
        boolean flag = false;
        FTPClient ftp = null;
        try {
            ftp = getFTPClient(ftpHost, ftpUserName, ftpPassword, ftpPort, timeout);
            ftp.changeWorkingDirectory(ftpPath);
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            fileName = new String(fileName.getBytes("GBK"), StandardCharsets.ISO_8859_1);
            ftp.storeFile(fileName, input);
            flag = true;
            log.info("upload success!");
            return flag;
        } catch (Exception e) {
            log.error("ftp upload failed", e);
        } finally {
            try {
                input.close();
                ftp.logout();
            } catch (Exception e) {
                log.info("close input stream failed!");
            }
        }
        return flag;
    }

}
