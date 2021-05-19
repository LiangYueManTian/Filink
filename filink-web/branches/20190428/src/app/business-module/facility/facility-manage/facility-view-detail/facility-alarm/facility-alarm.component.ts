import {Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {QueryCondition, SortCondition} from '../../../../../shared-module/entity/queryCondition';
import {Result} from '../../../../../shared-module/entity/result';
import {TableConfig} from '../../../../../shared-module/entity/tableConfig';
import {PageBean} from '../../../../../shared-module/entity/pageBean';
import {AlarmLanguageInterface} from '../../../../../../assets/i18n/alarm/alarm-language.interface';
import {DateHelperService, NzI18nService, NzModalService} from 'ng-zorro-antd';
import {AlarmService} from '../../../../../core-module/api-service/alarm';
import {FiLinkModalService} from '../../../../../shared-module/service/filink-modal/filink-modal.service';
import {getAlarmCleanStatus} from '../../../facility.config';
import {AlarmStoreService} from '../../../../../core-module/store/alarm.store.service';
import {FacilityLanguageInterface} from '../../../../../../assets/i18n/facility/facility.language.interface';
import {DateFormatString} from '../../../../../shared-module/entity/dateFormatString';
import {FormItem} from '../../../../../shared-module/component/form/form-config';
import {FormOperate} from '../../../../../shared-module/component/form/form-opearte.service';
import {RuleUtil} from '../../../../../shared-module/util/rule-util';
import {AbstractControl} from '@angular/forms';
import {CommonLanguageInterface} from '../../../../../../assets/i18n/common/common.language.interface';
import {differenceInCalendarDays} from 'date-fns';
import {Router} from '@angular/router';

@Component({
  selector: 'app-facility-alarm',
  templateUrl: './facility-alarm.component.html',
  styleUrls: ['./facility-alarm.component.scss']
})
export class FacilityAlarmComponent implements OnInit {
  chartOption = {};
  ringOption = {};
  ringOption_1 = {};
  columnarOption = {};
  columnarOption_1 = {};
  chartOption_1 = {};
  tableConfig: TableConfig;
  _dataSet = [];
  pageBean: PageBean = new PageBean(10, 1, 1);
  queryCondition: QueryCondition = new QueryCondition();
  public language: AlarmLanguageInterface;
  public commonLanguage: CommonLanguageInterface;
  public facilityLanguage: FacilityLanguageInterface;
  @ViewChild('alarmLevelTemp') alarmLevelTemp: TemplateRef<any>;
  @ViewChild('remarkTemp') remarkTemp: TemplateRef<any>;
  @ViewChild('ecTimeTemp') ecTimeTemp: TemplateRef<any>;
  @Input()
  deviceId: string;
  public _dataSetHistory: any = [];
  public tableConfig_H: TableConfig;
  formColumnRemark: FormItem[] = [];
  formStatus: FormOperate;
  formStatusRemark: FormOperate;
  creationWorkOrder;
  // 创建工单的数据
  creationWorkOrderData = {};
  private alarmType: string;
  public isLoading: boolean;
  remarkTable = false;
  private currentIndex: any;

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
    // 获取用户信息
    // if (sessionStorage.getItem('token')) {
    //   this.token = sessionStorage.getItem('token');
    //   const userInfo = JSON.parse(sessionStorage.getItem('userInfo'));
    //   this.userId = userInfo['id'];
    // }
    this.initTab();
    this.language = this.$nzI18n.getLocaleData('alarm');
    this.commonLanguage = this.$nzI18n.getLocaleData('common');
    this.facilityLanguage = this.$nzI18n.getLocaleData('facility');
    this.initTableConfig();
    this.refreshData();
    this.refreshHistory();
    this.initRemarkForm();
  }

  selectChange() {
  }

  /**
   * 获取当前告警列表信息
   */
  refreshData() {
    this.tableConfig.isLoading = true;
    // this.queryCondition.filterConditions = [{
    //   operator: 'eq',
    //   filterField: 'alarmSource',
    //   filterValue: this.deviceId
    // }];
    // this.queryCondition.sortCondition = {'sortField': 'alarmBeginTime', 'sortRule': 'desc'};
    this.$alarmService.queryAlarmDeviceId(this.deviceId).subscribe((res: Result) => {
      this.pageBean.Total = res.totalCount;
      this.tableConfig.isLoading = false;
      this._dataSet = res.data || [];
      this._dataSet.forEach(item => {
        item.alarmCleanStatus = getAlarmCleanStatus(this.$nzI18n, item.alarmCleanStatus);
        if (item.alarmNearTime) {
          item.alarmNearTime = this.$dateHelper.format(new Date(Number(item.alarmNearTime)), DateFormatString.DATE_FORMAT_STRING);
        }
        if (item.alarmCleanTime) {
          item.alarmCleanTime = this.$dateHelper.format(new Date(Number(item.alarmCleanTime)), DateFormatString.DATE_FORMAT_STRING);
        }
        if (item.alarmFixedLevel) {
          item['alarmLevelName'] = this.$alarmStoreService.getAlarmInfoByLevel(item.alarmFixedLevel).alarmLevelName;
          item.style = this.$alarmStoreService.getAlarmColorByLevel(item.alarmFixedLevel);
        }
      });
      if (this._dataSet.length > 5) {
        this._dataSet = this._dataSet.slice(0, 5);
      }
    }, () => {
      this.tableConfig.isLoading = false;

    });
  }

  refreshHistory() {
    this.tableConfig_H.isLoading = true;
    // this.queryCondition_H.filterConditions = [{
    //   operator: 'eq',
    //   filterField: 'alarmSource',
    //   filterValue: this.deviceId
    // }];
    // this.queryCondition_H.sortCondition = {'sortField': 'alarmBeginTime', 'sortRule': 'desc'};
    this.$alarmService.queryAlarmHistoryDeviceId(this.deviceId).subscribe((res: Result) => {
      this.pageBean.Total = res.totalCount;
      this.tableConfig_H.isLoading = false;
      this._dataSetHistory = res.data || [];
      this._dataSetHistory.forEach(item => {
        item.alarmCleanStatus = getAlarmCleanStatus(this.$nzI18n, item.alarmCleanStatus);
        if (item.alarmNearTime) {
          item.alarmNearTime = this.$dateHelper.format(new Date(Number(item.alarmNearTime)), DateFormatString.DATE_FORMAT_STRING);
        }
        if (item.alarmCleanTime) {
          item.alarmCleanTime = this.$dateHelper.format(new Date(Number(item.alarmCleanTime)), DateFormatString.DATE_FORMAT_STRING);
        }
        if (item.alarmFixedLevel) {
          item['alarmLevelName'] = this.$alarmStoreService.getAlarmInfoByLevel(item.alarmFixedLevel).alarmLevelName;
          item.style = this.$alarmStoreService.getAlarmColorByLevel(item.alarmFixedLevel);
        }
      });
      if (this._dataSetHistory.length > 5) {
        this._dataSetHistory = this._dataSetHistory.slice(0, 5);
      }
    }, () => {
      this.tableConfig_H.isLoading = false;
    });
  }

  private initTab() {
    this.ringOption = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        data: []
      },
      color: ['#fbd517', '#009edf', '#ff6608', '#e51216'],
      series: [
        {
          name: '数据占比',
          type: 'pie',
          radius: ['50%', '70%'],
          avoidLabelOverlap: false,
          label: {
            normal: {
              show: false,
              position: 'center'
            },
            emphasis: {
              show: true,
              textStyle: {
                fontSize: '30',
                fontWeight: 'bold'
              }
            }
          },
          labelLine: {
            normal: {
              show: false
            }
          },
          data: [
            {value: 1548, name: '告警'},
            {value: 335, name: '告警1'},
            {value: 310, name: '告警2'},
            {value: 234, name: '告警3'},
          ]
        }
      ]
    };
    this.ringOption_1 = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        data: []
      },
      color: ['#fbd517', '#009edf', '#ff6608', '#e51216'],
      series: [
        {
          name: '数据占比',
          type: 'pie',
          radius: ['50%', '70%'],
          avoidLabelOverlap: false,
          label: {
            normal: {
              show: false,
              position: 'center'
            },
            emphasis: {
              show: true,
              textStyle: {
                fontSize: '30',
                fontWeight: 'bold'
              }
            }
          },
          labelLine: {
            normal: {
              show: false
            }
          },
          data: [
            {value: 335, name: '告警'},
            {value: 310, name: '告警1'},
            {value: 234, name: '告警2'},
            {value: 135, name: '告警3'}
          ]
        }
      ]
    };

    this.columnarOption = {
      color: ['#009edf'],
      xAxis: {
        type: 'category',
        data: ['10月', '11月', '12月', '1月',
          '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月'
        ]
      },
      yAxis: {
        type: 'value'
      },
      series: [{
        data: [120, 200, 150, 80, 70, 110, 130, 90, 10, 25, 26, 55],
        type: 'bar'
      }]
    };
    this.columnarOption_1 = {
      color: ['#009edf'],
      xAxis: {
        type: 'category',
        data: ['10月', '11月', '12月', '1月',
          '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月'
        ]
      },
      yAxis: {
        type: 'value'
      },
      series: [{
        data: [120, 200, 150, 80, 70, 110, 130, 90, 10, 25, 26, 55],
        type: 'bar'
      }]
    };

    this.chartOption = {
      color: ['#009edf'],
      title: {},
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        data: []
      },
      series: [
        {
          name: '数据占比',
          type: 'pie',
          radius: '55%',
          center: ['50%', '50%'],
          color: ['#fbd517', '#009edf', '#ff6608', '#e51216'],
          data: [
            {value: 50, name: '次要'},
            {value: 50, name: '提示'},
            {value: 50, name: '主要'},
            {value: 50, name: '紧急'},
          ],
          itemStyle: {
            emphasis: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    };
    this.chartOption_1 = {
      title: {},
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        data: []
      },
      color: ['#fbd517', '#009edf', '#ff6608', '#e51216'],
      series: [
        {
          name: '数据占比',
          type: 'pie',
          radius: '55%',
          center: ['50%', '50%'],
          data: [
            {value: 50, name: '次要'},
            {value: 50, name: '提示'},
            {value: 50, name: '主要'},
            {value: 50, name: '紧急'},
          ],
          itemStyle: {
            emphasis: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    };
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
        title: this.language.alarmNearTime, key: 'alarmNearTime', width: 200,
      },
      {
        title: this.language.alarmCleanTime, key: 'alarmCleanTime', width: 200,
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

      {
        title: this.language.operate, key: '', width: 200, fixedStyle: {fixedRight: true, style: {right: '0px'}}
      },
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
          className: 'fiLink-clear',
          needConfirm: true,
          confirmContent: this.language.alarmAffirmClear,
          handle: (item) => {
            // const params = {
            //   'alarmCurrents': [{id: item.id}],
            //   'userId': this.userId,
            //   'token': this.token
            // };
            const params = [{id: item.id}];
            this.clearAlarm(params);
          }
        },
        {
          text: this.language.updateRemark,
          // needConfirm: true,
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
          className: 'fiLink-create',
          handle: (currentIndex) => {
            this.creationWorkOrder = true;
            this.alarmType = 'current';
            // this.initForm();
            this.creationWorkOrderData = currentIndex;
          }
        }
      ],
    }, config);
    this.tableConfig_H = Object.assign({
      operation: [
        {
          text: this.language.remark,
          // needConfirm: true,
          className: 'fiLink-edit',
          handle: (currentIndex) => {
            this.formStatusRemark.resetData({remarks: currentIndex.remark});
            this.currentIndex = currentIndex;
            this.alarmType = 'history';
            this.remarkTable = true;
          }
        },
        // {
        //   text: this.language.buildOrder,
        //   className: 'fiLink-create',
        //   handle: () => {
        //     this.creationWorkOrder = true;
        //     this.alarmType = 'history';
        //   }
        // }
      ]
    }, config);
  }
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
    const data = [{id: this.currentIndex.id, remark: this.formStatusRemark.getData('remarks')}];
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
}
