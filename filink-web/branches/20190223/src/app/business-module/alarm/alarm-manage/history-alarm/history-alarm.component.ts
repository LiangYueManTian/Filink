import { Component, OnInit, Injectable, ViewChild, TemplateRef } from '@angular/core';
import { PageBean } from '../../../../shared-module/entity/pageBean';
import { TableConfig } from '../../../../shared-module/entity/tableConfig';
import { ActivatedRoute, Router } from '@angular/router';
import { NzI18nService, NzMessageService } from 'ng-zorro-antd';
import { AlarmService } from '../../../../core-module/api-service/alarm';
import { Result } from '../../../../shared-module/entity/result';
import { AlarmLanguageInterface } from '../../alarm-language.interface';
import { FilterCondition, QueryCondition, SortCondition } from '../../../../shared-module/entity/queryCondition';
import { FiLinkModalService } from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {AlarmStoreService} from '../../../../core-module/store/alarm.store.service';
import {CommonUtil} from '../../../../shared-module/util/common-util';

@Component({
  selector: 'app-history-alarm',
  templateUrl: './history-alarm.component.html',
  styleUrls: ['./history-alarm.component.scss']
})
export class HistoryAlarmComponent implements OnInit {
  _dataSet = [];
  pageBean: PageBean = new PageBean(10, 1, 1);
  tableConfig: TableConfig;
  queryCondition: QueryCondition = new QueryCondition();
  public language: AlarmLanguageInterface;
  alarmId = null;
  deviceId = null;
  filterEvent;
  @ViewChild('alarmFixedLevelTemp') alarmFixedLevelTemp: TemplateRef<any>;
  @ViewChild('alarmCleanStatusTemp') alarmCleanStatusTemp: TemplateRef<any>;
  @ViewChild('alarmConfirmStatusTemp') alarmConfirmStatusTemp: TemplateRef<any>;
  @ViewChild('alarmSourceTypeTemp') alarmSourceTypeTemp: TemplateRef<any>;
  constructor(
    public $router: Router,
    public $nzI18n: NzI18nService,
    public $alarmService: AlarmService,
    public $message: FiLinkModalService,
    public $active: ActivatedRoute,
    public $alarmStoreService: AlarmStoreService
  ) { }


  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('alarm');
    this.initTableConfig();
    // 获取告警id
    if (this.$active.snapshot.queryParams.id) {
      this.alarmId = this.$active.snapshot.queryParams.id;
    }
    // 获取设备deviceId
    if (this.$active.snapshot.queryParams.deviceId) {
      this.deviceId = this.$active.snapshot.queryParams.deviceId;
    }
    if (this.alarmId) {
      const obj = {
        'pageCondition': { pageSize: this.pageBean.pageSize, pageNum: this.pageBean.pageIndex },
        'filterConditions': [{
          'filterField': 'id',
          'operator': 'eq',
          'filterValue': this.alarmId
        }]
      };
      this.refreshData(obj);
    } else if (this.deviceId) {
      const obj = {
        'pageCondition': { pageSize: this.pageBean.pageSize, pageNum: this.pageBean.pageIndex },
        'filterConditions': [{
          'filterField': 'alarmSource',
          'operator': 'eq',
          'filterValue': this.deviceId
        }]
      };
      this.refreshData(obj);
    } else {
      this.refreshData({ 'pageCondition': { pageSize: this.pageBean.pageSize, pageNum: this.pageBean.pageIndex } });
    }
  }

  pageChange(event) {
    if (this.deviceId) {
      // 当从首页进入后 将参数带入 翻页戴上
      this.queryCondition.filterConditions = [{
        'filterField': 'alarmSource',
        'operator': 'eq',
        'filterValue': this.deviceId
      }];
    } else {
      this.queryCondition.filterConditions = this.filterEvent;
    }
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }

  /**
   * 获取历史告警列表信息
   */
  refreshData(body?) {
    this.tableConfig.isLoading = true;
    this.$alarmService.queryAlarmHistoryList(body || this.queryCondition).subscribe((res: Result) => {
      this.tableConfig.isLoading = false;
      this.pageBean.Total = res.totalCount;
      this.pageBean.pageIndex = res.pageNum;
      this.pageBean.pageSize = res.size;
      this._dataSet = res.data;
      this._dataSet = res.data.map(item => {
        item.style = this.$alarmStoreService.getAlarmColorByLevel(item.alarmFixedLevel);
        return item;
      });
      this._dataSet.forEach(item => {
        if (item.alarmBeginTime) {
          item.alarmBeginTime = this.$nzI18n.formatDateCompatible(new Date(Number(item.alarmBeginTime)), 'yyyy-MM-dd HH:mm:ss');
        }
        if (item.alarmNearTime) {
          item.alarmNearTime = this.$nzI18n.formatDateCompatible(new Date(Number(item.alarmNearTime)), 'yyyy-MM-dd HH:mm:ss');
        }
        if (item.alarmConfirmTime) {
          item.alarmConfirmTime = this.$nzI18n.formatDateCompatible(new Date(Number(item.alarmConfirmTime)), 'yyyy-MM-dd HH:mm:ss');
        }
        if (item.alarmCleanTime) {
          item.alarmCleanTime = this.$nzI18n.formatDateCompatible(new Date(Number(item.alarmCleanTime)), 'yyyy-MM-dd HH:mm:ss');
        }
        // 告警持续时间
        if (item.alarmContinousTime) {
          item.alarmContinousTimeName = CommonUtil.setAlarmContinousTime( +item.alarmContinousTime,
          { year: this.language.year, month: this.language.month, day: this.language.day, hour: this.language.hour });
        }
      });
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showSizeChanger: true,
      noIndex: true,
      scroll: { x: '1200px', y: '600px' },
      columnConfig: [
        { type: 'select', fixedStyle: { fixedLeft: true, style: { left: '0px' } }, width: 60 },
        {
          type: 'serial-number', width: 62, title: this.language.serialNumber,
          fixedStyle: { fixedLeft: true, style: { left: '60px' } }
        },
        {
          title: this.language.alarmName, key: 'alarmName', width: 200, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' },
          fixedStyle: { fixedLeft: true, style: { left: '122px' } }
        },
        {
          title: this.language.alarmFixedLevel, key: 'alarmFixedLevel', width: 140, isShowSort: true,
          type: 'render',
          searchable: true,
          searchConfig: {
            type: 'select', selectInfo: [
              { label: this.language.urgent, value: '1' },
              { label: this.language.main, value: '2' },
              { label: this.language.secondary, value: '3' },
              { label: this.language.prompt, value: '4' }
            ]
          },
          renderTemplate: this.alarmFixedLevelTemp
        },
        {
          title: this.language.alarmobject, key: 'alarmobject', width: 120, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.areaName, key: 'areaName', width: 200, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.address, key: 'address', width: 200, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.responsibleDepartment, key: 'responsibleDepartment', width: 200, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.alarmSourceType, key: 'alarmSourceTypeid', width: 150, isShowSort: true,
          configurable: true,
          searchable: true,
          type: 'render',
          renderTemplate: this.alarmSourceTypeTemp,
          searchConfig: {
            type: 'select', selectInfo: [
              { label: this.language.opticalBox, value: '001' },
              { label: this.language.well, value: '030' },
              { label: this.language.distributionFrame, value: '060' },
              { label: this.language.junctionBox, value: '090' },
              { label: this.language.splittingBox, value: '150' }
            ]
          }
        },
        {
          title: this.language.alarmHappenCount, key: 'alarmHappenCount', width: 200, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.alarmCleanStatus, key: 'alarmCleanStatus', width: 200, isShowSort: true,
          type: 'render',
          searchable: true,
          searchConfig: {
            type: 'select', selectInfo: [
              { label: this.language.isClean, value: 1 },
              { label: this.language.deviceClean, value: 2 }
            ]
          },
          renderTemplate: this.alarmCleanStatusTemp
        },
        {
          title: this.language.alarmConfirmStatus, key: 'alarmConfirmStatus', width: 200, isShowSort: true,
          type: 'render',
          searchable: true,
          searchConfig: {
            type: 'select', selectInfo: [
              { label: this.language.isConfirm, value: 1 },
              { label: this.language.noConfirm, value: 2 }
            ]
          },
          renderTemplate: this.alarmConfirmStatusTemp
        },
        {
          title: this.language.alarmBeginTime, key: 'alarmBeginTime', width: 280, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'date' }
        },
        {
          title: this.language.alarmNearTime, key: 'alarmNearTime', width: 280, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'date' }
        },
        {
          title: this.language.alarmContinousTime, key: 'alarmContinousTimeName', width: 200, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.alarmConfirmTime, key: 'alarmConfirmTime', width: 280, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'date' }
        },
        {
          title: this.language.alarmCleanTime, key: 'alarmCleanTime', width: 280, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'date' }
        },
        {
          title: this.language.alarmCleanPeopleNickname, key: 'alarmCleanPeopleNickname', width: 120, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.alarmConfirmPeopleNickname, key: 'alarmConfirmPeopleNickname', width: 120, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.extraMsg, key: 'extraMsg', width: 200, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.remark, key: 'remark', width: 200, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.operate, searchable: true,
          searchConfig: { type: 'operate' },
          key: '', width: 120, fixedStyle: { fixedRight: true, style: { right: '0px' } }
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      searchReturnType: 'Array',
      topButtons: [],
      operation: [
        // {
        //   text: this.language.location,
        //   className: 'icon-location',
        //   handle: () => {
        //   }
        // },
        // {
        //   text: this.language.update,
        //   className: 'icon-update',
        //   handle: (currentIndex) => {
        //   }
        // },
        // {
        //   text: this.language.view,
        //   className: 'icon_view',
        //   handle: () => {
        //   }
        // },
        // {
        //   text: this.language.buildOrder,
        //   className: 'icon_build_order',
        //   handle: () => {
        //   }
        // }
      ],
      leftBottomButtons: [
        {
          text: this.language.historyAlarmSet, handle: (e) => {
            this.$router.navigate(['business/alarm/history-alarm-set']).then();
          }
        }
      ],
      sort: (event: SortCondition) => {
        if (this.deviceId) {
          // 当从首页进入后 将参数带入 翻页戴上
          this.queryCondition.filterConditions = [{
            'filterField': 'alarmSource',
            'operator': 'eq',
            'filterValue': this.deviceId
          }];
        } else {
          this.queryCondition.filterConditions = this.filterEvent;
        }
        if ( event.sortField === 'alarmContinousTimeName' ) {
          // 当进行告警持续时间排序时 传给后台的是 alarmContinousTime 这个参数
          this.queryCondition.sortCondition.sortField = 'alarmContinousTime';
        } else {
          this.queryCondition.sortCondition.sortField = event.sortField;
        }
        this.queryCondition.sortCondition.sortRule = event.sortRule;
        this.refreshData();
      },
      handleSearch: (event) => {
        event.forEach(item => {
          if (item.filterField === 'alarmHappenCount') {
            item.filterValue = Number(item.filterValue);
          }
          if (item.filterField === 'alarmBeginTime') {
            item.filterValue = String(item.filterValue);
          }
          if (item.filterField === 'alarmNearTime') {
            item.filterValue = String(item.filterValue);
          }
          if (item.filterField === 'alarmConfirmTime') {
            item.filterValue = String(item.filterValue);
          }
          if (item.filterField === 'alarmCleanTime') {
            item.filterValue = String(item.filterValue);
          }
          if ( item.filterField === 'alarmContinousTimeName') {
            item.filterValue = Number(item.filterValue);
            item.filterField = 'alarmContinousTime';
            item.operator = 'eq';
          }
        });
        this.pageBean = new PageBean(this.pageBean.pageSize, 1, 1);
        this.filterEvent = event;
        this.refreshData({
          'filterConditions': event,
          'pageCondition': { pageSize: this.pageBean.pageSize, pageNum: this.pageBean.pageIndex }
        });
      }
    };
  }

  /**
   * 历史告警设置
   */
  navigateToLevelSet() {
    this.$router.navigate(['business/alarm/history-alarm-set']).then();
  }
}
