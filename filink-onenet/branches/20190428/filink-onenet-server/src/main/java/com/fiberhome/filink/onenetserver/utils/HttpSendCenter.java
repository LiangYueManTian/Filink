package com.fiberhome.filink.onenetserver.utils;


import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.onenetserver.bean.device.OneNetResponse;
import com.fiberhome.filink.onenetserver.constant.NBStatus;
import com.fiberhome.filink.onenetserver.exception.OneNetException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *   oneNet平台HTTP请求发送类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Slf4j
public final class HttpSendCenter {
    /**Content-Type*/
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    /**http client*/
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
                                                    .connectTimeout(40, TimeUnit.SECONDS)
                                                    .readTimeout(40, TimeUnit.SECONDS)
                                                    .build();

    /**
     * GET
     * @param accessKey token
     * @param url url
     * @return 返回信息
     */
    public static OneNetResponse get(String accessKey, String url) {
        Request request = new Request.Builder()
                        .header("Authorization", accessKey)
                        .url(url)
                        .build();
        return handleRequest(request);
    }

    /**
     * POST
     * @param accessKey token
     * @param url url
     * @param body 参数
     * @return 返回信息
     */
    public static OneNetResponse post(String accessKey, String url, JSONObject body) {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE, body.toString());
        Request request = new Request.Builder()
                            .url(url)
                            .header("Authorization", accessKey)
                            .post(requestBody)
                            .build();
        return handleRequest(request);
    }
    /**
     * POST
     * @param accessKey token
     * @param url url
     * @return 返回信息
     */
    public static OneNetResponse postNotBody(String accessKey, String url) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE, new JSONObject().toString()))
                .header("Authorization", accessKey)
                .build();
        return handleRequest(request);
    }
    /**
     * DELETE
     * @param accessKey token
     * @param url url
     * @return 返回信息
     */
    public static OneNetResponse delete(String accessKey, String url) {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .header("Authorization", accessKey)
                .build();
        return handleRequest(request);
    }

    /**
     * 发送请求
     * @param request 请求
     * @return 返回信息
     */
    private static OneNetResponse handleRequest(Request request) {
        try {
            Response response = HTTP_CLIENT.newCall(request).execute();
            if (response != null) {
                String st = new String(response.body().bytes(), "utf-8");
                return JSON.parseObject(st, OneNetResponse.class);
            }else {
                throw new OneNetException(NBStatus.HTTP_REQUEST_ERROR);
            }
        } catch (IOException e) {
            log.info("http request error::{}", e.getMessage());
            throw new OneNetException(NBStatus.HTTP_REQUEST_ERROR);
        }
    }
}
