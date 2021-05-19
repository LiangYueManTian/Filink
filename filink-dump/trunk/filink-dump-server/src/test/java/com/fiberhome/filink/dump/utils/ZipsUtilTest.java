package com.fiberhome.filink.dump.utils;

import com.fiberhome.filink.dump.service.TaskInfoService;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 压缩工具类
 * @author hedongwei@wistronits.com
 * @date 2019/7/29 15:38
 */
@RunWith(JMockit.class)
public class ZipsUtilTest {

    @Tested
    private ZipsUtil zipsUtil;

    @Injectable
    private TaskInfoService exportFeign;

    /**
     * 创建ZIP文件
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 15:41
     */
    @Test
    public void createZip() {
        String sourcePath = "/path";
        String zipNameAndPath = "/path";
        String taskId = "1";
        zipsUtil.createZip(sourcePath, zipNameAndPath, taskId);
    }
}
