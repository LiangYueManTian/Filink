package com.fiberhome.filink.boxhead.service.impl;

import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.boxhead.bean.BoxHead;
import com.fiberhome.filink.boxhead.dao.BoxHeadDao;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.UserFeign;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>
 * BoxHeadServiceImplTest
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/7/2
 */
@RunWith(JMockit.class)
public class BoxHeadServiceImplTest {
    @Tested
    private BoxHeadServiceImpl boxHeadService;
    @Injectable
    private BoxHeadDao boxHeadDao;
    @Injectable
    private UserFeign userFeign;
    @Mocked
    private RequestInfoUtils requestInfoUtils;
    @Mocked
    private NineteenUUIDUtils nineteenUUIDUtils;
    @Mocked
    private BoxHead boxHead;
    @Mocked
    private I18nUtils i18nUtils;

    @Test
    public void queryBoxHead() {
        boxHeadService.queryBoxHead();
    }

    @Test
    public void saveBoxHead() {

        boxHeadService.saveBoxHead(boxHead);

        new Expectations(){
            {
                boxHeadDao.selectOneByUserAndMenu((BoxHead) any);
                result = null;
            }
        };
        boxHeadService.saveBoxHead(boxHead);
    }
}
