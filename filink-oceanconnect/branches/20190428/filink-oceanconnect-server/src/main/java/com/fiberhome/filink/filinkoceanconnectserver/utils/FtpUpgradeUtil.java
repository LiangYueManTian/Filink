package com.fiberhome.filink.filinkoceanconnectserver.utils;

import com.fiberhome.filink.commonstation.utils.DeviceUpgradeUtil;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.FtpSettings;
import com.fiberhome.filink.redis.RedisUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 刷新文件
 *
 * @author CongcaiYu
 */
@Component
public class FtpUpgradeUtil {


    @Autowired
    private ParameterFeign parameterFeign;

    @Value("${constant.ftpFilePath}")
    private String ftpFilePath;
    /**
     * 刷新文件
     */
    public void refreshUpgradeFile() {
        FtpSettings ftpSettings = parameterFeign.queryFtpSettings();
        List<String> upgradeFileNames = DeviceUpgradeUtil.getUpgradeFileNames(ftpSettings, ftpFilePath);
        if (CollectionUtils.isEmpty(upgradeFileNames)) {
            return;
        }
        for (String name : upgradeFileNames) {
            name = FilenameUtils.getBaseName(name);
            String key = RedisKey.UPGRADE_FILE_PREFIX + name;
            RedisUtils.remove(key);
        }
    }
}
