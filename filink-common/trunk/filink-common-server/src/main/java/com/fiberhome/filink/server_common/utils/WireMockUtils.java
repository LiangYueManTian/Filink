package com.fiberhome.filink.server_common.utils;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * wireMock 工具类
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/20 11:20
 */
public class WireMockUtils {

    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String FILE_ADRESS = "mock/response/";

    private static final String IP = "10.5.24.144";
    private static final Integer PORT = 9999;

    /**
     * 指定请求路径 和 返回json文件名，json文件必须在mock/response下
     * 只需要在下面多次调用即可
     */
    public static void mockRequest(String url,String fileName) throws IOException {
        configureFor(IP, PORT);

        ClassPathResource pathResource = new ClassPathResource(FILE_ADRESS + fileName + ".json");
        String content = FileUtils.readFileToString(pathResource.getFile());
        stubFor(get(urlPathEqualTo(url))
                .willReturn(aResponse()
                        .withBody(content)
                        .withStatus(200)
                        .withHeader("Content-Type", CONTENT_TYPE)));
    }

    /**
     * 清空所有设置，慎用
     */
    public static void removeAllRequest() {
        WireMock.configureFor("10.5.24.144", PORT);
        removeAllMappings();
    }
}
