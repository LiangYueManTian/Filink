export interface LogManageInterface {
  /**
   * 查询系统日志
   * param params
   */
  findSystemLog(params);

  /**
   * 查询操作日志
   * param params
   */
  findOperateLog(params);

  /**
   * 安全日志
   * param params
   */
  findSecurityLog(params);
}
