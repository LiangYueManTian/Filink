package com.fiberhome.filink.gateway_security;

import com.fiberhome.filink.gateway_security.exception.AuthenticationUserException;
import com.fiberhome.filink.gateway_security.sms.codeGenerator.ValidateCode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.concurrent.TimeUnit;

/**
 * Redis 工具 后续使用common的工具类
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/2 4:53 PM
 */
@Service
public class RedisRepository {

    @Autowired
    private RedisTemplate redisTemplate;

    public void save(ServletWebRequest request, ValidateCode code,String type) {
        // 三十分钟超时时间
        redisTemplate.opsForValue().set(buildKey(request,type),code,30, TimeUnit.MINUTES);
    }

    private Object buildKey(ServletWebRequest request, String type) {
        // 机器码id
        String deviceId = request.getHeader("deviceId");
        if (StringUtils.isBlank(deviceId)) {
            throw new AuthenticationUserException("请求中没有携带机器码");
        }
        return "code::" + type.toString().toLowerCase() + "::" + deviceId;
    }

    public ValidateCode get(ServletWebRequest request, String type) {
        Object object = redisTemplate.opsForValue().get(buildKey(request, type));
        if (object == null) {
            return null;
        }
        return (ValidateCode) object;
    }

    public void remove(ServletWebRequest request, String type) {
        redisTemplate.delete(buildKey(request,type));
    }
}
