package com.fiberhome.filink.bean;


import org.apache.commons.codec.binary.Base64;
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
     * UTF-8
     */
    private static final String DECODE_BASE64_CHARSET_UTF8 = "UTF-8";

    /**
     * 获取用户id
     *
     * @return 用户id
     */
    public static String getUserId() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String userId = request.getHeader(USER_ID);
        return userId;
    }

    /**
     * 获取用户名称
     *
     * @return 用户名称
     */
    public static String getUserName() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String userName = encodeAsString(request.getHeader(USER_NAME));
        return userName;
    }

    /**
     * 获取权限id
     *
     * @return 权限id
     */
    public static String getRoleId() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String roleId = request.getHeader(ROLE_ID);
        return roleId;
    }

    /**
     * 获取权限名称
     *
     * @return 权限名称
     */
    public static String getRoleName() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String roleName = encodeAsString(request.getHeader(ROLE_NAME));
        return roleName;
    }

    /**
     * 获取token
     *
     * @return token
     */
    public static final String getToken() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String token = request.getHeader(TOKEN);
        return token;
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
