package com.fiberhome.filink.filinkoceanconnectserver.utils;

import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.redis.RedisUtils;
import mockit.Expectations;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * ProfileUtil测试类
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class ProfileUtilTest {

    @Tested
    private ProfileUtil profileUtil;

    @Test
    public void getProfileConfig() {
        new Expectations(RedisUtils.class){
            {
                RedisUtils.get(RedisKey.PROFILE_KEY);
                result = null;
            }
            {
                RedisUtils.set(RedisKey.PROFILE_KEY, any);
            }
        };
        profileUtil.getProfileConfig();
    }

    @Test
    public void initProfileConfig() {
        new Expectations(RedisUtils.class){
            {
                RedisUtils.set(RedisKey.PROFILE_KEY, any);
            }
        };
        profileUtil.initProfileConfig();
    }
}