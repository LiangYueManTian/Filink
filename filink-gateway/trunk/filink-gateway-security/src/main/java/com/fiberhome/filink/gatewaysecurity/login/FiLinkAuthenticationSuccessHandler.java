package com.fiberhome.filink.gatewaysecurity.login;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.gatewaysecurity.async.LoginAsync;
import com.fiberhome.filink.gatewaysecurity.bean.LoginDetailInfoDto;
import com.fiberhome.filink.gatewaysecurity.constant.I18Const;
import com.fiberhome.filink.gatewaysecurity.constant.LoginConstant;
import com.fiberhome.filink.gatewaysecurity.constant.LoginResultCode;
import com.fiberhome.filink.gatewaysecurity.utils.IpUtil;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.*;
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
import java.util.*;
import java.util.stream.Collectors;


/**
 * ??????????????????
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/30 4:26 PM
 */
@Slf4j
@Component(value = "fiLinkAuthenticationSuccessHandler")
public class FiLinkAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final String LOGIN_MODEL = "1504101";

    private static final String MENU_SPLIT = "/";

    @Autowired
    private ClientDetailsService detailsService;

    @Autowired
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private LogProcess logProcess;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    @Autowired
    private LoginAsync loginAsync;

    /**
     * @param httpServletRequest
     * @param httpServletResponse
     * @param authentication      ??????????????????????????????
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException {
        log.info("User login???Verify request header information");
        String loginSource = httpServletRequest.getHeader(LoginConstant.LOGIN_SOURCE);
        String header = httpServletRequest.getHeader(LoginConstant.AUTHORIZATION);
        // ??????header?????????????????????basic?????? ?????????
        if (header == null || !header.startsWith(LoginConstant.BASIC)) {
            log.info("User login???The client information is invalid");
            httpServletResponse.setHeader("Content-Type", "application/json;charset=utf-8");
            httpServletResponse.getWriter().write(JSONObject.toJSONString(ResultUtils.success(
                    ResultUtils.warn(LoginResultCode.NO_CLIENT_INFO, I18nUtils.getSystemString(I18Const.NO_CLIENT_INFO)))));
            return;
        }

        String[] tokens = extractAndDecodeHeader(header, httpServletRequest);
        assert tokens.length == 2;

        String clientId = tokens[0];
        String clientSecret = tokens[1];

        ClientDetails clientDetails = detailsService.loadClientByClientId(clientId);

        if (null == clientDetails) {
            log.info("User login???The client information is invalid");
            httpServletResponse.setHeader("Content-Type", "application/json;charset=utf-8");
            httpServletResponse.getWriter().write(JSONObject.toJSONString(ResultUtils.success(
                    ResultUtils.warn(LoginResultCode.NO_CLIENT_DETAIL_INFO, I18nUtils.getSystemString(I18Const.NO_CLIENT_DETAIL_INFO)))));
            return;
        }

        if (!StringUtils.equals(clientDetails.getClientSecret(), clientSecret)) {
            log.info("User login???The client information is invalid");
            httpServletResponse.setHeader("Content-Type", "application/json;charset=utf-8");
            httpServletResponse.getWriter().write(JSONObject.toJSONString(ResultUtils.success(
                    ResultUtils.warn(LoginResultCode.CLIENT_INFO_SECRET_WRONG, I18nUtils.getSystemString(I18Const.CLIENT_INFO_SECRET_WRONG)))));
            return;
        }


        String ipAdrress = IpUtil.getIpAdrress(getRequest());
        int port = IpUtil.getPort(getRequest());

        TokenRequest tokenRequest =
                new TokenRequest(MapUtils.EMPTY_MAP, clientId, clientDetails.getScope(), LoginConstant.PARAMETER_CUSTOM);

        OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);

        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);

        // ??????token
        OAuth2AccessToken accessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
        DefaultOAuth2AccessToken newAccessToken = new DefaultOAuth2AccessToken(accessToken);
        String newToken = UUID.randomUUID().toString();
        newAccessToken.setValue(newToken);

        User loginUser = (User) authentication.getPrincipal();
        String userName = loginUser.getUsername();

        //??????userName?????????????????????????????????????????????????????????
        httpServletResponse.setContentType(LoginConstant.RESPONSE_HEAD_TYPE);
        UserParameter loginUserParameter = new UserParameter();
        loginUserParameter.setUserName(userName);
        loginUserParameter.setLoginIp(ipAdrress);
        loginUserParameter.setLoginSourse(loginSource);
        UserLoginInfoParam userLoginInfoParam = new UserLoginInfoParam();
        userLoginInfoParam.setLoginUserParameter(loginUserParameter);
        UserParameter userParameter = new UserParameter();
        userParameter.setUserName(userName);
        userParameter.setToken(newAccessToken.toString());
        userParameter.setLoginIp(ipAdrress);
        userParameter.setPort(port);
        userParameter.setLoginSourse(loginSource);
        userLoginInfoParam.setUserParameter(userParameter);
        log.info("User {} login???Call user service to get login related information", userName);
        LoginInfoBean loginInfoBean = userFeign.queryUserLoginInfo(userLoginInfoParam);
        Result result = checkUserLogin(loginInfoBean.getLoginCode());
        if (result.getCode() != ResultCode.SUCCESS) {
            log.info("User {} login???User service check can not login", userName);
            httpServletResponse.getWriter().write(JSONObject.toJSONString(ResultUtils.success(result)));
        } else {
            log.info("User {} login???User service inspection can be login in", userName);
            //??????????????????????????????
            Object user = loginInfoBean.getUser();

            com.fiberhome.filink.userapi.bean.User toUser = JSONArray.
                    toJavaObject((JSON) JSONArray.toJSON(user), com.fiberhome.filink.userapi.bean.User.class);

            loginAsync.loginLog(toUser, LOGIN_MODEL, LogConstants.LOG_TYPE_SECURITY);
            //?????????????????????

            //??????????????????
            MenuTemplateAndMenuInfoTree showMenuTemplate = getMenuTree(toUser.getRole(), loginInfoBean.getShowMenuTemplate());

            LoginDetailInfoDto loginDetailInfoDto = new LoginDetailInfoDto(newAccessToken, showMenuTemplate, user);

            httpServletResponse.getWriter().write(JSONObject.toJSONString(ResultUtils.success(
                    ResultUtils.success(ResultCode.SUCCESS,
                            I18nUtils.getSystemString(I18Const.LOGIN_SUCCESS), loginDetailInfoDto))));
        }
    }

    /**
     * Decodes the header into a username and password.
     *
     * @throws BadCredentialsException if the Basic header is not present or is not valid
     *                                 Base64
     */
    private String[] extractAndDecodeHeader(String header, HttpServletRequest request)
            throws IOException {

        byte[] base64Token = header.substring(6).getBytes(LoginConstant.ENCODE_FORMAT);
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(
                    I18nUtils.getSystemString(I18Const.FAILED_DECODE_TOKEN));
        }

        String token = new String(decoded, LoginConstant.ENCODE_FORMAT);

        int delim = token.indexOf(LoginConstant.TOKEN_SPLIE);

        if (delim == -1) {
            throw new BadCredentialsException(I18nUtils.getSystemString(I18Const.INVALID_AUTHENTICATION_TOKEN));
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }

    private void loginLog(com.fiberhome.filink.userapi.bean.User user, String model, String logType) {

        systemLanguageUtil.querySystemLanguage();
        //??????????????????
        //String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(LoginConstant.LOG_ID_TIP);
        //????????????????????????
        addLogBean.setFunctionCode(model);
        //??????????????????id
        addLogBean.setOptObjId(user.getId());

        addLogBean.setOptObj(user.getUserName());
        addLogBean.setOptUserCode(user.getUserCode());
        addLogBean.setOptUserName(user.getUserName());
        addLogBean.setCreateUser(user.getUserName());
        addLogBean.setOptUserRole(user.getRoleId());
        addLogBean.setOptUserRoleName(user.getRole().getRoleName());
        //???????????????
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);

        //??????????????????
        logProcess.addSecurityLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    public HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
    }

    /**
     * ??????????????????????????????
     *
     * @param role ????????????
     */
    public MenuTemplateAndMenuInfoTree getMenuTree(Role role, MenuTemplateAndMenuInfoTree showMenuTemplate) {

        if (role != null) {
            List<Permission> permissionList = role.getPermissionList();
            Set<String> menuSet = new HashSet<>();
            if (permissionList != null) {
                permissionList.forEach(permission -> {
                    if (StringUtils.isNotBlank(permission.getMenuId())) {
                        String[] split = permission.getMenuId().split(MENU_SPLIT);
                        menuSet.addAll(Arrays.asList(split));
                    }
                });
                //???????????????????????????????????????????????????
                List<MenuInfoTree> menuList = showMenuTemplate.getMenuInfoTrees().stream().filter(menuInfoTree -> menuSet.
                        contains(menuInfoTree.getMenuId())).collect(Collectors.toList());
                //????????????????????????????????????
                if (menuList != null) {

                    List<MenuInfoTree> treeMenu = getTreeMenu(menuList);
                    showMenuTemplate.setMenuInfoTrees(treeMenu);
                    return showMenuTemplate;
                }
            }
        }
        return null;
    }

    /**
     * ???????????????????????????
     *
     * @param menuList
     */
    public List<MenuInfoTree> getTreeMenu(List<MenuInfoTree> menuList) {

        Map<String, MenuInfoTree> menuMap = new HashMap<>();
        menuList.forEach(menu -> {
            menuMap.put(menu.getMenuId(), menu);
        });

        Set<String> keyS = menuMap.keySet();
        keyS.forEach(key -> {
            if (menuMap.get(key).getParentMenuId() != null) {
                MenuInfoTree parentMenuInfo = menuMap.get(menuMap.get(key).getParentMenuId());
                if (parentMenuInfo != null) {
                    List<MenuInfoTree> childrenMenu = parentMenuInfo.getChildren();
                    if (childrenMenu == null) {
                        childrenMenu = new ArrayList<>();
                    }

                    childrenMenu.add(menuMap.get(key));
                    //???????????????menu_sort????????????
                    sortFirstMenuList(childrenMenu, 0, childrenMenu.size() - 1);

                    parentMenuInfo.setChildren(childrenMenu);
                    menuMap.put(menuMap.get(key).getParentMenuId(), parentMenuInfo);
                }
            }
        });

        Collection<MenuInfoTree> values = menuMap.values();
        menuList = new ArrayList<>(values);
        //??????????????????1?????????
        List<MenuInfoTree> firstMenu = menuList.stream().filter(menu -> menu.getMenuLevel().equals(1)).collect(Collectors.toList());
        sortFirstMenuList(firstMenu, 0, firstMenu.size() - 1);
        return firstMenu;
    }

    /**
     * ?????????????????????????????????
     *
     * @param menuInfoTreeList ????????????
     * @return
     */
    public static void sortFirstMenuList(List<MenuInfoTree> menuInfoTreeList, int start, int end) {
        if (start >= end) {
            return;
        }
        int i = start;
        int j = end;
        int base = menuInfoTreeList.get(start).getMenuSort();
        MenuInfoTree baseMenu = menuInfoTreeList.get(start);
        while (i != j) {
            while (menuInfoTreeList.get(j).getMenuSort() >= base && j > i) {
                j--;
            }
            while (menuInfoTreeList.get(i).getMenuSort() <= base && i < j) {
                i++;
            }
            if (i < j) {
                MenuInfoTree temp = menuInfoTreeList.get(i);
                menuInfoTreeList.set(i, menuInfoTreeList.get(j));
                menuInfoTreeList.set(j, temp);
            }
        }
        menuInfoTreeList.set(start, menuInfoTreeList.get(i));
        menuInfoTreeList.set(i, baseMenu);

        sortFirstMenuList(menuInfoTreeList, start, i - 1);
        sortFirstMenuList(menuInfoTreeList, i + 1, end);
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    private Result checkUserLogin(Integer loginCode) {
        switch (loginCode) {
            case LoginResultCode.SYSTEM_SERVICE_ERROR:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(I18Const.SYSTEM_SERVICE_ERROR));
            case LoginResultCode.IP_NOT_HAVE_PERMISSION:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(I18Const.IP_NOT_HAVE_PERMISSION));
            case LoginResultCode.USER_SERVICE_ERROR:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(I18Const.USER_SERVICE_ERROR));
            case LoginResultCode.MORE_THAN_MAXUSERNUMBER:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(I18Const.MORE_THAN_MAXUSERNUMBER));
            case LoginResultCode.USER_HAS_FORBIDDEN:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(I18Const.USER_HAS_FORBIDDEN));
            case LoginResultCode.LICENE_HAS_EXPIRATION:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(I18Const.LICENE_HAS_EXPIRATION));
            case LoginResultCode.LICENSE_NOT_STARTED:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(I18Const.LICENSE_NOT_STARTED));
            case LoginResultCode.USER_HAS_LOCKED:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(I18Const.USER_HAS_LOCKED));
            case LoginResultCode.LICENSE_IS_NULL:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(I18Const.LICENSE_IS_NULL));
            case LoginResultCode.MUILT_USER_THAN_MAXUSERNUMBER:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(I18Const.MUILT_USER_THAN_MAXUSERNUMBER));
            case LoginResultCode.USER_EXCEED_VALID_TIME:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(I18Const.USER_EXCEED_VALID_TIME));
            case LoginResultCode.NO_APP_LOGIN_PERMISSION:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(I18Const.NO_APP_LOGIN_PERMISSION));
            default:
                break;
        }

        return ResultUtils.success();
    }
}
