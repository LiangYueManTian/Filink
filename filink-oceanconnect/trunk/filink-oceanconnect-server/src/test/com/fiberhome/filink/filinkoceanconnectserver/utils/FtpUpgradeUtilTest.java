package com.fiberhome.filink.filinkoceanconnectserver.utils;

import com.fiberhome.filink.commonstation.utils.DeviceUpgradeUtil;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.FtpSettings;
import com.fiberhome.filink.redis.RedisUtils;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * ftpUpgradeUtil测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class FtpUpgradeUtilTest {

    @Tested
    private FtpUpgradeUtil ftpUpgradeUtil;

    @Injectable
    private ParameterFeign parameter;

    @Injectable
    private String ftpFilePath;

    @Before
    public void setUp() {
        ftpFilePath = "11";
    }

    @Test
    public void refreshUpgradeFile() {
        //list为空
        FtpSettings ftpSettings = new FtpSettings();
        List<String> upgradeFileNames = new ArrayList<>();
        new Expectations() {
            {
                parameter.queryFtpSettings();
                result = ftpSettings;
            }
        };
        new MockUp<DeviceUpgradeUtil>() {
            @Mock
            public List<String> getUpgradeFileNames(FtpSettings ftpSettings, String filePath) {
                return upgradeFileNames;
            }
        };
        ftpUpgradeUtil.refreshUpgradeFile();

        //list不为空
        upgradeFileNames.add("config.ini");
        new Expectations() {
            {
                parameter.queryFtpSettings();
                result = ftpSettings;
            }
        };
        new MockUp<DeviceUpgradeUtil>() {
            @Mock
            public List<String> getUpgradeFileNames(FtpSettings ftpSettings, String filePath) {
                return upgradeFileNames;
            }
        };
        new MockUp<FilenameUtils>() {
            @Mock
            public String getBaseName(String filename) {
                return "config.ini";
            }
        };
        String key = RedisKey.UPGRADE_FILE_PREFIX + "config.ini";
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.remove(key);
            }
        };
        ftpUpgradeUtil.refreshUpgradeFile();
    }
}