package com.fiberhome.filink.boxhead.controller;

import com.fiberhome.filink.boxhead.bean.BoxHead;
import com.fiberhome.filink.boxhead.service.BoxHeadService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>
 * BoxHeadControllerTest
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/7/2
 */
@RunWith(JMockit.class)
public class BoxHeadControllerTest {

    @Tested
    private BoxHeadController boxHeadController;
    @Injectable
    private BoxHeadService boxHeadService;
    @Mocked
    private I18nUtils i18nUtils;

    @Test
    public void queryBoxHead() {
        boxHeadController.queryBoxHead();
    }

    @Test
    public void saveBoxHead() {
        BoxHead boxHead = new BoxHead();
        boxHeadController.saveBoxHead(boxHead);
        boxHead.setMenuId("3-02");
        boxHead.setCustom("custom");
        boxHeadController.saveBoxHead(boxHead);
    }
}
