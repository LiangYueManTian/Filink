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
    queryEveryAlarmCount(): Observable<Object>;

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
     * 查询告警过滤
     */
    queryAlarmFiltration(): Observable<Object>;

    /**
     * 首页查询当前告警
     */
    queryAlarmDeviceId(id): Observable<Object>;
}


