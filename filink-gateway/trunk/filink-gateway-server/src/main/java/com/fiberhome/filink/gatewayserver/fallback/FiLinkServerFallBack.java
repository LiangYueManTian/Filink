package com.fiberhome.filink.gatewayserver.fallback;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.gatewayserver.constant.GatewayI18n;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * filink服务回滚处理
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class FiLinkServerFallBack implements FallbackProvider {

    private final int ERROR_CODE = 9999;

    private final String SERVER_PLACE_HOLDER = "${server}";

    /**
     * 响应处理
     *
     * @param cause 错误信息
     * @return 响应消息
     */
    @Override
    public ClientHttpResponse fallbackResponse(Throwable cause) {
        try {
            //获取请求路径
            RequestContext context = RequestContext.getCurrentContext();
            HttpServletRequest request = context.getRequest();
            String url = request.getRequestURI();
            String serviceId = url.split("/")[1];
            log.info("the request url: {}", url);
            log.info("call failed: {}", cause);
            return getResponse(serviceId);
        } catch (Exception e) {
            return getResponse(null);
        }
    }


    /**
     * 获取ClientHttpResponse
     *
     * @param serviceId serviceId
     * @return ClientHttpResponse
     */
    private ClientHttpResponse getResponse(String serviceId) {

        return new ClientHttpResponse() {

            @Override
            public InputStream getBody() throws IOException {
                String message;
                try {
                    if (StringUtils.isEmpty(serviceId)) {
                        message = I18nUtils.getSystemString(GatewayI18n.URL_ERROR);
                    } else {
                        String systemString = I18nUtils.getSystemString(GatewayI18n.SERVER_ERROR);
                        message = systemString.replace(SERVER_PLACE_HOLDER, serviceId);
                    }
                } catch (Exception e) {
                    message = I18nUtils.getSystemString(GatewayI18n.URL_ERROR);
                }
                Result result = ResultUtils.warn(ERROR_CODE, message);
                return new ByteArrayInputStream(JSONObject.toJSONString(result).getBytes("UTF-8"));
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                return headers;
            }

            @Override
            public HttpStatus getStatusCode() throws IOException {

                return HttpStatus.MULTI_STATUS;
            }

            @Override
            public int getRawStatusCode() throws IOException {

                return HttpStatus.BAD_GATEWAY.value();
            }

            @Override
            public String getStatusText() throws IOException {

                return HttpStatus.BAD_GATEWAY.getReasonPhrase();
            }

            @Override
            public void close() {

            }

        };
    }

    /**
     * 需要回滚的路由
     *
     * @return 路由
     */
    @Override
    public String getRoute() {
        return null;
    }

    /**
     * 响应处理
     *
     * @return 响应信息
     */
    @Override
    public ClientHttpResponse fallbackResponse() {
        return null;
    }
}
