package com.fiberhome.filink.menu.controller;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.menu.bean.MenuTemplate;
import com.fiberhome.filink.menu.constant.MenuConstant;
import com.fiberhome.filink.menu.constant.MenuI18nConstant;
import com.fiberhome.filink.menu.constant.MenuResultCodeConstant;
import com.fiberhome.filink.menu.dto.MenuTemplateAndMenuInfoTree;
import com.fiberhome.filink.menu.service.MenuService;
import com.fiberhome.filink.menu.utils.CheckUtil;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * MenuController
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
@RestController
@RequestMapping("/menu")
public class MenuController {
    /**
     * 自动注入menu服务层
     */
    @Autowired
    private MenuService menuService;

    /**
     * 查询菜单配置模板
     *
     * @param queryCondition 查询条件
     * @return 查询结果
     */
    @PostMapping("/queryListMenuTemplateByPage")
    public Result queryListMenuTemplateByPage(@RequestBody QueryCondition<MenuTemplate> queryCondition) {
        return menuService.queryListMenuTemplateByPage(queryCondition);
    }

    /**
     * 启用菜单配置模板
     *
     * @param menuTemplateId
     * @return 启用结果
     */
    @GetMapping("/openMenuTemplate/{menuTemplateId}")
    public Result openMenuTemplate(@PathVariable String menuTemplateId) {
        if (StringUtils.isEmpty(menuTemplateId)) {
            return ResultUtils.warn(MenuResultCodeConstant.PARAM_NULL, I18nUtils.getSystemString(MenuI18nConstant.PARAM_NULL));
        }
        return menuService.openMenuTemplate(menuTemplateId);
    }

    /**
     * 查询默认菜单
     *
     * @return 默认菜单
     */
    @GetMapping("/getDefaultMenuTemplate")
    public Result getDefaultMenuTemplate() {
        return menuService.getDefaultMenuTemplate();
    }

    /**
     * 验证菜单配置名称重复
     *
     * @param menuTemplate id  要验证的名称
     * @return 验证结果
     */
    @PostMapping("/queryMenuTemplateNameIsExists")
    public Result queryMenuTemplateNameIsExists(@RequestBody MenuTemplate menuTemplate) {
        if (StringUtils.isEmpty(menuTemplate.getTemplateName())) {
            return ResultUtils.warn(MenuResultCodeConstant.MENU_NAME_NULL, I18nUtils.getSystemString(MenuI18nConstant.MENU_NAME_NULL));
        }
        return menuService.queryMenuTemplateNameIsExists(menuTemplate);
    }

    /**
     * 新增菜单配置模板
     *
     * @param menuTemplateAndMenuInfoTree 传入的菜单模板及对应的菜单树形结构
     * @return 增加结果
     */
    @PostMapping("/addMenuTemplate")
    public Result addMenuTemplate(@RequestBody MenuTemplateAndMenuInfoTree menuTemplateAndMenuInfoTree) {
        String userId = RequestInfoUtils.getUserId();
        menuTemplateAndMenuInfoTree.setCreateUser(userId);
        //参数格式化 当前去空格
        if (!CheckUtil.cheackParameterAndFormat(menuTemplateAndMenuInfoTree)) {
            return ResultUtils.warn(MenuResultCodeConstant.MENU_NAME_NULL, I18nUtils.getSystemString(MenuI18nConstant.MENU_NAME_NULL));
        }
        //添加菜单模板的时候加锁
        String lockKey = MenuConstant.ADD_MENU_TEMPLATE_LOCK;
        //等待获取锁的时间，单位ms
        int acquireTimeout = 10000;
        //拿到锁的超时时间
        int timeout = 5000;
        //获取时间锁
        String lockIdentifier = RedisUtils.lockWithTimeout(lockKey, acquireTimeout, timeout);
        if (StringUtils.isEmpty(lockIdentifier)) {
            return ResultUtils.warn(MenuResultCodeConstant.BUSY_SERVER, I18nUtils.getSystemString(MenuI18nConstant.BUSY_SERVER));
        }
        Result result = menuService.addMenuTemplate(menuTemplateAndMenuInfoTree);
        //释放锁
        RedisUtils.releaseLock(lockKey, lockIdentifier);
        return result;
    }

    /**
     * 查询菜单配置模板详情
     *
     * @param menuTemplateId 查询id
     * @return 查询结果
     */
    @GetMapping("/getMenuTemplateByMenuTemplateId/{menuTemplateId}")
    public Result getMenuTemplateByMenuTemplateId(@PathVariable String menuTemplateId) {
        if (StringUtils.isEmpty(menuTemplateId)) {
            return ResultUtils.warn(MenuResultCodeConstant.PARAM_NULL, I18nUtils.getSystemString(MenuI18nConstant.PARAM_NULL));
        }
        return menuService.getMenuTemplateByMenuTemplateId(menuTemplateId);
    }

    /**
     * 修改菜单配置模板
     *
     * @param menuTemplateAndMenuInfoTree 传入菜单信息
     * @return 修改结果
     */
    @PutMapping("/updateMenuTemplate")
    public Result updateMenuTemplate(@RequestBody MenuTemplateAndMenuInfoTree menuTemplateAndMenuInfoTree) {
        String userId = RequestInfoUtils.getUserId();
        menuTemplateAndMenuInfoTree.setUpdateUser(userId);
        //参数格式化 当前去空格
        if (!CheckUtil.cheackParameterAndFormat(menuTemplateAndMenuInfoTree)) {
            return ResultUtils.warn(MenuResultCodeConstant.MENU_NAME_NULL, I18nUtils.getSystemString(MenuI18nConstant.MENU_NAME_NULL));
        }
        //更新菜单模板的时候加锁
        String lockKey = MenuConstant.UPDATE_MENU_TEMPLATE_LOCK;
        //等待获取锁的时间，单位ms
        int acquireTimeout = 10000;
        //拿到锁的超时时间
        int timeout = 5000;
        //获取时间锁
        String lockIdentifier = RedisUtils.lockWithTimeout(lockKey, acquireTimeout, timeout);
        if (StringUtils.isEmpty(lockIdentifier)) {
            return ResultUtils.warn(MenuResultCodeConstant.BUSY_SERVER, I18nUtils.getSystemString(MenuI18nConstant.BUSY_SERVER));
        }
        Result result = menuService.updateMenuTemplate(menuTemplateAndMenuInfoTree);
        //释放锁
        RedisUtils.releaseLock(lockKey, lockIdentifier);
        return result;
    }

    /**
     * 查询菜单配置模板是否启用的接口
     *
     * @param menuTemplateIds 要查询的id数组
     * @return 查询结果
     */
    @PostMapping("/queryMenuTemplateIsOpen")
    public Result queryMenuTemplateIsOpen(@RequestBody List<String> menuTemplateIds) {
        if (menuTemplateIds.size() == 0) {
            return ResultUtils.warn(MenuResultCodeConstant.PARAM_NULL, I18nUtils.getSystemString(MenuI18nConstant.PARAM_NULL));
        }
        return menuService.queryMenuTemplateIsOpen(menuTemplateIds);
    }

    /**
     * 删除菜单配置模板
     *
     * @param menuTemplateIds 要删除的模板id
     * @return 删除结果
     */
    @PostMapping("/deleteMenuTemplate")
    public Result deleteMenuTemplate(@RequestBody List<String> menuTemplateIds) {
        if (menuTemplateIds.size() == 0) {
            return ResultUtils.warn(MenuResultCodeConstant.PARAM_NULL, I18nUtils.getSystemString(MenuI18nConstant.SELECT_THE_MENU_TEMPLATE));
        }
        try {
            return menuService.deleteMenuTemplate(menuTemplateIds);
        } catch (Exception e) {
            return ResultUtils.warn(MenuResultCodeConstant.DIRTY_DATA, e.getMessage());
        }
    }

    /**
     * 查找已经启用的模板
     *
     * @return 查找结果
     */
    @PostMapping("/getShowMenuTemplate")
    public MenuTemplateAndMenuInfoTree getShowMenuTemplate() {
        return menuService.getShowMenuTemplate();
    }
}
