package com.fiberhome.filink.alarmsetserver.exception;

import com.fiberhome.filink.alarmsetserver.constant.AppConstant;
import com.fiberhome.filink.alarmsetserver.constant.AlarmSetResultCode;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmDelayHandlerTest {

    @Tested
    private AlarmDelayHandler alarmDelayHandler;
    @Test
    public void handlerAlarmCurrentExceptionTest() {
        new Expectations() {
            {
                I18nUtils.getSystemString(AppConstant.ALARM_SETTING_ABNORMAL);
                result = "服务异常";
            }
        };
        Result result = alarmDelayHandler.handlerAlarmCurrentException(new FilinkAlarmDelayException("服务异常"));
        Assert.assertEquals(result.getCode(), (int) AlarmSetResultCode.ALARM_SETTING_ABNORMAL);
    }
}