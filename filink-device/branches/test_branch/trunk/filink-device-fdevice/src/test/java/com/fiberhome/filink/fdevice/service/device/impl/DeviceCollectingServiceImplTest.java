package com.fiberhome.filink.fdevice.service.device.impl;

import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.fdevice.async.DeviceAttentionAsync;
import com.fiberhome.filink.fdevice.bean.device.DeviceCollecting;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.dao.device.DeviceCollectingDao;
import com.fiberhome.filink.fdevice.dao.device.DeviceInfoDao;
import com.fiberhome.filink.fdevice.dto.DeviceAttentionDto;
import com.fiberhome.filink.fdevice.dto.DeviceParam;
import com.fiberhome.filink.fdevice.exception.FilinkAttentionDataException;
import com.fiberhome.filink.fdevice.exception.FilinkAttentionRepeatException;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.mysql.MpQueryHelper;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * DeviceCollectingServiceImpl 测试类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/18
 */
@RunWith(JMockit.class)
public class DeviceCollectingServiceImplTest {
    /**
     * 测试对象
     */
    @Tested
    private DeviceCollectingServiceImpl deviceCollectingService;

    /**
     * 注入持久层
     */
    @Injectable
    private DeviceCollectingDao deviceCollectingDao;
    @Injectable
    private DeviceInfoDao deviceInfoDao;
    @Injectable
    private UserFeign userFeign;
    @Injectable
    private DeviceInfoService deviceInfoService;
    @Injectable
    private SystemLanguageUtil systemLanguageUtil;
    @Injectable
    private DeviceAttentionAsync deviceAttentionAsync;

    /**
     * mock
     */
    @Mocked
    private DeviceCollecting deviceCollecting;

    private QueryCondition<DeviceCollecting> queryCondition;
    @Mocked
    private List<FilterCondition> filterConditions;
    @Mocked
    private RequestInfoUtils requestInfoUtils;
    @Mocked
    private I18nUtils i18nUtils;
    @Mocked
    private User user;
    @Mocked
    private MpQueryHelper mpQueryHelper;

    private DeviceParam deviceParam;


    @Before
    public void setUp(){
        queryCondition = new QueryCondition<>();
        queryCondition.setFilterConditions(new ArrayList<>());
        deviceParam = new DeviceParam();
        deviceParam.setAreaIds(new ArrayList<>(Arrays.asList("id1")));
        deviceParam.setDeviceTypes(new ArrayList<>(Arrays.asList("deviceType1")));
    }


    @Test
    public void addDeviceCollectingTest() {
        //设施不存在
        new Expectations() {
            {
                deviceInfoDao.selectDeviceById(anyString);
                result = null;
            }
        };
        try {
            deviceCollectingService.addDeviceCollecting(deviceCollecting);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAttentionDataException.class);
        }

        //设施存在，用户不存在
        new Expectations() {
            {
                deviceInfoDao.selectDeviceById(anyString);
                result = new DeviceInfo();
                userFeign.queryUserById(anyString);
                result = false;
            }
        };
        try {
            deviceCollectingService.addDeviceCollecting(deviceCollecting);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAttentionDataException.class);
        }

        //设施存在，用户存在,用户已关注该设施
        new Expectations() {
            {
                deviceInfoDao.selectDeviceById(anyString);
                result = new DeviceInfo();
                userFeign.queryUserById(anyString);
                result = true;
                deviceCollectingDao.selectOne((DeviceCollecting) any);
                result = deviceCollecting;

            }
        };
        try {
            deviceCollectingService.addDeviceCollecting(deviceCollecting);
        } catch (Exception e) {
            Assert.assertTrue(e.getClass() == FilinkAttentionRepeatException.class);
        }


        //设施存在，用户存在,正常关注
        new Expectations() {
            {
                deviceInfoDao.selectDeviceById(anyString);
                result = new DeviceInfo();
                userFeign.queryUserById(anyString);
                result = true;
                deviceCollectingDao.selectOne((DeviceCollecting) any);
                result = null;

            }
        };
        deviceCollectingService.addDeviceCollecting(deviceCollecting);

    }

    @Test
    public void delDeviceCollectingTest() {
        //传入userId，deviceId，取消关注 失败
        new Expectations() {
            {
                deviceCollecting.getUserId();
                result = "adadasd";
                deviceCollecting.getDeviceId();
                result = "adadasd";

            }
        };
        deviceCollectingService.delDeviceCollecting(deviceCollecting);

        //已取消关注，将这个设施id推送给前台
        new Expectations() {
            {
                deviceCollecting.getUserId();
                result = "adadasd";
                deviceCollecting.getDeviceId();
                result = "adadasd";
                deviceCollectingDao.selectOneAttentionByDeviceId((DeviceCollecting) any);
                result = null;
            }
        };
        deviceCollectingService.delDeviceCollecting(deviceCollecting);


    }

    @Test
    public void queryDeviceCollectingListTest() {
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(1);
        new Expectations() {
            {
                RequestInfoUtils.getUserId();
                result = "0";
                userFeign.queryCurrentUser(anyString,anyString);
                result = user;
                deviceInfoService.getUserAuth((User)any);
                result = null;
            }
        };
        deviceCollectingService.queryDeviceCollectingList(queryCondition);

        new Expectations() {
            {
                RequestInfoUtils.getUserId();
                result = "0";
                userFeign.queryCurrentUser(anyString,anyString);
                result = user;
                deviceInfoService.getUserAuth((User)any);
                result = deviceParam;
            }
        };
        deviceCollectingService.queryDeviceCollectingList(queryCondition);
    }

    @Test
    public void queryDeviceCollectingCountByTypeTest() {
        //查询成功
        new Expectations() {
            {
                deviceCollectingDao.attentionCount(anyString);
                result = new ArrayList<DeviceAttentionDto>();
            }
        };
        deviceCollectingService.queryDeviceCollectingCountByType();
    }

    @Test
    public void queryAttentionListTest(){
        new Expectations(RequestInfoUtils.class){
            {
                RequestInfoUtils.getUserId();
                result = "0";
                userFeign.queryCurrentUser(anyString,anyString);
                result = user;
            }
        };

        deviceCollectingService.queryAttentionList();
    }

}
