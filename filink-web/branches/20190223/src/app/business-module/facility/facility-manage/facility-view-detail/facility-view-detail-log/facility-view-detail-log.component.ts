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
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-facility-view-detail-log',
  templateUrl: './facility-view-detail-log.component.html',
  styleUrls: ['./facility-view-detail-log.component.scss']
})
export class FacilityViewDetailLogComponent implements OnInit {
  @Input()
  deviceId: string;
  facilityLogSet: any = [];
  operationLogSet: any = [];
  facilityLogPageBean: PageBean = new PageBean(10, 1, 1);
  operationLogPageBean: PageBean = new PageBean(10, 1, 1);
  facilityLogTableConfig: TableConfig;
  operationLogTableConfig: TableConfig;
  public language: FacilityLanguageInterface;
  public logLanguage: any = {};
  public _logLanguage: any = {};
  @ViewChild('optResult') private optResult;
  private queryCondition: QueryCondition = new QueryCondition();

  constructor(private $nzI18n: NzI18nService,
              private $facilityService: FacilityService,
              private $logManageService: LogManageService,
              private $router: Router,
              private datePipe: DatePipe,) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
    this.logLanguage = this.$nzI18n.getLocaleData('systemSetting');
    this._logLanguage = this.$nzI18n.getLocaleData('log');
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

  private initTableConfig() {
    this.facilityLogTableConfig = {
      isDraggable: false,
      isLoading: false,
      scroll: {x: '512px', y: '600px'},
      noIndex: true,
      columnConfig: [
        {type: 'serial-number', width: 62, title: this.language.serialNumber, fixedStyle: {fixedLeft: true, style: {left: '0px'}}},
        {title: this.language.deviceLogName, key: 'logName', width: 100, fixedStyle: {fixedLeft: true, style: {left: '62px'}}},
        {
          title: this.language.deviceType_a, key: 'deviceType', width: 100,
          searchConfig: {type: 'select', selectInfo: getDeviceType(this.$nzI18n), label: 'label', value: 'code'}
        },
        {
          title: this.language.extraRemarks, key: 'remarks', width: 100,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.createTime, key: 'currentTime', width: 150,
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
      isDraggable: false,
      isLoading: false,
      showSearchSwitch: false,
      showRowSelection: false,
      showSizeChanger: false,
      scroll: {x: '662px', y: '600px'},
      noIndex: true,
      columnConfig: [
        {type: 'serial-number', width: 62, title: this.language.serialNumber, fixedStyle: {fixedLeft: true, style: {left: '0px'}}},
        {
          title: this.logLanguage.optUserName,
          key: 'optUserName',
          width: 150,
          fixedStyle: {fixedLeft: true, style: {left: '62px'}}
        },
        {
          title: this.logLanguage.optTime,
          key: 'optTime',
          width: 150,
        },
        {
          title: this.logLanguage.optResult,
          key: 'optResult',
          width: 150,
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
        if (item.currentTime) {
          item.currentTime = this.$nzI18n.formatDateCompatible(new Date(item.currentTime), 'yyyy-MM-dd HH:mm:ss');
        }
        if (item.deviceType) {
          item['_deviceType'] = item.deviceType;
          item.deviceType = getDeviceType(this.$nzI18n, item.deviceType);
        }
      });
      if (this.facilityLogSet.length > 5) {
        this.facilityLogSet = this.facilityLogSet.slice(0, 5);
      }
    });
    condition.filterField = 'optObjId';
    this.queryCondition.sortCondition.sortField = 'optTime';
    this.queryCondition.sortCondition.sortRule = 'desc';
    this.$logManageService.findOperateLog(this.queryCondition).subscribe((result: Result) => {
      this.operationLogTableConfig.isLoading = false;
      this.operationLogSet = result.data || [];
      this.operationLogSet.forEach(item => {
        if (item.optTime) {
          item.optTime = this.$nzI18n.formatDateCompatible(new Date(Number(item.optTime)), 'yyyy-MM-dd HH:mm:ss');
        }
      });
      if (this.operationLogSet.length > 5) {
        this.operationLogSet = this.operationLogSet.slice(0, 5);
      }
    });
  }
}
