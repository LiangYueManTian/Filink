package com.fiberhome.filink.gateway_security.login;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.gateway_security.Bean.LoginDetailInfoDto;
import com.fiberhome.filink.gateway_security.exception.AuthenticationUserException;
import com.fiberhome.filink.gateway_security.utils.IpUtil;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.utils.LogConstants;
import com.fiberhome.filink.menuapi.api.MenuFeign;
import com.fiberhome.filink.menuapi.bean.MenuTemplateAndMenuInfoTree;
import com.fiberhome.filink.user_api.api.UserFeign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * 登录成功处理
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/30 4:26 PM
 */
@Slf4j
@Component(value = "fiLinkAuthenticationSuccessHandler")
public class FiLinkAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final String LOGIN_MODEL = "1504101";

    @Autowired
    private ClientDetailsService detailsService;

    @Autowired
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private MenuFeign menuFeign;

    @Autowired
    private LogProcess logProcess;

    private static final String BASIC = "Basic";

    /**
     * @param httpServletRequest
     * @param httpServletResponse
     * @param authentication      封装了登录成功的信息
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException {
        log.info("登录成功");

        String header = httpServletRequest.getHeader("Authorization");
        // 如果header为空或者不是以basic开头 就不对,暂时注释
        if (header == null || !header.startsWith(BASIC)) {
            throw new AuthenticationUserException("请求头中无client信息");
        }

        String[] tokens = extractAndDecodeHeader(header, httpServletRequest);
        assert tokens.length == 2;

        String clientId = tokens[0];
        String clientSecret = tokens[1];

        ClientDetails clientDetails = detailsService.loadClientByClientId(clientId);

        if (null == clientDetails) {
            throw new AuthenticationUserException("client_id对应的配置信息不存在：：：" + clientId);
        } else if (!StringUtils.equals(clientDetails.getClientSecret(), clientSecret)) {
            throw new AuthenticationUserException("client_secret不匹配：：：" + clientId);
        }

        /*
         *4：自定义模式，可以指定password或者授权吗模式
         */
        //获取当前访问设备的ip
        String ipAdrress = IpUtil.getIpAdrress(getRequest());
        //ip中的.调用方法时会去除ip最后一段，将.换成-
        ipAdrress = ipAdrress.replace(".","-");

        TokenRequest tokenRequest =
                new TokenRequest(MapUtils.EMPTY_MAP,clientId,clientDetails.getScope(),"custom");

        OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);

        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);

        // 生成token
        OAuth2AccessToken accessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
        DefaultOAuth2AccessToken newAccessToken = new DefaultOAuth2AccessToken(accessToken);
        String newToken = UUID.randomUUID().toString();
        newAccessToken.setValue(newToken);

        User loginUser = (User) authentication.getPrincipal();
        String userName = loginUser.getUsername();

        //获取到用户的详细信息
        Object user = userFeign.queryUserByNmae(userName, newAccessToken.toString(),ipAdrress);

        com.fiberhome.filink.user_api.bean.User toUser = JSONArray.
                toJavaObject((JSON) JSONArray.toJSON(user), com.fiberhome.filink.user_api.bean.User.class);
        //添加用户登录成功的日志
        loginLog(toUser,LOGIN_MODEL, LogConstants.LOG_TYPE_SECURITY);

        //获取菜单信息
        MenuTemplateAndMenuInfoTree showMenuTemplate = menuFeign.getShowMenuTemplate();

        httpServletResponse.setContentType("application/json;charset=UTF-8");
        LoginDetailInfoDto loginDetailInfoDto = new LoginDetailInfoDto(newAccessToken, showMenuTemplate, user);

        httpServletResponse.getWriter().write(JSONObject.toJSONString(ResultUtils.success(ResultUtils.success(ResultCode.SUCCESS,"登录成功",loginDetailInfoDto))));
    }

    /**
     * Decodes the header into a username and password.
     *
     * @throws BadCredentialsException if the Basic header is not present or is not valid
     *                                 Base64
     */
    private String[] extractAndDecodeHeader(String header, HttpServletRequest request)
            throws IOException {

        byte[] base64Token = header.substring(6).getBytes("UTF-8");
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(
                    "Failed to decode basic authentication  token");
        }

        String token = new String(decoded, "UTF-8");

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }

    private void loginLog(com.fiberhome.filink.user_api.bean.User user, String model, String logType) {

        //获取日志类型
        //String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("id");
        //获得操作对象名称
        addLogBean.setFunctionCode(model);
        //获得操作对象id
        addLogBean.setOptObjId(user.getId());

        addLogBean.setOptObj(user.getUserName());
        addLogBean.setOptUserCode(user.getUserCode());
        addLogBean.setOptUserName(user.getUserName());
        addLogBean.setCreateUser(user.getUserName());
        addLogBean.setOptUserRole(user.getRoleId());
        addLogBean.setOptUserRoleName(user.getRole().getRoleName());
        //操作为新增
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);

        //新增操作日志
        logProcess.addSecurityLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    public HttpServletRequest getRequest(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
    }
}
