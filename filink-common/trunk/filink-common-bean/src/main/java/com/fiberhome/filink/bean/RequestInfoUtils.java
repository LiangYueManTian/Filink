package com.fiberhome.filink.bean;


import org.apache.commons.codec.binary.Base64;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiqizhu@wistronits.com
 * @Date: 2019/3/13 11:29
 */
public class RequestInfoUtils {
    /**
     * 用户id
     */
    private static final String USER_ID = "userId";
    /**
     * 用户名称
     */
    private static final String USER_NAME = "userName";
    /**
     * 角色id
     */
    private static final String ROLE_ID = "roleId";
    /**
     * 角色名称
     */
    private static final String ROLE_NAME = "roleName";
    /**
     * token
     */
    private static final String TOKEN = "Authorization";
    /**
     * language
     */
    private static final String LANGUAGE = "language";
    /**
     * UTF-8
     */
    private static final String DECODE_BASE64_CHARSET_UTF8 = "UTF-8";

    /**
     * 获取用户id
     *
     * @return 用户id
     */
    public static String getUserId() {
        return RequestInfoUtils.getHeadParam(USER_ID);
    }

    /**
     * 获取用户名称
     *
     * @return 用户名称
     */
    public static String getUserName() {
        return RequestInfoUtils.getHeadParam(USER_NAME);
    }

    /**
     * 获取权限id
     *
     * @return 权限id
     */
    public static String getRoleId() {
        return RequestInfoUtils.getHeadParam(ROLE_ID);
    }

    /**
     * 获取权限名称
     *
     * @return 权限名称
     */
    public static String getRoleName() {
        return RequestInfoUtils.getHeadParam(ROLE_NAME);
    }

    /**
     * 获取token
     *
     * @return token
     */
    public static final String getToken() {
        return RequestInfoUtils.getHeadParam(TOKEN);
    }

    /**
     * 获取language
     *
     * @return language
     */
    public static final String getLanguage() {
        return RequestInfoUtils.getHeadParam(LANGUAGE);
    }

    /**
     * 获取请求头属性值
     * @author hedongwei@wistronits.com
     * @date 2019/2/12
     * @param paramName 获取的名称
     * @return paramValue 获取的属性值
     */
    public static String getHeadParam(String paramName) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(servletRequestAttributes)) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            if (!ObjectUtils.isEmpty(request)) {
                String paramValue = request.getHeader(paramName);
                if (null == paramValue) {
                    return "";
                } else {
                    //参数名为userName和roleName时需要解码
                    if (USER_NAME.equals(paramName)
                            || ROLE_NAME.equals(paramName)) {
                        if (!ObjectUtils.isEmpty(paramValue)) {
                            paramValue = encodeAsString(paramValue);
                        }
                    }
                    return paramValue;
                }
            }
        }
        return "";
    }

    /**
     * 解码头部参数
     *
     * @param paramValue 编码参数
     * @author hedongwei@wistronits.com
     * @date 2019/2/21 11:44
     */
    private static String encodeAsString(String paramValue) {
        try {
            Base64 base64 = new Base64();
            //解码
            paramValue = new String(base64.decode(paramValue), DECODE_BASE64_CHARSET_UTF8);
        } catch (Exception e) {
            return paramValue;
        }
        return paramValue;
    }
}
