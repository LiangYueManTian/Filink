import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {PageBean} from '../../../../../shared-module/entity/pageBean';
import {TableConfig} from '../../../../../shared-module/entity/tableConfig';
import {FilterCondition, QueryCondition, SortCondition} from '../../../../../shared-module/entity/queryCondition';
import {getDeviceType} from '../../../facility.config';
import {NzI18nService} from 'ng-zorro-antd';
import {FacilityLanguageInterface} from '../../../../../../assets/i18n/facility/facility.language.interface';
import {Result} from '../../../../../shared-module/entity/result';
import {FacilityService} from '../../../../../core-module/api-service/facility/facility-manage';
import {Router} from '@angular/router';
import {LogManageService} from '../../../../../core-module/api-service/system-setting/log-manage/log-manage.service';
import {CommonLanguageInterface} from '../../../../../../assets/i18n/common/common.language.interface';

/**
 * 设施详情设施日志组件
 */
@Component({
  selector: 'app-facility-view-detail-log',
  templateUrl: './facility-view-detail-log.component.html',
  styleUrls: ['./facility-view-detail-log.component.scss']
})
export class FacilityViewDetailLogComponent implements OnInit {
  // 设施id
  @Input()
  deviceId: string;
  // 是否有主控
  @Input()
  hasControl: boolean;
  // 设施日志列表数据
  facilityLogSet: any = [];
  // 操作日志数据
  operationLogSet: any = [];
  // 设施日志分页
  facilityLogPageBean: PageBean = new PageBean(10, 1, 1);
  // 操作日志分页
  operationLogPageBean: PageBean = new PageBean(10, 1, 1);
  // 设施日志列表配置
  facilityLogTableConfig: TableConfig;
  // 操作日志列表配置
  operationLogTableConfig: TableConfig;
  // 设施语言包
  public language: FacilityLanguageInterface;
  // 系统设置语言包
  public logLanguage: any = {};
  // 日志语言包
  public _logLanguage: any = {};
  // 公共语言包
  public commonLanguage: CommonLanguageInterface;
  // 操作结果
  @ViewChild('optResult') private optResult;
  // 查询条件
  private queryCondition: QueryCondition = new QueryCondition();

  constructor(private $nzI18n: NzI18nService,
              private $facilityService: FacilityService,
              private $logManageService: LogManageService,
              private $router: Router) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
    this.logLanguage = this.$nzI18n.getLocaleData('systemSetting');
    this._logLanguage = this.$nzI18n.getLocaleData('log');
    this.commonLanguage = this.$nzI18n.getLocaleData('common');
    this.initTableConfig();
    this.refreshData();
  }

  /**
   * 设施日志翻页处理
   * param event
   */
  facilityLogPageChange(event) {

  }

  /**
   * 操作日志翻页处理
   * param event
   */
  operationLogPageChange(event) {

  }

  /**
   * 初始化列表配置
   */
  private initTableConfig() {
    this.facilityLogTableConfig = {
      isDraggable: true,
      isLoading: false,
      scroll: {x: '512px', y: '600px'},
      noIndex: true,
      columnConfig: [
        // {type: 'serial-number', width: 62, title: this.language.serialNumber, fixedStyle: {fixedLeft: true, style: {left: '0px'}}},
        {title: this.language.deviceLogName, key: 'logName', width: 100},
        {
          title: this.language.deviceType_a, key: 'deviceType', width: 100,
          searchConfig: {type: 'select', selectInfo: getDeviceType(this.$nzI18n), label: 'label', value: 'code'}
        },
        {
          title: this.language.extraRemarks, key: 'remarks', width: 100,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.createTime, key: 'currentTime', width: 150, pipe: 'date',
        },
      ],
      showPagination: false,
      bordered: false,
      showSearch: false,
      topButtons: [],
      operation: [],
      leftBottomButtons: [],
      sort: (event: SortCondition) => {
      },
      handleSearch: (event) => {
      }
    };
    this.operationLogTableConfig = {
      isDraggable: this.hasControl,
      isLoading: false,
      showSearchSwitch: false,
      showRowSelection: false,
      showSizeChanger: false,
      scroll: {x: '662px', y: '600px'},
      noIndex: true,
      columnConfig: [
        // {type: 'serial-number', width: 62, title: this.language.serialNumber, fixedStyle: {fixedLeft: true, style: {left: '0px'}}},
        {
          title: this.logLanguage.optUserName,
          key: 'optUserName',
          width: 150
        },
        {
          title: this.logLanguage.optTime,
          key: 'optTime',
          width: 150,
          pipe: 'date',
        },
        {
          title: this.logLanguage.optResult,
          key: 'optResult',
          width: 80,
          type: 'render',
          renderTemplate: this.optResult,
        },
        {
          title: this.logLanguage.detailInfo,
          key: 'detailInfo',
          width: 150,
        },
      ],
      showPagination: false,
      bordered: false,
      showSearch: false,
      topButtons: [],
      operation: [],
      leftBottomButtons: [],
      sort: (event: SortCondition) => {
      },
      handleSearch: (event) => {
      }
    };
  }

  /**
   * 导航跳转
   * param url
   */
  navigatorTo(url) {
    this.$router.navigate([url], {queryParams: {id: this.deviceId}}).then();
  }

  /**
   * 刷新数据
   */
  private refreshData() {
    this.facilityLogTableConfig.isLoading = true;
    this.operationLogTableConfig.isLoading = true;
    const condition = new FilterCondition('deviceId');
    condition.filterValue = this.deviceId;
    condition.operator = 'eq';
    this.queryCondition.filterConditions = [condition];
    this.$facilityService.queryDeviceLogListByPage(this.queryCondition).subscribe((result: Result) => {
      this.facilityLogTableConfig.isLoading = false;
      this.facilityLogSet = result.data || [];
      this.facilityLogSet.forEach(item => {
        if (item.deviceType) {
          item['_deviceType'] = item.deviceType;
          item.deviceType = getDeviceType(this.$nzI18n, item.deviceType);
        }
      });
      if (this.facilityLogSet.length > 5) {
        this.facilityLogSet = this.facilityLogSet.slice(0, 5);
      }
    }, () => {
      this.facilityLogTableConfig.isLoading = false;
    });
    condition.filterField = 'optObjId';
    this.queryCondition.sortCondition.sortField = 'optTime';
    this.queryCondition.sortCondition.sortRule = 'desc';
    this.$logManageService.findOperateLog(this.queryCondition).subscribe((result: Result) => {
      this.operationLogTableConfig.isLoading = false;
      this.operationLogSet = result.data || [];
      if (this.operationLogSet.length > 5) {
        this.operationLogSet = this.operationLogSet.slice(0, 5);
      }
    }, () => {
      this.operationLogTableConfig.isLoading = false;
    });
  }
}
