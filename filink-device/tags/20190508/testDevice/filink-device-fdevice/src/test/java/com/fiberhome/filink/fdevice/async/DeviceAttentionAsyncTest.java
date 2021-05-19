package com.fiberhome.filink.fdevice.async;

import com.fiberhome.filink.fdevice.dto.DeviceAttentionDto;
import com.fiberhome.filink.fdevice.stream.DeviceStreams;
import com.fiberhome.filink.userapi.api.UserFeign;
import mockit.Expectations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.messaging.support.MessageBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * DeviceAttentionAsyncTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/9
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceAttentionAsyncTest {
    @InjectMocks
    private DeviceAttentionAsync deviceAttentionAsync;

    @Mock
    private UserFeign userFeign;
    @Mock
    private DeviceStreams deviceStreams;

    @Test
    public void sendAttentionInfo() {
        new Expectations(MessageBuilder.class) {
            {
                MessageBuilder.withPayload(any);
                result = null;
            }
        };
        List<String> tokenList = new ArrayList<>();
        tokenList.add("c");
        DeviceAttentionDto deviceAttentionDto = new DeviceAttentionDto();
        deviceAttentionDto.setUserId("z");
        when(userFeign.queryTokenByUserId(anyString())).thenReturn(tokenList);
        try {
            deviceAttentionAsync.sendAttentionInfo("0", deviceAttentionDto);
        } catch (Exception e) {
        }
        try {
            deviceAttentionAsync.sendAttentionInfo("1", deviceAttentionDto);
        } catch (Exception e) {
        }

    }
}