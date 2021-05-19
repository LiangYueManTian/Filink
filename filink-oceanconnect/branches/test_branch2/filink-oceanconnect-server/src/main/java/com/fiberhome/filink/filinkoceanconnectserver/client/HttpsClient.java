package com.fiberhome.filink.filinkoceanconnectserver.client;

import com.fiberhome.filink.filinkoceanconnectserver.utils.DefaultHostnameVerifier;
import com.fiberhome.filink.filinkoceanconnectserver.utils.StreamClosedHttpResponse;
import com.fiberhome.filink.filinkoceanconnectserver.utils.StreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * https工具类
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class HttpsClient {
    public final static String HTTPGET = "GET";

    public final static String HTTPPUT = "PUT";

    public final static String HTTPPOST = "POST";

    public final static String HTTPDELETE = "DELETE";

    public final static String HTTPACCEPT = "Accept";

    public final static String CONTENT_LENGTH = "Content-Length";

    public final static String CHARSET_UTF8 = "UTF-8";

    private CloseableHttpClient httpClient;

    private final String CERT_PWD = "IoM@1234";


    /**
     * 双向验证
     *
     * @throws Exception Exception
     */
    public void initSSLConfigForTwoWay() throws Exception {
        // 1 Import your own certificate
        KeyStore selfCert = KeyStore.getInstance("pkcs12");
        ClassPathResource self = new ClassPathResource("outgoing.CertwithKey.pkcs12");
        selfCert.load(self.getInputStream(), CERT_PWD.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("sunx509");
        kmf.init(selfCert, CERT_PWD.toCharArray());

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(kmf.getKeyManagers(), new TrustManager[]{new OceanTrustManager()}, null);

        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sc, new DefaultHostnameVerifier());

        httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
                .build();
    }


    /**
     * 发送POST
     *
     * @param url       String
     * @param headerMap Map<String, String>
     * @param content   String
     * @return HttpResponse
     */
    public HttpResponse doPostJson(String url, Map<String, String> headerMap,
                                   String content) {
        HttpPost request = new HttpPost(url);
        addRequestHeader(request, headerMap);
        request.setEntity(new StringEntity(content,
                ContentType.APPLICATION_JSON));
        return executeHttpRequest(request);
    }


    /**
     * 修改文件
     *
     * @param url       请求地址
     * @param headerMap 头信息
     * @param file      文件信息
     * @return 响应
     */
    public StreamClosedHttpResponse doPostMultipartFile(String url, Map<String, String> headerMap, File file) {
        HttpPost request = new HttpPost(url);
        addRequestHeader(request, headerMap);
        FileBody fileBody = new FileBody(file);
        // Content-Type:multipart/form-data; boundary=----WebKitFormBoundarypJTQXMOZ3dLEzJ4b
        HttpEntity reqEntity = (HttpEntity) MultipartEntityBuilder.create().addPart("file", fileBody).build();
        request.setEntity(reqEntity);
        return (StreamClosedHttpResponse) executeHttpRequest(request);
    }


    /**
     * post
     *
     * @param url       请求地址
     * @param headerMap 头信息
     * @param content   body
     * @return 响应
     */
    public StreamClosedHttpResponse doPostJsonGetStatusLine(String url, Map<String, String> headerMap, String content) {
        HttpPost request = new HttpPost(url);
        addRequestHeader(request, headerMap);
        request.setEntity(new StringEntity(content,
                ContentType.APPLICATION_JSON));
        HttpResponse response = executeHttpRequest(request);
        if (null == response) {
            log.info("The response body is null.");
        }
        return (StreamClosedHttpResponse) response;
    }


    /**
     * get
     *
     * @param url     请求地址
     * @param content body
     * @return 响应
     */
    public StreamClosedHttpResponse doPostJsonGetStatusLine(String url, String content) {
        HttpPost request = new HttpPost(url);
        request.setEntity(new StringEntity(content,
                ContentType.APPLICATION_JSON));
        HttpResponse response = executeHttpRequest(request);
        if (null == response) {
            log.info("The response body is null.");
        }
        return (StreamClosedHttpResponse) response;
    }

    /**
     * 参数转换
     *
     * @param params 参数
     * @return 键值对集合
     */
    private List<NameValuePair> paramsConverter(Map<String, String> params) {
        List<NameValuePair> nvps = new LinkedList<NameValuePair>();
        Set<Map.Entry<String, String>> paramsSet = params.entrySet();
        for (Map.Entry<String, String> paramEntry : paramsSet) {
            nvps.add(new BasicNameValuePair(paramEntry.getKey(), paramEntry
                    .getValue()));
        }
        return nvps;
    }


    /**
     * 表单提交
     *
     * @param url        请求地址
     * @param formParams 表单参数
     * @return 响应
     * @throws Exception 异常
     */
    public StreamClosedHttpResponse doPostFormUrlEncodedGetStatusLine(
            String url, Map<String, String> formParams) throws Exception {
        HttpPost request = new HttpPost(url);

        request.setEntity(new UrlEncodedFormEntity(paramsConverter(formParams)));

        HttpResponse response = executeHttpRequest(request);
        if (null == response) {
            log.info("The response body is null.");
            throw new Exception();
        }
        return (StreamClosedHttpResponse) response;
    }


    /**
     * put请求
     *
     * @param url       请求地址
     * @param headerMap 头信息
     * @param content   body
     * @return 响应
     */
    public HttpResponse doPutJson(String url, Map<String, String> headerMap,
                                  String content) {
        HttpPut request = new HttpPut(url);
        addRequestHeader(request, headerMap);
        request.setEntity(new StringEntity(content,
                ContentType.APPLICATION_JSON));
        return executeHttpRequest(request);
    }

    /**
     * put请求
     *
     * @param url       请求地址
     * @param headerMap 头信息
     * @return 响应
     */
    public HttpResponse doPut(String url, Map<String, String> headerMap) {
        HttpPut request = new HttpPut(url);
        addRequestHeader(request, headerMap);
        return executeHttpRequest(request);
    }

    /**
     * get
     *
     * @param url       请求地址
     * @param headerMap 头信息
     * @param content   body
     * @return 响应
     */
    public StreamClosedHttpResponse doPutJsonGetStatusLine(String url, Map<String, String> headerMap,
                                                           String content) {
        HttpResponse response = doPutJson(url, headerMap, content);
        if (null == response) {
            log.info("The response body is null.");
        }
        return (StreamClosedHttpResponse) response;
    }

    /**
     * get
     *
     * @param url       请求地址
     * @param headerMap 头信息
     * @return 响应
     */
    public StreamClosedHttpResponse doPutGetStatusLine(String url, Map<String, String> headerMap) {
        HttpResponse response = doPut(url, headerMap);
        if (null == response) {
            log.info("The response body is null.");
        }

        return (StreamClosedHttpResponse) response;
    }

    /**
     * profile get请求
     *
     * @param url         请求地址
     * @param queryParams 参数信息
     * @param headerMap   头信息
     * @return 响应
     * @throws Exception 异常
     */
    public HttpResponse doGetWithParas(String url, Map<String, String> queryParams,
                                       Map<String, String> headerMap)
            throws Exception {
        HttpGet request = new HttpGet();
        addRequestHeader(request, headerMap);

        URIBuilder builder;
        try {
            builder = new URIBuilder(url);
        } catch (URISyntaxException e) {
            log.info("URISyntaxException: ", e);
            throw new Exception(e);

        }

        if (queryParams != null && !queryParams.isEmpty()) {
            builder.setParameters(paramsConverter(queryParams));
        }
        request.setURI(builder.build());

        return executeHttpRequest(request);
    }

    /**
     * profile get
     *
     * @param url         请求地址
     * @param queryParams 参数信息
     * @param headerMap   头信息
     * @return 响应
     * @throws Exception 异常
     */
    public StreamClosedHttpResponse doGetWithParasGetStatusLine(String url,
                                                                Map<String, String> queryParams, Map<String, String> headerMap)
            throws Exception {
        HttpResponse response = doGetWithParas(url, queryParams, headerMap);
        if (null == response) {
            log.info("The response body is null.");
        }
        return (StreamClosedHttpResponse) response;
    }

    /**
     * profile delete
     *
     * @param url         请求地址
     * @param queryParams 参数信息
     * @param headerMap   头信息
     * @return 响应
     * @throws Exception 异常
     */
    public HttpResponse doDeleteWithParas(String url, Map<String, String> queryParams,
                                          Map<String, String> headerMap) throws Exception {
        HttpDelete request = new HttpDelete(url);
        addRequestHeader(request, headerMap);
        URIBuilder builder;
        try {
            builder = new URIBuilder(url);
        } catch (URISyntaxException e) {
            log.info("URISyntaxException: {}", e);
            throw new Exception(e);
        }
        if (queryParams != null && !queryParams.isEmpty()) {
            builder.setParameters(paramsConverter(queryParams));
        }
        request.setURI(builder.build());

        return executeHttpRequest(request);
    }

    /**
     * delete请求
     *
     * @param url         请求地址
     * @param queryParams 参数信息
     * @param headerMap   头信息
     * @return 响应
     * @throws Exception 异常
     */
    public StreamClosedHttpResponse doDeleteWithParasGetStatusLine(String url,
                                                                   Map<String, String> queryParams, Map<String, String> headerMap)
            throws Exception {
        HttpResponse response = doDeleteWithParas(url, queryParams, headerMap);
        if (null == response) {
            log.info("The response body is null.");
        }
        return (StreamClosedHttpResponse) response;
    }

    /**
     * 增加请求头
     *
     * @param request   请求
     * @param headerMap 头信息
     */
    private static void addRequestHeader(HttpUriRequest request,
                                         Map<String, String> headerMap) {
        if (headerMap == null) {
            return;
        }
        for (String headerName : headerMap.keySet()) {
            if (CONTENT_LENGTH.equalsIgnoreCase(headerName)) {
                continue;
            }
            String headerValue = headerMap.get(headerName);
            request.addHeader(headerName, headerValue);
        }
    }

    /**
     * 执行http请求
     *
     * @param request 请求参数
     * @return 响应
     */
    private HttpResponse executeHttpRequest(HttpUriRequest request) {
        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("executeHttpRequest failed.");
        } finally {
            try {
                response = new StreamClosedHttpResponse(response);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("executeHttpRequest failed: " + e.getMessage());
            }
        }
        return response;
    }

    /**
     * 获取body
     *
     * @param response 响应
     * @return body
     */
    public String getHttpResponseBody(HttpResponse response) {
        try {
            if (response == null) {
                return null;
            }
            String body = null;
            if (response instanceof StreamClosedHttpResponse) {
                body = ((StreamClosedHttpResponse) response).getContent();
            } else {
                HttpEntity entity = response.getEntity();
                if (entity != null && entity.isStreaming()) {
                    String encoding = entity.getContentEncoding() != null ? entity
                            .getContentEncoding().getValue() : null;
                    body = StreamUtil.inputStream2String(entity.getContent(),
                            encoding);
                }
            }
            return body;
        } catch (Exception e) {
            log.error("get http response body failed>>>>>>>>");
        }
        return null;
    }

}
