package com.fiberhome.filink.filinkoceanconnectserver.utils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 默认主机名校验
 * @author CongcaiYu
 */
public class DefaultHostnameVerifier implements HostnameVerifier {

    /**
     * 校验方法
     * @param hostname 主机名
     * @param session ssl session
     * @return 是否通过
     */
    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
