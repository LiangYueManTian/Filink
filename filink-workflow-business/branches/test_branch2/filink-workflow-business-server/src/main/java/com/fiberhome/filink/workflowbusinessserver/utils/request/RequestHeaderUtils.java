package com.fiberhome.filink.workflowbusinessserver.utils.request;

import com.fiberhome.filink.workflowbusinessserver.constant.RequestHeaderConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hedongwei@wistronits.com
 * description
 * @date 2019/2/27 23:04
 */
@Slf4j
public class RequestHeaderUtils {

    /**
     * 解码头部参数
     * @author hedongwei@wistronits.com
     * @date  2019/2/21 11:44
     * @param paramValue 编码参数
     */
    private static String encodeAsString(String paramValue) {
        try {
            Base64 base64 = new Base64();
            //解码
            paramValue = new String(base64.decode(paramValue),  WorkFlowBusinessConstants.DECODE_BASE64_CHARSET_UTF8);
        } catch (Exception e) {
            log.error("get request body header error, param is " + paramValue, e);
            return paramValue;
        }
        return paramValue;
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
            String paramValue = request.getHeader(paramName);
            if (null == paramValue) {
                return "";
            } else {
                //参数名为userName和roleName时需要解码
                if (RequestHeaderConstants.PARAM_USER_NAME.equals(paramName)
                        || RequestHeaderConstants.PARAM_ROLE_NAME.equals(paramName)) {
                    if (!StringUtils.isEmpty(paramValue)) {
                        paramValue = RequestHeaderUtils.encodeAsString(paramValue);
                    }
                }
                return paramValue;
            }
        }
        return "";
    }
}
