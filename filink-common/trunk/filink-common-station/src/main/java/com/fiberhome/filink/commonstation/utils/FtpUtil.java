package com.fiberhome.filink.commonstation.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

/**
 * ftp工具类
 *
 * @author CongcaiYu
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
                log.info("username or password error>>>");
                ftpClient.disconnect();
            } else {
                log.info("ftp connect success>>>");
            }
        } catch (SocketException e) {
            log.info("ftp ip is error {}", e);
        } catch (IOException e) {
            log.info("ftp port is error {}", e);
        }
        return ftpClient;
    }

    /**
     * 下载文件
     *
     * @param ftpHost     ftp服务器地址
     * @param ftpUserName anonymous匿名用户登录，不需要密码。administrator指定用户登录
     * @param ftpPassword 指定用户密码
     * @param ftpPort     ftp服务员器端口号
     * @param ftpPath     ftp文件存放物理路径
     * @param timeout     超时时间
     */
    public static FTPFile[] getFiles(String ftpHost, String ftpUserName, String ftpPassword,
                                     int ftpPort, String ftpPath, int timeout) {
        FTPClient ftpClient = null;

        try {
            ftpClient = getFTPClient(ftpHost, ftpUserName, ftpPassword, ftpPort, timeout);
            // 中文支持
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(ftpPath);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            ftpClient.logout();
            ftpClient.disconnect();
            return ftpFiles;
        } catch (SocketException e) {
            log.error("ftp connect failed {}", e);
        } catch (IOException e) {
            log.error("read file error {}", e);
        }
        return null;
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
    public static void uploadFile(String ftpHost, String ftpUserName,
                                  String ftpPassword, int ftpPort, String ftpPath,
                                  String fileName, InputStream input, int timeout) {
        FTPClient ftp = null;
        try {
            ftp = getFTPClient(ftpHost, ftpUserName, ftpPassword, ftpPort, timeout);
            ftp.changeWorkingDirectory(ftpPath);
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            fileName = new String(fileName.getBytes("GBK"), StandardCharsets.ISO_8859_1);
            ftp.storeFile(fileName, input);
            input.close();
            ftp.logout();
            System.out.println("upload succes!");
        } catch (Exception e) {
            log.error("upload file error {}", e);
        }
    }

}