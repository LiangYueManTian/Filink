package com.fiberhome.filink.menu.service.impl;


import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.menu.bean.MenuRelation;
import com.fiberhome.filink.menu.bean.MenuTemplate;
import com.fiberhome.filink.menu.constant.MenuFunctionCode;
import com.fiberhome.filink.menu.constant.MenuI18nConstant;
import com.fiberhome.filink.menu.constant.MenuResultCodeConstant;
import com.fiberhome.filink.menu.dto.MenuInfoTree;
import com.fiberhome.filink.menu.dto.MenuTemplateAndMenuInfoTree;
import com.fiberhome.filink.menu.exception.FilinkMenuDateBaseException;
import com.fiberhome.filink.menu.exception.FilinkMenuDirtyDataException;
import com.fiberhome.filink.menu.service.MenuInfoService;
import com.fiberhome.filink.menu.service.MenuRelationService;
import com.fiberhome.filink.menu.service.MenuService;
import com.fiberhome.filink.menu.service.MenuTemplateService;
import com.fiberhome.filink.menu.stream.MenuStreams;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemcommons.utils.SystemLanguageUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fiberhome.filink.menu.constant.MenuConstant.*;


/**
 * <p>
 * 菜单服务实现类，控制层调用
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
@Service
public class MenuServiceImpl implements MenuService {
    /**
     * 自动注入menuTemplateService
     */
    @Autowired
    private MenuTemplateService menuTemplateService;
    /**
     * 自动注入menuInfoService
     */
    @Autowired
    private MenuInfoService menuInfoService;
    /**
     * 自动注入menuRelationService
     */
    @Autowired
    private MenuRelationService menuRelationService;
    /**
     * 注入日志
     */
    @Autowired
    private LogProcess logProcess;
    /**
     * 自动注入菜单消息流接口
     */
    @Autowired
    private MenuStreams menuStreams;
    /**
     * 语言工具包
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;
    /**
     * 查询菜单配置模板
     *
     * @param queryCondition 查询条件
     * @return 查询结果
     */
    @Override
    public Result queryListMenuTemplateByPage(QueryCondition<MenuTemplate> queryCondition) {
        return menuTemplateService.queryListMenuTemplateByPage(queryCondition);
    }

    /**
     * 启用菜单配置模板
     *
     * @param menuTemplateId 要启用的id
     * @return 启用结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result openMenuTemplate(String menuTemplateId) {
        return menuTemplateService.openMenuTemplate(menuTemplateId);
    }

    /**
     * 查询默认菜单
     *
     * @return 查询结果
     */
    @Override
    public Result getDefaultMenuTemplate() {
        return menuInfoService.getDefaultMenuTemplate();
    }

    /**
     * 验证菜单配置名称重复
     *
     * @param menuTemplate id  要验证的名称
     * @return 验证结果
     */
    @Override
    public Result queryMenuTemplateNameIsExists(MenuTemplate menuTemplate) {
        boolean b = menuTemplateService.queryMenuTemplateNameIsExists(menuTemplate);
        if (b) {
            return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(MenuI18nConstant.NAME_IS_AVAILABLE));
        }
        return ResultUtils.warn(MenuResultCodeConstant.NAME_IS_EXIST, I18nUtils.getSystemString(MenuI18nConstant.NAME_IS_EXIST));
    }

    /**
     * 新增菜单配置模板
     *
     * @param menuTemplateAndMenuInfoTree 传入的菜单模板及对应的菜单树形结构
     * @return 增加结果
     */
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_ADD, logType = LogConstants.ADD_LOG_LOCAL_FILE, functionCode = MenuFunctionCode.ADD_MENU_TEMPLATE_CODE, dataGetColumnName = "templateName", dataGetColumnId = "menuTemplateId")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result addMenuTemplate(MenuTemplateAndMenuInfoTree menuTemplateAndMenuInfoTree) {
        systemLanguageUtil.querySystemLanguage();
        //创建新增菜单模板
        MenuTemplate menuTemplate = new MenuTemplate();
        boolean b = insertMenuTemplateByMenuTemplateAndMenuInfoTree(menuTemplate, menuTemplateAndMenuInfoTree);
        if (!b) {
            return ResultUtils.warn(MenuResultCodeConstant.NAME_IS_EXIST, I18nUtils.getSystemString(MenuI18nConstant.NAME_IS_EXIST));
        }
        //生成并设置uuid
        String uuid = NineteenUUIDUtils.uuid();
        menuTemplate.setMenuTemplateId(uuid);
        menuTemplateAndMenuInfoTree.setMenuTemplateId(uuid);
        //从传入参数中获取树状结构
        List<MenuInfoTree> menuInfoTreeList = menuTemplateAndMenuInfoTree.getMenuInfoTrees();
        //拆分树状结构、校验必填字段并保存入menuRelations
        if (menuInfoTreeList == null || menuInfoTreeList.size() == 0) {
            return ResultUtils.warn(MenuResultCodeConstant.PARAM_NULL, I18nUtils.getSystemString(MenuI18nConstant.PARAM_NULL));
        }
        List<MenuRelation> menuRelations = splitTree(menuInfoTreeList, uuid);
        if (menuRelations == null) {
            return ResultUtils.warn(MenuResultCodeConstant.PARAM_NULL, I18nUtils.getSystemString(MenuI18nConstant.PARAM_NULL));
        }
        //处理拆分树状结构的保存结果并保存在数据库
        boolean b2 = insertMenuRelation(menuRelations);
        if (!b2) {
            return ResultUtils.warn(MenuResultCodeConstant.DIRTY_DATA, I18nUtils.getSystemString(MenuI18nConstant.DIRTY_DATA));
        }
        //保存菜单模板数据到数据库
        Boolean flag = menuTemplateService.addMenuTemplate(menuTemplate);
        if (!flag) {
            return ResultUtils.warn(MenuResultCodeConstant.DATE_BASE_ERROR, I18nUtils.getSystemString(MenuI18nConstant.DATABASE_ERROR));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(MenuI18nConstant.INCREASE_DATA_SUCCESS));
    }

    /**
     * 查询菜单配置模板详情
     *
     * @param menuTemplateId 查询id
     * @return 查询结果
     */
    @Override
    public Result getMenuTemplateByMenuTemplateId(String menuTemplateId) {
        MenuTemplate menuTemplate = menuTemplateService.selectById(menuTemplateId);
        if (menuTemplate == null || ONE.equals(menuTemplate.getIsDeleted())) {
            return ResultUtils.warn(MenuResultCodeConstant.MENU_TEMPLATE_HAS_BEEN_DELETED, I18nUtils.getSystemString(MenuI18nConstant.MENU_TEMPLATE_HAS_BEEN_DELETED));
        }
        //创建返回实体
        MenuTemplateAndMenuInfoTree menuTemplateAndMenuInfoTree = new MenuTemplateAndMenuInfoTree();
        //拷贝模板数据到实体中
        BeanUtils.copyProperties(menuTemplate, menuTemplateAndMenuInfoTree);
        //查询所有关联表信息
        List<MenuInfoTree> menuInfoList = menuRelationService.selectMenuRelationAndMenuInfo(menuTemplateId);
        //设置实体树属性
        menuTemplateAndMenuInfoTree.setMenuInfoTrees(menuInfoList);
        //返回结果
        return ResultUtils.success(menuTemplateAndMenuInfoTree);
    }

    /**
     * 修改菜单配置模板
     *
     * @param menuTemplateAndMenuInfoTree 修改信息
     * @return 修改结果
     */
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_UPDATE, logType = LogConstants.ADD_LOG_LOCAL_FILE, functionCode = MenuFunctionCode.UPDATE_MENU_TEMPLATE_CODE, dataGetColumnName = "templateName", dataGetColumnId = "menuTemplateId")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result updateMenuTemplate(MenuTemplateAndMenuInfoTree menuTemplateAndMenuInfoTree) {
        systemLanguageUtil.querySystemLanguage();
        String menuTemplateId = menuTemplateAndMenuInfoTree.getMenuTemplateId();
        Integer version = menuTemplateAndMenuInfoTree.getVersion();
        //检验必填字段
        if (menuTemplateAndMenuInfoTree.checkParameter()) {
            return ResultUtils.warn(MenuResultCodeConstant.PARAM_NULL, I18nUtils.getSystemString(MenuI18nConstant.PARAM_NULL));
        }
        //构建查询删除条件map
        Map map = new HashMap(1);
        //将id信息放入map
        map.put(MENU_TEMPLATE_ID, menuTemplateId);
        //根据id查询模板信息
        MenuTemplate menuTemplate = menuTemplateService.selectById(menuTemplateId);
        //判断模板是否存在
        if (menuTemplate == null || ONE.equals(menuTemplate.getIsDeleted())) {
            return ResultUtils.warn(MenuResultCodeConstant.MENU_TEMPLATE_HAS_BEEN_DELETED, I18nUtils.getSystemString(MenuI18nConstant.MENU_TEMPLATE_HAS_BEEN_DELETED));
        }
        //取得数据库版本信息并校验是否与当前版本一致
        Integer dBVersion = menuTemplate.getVersion();
        if (!version.equals(dBVersion)) {
            return ResultUtils.warn(MenuResultCodeConstant.INCONSISTENT_VERSION, I18nUtils.getSystemString(MenuI18nConstant.INCONSISTENT_VERSION));
        }
        //查询当前模板启用状态
        String templateStatus = menuTemplate.getTemplateStatus();
        //查询是否进行禁用模板操作
        String templateStatus2 = menuTemplateAndMenuInfoTree.getTemplateStatus();
        //要操作模板是否是已经启用的模板
        boolean isUsing = ONE.equals(templateStatus);
        if (isUsing) {
            //是否要进行禁用操作
            if (ZERO.equals(templateStatus2)) {
                return ResultUtils.warn(MenuResultCodeConstant.MENU_TEMPLATE_IS_OPEN, I18nUtils.getSystemString(MenuI18nConstant.TEMPLATE_IN_USE));
            }
        }
        //构建模板表修改实体
        MenuTemplate updateMenuTemplate = new MenuTemplate();
        BeanUtils.copyProperties(menuTemplateAndMenuInfoTree, updateMenuTemplate);
        //判断名称是否存在
        boolean mB = menuTemplateService.queryMenuTemplateNameIsExists(updateMenuTemplate);
        if (!mB) {
            return ResultUtils.warn(MenuResultCodeConstant.NAME_IS_EXIST, I18nUtils.getSystemString(MenuI18nConstant.NAME_IS_EXIST));
        }
        //版本号加一
        updateMenuTemplate.setVersion(dBVersion + 1);
        //拆分树状结构、校验必填字段并保存入menuRelations
        List<MenuRelation> menuRelations = splitTree(menuTemplateAndMenuInfoTree.getMenuInfoTrees(), menuTemplateId);
        if (menuRelations == null) {
            return ResultUtils.warn(MenuResultCodeConstant.PARAM_NULL, I18nUtils.getSystemString(MenuI18nConstant.PARAM_NULL));
        }
        //删除关系表
        boolean b = menuRelationService.deleteByMap(map);
        if (b) {
            //新增关系表
            boolean b2 = insertMenuRelation(menuRelations);
            //更新模板表
            if (b2) {
                if (ONE.equals(templateStatus2)) {
                    menuTemplateService.updateAllMenuTemplate();
                }
                boolean b1 = menuTemplateService.updateById(updateMenuTemplate);
                if (b1) {
                    if (isUsing) {
                        WebSocketMessage socketMessage = new WebSocketMessage();
                        socketMessage.setChannelKey("menu");
                        socketMessage.setChannelId("210200");
                        socketMessage.setMsgType(1);
                        socketMessage.setMsg(ResultUtils.success(MenuResultCodeConstant.USED_MENU_TEMPLATE_UPDATE));
                        Message<WebSocketMessage> message = MessageBuilder.withPayload(socketMessage).build();
                        menuStreams.menuWebSocketOutput().send(message);
                    }
                    return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(MenuI18nConstant.UPDATE_SUCCESS));
                }
            }
        }
        return ResultUtils.warn(MenuResultCodeConstant.DIRTY_DATA, I18nUtils.getSystemString(MenuI18nConstant.DIRTY_DATA));
    }

    /**
     * 查询菜单配置模板是否启用的接口
     *
     * @param menuTemplateIds 要查询的id数组
     * @return 查询结果
     */
    @Override
    public Result queryMenuTemplateIsOpen(List<String> menuTemplateIds) {
        boolean b = menuTemplateService.queryMenuTemplateIsOpen(menuTemplateIds);
        return ResultUtils.success(b);
    }

    /**
     * 删除菜单配置模板
     *
     * @param menuTemplateIds 要删除的模板id
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result deleteMenuTemplate(List<String> menuTemplateIds) {
        systemLanguageUtil.querySystemLanguage();
        List<MenuTemplate> dbMenuTemplateList = menuTemplateService.selectBatchIds(menuTemplateIds);
        if (dbMenuTemplateList == null || dbMenuTemplateList.size() != menuTemplateIds.size()) {
            return ResultUtils.warn(MenuResultCodeConstant.DELETED_MENU_TEMPLATE_EXISTS, I18nUtils.getSystemString(MenuI18nConstant.DELETED_MENU_TEMPLATE_EXISTS));
        }
        for (MenuTemplate menuTemplate : dbMenuTemplateList) {
            if (ONE.equals(menuTemplate.getIsDeleted())) {
                return ResultUtils.warn(MenuResultCodeConstant.DELETED_MENU_TEMPLATE_EXISTS, I18nUtils.getSystemString(MenuI18nConstant.DELETED_MENU_TEMPLATE_EXISTS));
            }
        }
        boolean menuTemplateIsOpen = menuTemplateService.queryMenuTemplateIsOpen(menuTemplateIds);
        //是否存在已启用菜单模板
        if (!menuTemplateIsOpen) {
            return ResultUtils.warn(MenuResultCodeConstant.MENU_TEMPLATE_IS_OPEN,I18nUtils.getSystemString(MenuI18nConstant.MENU_TEMPLATE_IS_OPEN));
        }
        //默认结果
        boolean flag = true;
        //遍历模板信息 查询关联信息
        for (MenuTemplate menuTemplate : dbMenuTemplateList) {
            //构建查询map
            Map map = new HashMap<>(1);
            //将逻辑删除字段设置为1
            menuTemplate.setIsDeleted(ONE);
            //id放入查询删除条件map中
            map.put(MENU_TEMPLATE_ID, menuTemplate.getMenuTemplateId());
            //批量删除关系信息
            boolean b = menuRelationService.deleteByMap(map);
            //如果有不成功，返回false
            if (!b) {
                flag = false;
            }
        }
        //批量更新模板信息
        boolean b = menuTemplateService.updateBatchById(dbMenuTemplateList);
        if (flag && b) {
            addLogByMenuTemplates(dbMenuTemplateList);
            return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(MenuI18nConstant.SUCCESSFULLY_DELETED));
            //如果失败 全部回滚
        } else {
            throw new FilinkMenuDateBaseException();
        }
    }

    /**
     * 查询展示的模板
     *
     * @return 查询结果
     */
    @Override
    public MenuTemplateAndMenuInfoTree getShowMenuTemplate() {
        //构建查询map
        Map map = new HashMap(1);
        map.put(TEMPLATE_STATUS, ONE);
        List<MenuTemplate> list = menuTemplateService.selectByMap(map);
        if (list.size() != 1) {
            throw new FilinkMenuDirtyDataException();
        }
        MenuTemplate menuTemplate = list.get(0);
        String menuTemplateId = menuTemplate.getMenuTemplateId();
        //创建返回实体
        MenuTemplateAndMenuInfoTree menuTemplateAndMenuInfoTree = new MenuTemplateAndMenuInfoTree();
        //拷贝模板数据到实体中
        BeanUtils.copyProperties(menuTemplate, menuTemplateAndMenuInfoTree);
        //查询所有关联表信息
        List<MenuInfoTree> menuInfoList = menuRelationService.getShowMenuTemplate(menuTemplateId);
        //设置实体树属性
        menuTemplateAndMenuInfoTree.setMenuInfoTrees(menuInfoList);
        //返回结果
        return menuTemplateAndMenuInfoTree;
    }

    private List<MenuRelation> splitTree(List<MenuInfoTree> menuInfoTreeList, String menuTemplateId) {
        List<MenuRelation> menuRelations = new ArrayList<>();
        if (findChild(menuInfoTreeList, menuTemplateId, menuRelations)) {
            return menuRelations;
        }
        return null;
    }

    /**
     * 拆分树并保存在menuRelations
     *
     * @param menuInfoTreeList 要拆分的树
     * @param menuTemplateId   关系实体中对应的模板表Id
     * @return 校验结果
     */
    private boolean findChild(List<MenuInfoTree> menuInfoTreeList, String menuTemplateId, List<MenuRelation> menuRelations) {
        //遍历集合
        for (MenuInfoTree menuInfoTree : menuInfoTreeList) {
            //创建菜单关系实体
            MenuRelation menuRelation = new MenuRelation();
            //拷贝菜单关系属性
            BeanUtils.copyProperties(menuInfoTree, menuRelation);
            //生成菜单关系id并保存
            menuRelation.setMenuRelationId(NineteenUUIDUtils.uuid());
            //保存关系实体中对应的模板表Id
            menuRelation.setMenuTemplateId(menuTemplateId);
            //非空字段校验
            if (menuRelation.checkParameter()) {
                return false;
            }
            //保存在menuRelations
            menuRelations.add(menuRelation);
            //如果有子数据
            if (menuInfoTree.getChildren() != null && menuInfoTree.getChildren().size() > 0) {
                findChild(menuInfoTree.getChildren(), menuTemplateId, menuRelations);
            }
        }
        return true;
    }


    /**
     * 添加树状菜单关系信息，用于新增、更新操作
     *
     * @return 添加结果
     */
    private boolean insertMenuRelation(List<MenuRelation> menuRelations) {
        //获取树状结构中的所有menuid
        List<String> menuInfoIds = new ArrayList<>();
        for (MenuRelation menuRelation : menuRelations) {
            menuInfoIds.add(menuRelation.getMenuId());
        }
        //添加菜单关系信息到数据库
        if (menuRelationService.addMenuRelations(menuRelations)) {
            return true;
        }
        return false;
    }

    /**
     * 删除菜单模板的日志
     *
     * @param menuTemplateList 日志信息
     */
    private void addLogByMenuTemplates(List<MenuTemplate> menuTemplateList) {
        List<AddLogBean> addLogBeanList = new ArrayList<AddLogBean>();
        //业务数据
        //遍历业务数据
        if (menuTemplateList.size() == 0) {
            return;
        }
        for (MenuTemplate menuTemplate : menuTemplateList) {
            //获取日志类型
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("menuTemplateId");
            addLogBean.setDataName("templateName");
            //获得操作对象名称
            addLogBean.setOptObj(menuTemplate.getTemplateName());
            addLogBean.setFunctionCode(MenuFunctionCode.DELETE_MENU_TEMPLATE_CODE);
            //获得操作对象id
            addLogBean.setOptObjId(menuTemplate.getMenuTemplateId());
            //操作为新增
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            addLogBeanList.add(addLogBean);
        }
        if (0 < addLogBeanList.size()) {
            //新增操作日志
            logProcess.addOperateLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }

    private boolean insertMenuTemplateByMenuTemplateAndMenuInfoTree(MenuTemplate menuTemplate, MenuTemplateAndMenuInfoTree menuTemplateAndMenuInfoTree) {
        //从传入数据中拷贝属性
        BeanUtils.copyProperties(menuTemplateAndMenuInfoTree, menuTemplate);
        //判断名称是否存在
        boolean b = menuTemplateService.queryMenuTemplateNameIsExists(menuTemplate);
        if (!b) {
            return false;
        }
        return true;
    }
}



