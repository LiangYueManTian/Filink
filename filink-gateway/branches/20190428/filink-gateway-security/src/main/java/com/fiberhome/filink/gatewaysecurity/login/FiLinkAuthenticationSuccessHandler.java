package com.fiberhome.filink.gatewaysecurity.login;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.gatewaysecurity.bean.LoginDetailInfoDto;
import com.fiberhome.filink.gatewaysecurity.constant.I18Const;
import com.fiberhome.filink.gatewaysecurity.constant.UserConst;
import com.fiberhome.filink.gatewaysecurity.exception.AuthenticationUserException;
import com.fiberhome.filink.gatewaysecurity.utils.IpUtil;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.menuapi.api.MenuFeign;
import com.fiberhome.filink.menuapi.bean.MenuInfoTree;
import com.fiberhome.filink.menuapi.bean.MenuTemplateAndMenuInfoTree;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Permission;
import com.fiberhome.filink.userapi.bean.Role;
import com.fiberhome.filink.userapi.bean.UserParameter;
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
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


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

    private static final String MENU_SPLIT = "/";

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

        String loginSource = httpServletRequest.getHeader(UserConst.LOGIN_SOURCE);
        String header = httpServletRequest.getHeader(UserConst.AUTHORIZATION);
        // 如果header为空或者不是以basic开头 就不对
        if (header == null || !header.startsWith(UserConst.BASIC)) {
            throw new AuthenticationUserException(I18nUtils.getString(I18Const.NO_CLIENT_INFO));
        }

        String[] tokens = extractAndDecodeHeader(header, httpServletRequest);
        assert tokens.length == 2;

        String clientId = tokens[0];
        String clientSecret = tokens[1];

        ClientDetails clientDetails = detailsService.loadClientByClientId(clientId);

        if (null == clientDetails) {
            throw new AuthenticationUserException(I18nUtils.getString(I18Const.NO_CLIENT_DETAIL_INFO));
        }

        if (!StringUtils.equals(clientDetails.getClientSecret(), clientSecret)) {
            throw new AuthenticationUserException(I18nUtils.getString(I18Const.CLIENT_INFO_SECRET_WRONG));
        }


        String ipAdrress = IpUtil.getIpAdrress(getRequest());
        int port = IpUtil.getPort(getRequest());

        TokenRequest tokenRequest =
                new TokenRequest(MapUtils.EMPTY_MAP, clientId, clientDetails.getScope(), UserConst.PARAMETER_CUSTOM);

        OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);

        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);

        // 生成token
        OAuth2AccessToken accessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
        DefaultOAuth2AccessToken newAccessToken = new DefaultOAuth2AccessToken(accessToken);
        String newToken = UUID.randomUUID().toString();
        newAccessToken.setValue(newToken);

        User loginUser = (User) authentication.getPrincipal();
        String userName = loginUser.getUsername();

        //根据userName调用用户的服务，是否存在不让登陆的信息
        httpServletResponse.setContentType(UserConst.RESPONSE_HEAD_TYPE);
        Result result = checkUserLogin(userName, ipAdrress,loginSource);
        if (result.getCode() != ResultCode.SUCCESS) {

            httpServletResponse.getWriter().write(JSONObject.toJSONString(ResultUtils.success(result)));
        } else {

            //获取到用户的详细信息
            UserParameter userParameter = new UserParameter();
            userParameter.setUserName(userName);
            userParameter.setToken(newAccessToken.toString());
            userParameter.setLoginIp(ipAdrress);
            userParameter.setPort(port);
            userParameter.setLoginSourse(loginSource);
            Object user = userFeign.queryUserByName(userParameter);

            com.fiberhome.filink.userapi.bean.User toUser = JSONArray.
                    toJavaObject((JSON) JSONArray.toJSON(user), com.fiberhome.filink.userapi.bean.User.class);

            loginLog(toUser, LOGIN_MODEL, LogConstants.LOG_TYPE_SECURITY);
            //将在线人数减一

            //获取菜单信息
            MenuTemplateAndMenuInfoTree showMenuTemplate = getMenuTree(toUser.getRole());

            LoginDetailInfoDto loginDetailInfoDto = new LoginDetailInfoDto(newAccessToken, showMenuTemplate, user);

            httpServletResponse.getWriter().write(JSONObject.toJSONString(ResultUtils.success(
                    ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getString(I18Const.LOGIN_SUCCESS), loginDetailInfoDto))));
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

        byte[] base64Token = header.substring(6).getBytes(UserConst.ENCODE_FORMAT);
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(
                    I18nUtils.getString(I18Const.FAILED_DECODE_TOKEN));
        }

        String token = new String(decoded,UserConst.ENCODE_FORMAT);

        int delim = token.indexOf(UserConst.TOKEN_SPLIE);

        if (delim == -1) {
            throw new BadCredentialsException(I18nUtils.getString(I18Const.INVALID_AUTHENTICATION_TOKEN));
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }

    private void loginLog(com.fiberhome.filink.userapi.bean.User user, String model, String logType) {

        //获取日志类型
        //String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(UserConst.LOG_ID_TIP);
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

    public HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
    }

    /**
     * 根据角色获取菜单信息
     *
     * @param role 角色信息
     */
    public MenuTemplateAndMenuInfoTree getMenuTree(Role role) {

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
                //获取所有的菜单数据
                MenuTemplateAndMenuInfoTree showMenuTemplate = menuFeign.getShowMenuTemplate();
                //从菜单中筛选中用户权限中带有的菜单
                List<MenuInfoTree> menuList = showMenuTemplate.getMenuInfoTrees().stream().filter(menuInfoTree -> menuSet.
                        contains(menuInfoTree.getMenuId())).collect(Collectors.toList());
                //将获取到的菜单打成树结构
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
     * 将菜单修改为树结构
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
            System.out.println(menuMap.get(key));
            if (menuMap.get(key).getParentMenuId() != null) {
                MenuInfoTree parentMenuInfo = menuMap.get(menuMap.get(key).getParentMenuId());
                List<MenuInfoTree> childrenMenu = parentMenuInfo.getChildren();
                if (childrenMenu == null) {
                    childrenMenu = new ArrayList<>();
                }

                childrenMenu.add(menuMap.get(key));
                //对菜单按照menu_sort进行排序
                sortFirstMenuList(childrenMenu, 0, childrenMenu.size() - 1);

                parentMenuInfo.setChildren(childrenMenu);
                menuMap.put(menuMap.get(key).getParentMenuId(), parentMenuInfo);
            }
        });

        Collection<MenuInfoTree> values = menuMap.values();
        menuList = new ArrayList<>(values);
        //筛选出级别为1的菜单
        List<MenuInfoTree> firstMenu = menuList.stream().filter(menu -> menu.getMenuLevel().equals(1)).collect(Collectors.toList());
        sortFirstMenuList(firstMenu, 0, firstMenu.size() - 1);
        return firstMenu;
    }

    /**
     * 对菜单列表进行快速排序
     *
     * @param menuInfoTreeList 菜单列表
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
     * 检查用户是否能够登录
     *
     * @param userName  用户名
     * @param ipAdrress 用户ip
     * @return
     */
    private Result checkUserLogin(String userName, String ipAdrress,String loginSource) {

        UserParameter loginUserParameter = new UserParameter();
        loginUserParameter.setUserName(userName);
        loginUserParameter.setLoginIp(ipAdrress);
        loginUserParameter.setLoginSourse(loginSource);
        Integer loginCode = userFeign.validateUserLogin(loginUserParameter);
        switch (loginCode){
            case UserConst.SYSTEM_SERVICE_ERROR:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(I18Const.SYSTEM_SERVICE_ERROR));
            case UserConst.IP_NOT_HAVE_PERMISSION:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(I18Const.IP_NOT_HAVE_PERMISSION));
            case UserConst.USER_SERVICE_ERROR:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(I18Const.USER_SERVICE_ERROR));
            case UserConst.MORE_THAN_MAXUSERNUMBER:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(I18Const.MORE_THAN_MAXUSERNUMBER));
            case UserConst.USER_HAS_FORBIDDEN:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(I18Const.USER_HAS_FORBIDDEN));
            case UserConst.LICENE_HAS_EXPIRATION:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(I18Const.LICENE_HAS_EXPIRATION));
            case UserConst.USER_HAS_LOCKED:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(I18Const.USER_HAS_LOCKED));
            case UserConst.LICENSE_IS_NULL:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(I18Const.LICENSE_IS_NULL));
            case UserConst.MUILT_USER_THAN_MAXUSERNUMBER:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(I18Const.MUILT_USER_THAN_MAXUSERNUMBER));
            case UserConst.USER_EXCEED_VALID_TIME:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(I18Const.USER_EXCEED_VALID_TIME));
            case UserConst.NO_APP_LOGIN_PERMISSION:
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(I18Const.NO_APP_LOGIN_PERMISSION));
            default:
                break;
        }

        return ResultUtils.success();
    }
}
