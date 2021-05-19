package com.fiberhome.filink.logapi.utils;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取ip地址
 * @author hedongwei@wistronits.com
 * @date: 2019/2/12 17:04
 */
public class IpAdrressUtil {

    /**
     * 真实IP
     */
    public static final String REAL_IP = "X-Real-IP";

    /**
     * 转发给
     */
    public static final String X_FORWARD_FOR = "X-Forwarded-For";

    /**
     * 代理客户端ip
     */
    public static final String PROXY_CLIENT_IP = "Proxy-Client-IP";

    /**
     * WL代理客户端ip
     */
    public static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";

    /**
     * HTTP代理客户端ip
     */
    public static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";

    /**
     * HTTPX转发
     */
    public static final String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";

    /**
     * 没有的意思
     */
    public static final String UN_KNOWN = "unKnown";

    /**
     * 没有ip
     */
    public static final String LOCAL_INFO = "0:0:0:0:0:0:0:1";

    /**
     * 本地ip
     */
    public static final String LOCAL_ADDRESS = "127.0.0.1";



    /**
     * 获取Ip地址
     *
     * @param request
     * @return
     */
    public static String getIpAdrress(HttpServletRequest request) {
        String xIp = request.getHeader(IpAdrressUtil.REAL_IP);
        String xFor = request.getHeader(IpAdrressUtil.X_FORWARD_FOR);
        if (StringUtils.isNotEmpty(xFor) && !IpAdrressUtil.UN_KNOWN.equalsIgnoreCase(xFor)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = xFor.indexOf(",");
            if (index != -1) {
                return xFor.substring(0, index);
            } else {
                return xFor;
            }
        }
        xFor = xIp;
        if (StringUtils.isNotEmpty(xFor) && !IpAdrressUtil.UN_KNOWN.equalsIgnoreCase(xFor)) {
            return xFor;
        }
        if (StringUtils.isBlank(xFor) || IpAdrressUtil.UN_KNOWN.equalsIgnoreCase(xFor)) {
            xFor = request.getHeader(IpAdrressUtil.PROXY_CLIENT_IP);
        }
        if (StringUtils.isBlank(xFor) || IpAdrressUtil.UN_KNOWN.equalsIgnoreCase(xFor)) {
            xFor = request.getHeader(IpAdrressUtil.WL_PROXY_CLIENT_IP);
        }
        if (StringUtils.isBlank(xFor) || IpAdrressUtil.UN_KNOWN.equalsIgnoreCase(xFor)) {
            xFor = request.getHeader(IpAdrressUtil.HTTP_CLIENT_IP);
        }
        if (StringUtils.isBlank(xFor) || IpAdrressUtil.UN_KNOWN.equalsIgnoreCase(xFor)) {
            xFor = request.getHeader(IpAdrressUtil.HTTP_X_FORWARDED_FOR);
        }
        if (StringUtils.isBlank(xFor) || IpAdrressUtil.UN_KNOWN.equalsIgnoreCase(xFor)) {
            xFor = request.getRemoteAddr();
        }
        if (IpAdrressUtil.LOCAL_INFO.equals(xFor)) {
            xFor = IpAdrressUtil.LOCAL_ADDRESS;
        }
        return xFor;
    }
}

