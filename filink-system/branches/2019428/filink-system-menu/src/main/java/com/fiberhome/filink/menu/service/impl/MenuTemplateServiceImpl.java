package com.fiberhome.filink.menu.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.menu.bean.MenuI18n;
import com.fiberhome.filink.menu.bean.MenuTemplate;
import com.fiberhome.filink.menu.dao.MenuTemplateDao;
import com.fiberhome.filink.menu.service.MenuTemplateService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.menu.utils.CheckUtil;
import com.fiberhome.filink.menu.utils.MenuRusultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fiberhome.filink.server_common.utils.MpQueryHelper.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-12
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MenuTemplateServiceImpl extends ServiceImpl<MenuTemplateDao, MenuTemplate> implements MenuTemplateService {
    /**
     * 自动注入menuTemplateDao
     */
    @Autowired
    private MenuTemplateDao menuTemplateDao;
    /**
     * 开启状态
     */
    private static final String ONE = "1";
    /**
     * 0
     */
    private static final String ZERO = "0";
    /**
     * 模板名称
     */
    private static final String TEMPLATE_NAME = "template_name";
    /**
     * 是否删除
     */
    private static final String IS_DELETED = "is_deleted";

    /**
     * 查询菜单配置模板
     *
     * @param queryCondition 查询条件
     * @return 查询结果
     */
    @Override
    public Result queryListMenuTemplateByPage(QueryCondition<MenuTemplate> queryCondition) {
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("is_deleted");
        filterCondition.setOperator("neq");
        filterCondition.setFilterValue(1);
        if (queryCondition.getFilterConditions() == null) {
            return ResultUtils.warn(MenuRusultCode.PARAM_NULL, I18nUtils.getString(MenuI18n.PARAM_NULL));
        }
        queryCondition.getFilterConditions().add(filterCondition);
        // 构造分页条件
        if (queryCondition.getPageCondition() == null || CheckUtil.checkPageConditionNull(queryCondition.getPageCondition())) {
            return ResultUtils.warn(MenuRusultCode.PARAM_NULL, I18nUtils.getString(MenuI18n.PARAM_NULL));
        }
        // 无排序时的默认排序（当前按照创建时间降序）
        if (queryCondition.getSortCondition() == null || StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())) {
            SortCondition sortCondition = new SortCondition();
            sortCondition.setSortField("create_time");
            sortCondition.setSortRule("desc");
            queryCondition.setSortCondition(sortCondition);
        }
        Page page = myBatiesBuildPage(queryCondition);
        // 构造过滤、排序等条件
        EntityWrapper wrapper = myBatiesBuildQuery(queryCondition);
        List<MenuTemplate> menuTemplateList = menuTemplateDao.selectPage(page, wrapper);
        Integer count = menuTemplateDao.selectCount(wrapper);
        PageBean pageBean = myBatiesBuildPageBean(page, count, menuTemplateList);
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * 启用菜单配置模板
     *
     * @param menuTemplateId 要启用的id
     * @return 启用结果
     */
    @Override
    public Result openMenuTemplate(String menuTemplateId) {
        //构建该模板查询条件
        MenuTemplate menuTemplate = new MenuTemplate();
        menuTemplate.setMenuTemplateId(menuTemplateId);
        //查询要启用的菜单模板是否存在
        MenuTemplate menuTemplate2 = menuTemplateDao.selectOne(menuTemplate);
        if (menuTemplate2 == null) {
            return ResultUtils.warn(MenuRusultCode.DIRTY_DATA, I18nUtils.getString(MenuI18n.DIRTY_DATA));
        }
        //将所有模板禁用
        menuTemplateDao.updateAllMenuTemplate();
        //启用该模板
        menuTemplate.setTemplateStatus(ONE);
        Integer integer = menuTemplateDao.updateById(menuTemplate);
        if (integer == 1) {
            return ResultUtils.success(true);
        }
        return ResultUtils.warn(MenuRusultCode.DATE_BASE_ERROR, I18nUtils.getString(MenuI18n.DATABASE_ERROR));
    }

    /**
     * 验证菜单配置名称重复
     *
     * @param menuTemplate id  要验证的名称
     * @return 验证结果
     */
    @Override
    public boolean queryMenuTemplateNameIsExists(MenuTemplate menuTemplate) {
        //获取传入的id
        String menuTemplateId = menuTemplate.getMenuTemplateId();
        //获取传入的名称
        String templateName = menuTemplate.getTemplateName();
        //构建新的查询实体，并将名称放进去
        Map map = new HashMap(1);
        map.put(TEMPLATE_NAME, templateName);
        map.put(IS_DELETED, ZERO);
        //查询结果
        List<MenuTemplate> list = menuTemplateDao.selectByMap(map);
        //如果查询结果不存在或者查询的结果id与传入id一致，则名称可用
        if (list == null || list.size() == 0) {
            return true;
        }
        for (MenuTemplate menuTemplate1 : list) {
            if (menuTemplate1.getMenuTemplateId().equals(menuTemplateId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 新增菜单模板
     *
     * @param menuTemplate 新增实体
     * @return 新增结果
     */
    @Override
    public boolean addMenuTemplate(MenuTemplate menuTemplate) {
        if (ONE.equals(menuTemplate.getTemplateStatus())) {
            menuTemplateDao.updateAllMenuTemplate();
        }
        Integer result = menuTemplateDao.insert(menuTemplate);
        return result == 1;
    }

    /**
     * 查询菜单配置模板是否启用的接口
     *
     * @param menuTemplateIds 要查询的id数组
     * @return 查询结果
     */
    @Override
    public boolean queryMenuTemplateIsOpen(List<String> menuTemplateIds) {
        List<MenuTemplate> menuTemplates = menuTemplateDao.selectBatchIds(menuTemplateIds);
        if (menuTemplates.size() != menuTemplateIds.size()) {
            return false;
        }
        for (MenuTemplate menuTemplate : menuTemplates) {
            if (ONE.equals(menuTemplate.getTemplateStatus())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将所有模板设置为禁用
     */
    @Override
    public Integer updateAllMenuTemplate() {
        return menuTemplateDao.updateAllMenuTemplate();
    }
}
