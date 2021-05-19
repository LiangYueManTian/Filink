import {Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {QueryCondition, SortCondition} from '../../../../../shared-module/entity/queryCondition';
import {Result} from '../../../../../shared-module/entity/result';
import {TableConfig} from '../../../../../shared-module/entity/tableConfig';
import {PageBean} from '../../../../../shared-module/entity/pageBean';
import {AlarmLanguageInterface} from '../../../../../../assets/i18n/alarm/alarm-language.interface';
import {DateHelperService, NzI18nService, NzModalService} from 'ng-zorro-antd';
import {AlarmService} from '../../../../../core-module/api-service/alarm';
import {FiLinkModalService} from '../../../../../shared-module/service/filink-modal/filink-modal.service';
import {AlarmLevelCode, alarmNameColorEnum, getAlarmCleanStatus, getAlarmLevel, getAlarmType} from '../../../facility.config';
import {AlarmStoreService} from '../../../../../core-module/store/alarm.store.service';
import {FacilityLanguageInterface} from '../../../../../../assets/i18n/facility/facility.language.interface';
import {FormItem} from '../../../../../shared-module/component/form/form-config';
import {FormOperate} from '../../../../../shared-module/component/form/form-opearte.service';
import {RuleUtil} from '../../../../../shared-module/util/rule-util';
import {CommonLanguageInterface} from '../../../../../../assets/i18n/common/common.language.interface';
import {Router} from '@angular/router';
import {DateType, TimeItem} from '../../photo-viewer/timer-selector/timeSelector';
import {DeviceChartUntil} from '../device-chart-until';

/**
 * 设施详情告警板块组件
 */
@Component({
  selector: 'app-facility-alarm',
  templateUrl: './facility-alarm.component.html',
  styleUrls: ['./facility-alarm.component.scss']
})
export class FacilityAlarmComponent implements OnInit {
  // 当前告警饼图配置
  chartOption = {};
  // 当前告警环图配置
  ringOption = {};
  // 历史告警环图配置
  ringOptionHistory = {};
  // 告警增量配置
  columnarOption = {};
  // 历史告警饼图配置
  chartOptionHistory: any = {};
  // 列表配置
  tableConfig: TableConfig;
  // 列表数据
  _dataSet = [];
  // 分页实体
  pageBean: PageBean = new PageBean(10, 1, 1);
  // 查询条件
  queryCondition: QueryCondition = new QueryCondition();
  // 告警语言包
  public language: AlarmLanguageInterface;
  // 公共语言包
  public commonLanguage: CommonLanguageInterface;
  // 设施语言包
  public facilityLanguage: FacilityLanguageInterface;
  // 告警级别
  @ViewChild('alarmLevelTemp') alarmLevelTemp: TemplateRef<any>;
  // 期望完工时间
  @ViewChild('ecTimeTemp') ecTimeTemp: TemplateRef<any>;
  // 设施id
  @Input()
  deviceId: string;
  // 历史告警数据
  public _dataSetHistory: any = [];
  // 历史告警配置
  public tableConfig_H: TableConfig;
  // 备注表单
  formColumnRemark: FormItem[] = [];
  // 表单状态
  formStatus: FormOperate;
  // 备注表单状态
  formStatusRemark: FormOperate;
  // 创建工单显示隐藏
  creationWorkOrder;
  // 创建工单的数据
  creationWorkOrderData = {};
  // 告警类型
  private alarmType: string;
  // 是否加载
  public isLoading: boolean;
  // 备注显示隐藏
  remarkTable = false;
  // 当前数据
  private currentIndex: any;
  // 时间选择器数据
  timeList: Array<TimeItem> = [];
  // 时间选择器默认值
  dateType: DateType;
  // 页面是否加载
  pageLoading = false;
  // 告警创建工单区域id
  public areaId: any;

  constructor(public $nzI18n: NzI18nService,
              public $message: FiLinkModalService,
              private $dateHelper: DateHelperService,
              public $alarmStoreService: AlarmStoreService,
              private $modal: NzModalService,
              private $ruleUtil: RuleUtil,
              private $router: Router,
              public $alarmService: AlarmService) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('alarm');
    this.commonLanguage = this.$nzI18n.getLocaleData('common');
    this.facilityLanguage = this.$nzI18n.getLocaleData('facility');
    this.initTableConfig();
    this.refreshData();
    this.refreshHistory();
    this.initRemarkForm();
    // 初始化时间选择器列表
    this.timeList = [{
      label: this.language.oneWeek,
      value: DateType.ONE_WEEK
    }, {
      label: this.language.oneMonth,
      value: DateType.ONE_MONTH
    }, {
      label: this.language.threeMonth,
      value: DateType.THREE_MONTH
    }];
    this.dateType = this.timeList[0].value;

  }

  /**
   * 获取当前告警列表信息
   */
  refreshData() {
    this.tableConfig.isLoading = true;
    this.$alarmService.queryAlarmDeviceId(this.deviceId).subscribe((res: Result) => {
      this.pageBean.Total = res.totalCount;
      this.tableConfig.isLoading = false;
      this._dataSet = res.data || [];
      this._dataSet.forEach(item => {
        item.alarmCleanStatus = getAlarmCleanStatus(this.$nzI18n, item.alarmCleanStatus);
        if (item.alarmFixedLevel) {
          item['alarmLevelName'] = getAlarmLevel(this.$nzI18n, item.alarmFixedLevel);
          item.style = this.$alarmStoreService.getAlarmColorByLevel(item.alarmFixedLevel);
        }
        item.alarmName = getAlarmType(this.$nzI18n, item.alarmCode);
      });
      if (this._dataSet.length > 5) {
        this._dataSet = this._dataSet.slice(0, 5);
      }
    }, () => {
      this.tableConfig.isLoading = false;

    });
  }

  /**
   * 刷新历史告警列表
   */
  refreshHistory() {
    this.tableConfig_H.isLoading = true;
    this.$alarmService.queryAlarmHistoryDeviceId(this.deviceId).subscribe((res: Result) => {
      this.pageBean.Total = res.totalCount;
      this.tableConfig_H.isLoading = false;
      this._dataSetHistory = res.data || [];
      this._dataSetHistory.forEach(item => {
        item.alarmCleanStatus = getAlarmCleanStatus(this.$nzI18n, item.alarmCleanStatus);
        if (item.alarmFixedLevel) {
          item['alarmLevelName'] = getAlarmLevel(this.$nzI18n, item.alarmFixedLevel);
          item.style = this.$alarmStoreService.getAlarmColorByLevel(item.alarmFixedLevel);
        }
        item.alarmName = getAlarmType(this.$nzI18n, item.alarmCode);
      });
      if (this._dataSetHistory.length > 5) {
        this._dataSetHistory = this._dataSetHistory.slice(0, 5);
      }
    }, () => {
      this.tableConfig_H.isLoading = false;
    });
  }

  pageChange(event) {

  }

  /**
   * 清除告警
   * param params
   */
  clearAlarm(params) {
    this.$alarmService.updateAlarmCleanStatus(params).subscribe((res: Result) => {
      if (res.code === 0) {
        this.$message.success(res.msg);
        this.refreshData();
      } else {
        this.$message.error(res.msg);
      }
    });
  }

  private initTableConfig() {
    const columnConfig = [
      // {type: 'serial-number', width: 62, title: this.facilityLanguage.serialNumber, fixedStyle: {fixedLeft: true, style: {left: '0px'}}},
      {
        title: this.language.alarmName, key: 'alarmName', width: 100
      },
      {
        title: this.language.alarmFixedLevel, key: 'alarmFixedLevel', width: 120,
        type: 'render',
        renderTemplate: this.alarmLevelTemp,
      },
      {
        title: this.language.alarmHappenCount, key: 'alarmHappenCount', width: 100,
      },
      {
        title: this.language.alarmCleanStatus, key: 'alarmCleanStatus', width: 100,
      },
      {
        title: this.language.alarmCleanPeopleNickname, key: 'alarmCleanPeopleNickname', width: 100,
      },
      {
        title: this.language.alarmNearTime, key: 'alarmNearTime', width: 200, pipe: 'date',
      },
      {
        title: this.language.alarmCleanTime, key: 'alarmCleanTime', width: 200, pipe: 'date',
      },
      {
        title: this.language.alarmAdditionalInformation, key: 'extraMsg', width: 200,
      },
      {
        title: this.language.alarmobject, key: 'alarmObject', width: 120,
      },
      {
        title: this.language.remark, key: 'remark', width: 200,
      },

      // {
      //   title: this.language.operate, key: '', width: 200, fixedStyle: {fixedRight: true, style: {right: '0px'}}
      // },
    ];
    const config = {
      noIndex: true,
      isDraggable: true,
      isLoading: true,
      showSearchSwitch: false,
      scroll: {x: '1366px', y: '600px'},
      columnConfig: columnConfig,
      showPagination: false,
      bordered: false,
      searchReturnType: 'object',
      leftBottomButtons: [],
      sort: (event: SortCondition) => {
        this.queryCondition.sortCondition.sortField = event.sortField;
        this.queryCondition.sortCondition.sortRule = event.sortRule;
        this.refreshData();
      },
    };
    this.tableConfig = Object.assign({
      operation: [
        {
          text: this.language.alarmClean,
          permissionCode: '02-1-2',
          className: 'fiLink-clear',
          needConfirm: true,
          confirmContent: this.language.alarmAffirmClear,
          handle: (item) => {
            const params = [{id: item.id}];
            this.clearAlarm(params);
          }
        },
        {
          text: this.language.updateRemark,
          permissionCode: '02-1-4',
          className: 'fiLink-edit',
          handle: (currentIndex) => {
            this.formStatusRemark.resetData({remarks: currentIndex.remark});
            this.currentIndex = currentIndex;
            this.alarmType = 'current';
            this.remarkTable = true;
          }
        },
        {
          text: this.language.buildOrder,
          permissionCode: '06-2-1-1',
          className: 'fiLink-create',
          handle: (currentIndex) => {
            this.creationWorkOrder = true;
            this.areaId = currentIndex.areaId;
            this.alarmType = 'current';
            this.creationWorkOrderData = currentIndex;
          }
        }
      ],
    }, config);
    this.tableConfig.columnConfig = this.tableConfig.columnConfig.concat([
      {title: this.language.operate, key: '', width: 200, fixedStyle: {fixedRight: true, style: {right: '0px'}}}
    ]);
    this.tableConfig_H = Object.assign({
      // 历史告警备注暂时去掉
      // operation: [
      // {
      //   text: this.language.remark,
      //   permissionCode: '02-2-1',
      //   className: 'fiLink-edit',
      //   handle: (currentIndex) => {
      //     this.formStatusRemark.resetData({remarks: currentIndex.remark});
      //     this.currentIndex = currentIndex;
      //     this.alarmType = 'history';
      //     this.remarkTable = true;
      //   }
      // }
      // ]
    }, config);
  }

  /**
   * 表单状态
   * param event
   */
  formInstanceRemark(event) {
    this.formStatusRemark = event.instance;
  }

  /**
   * 初始化remark
   */
  initRemarkForm() {
    this.formColumnRemark = [
      {
        label: this.facilityLanguage.remarks, key: 'remarks', type: 'textarea',
        col: 24,
        width: 500,
        labelWidth: 76,
        rule: [this.$ruleUtil.getRemarkMaxLengthRule(), this.$ruleUtil.getNameRule()],
        customRules: [this.$ruleUtil.getNameCustomRule()]
      }
    ];
  }

  /**
   * 修改备注
   */
  updateAlarmRemark() {
    const data = [{id: this.currentIndex.id, remark: this.formStatusRemark.getData('remarks') || null}];
    let reqMethod, refreshMethod;
    if (this.alarmType === 'history') {
      reqMethod = 'updateHistoryAlarmRemark';
      refreshMethod = 'refreshHistory';
    } else {
      reqMethod = 'updateAlarmRemark';
      refreshMethod = 'refreshData';
    }
    this.isLoading = true;
    this.$alarmService[reqMethod](data).subscribe((res: Result) => {
      if (res.code === 0) {
        this[refreshMethod]();
        this.isLoading = false;
        this.$message.success(res.msg);
      } else {
        this.$message.error(res.msg);
      }
    });
  }

  /**
   * 跳转到对应的告警页面
   * param url
   */
  navigatorTo(url) {
    this.$router.navigate([url], {queryParams: {deviceId: this.deviceId}}).then();
  }

  /**
   * 日期选择回调
   * param event
   */
  changeFilter(event) {
    this.pageLoading = true;
    const body = {
      deviceId: this.deviceId,
      beginTime: new Date(event.startTime).getTime(),
      endTime: new Date(event.endTime).getTime()
    };
    this.currentLevelStatistics(body);
    this.historyLevelStatistics(body);
    this.currentSourceNameStatistics(body);
    this.historySourceNameStatistics(body);
    this.queryAlarmSourceIncremental(body);
  }

  /**
   * 设施统计当前告警级别信息
   */
  currentLevelStatistics(body) {
    this.$alarmService.currentLevelStatistics(body).subscribe((result: Result) => {
      this.pageLoading = false;
      if (result.code === 0) {
        const data = this.convertAlarmLevelData(result).data;
        const color = this.convertAlarmLevelData(result).color;
        this.chartOption = DeviceChartUntil.setAlarmLevelStatisticsChartOption(this.language.currentAlarm, data, color);
      }
    }, () => {
      this.pageLoading = false;
    });
  }

  /**
   * 设施统计历史告警级别信息
   */
  historyLevelStatistics(body) {
    this.$alarmService.historyLevelStatistics(body).subscribe((result: Result) => {
      this.pageLoading = false;
      if (result.code === 0) {
        const data = this.convertAlarmLevelData(result).data;
        const color = this.convertAlarmLevelData(result).color;
        this.chartOptionHistory = DeviceChartUntil.setAlarmLevelStatisticsChartOption(this.language.historyAlarm, data, color);
      }
    }, () => {
      this.pageLoading = false;
    });
  }

  /**
   * 设施统计当前告警名称信息
   */
  currentSourceNameStatistics(body) {
    this.$alarmService.currentSourceNameStatistics(body).subscribe((result: Result) => {
      this.pageLoading = false;
      if (result.code === 0) {
        const arr = [];
        Object.keys(result.data).forEach(item => {
          const value = {
            value: result.data[item], name: getAlarmType(this.$nzI18n, item),
            itemStyle: {color: alarmNameColorEnum[item]}
          };
          arr.push(value);
        });
        this.ringOption = DeviceChartUntil.setAlarmNameStatisticsChartOption(this.language.currentAlarm, arr);
      }
    }, () => {
      this.pageLoading = false;
    });
  }

  /**
   * 设施统计历史告警名称信息
   */
  historySourceNameStatistics(body) {
    this.$alarmService.historySourceNameStatistics(body).subscribe((result: Result) => {
      this.pageLoading = false;
      if (result.code === 0) {
        const arr = this.convertAlarmNameData(result);
        this.ringOptionHistory = DeviceChartUntil.setAlarmNameStatisticsChartOption(this.language.historyAlarm, arr);
      }
    }, () => {
      this.pageLoading = false;
    });
  }

  /**
   * 查询告警增量
   * param body
   */
  queryAlarmSourceIncremental(body) {
    this.$alarmService.queryAlarmSourceIncremental(body).subscribe((result: Result) => {
      this.pageLoading = false;
      if (result.code === 0) {
        let data = result.data || [];
        data = data.sort((a, b) => {
          if (+new Date(a.groupTime) - (+new Date(b.groupTime)) > 0) {
            return 1;
          } else {
            return -1;
          }
        });
        const seriesData = [];
        data.forEach(item => {
          seriesData.push([item.groupTime, item.count]);
        });
        this.columnarOption = DeviceChartUntil.setAlarmSourceIncrementalChartOption(seriesData);
      }
    }, () => {
      this.pageLoading = false;
    });
  }

  /**
   * 转换告警级别数据
   * param result
   * returns any[]
   */
  private convertAlarmLevelData(result) {
    const totalCount = result.data.minorAlarmCount + result.data.hintAlarmCount +
      result.data.mainAlarmCount + result.data.urgentAlarmCount;
    const color = [
      // 次要
      this.$alarmStoreService.getAlarmColorByLevel(AlarmLevelCode.SECONDARY).backgroundColor,
      // 提示
      this.$alarmStoreService.getAlarmColorByLevel(AlarmLevelCode.PROMPT).backgroundColor,
      // 主要
      this.$alarmStoreService.getAlarmColorByLevel(AlarmLevelCode.MAIN).backgroundColor,
      // 紧急
      this.$alarmStoreService.getAlarmColorByLevel(AlarmLevelCode.URGENT).backgroundColor,
    ];
    const data = [
      // 次要
      {value: (totalCount !== 0 && result.data.minorAlarmCount === 0) ? null : result.data.minorAlarmCount, name: this.language.secondary},
      // 提示
      {value: (totalCount !== 0 && result.data.hintAlarmCount === 0) ? null : result.data.hintAlarmCount, name: this.language.prompt},
      // 主要
      {value: (totalCount !== 0 && result.data.mainAlarmCount === 0) ? null : result.data.mainAlarmCount, name: this.language.main},
      // 紧急
      {value: (totalCount !== 0 && result.data.urgentAlarmCount === 0) ? null : result.data.urgentAlarmCount, name: this.language.urgent},
    ];
    return {color, data};
  }

  /**
   * 转换告警名称数据
   * param result
   * returns {any[]}
   */
  private convertAlarmNameData(result) {
    const arr = [];
    Object.keys(result.data).forEach(item => {
      const value = {
        value: result.data[item], name: getAlarmType(this.$nzI18n, item),
        itemStyle: {color: alarmNameColorEnum[item]}
      };
      arr.push(value);
    });
    return arr;
  }
}
