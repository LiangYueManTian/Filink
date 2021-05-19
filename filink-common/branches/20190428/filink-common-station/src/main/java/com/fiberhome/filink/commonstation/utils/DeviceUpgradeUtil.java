package com.fiberhome.filink.commonstation.utils;

import com.fiberhome.filink.commonstation.entity.config.UpgradeConfig;
import com.fiberhome.filink.parameter.bean.FtpSettings;
import lombok.extern.log4j.Log4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 解压工具类
 *
 * @author CongcaiYu
 */
@Log4j
public class DeviceUpgradeUtil {

    private static final String CONFIG_NAME = "config.ini";

    private static final String SOFT_VERSION = "SOFT_VERSION";

    private static final String DEPENDENT_HARD_VERSION = "DEPENDENT_HARD_VERSION";

    private static final String DEPENDENT_SOFT_VERSION = "DEPENDENT_SOFT_VERSION";

    private static final String SHA_256 = "SHA-256";

    private static final String GENERATION_TIME = "GENERATION_TIME";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd/hh/mm");


    /**
     * @param ftpSettings   ftp信息
     * @param ziPassword    解压文件密码(可以为空)
     * @param filePath      ftp文件路径
     * @param tmpDir        临时文件夹
     * @param upgradeConfig 升级信息
     */
    public static void setUpgradeConfig(FtpSettings ftpSettings, String ziPassword, String filePath, String tmpDir, UpgradeConfig upgradeConfig) {
        try {
            FTPClient ftpClient = FtpUtil.getFTPClient(ftpSettings.getInnerIpAddress(), ftpSettings.getUserName(), ftpSettings.getPassword(),
                    ftpSettings.getPort(), ftpSettings.getDisconnectTime() * 1000);
            // 中文支持
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalActiveMode();
            ftpClient.changeWorkingDirectory(filePath);
            //目录不存在则创建
            File tmpDirFile = new File(tmpDir);
            if (!tmpDirFile.exists()) {
                tmpDirFile.mkdirs();
            }
            //将ftp文件转成file
            File tmpFile = new File(tmpDir + File.separator + UUID.randomUUID().toString());
            FileOutputStream outputStream = new FileOutputStream(tmpFile);
            ftpClient.retrieveFile(filePath, outputStream);
            ftpClient.logout();
            ftpClient.disconnect();
            outputStream.close();
            // 首先创建ZipFile指向磁盘上的.zip文件
            ZipFile zFile = new ZipFile(tmpFile);
            zFile.setFileNameCharset("GBK");
            // 解压目录
            if (zFile.isEncrypted()) {
                // 设置密码
                zFile.setPassword(ziPassword.toCharArray());
            }
            // 将文件抽出到解压目录(解压)
            List<FileHeader> fileHeaders = zFile.getFileHeaders();
            InputStream configInputStream = null;
            String hexBinFile = "";
            for (FileHeader fileHeader : fileHeaders) {
                String fileName = fileHeader.getFileName();
                if (fileName.contains(CONFIG_NAME)) {
                    configInputStream = zFile.getInputStream(fileHeader);
                } else {
                    InputStream binInputStream = zFile.getInputStream(fileHeader);
                    hexBinFile = inputStreamToHexStr(binInputStream);
                }
            }
            if (configInputStream == null) {
                log.error("there is no file named : " + CONFIG_NAME);
                return;
            }
            Properties properties = new Properties();
            properties.load(configInputStream);
            configInputStream.close();
            //构造设施升级校验配置类对象
            upgradeConfig.setHexBinFile(hexBinFile);
            upgradeConfig.setSoftwareVersion(properties.getProperty(SOFT_VERSION));
            upgradeConfig.setDependentSoftVersion(properties.getProperty(DEPENDENT_SOFT_VERSION));
            upgradeConfig.setDependentHardVersion(properties.getProperty(DEPENDENT_HARD_VERSION));
            upgradeConfig.setSha256(properties.getProperty(SHA_256));
            //解析时间
            long generationTime = DATE_FORMAT.parse(properties.getProperty(GENERATION_TIME)).getTime();
            upgradeConfig.setGenerationTime(generationTime);
            //删除临时文件
            tmpFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取所有升级文件名称
     * @param ftpSettings   ftp信息
     * @param filePath      ftp文件路径
     * @return 所有升级文件名称
     */
    public static List<String> getUpgradeFileNames(FtpSettings ftpSettings, String filePath) {
        List<String> upgradeFileNames = new ArrayList<>();
        try {
            FTPClient ftpClient = FtpUtil.getFTPClient(ftpSettings.getInnerIpAddress(), ftpSettings.getUserName(), ftpSettings.getPassword(),
                    ftpSettings.getPort(), ftpSettings.getDisconnectTime() * 1000);
            // 中文支持
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalActiveMode();
            ftpClient.changeWorkingDirectory(filePath);
            String[] ftpFileNames = ftpClient.listNames();
            ftpClient.logout();
            ftpClient.disconnect();
            if (ftpFileNames == null || ftpFileNames.length == 0) {
                log.warn("Ftp files is null");
            } else {
                upgradeFileNames = Arrays.asList(ftpFileNames);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return upgradeFileNames;
    }



    /**
     * 流转成16进制
     *
     * @param inputStream 输入流
     * @return 16进制数据
     */
    private static String inputStreamToHexStr(InputStream inputStream) {
        int length = 1024;
        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new BufferedInputStream(inputStream);
            out = new ByteArrayOutputStream(length);
            byte[] temp = new byte[length];
            int size = 0;
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            return HexUtil.bytesToHexString(out.toByteArray());
        } catch (Exception e) {
            log.error("inputStream to hexStr failed>>>>>");
        } finally {
            try {
                in.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}