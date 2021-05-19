import { Observable } from 'rxjs';

export interface AlarmInterface {
    /**
     *查询当前告警列表信息
     */
    queryCurrentAlarmList(body): Observable<Object>;

    /**
    *查询单个当前告警的信息
    */
    queryCurrentAlarmInfoById(body): Observable<Object>;

    /**
    *批量修改当前告警备注信息
    */
    updateAlarmRemark(body): Observable<Object>;

    /**
    *批量设置当前告警的告警确认状态
    */
    updateAlarmConfirmStatus(body): Observable<Object>;

    /**
    *批量设置当前告警的告警清除状态
    */
    updateAlarmCleanStatus(body): Observable<Object>;

    /**
   *查询各级别告警总数
   */
    queryEveryAlarmCount(id: number): Observable<Object>;

    /**
    *查询历史告警列表信息
    */
    queryAlarmHistoryList(body): Observable<Object>;

    /**
   *查询单个历史告警的信息
   */
    queryAlarmHistoryInfoById(body): Observable<Object>;

    /**
   *查询告警级别列表信息
   */
    queryAlarmLevelList(body): Observable<Object>;

    /**
   *查询当前告警设置列表信息
   */
    queryAlarmCurrentSetList(body): Observable<Object>;

  /**
   * 查询没有关联工单的告警
   */
    queryAlarmCurrentPage(body): Observable<Object>;

    /**
   *告警级别颜色/声音设置
   */
    updateAlarmColorAndSound(body): Observable<Object>;

    /**
   *告警级别设置
   */
    updateAlarmLevel(body): Observable<Object>;

    /**
     * 新增当前告警设置
     */
    insertAlarmCurrentSet(body): Observable<Object>;

    /**
     * 修改当前告警设置
     */
    updateAlarmCurrentSet(body): Observable<Object>;

    /**
     * 查询单个当前告警设置信息
     */
    queryAlarmLevelSetById(id: string): Observable<Object>;

    /**
     * 删除当前告警设置
     */
    deleteAlarmCurrentSet(body): Observable<Object>;


    /**
    * 查询单个当前告警级别信息
    */
    queryAlarmLevelById(id: string): Observable<Object>;

    /**
    * 查询历史告警设置信息
    */
    queryAlarmDelay(body): Observable<Object>;

    /**
     * 历史告警设置
     */
    updateAlarmDelay(body): Observable<Object>;

    /**
     * 告警提示音选择
     */
    selectAlarmEnum(body): Observable<Object>;

    /**
     * 告警数量websocket服务
     */
    websocket(): Observable<Object>;

    /**
     * 查询告警名称
     */
    queryAlarmName(): Observable<Object>;

    /**
     * 查询告警级别
     */
    queryAlarmLevel(): Observable<Object>;

    /**
     * 查询告警关联部门
     */
    queryDepartmentId(body): Observable<Object>;

  /**
   * 当前告警未查询到责任单位则查询历史告警
   */
  queryDepartmentHistory(body): Observable<Object>;

  queryAlarmFiltration(body): Observable<Object>;
    /**
     *  修改告警状态
     * @param status 状态
     * @param idArray 数据id
     */
    updateStatus(status, idArray): Observable<Object>;

    /**
     *  修改是否库存
     * @param status 状态
     * @param idArray 数据id
     */
    updateAlarmStorage(status, idArray): Observable<Object>;
    /**
     *
     * @param body 新增
     */
    addAlarmFiltration(body): Observable<Object>;

    /**
     *
     *  @param id 编辑情况下 通过id查询数据
     */
    queryAlarmById(id: string[]): Observable<Object>;

    /**
     *  修改告警过滤
     * param body
     */
    updateAlarmFiltration(body): Observable<Object>;

    // 查询告警远程通知列表数据
    queryAlarmRemote(body): Observable<Object>;

    // 告警远程通知 修改启禁状态
    updateRemoteStatus(status, idArray): Observable<Object>;

    // 请求告警转工单 列表数据
    queryAlarmWorkOrder(body): Observable<Object>;

    // 告警转工单 删除
    deleteAlarmWork(body): Observable<Object>;
    // 告警转工单 启禁状态
    updateRemoteStatus(status, idArray): Observable<Object>;
    // 告警转工单 新增
    addAlarmWork(body): Observable<Object>;
    // 告警转工单 编辑
    updateAlarmWork(body): Observable<Object>;

    /**
     * 首页查询当前告警
     */
    queryAlarmDeviceId(id): Observable<Object>;

    /**
     * 首页查询历史告警
     */
    queryAlarmHistoryDeviceId(id): Observable<Object>;

    /**
     * 告警转工单 编辑 通过ID查询数据
     * param
     */
    queryAlarmWorkById(id): Observable<Object>;
    /**
     * 告警远程通知 请求通知人
     * param body
     */
    queryUser(body): Observable<Object>;
    // 告警远程通知 新增
    addAlarmRemote(body): Observable<Object>;
    // 告警远程通知 通过ID查询
    queryAlarmRemoteById(id): Observable<Object>;
    // 当前告警 模板列表查询
    queryAlarmTemplateList(body): Observable<Object>;
    // 告警远程通知 新增页面 根据通知人查询 区域
    areaListById(id: string[]): Observable<Object>;
    // 当前告警 模板列表 删除
    deleteAlarmTemplateList(id: string[]): Observable<Object>;
    // 告警转工单 获取相关区域
    getArea(id: string[]): Observable<Object>;
    // 告警转工单 获取相关设施类型
    getDeviceTypeList(id: string[]): Observable<Object>;
    // 告警远程通知 编辑
    updateAlarmRemarklist(body): Observable<Object>;
    // 当前告警 导出
    exportAlarmList(body): Observable<Object>;
    // 当前告警 查看图片
    examinePicture(alarmId): Observable<Object>;
    // 当前告警 创建工单
    addClearFailureProc(body): Observable<Object>;
    // 当前告警 告警模板 通过ID查询数据
    queryAlarmTemplateById(id: string[]): Observable<Object>;
    // 历史告警 修改备注
    updateHistoryAlarmRemark(body): Observable<Object>;
    // 历史告警 导出
    exportHistoryAlarmList(body): Observable<Object>;
    // 历史告警 查看图片
    examinePictureHistory(alarmId): Observable<Object>;
    // 查询当前告警设置列表
    queryAlarmSetList(body): Observable<Object>;
    // 告警远程通知 通知区域获取单位
    areaGtUnit(body): Observable<Object>;
    // 告警远程通知 新增页面 通过区域 设施类型 查询通知人
    queryUserInfoByDeptAndDeviceType(body): Observable<Object>;
    // 告警远程通知 通过区域获取设施类型
    getDeviceType(body): Observable<Object>;
}


