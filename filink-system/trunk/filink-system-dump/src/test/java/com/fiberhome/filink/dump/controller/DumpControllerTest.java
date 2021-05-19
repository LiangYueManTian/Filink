package com.fiberhome.filink.dump.controller;

import com.fiberhome.filink.dump.bean.DumpBean;
import com.fiberhome.filink.dump.bean.DumpParam;
import com.fiberhome.filink.dump.service.DumpService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>
 *
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/7/2
 */
@RunWith(JMockit.class)
public class DumpControllerTest {

    @Tested
    private DumpController dumpController;
    @Injectable
    private DumpService dumpService;
    @Mocked
    private I18nUtils i18nUtils;



    @Test
    public void updateDump() {
        DumpParam dumpParam = new DumpParam();
        dumpController.updateDump(dumpParam);
        dumpParam.setParamId("id");
        dumpParam.setParamType("type");
        dumpController.updateDump(dumpParam);
        dumpParam.setDumpBean(new DumpBean());
        dumpController.updateDump(dumpParam);
    }

    @Test
    public void queryDump() {
        dumpController.queryDump("type");
    }
}