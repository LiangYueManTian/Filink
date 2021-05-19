package com.fiberhome.filink.onenetserver.utils;

import com.fiberhome.filink.commonstation.utils.DeviceUpgradeUtil;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.FtpSettings;
import com.fiberhome.filink.redis.RedisUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(JMockit.class)
public class FtpUpgradeUtilTest {
    /**测试对象 FtpUpgradeUtil*/
    @Tested
    private FtpUpgradeUtil ftpUpgradeUtil;
    /**Mock ParameterFeign*/
    @Injectable
    private ParameterFeign parameterFeign;

    /**
     * refreshUpgradeFile
     */
    @Test
    public void refreshUpgradeFileTest() {
        FtpSettings ftpSettings = new FtpSettings();
        List<String> upgradeFileNames = new ArrayList<>();
        new Expectations(RedisUtils.class, DeviceUpgradeUtil.class) {
            {
                parameterFeign.queryFtpSettings();
                result = ftpSettings;
                DeviceUpgradeUtil.getUpgradeFileNames(ftpSettings, anyString);
                result = upgradeFileNames;
                RedisUtils.remove(anyString);
            }
        };
        ftpUpgradeUtil.refreshUpgradeFile();
        upgradeFileNames.add("test");
        ftpUpgradeUtil.refreshUpgradeFile();
    }
}
