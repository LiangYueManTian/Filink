package com.fiberhome.filink.menu.controller;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.menu.bean.MenuI18n;
import com.fiberhome.filink.menu.bean.MenuTemplate;
import com.fiberhome.filink.menu.dto.MenuTemplateAndMenuInfoTree;
import com.fiberhome.filink.menu.exception.FilinkMenuDateFormatException;
import com.fiberhome.filink.menu.service.MenuService;
import com.fiberhome.filink.menu.utils.MenuRusultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServletRequest;
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
     * 锁
     */
    private String lock = "lock";

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
            return ResultUtils.warn(MenuRusultCode.PARAM_NULL, I18nUtils.getString(MenuI18n.PARAM_NULL));
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
            return ResultUtils.warn(MenuRusultCode.MENU_NAME_NULL, I18nUtils.getString(MenuI18n.MENU_NAME_NULL));
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
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String userId = request.getHeader("userId");
        menuTemplateAndMenuInfoTree.setCreateUser(userId);
        String menuTemplateName = menuTemplateAndMenuInfoTree.getTemplateName();
        //参数格式化 当前去空格
        menuTemplateAndMenuInfoTree.parameterFormat();
        if (StringUtils.isEmpty(menuTemplateName)) {
            return ResultUtils.warn(MenuRusultCode.MENU_NAME_NULL, I18nUtils.getString(MenuI18n.MENU_NAME_NULL));
        }
        if(!menuTemplateAndMenuInfoTree.checkParameterFormat()) {
            throw new FilinkMenuDateFormatException();
        }
        synchronized (lock) {
            return menuService.addMenuTemplate(menuTemplateAndMenuInfoTree);
        }
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
            return ResultUtils.warn(MenuRusultCode.PARAM_NULL, I18nUtils.getString(MenuI18n.PARAM_NULL));
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
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String userId = request.getHeader("userId");
        menuTemplateAndMenuInfoTree.setUpdateUser(userId);
        //参数格式化 当前去空格
        menuTemplateAndMenuInfoTree.parameterFormat();
        if (StringUtils.isEmpty(menuTemplateAndMenuInfoTree.getTemplateName())) {
            return ResultUtils.warn(MenuRusultCode.MENU_NAME_NULL, I18nUtils.getString(MenuI18n.MENU_NAME_NULL));
        }
        if(!menuTemplateAndMenuInfoTree.checkParameterFormat()) {
            throw new FilinkMenuDateFormatException();
        }
        return menuService.updateMenuTemplate(menuTemplateAndMenuInfoTree);
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
            return ResultUtils.warn(MenuRusultCode.PARAM_NULL, I18nUtils.getString(MenuI18n.PARAM_NULL));
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
            return ResultUtils.warn(MenuRusultCode.PARAM_NULL, I18nUtils.getString(MenuI18n.SELECT_THE_MENU_TEMPLATE));
        }
        try {
            return menuService.deleteMenuTemplate(menuTemplateIds);
        } catch (Exception e) {
            return ResultUtils.warn(MenuRusultCode.DIRTY_DATA, e.getMessage());
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
