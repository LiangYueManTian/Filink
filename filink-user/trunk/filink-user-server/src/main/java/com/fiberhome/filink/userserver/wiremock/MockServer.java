package com.fiberhome.filink.userserver.wiremock;

import com.fiberhome.filink.server_common.utils.WireMockUtils;

import java.io.IOException;


/**
 * wiremock
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/20 10:37
 */
public class MockServer {

    public static void main(String[] args) throws IOException {

        /**
         * 指定请求路径 和 返回json文件名，json文件必须在mock/response下
         * 只需要在下面多次调用即可
         */
        WireMockUtils.mockRequest("/user/testDb", "user");
        WireMockUtils.mockRequest("/user/cong", "user");
    }
}
