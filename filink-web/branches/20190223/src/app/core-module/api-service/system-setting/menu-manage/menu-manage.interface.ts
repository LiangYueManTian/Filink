export interface MenuManageInterface {
  /**
   * 查询默认菜单
   */
  getDefaultMenuTemplate();

  /**
   * 新增菜单模板
   * param params
   */
  addMenuTemplate(params);

  /**
   * 菜单管理列表查询
   * param params
   */
  queryListMenuTemplateByPage(params);

  /**
   * 启用菜单模板
   * param id
   */
  openMenuTemplate(id);

  /**
   * 删除菜单模板
   * param ids
   */
  deleteMenuTemplate(ids);

  /**
   * 根据id获取模板详情
   * param id
   */
  getMenuTemplateByMenuTemplateId(id);

  /**
   * 模板信息修改
   * param params
   */
  updateMenuTemplate(params);

  /**
   * 获取开启的菜单模板
   */
  getShowMenuTemplate();
}
