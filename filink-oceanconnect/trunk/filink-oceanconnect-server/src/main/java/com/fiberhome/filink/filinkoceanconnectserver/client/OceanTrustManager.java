package com.fiberhome.filink.filinkoceanconnectserver.client;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * oceanConnect信任管理
 * @author CongcaiYu
 */
public class OceanTrustManager implements X509TrustManager {

    /**
     * 检查客户端信任证书
     * @param x509Certificates 证书
     * @param s 校验
     * @throws CertificateException 异常
     */
    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    /**
     * 检查服务端信任证书
     * @param x509Certificates 证书
     * @param s 校验
     * @throws CertificateException 异常
     */
    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    /**
     * 信任证书
     * @return 证书
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
