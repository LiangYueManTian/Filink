export interface SystemParameterInterface {


  /**
   * 更新设置
   */
  updateSystem(type, body);

  /**
   * 获取系统语言
   */
  queryLanguage();

  /**
   * 查询系统初始化配置
   */
  selectDisplaySettingsParamForPageCollection();

  /**
   * email测试
   */
  testEmail(body);

  /**
   * phone测试
   */
  testPhone(body);

  /**
   * phone测试
   */
  ftpSettingsTest(body)

}
