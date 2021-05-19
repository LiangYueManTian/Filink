package com.fiberhome.filink.workflowbusinessserver.service.impl.procnotice;

import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.AliAccessKey;
import com.fiberhome.filink.smsapi.bean.AliyunPush;
import com.fiberhome.filink.smsapi.bean.Ios;
import com.fiberhome.filink.smsapi.send.aliyun.AliyunMobilePush;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.NoticeDownloadUserReq;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcBaseService;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工单提醒测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/31 16:03
 */
@RunWith(JMockit.class)
public class ProcNoticeServiceTest {

    @Tested
    private ProcNoticeServiceImpl procNoticeService;

    @Injectable
    private ProcBaseService procBaseService;

    @Injectable
    private UserFeign userFeign;

    @Injectable
    private AliyunMobilePush aliyunMobilePush;

    @Injectable
    private ParameterFeign parameterFeign;

    /**
     * 每次发送消息个数
     */
    @Injectable
    private Integer pushSendNumber;

    /**
     * 系统语言环境
     */
    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 获取用户提醒集合
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 16:17
     */
    @Test
    public void getNoticeUserList() {
        NoticeDownloadUserReq noticeDownloadUserReq = new NoticeDownloadUserReq();
        procNoticeService.getNoticeUserList(noticeDownloadUserReq);

        new Expectations() {
            {
                userFeign.queryUserByIdList((List<String>) any);
                List<User> userList = new ArrayList<>();
                User user = new User();
                user.setId("1");
                userList.add(user);
                result = userList;
            }
        };
        noticeDownloadUserReq.setAssign("1");
        procNoticeService.getNoticeUserList(noticeDownloadUserReq);
    }

    /**
     * 获取默认阿里云手机推送
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 16:17
     */
    @Test
    public void getDefaultAliYunMobilePush() {
        new Expectations() {
            {
                parameterFeign.queryMobilePush();
                result = null;
            }
        };
        procNoticeService.getDefaultAliYunMobilePush();


        new Expectations() {
            {
                parameterFeign.queryMobilePush();
                AliAccessKey aliAccessKey = new AliAccessKey();
                aliAccessKey.setAccessKeyId("1");
                result = aliAccessKey;
            }
        };
        procNoticeService.getDefaultAliYunMobilePush();
    }


    /**
     * 获取阿里云推送参数
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 16:41
     */
    @Test
    public void getAliYunPushParam() {
        String ids = "1";
        String title = "title";
        String body = "body";
        Long appKey = 66555L;

        new MockUp<ProcNoticeServiceImpl>(){
            @Mock
            public AliyunPush getDefaultAliYunMobilePush() {
                return null;
            }
        };

        procNoticeService.getAliYunPushParam(ids, title, body, appKey);

        new MockUp<ProcNoticeServiceImpl>(){
            @Mock
            public AliyunPush getDefaultAliYunMobilePush() {
                AliyunPush aliyunPush = new AliyunPush();
                aliyunPush.setIos(new Ios());
                return aliyunPush;
            }
        };

        procNoticeService.getAliYunPushParam(ids, title, body, appKey);
    }


    /**
     * 发送消息
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 16:48
     */
    @Test
    public void noticeInfo() {
        procNoticeService.noticeInfo();
    }


    /**
     * 通知下载人员
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 16:49
     */
    @Test
    public void noticeDownloadUser() {
        NoticeDownloadUserReq noticeDownloadUserReq = new NoticeDownloadUserReq();
        new Expectations() {
            {
                procBaseService.checkProcId(anyString);
                result = ResultUtils.success();
            }
        };
        procNoticeService.noticeDownloadUser(noticeDownloadUserReq);

        Integer maxSendNumber = 1;
        ReflectionTestUtils.setField(procNoticeService, "pushSendNumber", maxSendNumber);

        new Expectations() {
            {
                procBaseService.checkProcId(anyString);
                result = null;
            }
        };


        new MockUp<ProcNoticeServiceImpl>(){
            @Mock
            public List<User> getNoticeUserList(List<String> userIdList) {
                List<User> userList = new ArrayList<>();
                User user = new User();
                user.setId("1");
                user.setPhoneType(0);
                user.setPushId("22");
                user.setAppKey("3000");
                userList.add(user);
                user = new User();
                user.setPushId("33");
                userList.add(user);
                return userList;
            }
        };

        new MockUp<ProcNoticeServiceImpl>(){
            @Mock
            public List<String> getAppDeviceList(Map<String, List<String>> appDeviceMap, User user) {
                List<String> deviceList = new ArrayList<>();
                deviceList.add("3000");
                deviceList.add("4000");
                return deviceList;
            }
        };
        procNoticeService.noticeDownloadUser(noticeDownloadUserReq);
    }

    /**
     * 当发送消息的数量等于设置的发送数量时 获取设置的appMap
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 17:26
     */
    @Test
    public void equalSetNoticeNumberProcess() {
        Map<String, List<List<String>>> appKeyMap = new HashMap<>();
        List<List<String>> listInfo = new ArrayList<>();
        List<String> listInfoOne = new ArrayList<>();
        listInfoOne.add("1");
        listInfoOne.add("2");
        listInfo.add(listInfoOne);
        appKeyMap.put("1", listInfo);
        String appKey = "1";
        List<String> appDeviceList = new ArrayList<>();
        procNoticeService.equalSetNoticeNumberProcess(appKeyMap, appKey, appDeviceList);
    }


    /**
     * 获取app关联设施集合
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 17:31
     */
    @Test
    public void getAppDeviceList() {
        Map<String, List<String>> appDeviceMap = new HashMap<>();
        List<String> deviceIds = new ArrayList<>();
        deviceIds.add("1");
        appDeviceMap.put("1", deviceIds);
        User user = new User();
        user.setAppKey("1");
        procNoticeService.getAppDeviceList(appDeviceMap, user);
    }


    /**
     * 获取提醒用户集合
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 17:37
     */
    @Test
    public void getNoticeUserListInfo() {
        new Expectations() {
            {
                userFeign.queryOnlineUserByIdList((List<String>) any);
                List<User> userList = new ArrayList<>();
                User userInfo = new User();
                userInfo.setUserName("name");
                userList.add(userInfo);
                result = userList;
            }
        };
        procNoticeService.getNoticeUserList(new ArrayList<>());
    }

}
