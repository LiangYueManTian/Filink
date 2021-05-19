import { Component, OnInit, ViewChild, TemplateRef, Output, EventEmitter } from '@angular/core';
import { PageBean } from '../../../../../shared-module/entity/pageBean';
import { TableConfig } from '../../../../../shared-module/entity/tableConfig';
import { ActivatedRoute, Router } from '@angular/router';
import { NzI18nService, DateHelperService } from 'ng-zorro-antd';
import { AlarmService } from '../../../../../core-module/api-service/alarm';
import { Result } from '../../../../../shared-module/entity/result';
import { AlarmLanguageInterface } from '../../../../../../assets/i18n/alarm/alarm-language.interface';
import { QueryCondition, SortCondition } from '../../../../../shared-module/entity/queryCondition';
import { FiLinkModalService } from '../../../../../shared-module/service/filink-modal/filink-modal.service';
import { AlarmStoreService } from '../../../../../core-module/store/alarm.store.service';
import {DateFormatString} from '../../../../../shared-module/entity/dateFormatString';

@Component({
  selector: 'app-alarm-filtration-rule',
  templateUrl: './alarm-filtration-rule.component.html',
  styleUrls: ['./alarm-filtration-rule.component.scss']
})
export class AlarmFiltrationRuleComponent implements OnInit {

  _dataSet = [];
  pageBean: PageBean = new PageBean(10, 1, 1);
  tableConfig: TableConfig;
  queryCondition: QueryCondition = new QueryCondition();
  public language: AlarmLanguageInterface;
  deviceId = null;
  particularsData: any = [];
  filterEvent;
  display = {
    particulars: false,
    rule: true
  };
  modalFooter;
  _selectedAlarm = {};
  @Output()
  resultAndClose = new EventEmitter();
  @ViewChild('alarmCleanStatusTemp') alarmCleanStatusTemp: TemplateRef<any>;
  @ViewChild('alarmConfirmStatusTemp') alarmConfirmStatusTemp: TemplateRef<any>;
  @ViewChild('alarmSourceTypeTemp') alarmSourceTypeTemp: TemplateRef<any>;
  @ViewChild('isNoStartTemp') isNoStartTemp: TemplateRef<any>;
  @ViewChild('isNoStorageTemp') isNoStorageTemp: TemplateRef<any>;
  @ViewChild('filtrationConditionTemp') filtrationConditionTemp: TemplateRef<any>;
  @ViewChild('particulars') particulars: TemplateRef<any>;
  @ViewChild('radioTemp') radioTemp: TemplateRef<any>;

  constructor(
    public $router: Router,
    public $nzI18n: NzI18nService,
    public $alarmService: AlarmService,
    public $message: FiLinkModalService,
    public $active: ActivatedRoute,
    public $alarmStoreService: AlarmStoreService,
    private $dateHelper: DateHelperService,
  ) {
    this.language = this.$nzI18n.getLocaleData('alarm');
   }

  /**
   * 获取告警过滤列表信息
   */
  refreshData(filterEvent?) {
    this.tableConfig.isLoading = true;
    const data = filterEvent ? { 'bizCondition': filterEvent } : { 'bizCondition': {} };
    data.bizCondition = { ...data.bizCondition,
      'sortProperties': this.queryCondition.sortCondition.sortField,
      'sort': this.queryCondition.sortCondition.sortRule };
    this.$alarmService.queryAlarmFiltration( data ).subscribe((res: Result) => {
      this.tableConfig.isLoading = false;
      if (res.code === 0) {
        this.pageBean.Total = res.totalCount;
        this.pageBean.pageSize = res.data.length;
        this._dataSet = res.data.map( item => {
          item.style = this.$alarmStoreService.getAlarmColorByLevel(item.alarmFixedLevel);
          item.alarmName = item.alarmFilterRuleNameList.map(items => {
              return items.alarmName && items.alarmName.alarmName;
          }).join(',');
          if (item.createTime) {
            item.createTime = this.$dateHelper.format(new Date(item.createTime), DateFormatString.DATE_FORMAT_STRING);
          }
          item.status = item.status + '';
          item.stored = item.stored + '';
          return item;
        });
      } else {
        // 请求错误
        this.$message.success(res.msg);
      }
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
      notShowPrint: true,
      scroll: { x: '1200px', y: '600px' },
      columnConfig: [
        // { type: 'select', fixedStyle: { fixedLeft: true, style: { left: '0px' } }, width: 62 },
        {
          title: '',
          type: 'render',
          renderTemplate: this.radioTemp,
          fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 62
        },
        {
          type: 'serial-number', width: 62, title: this.language.serialNumber,
          fixedStyle: { fixedLeft: true, style: { left: '60px' } }
        },
        {
          title: this.language.name, key: 'ruleName',
          width: 100,
          searchable: true,
          searchConfig: { type: 'input' },
          fixedStyle: { fixedLeft: true, style: { left: '122px' } },
        },
        {
          // 过滤条件
          title: this.language.filterConditions,
          key: 'alarmName',
          width: 200,
          searchable: true,
          type: 'render',
          searchConfig: { type: 'input' },
          renderTemplate: this.filtrationConditionTemp,
        },
        {
          title: this.language.opertionUser, key: 'operationUser', width: 120, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.openStatus, key: 'status', width: 120, isShowSort: true,
          searchable: true,
          type: 'render',
          renderTemplate: this.isNoStartTemp,
          searchConfig: {
            type: 'select', selectType: 'multiple', selectInfo: [
              { label: this.language.disable, value: '2' },
              { label: this.language.enable, value: '1' }
            ]
          },
          handleFilter: ($event) => {},
        },
        {
          title: this.language.createTime, key: 'createTime',
          width: 280, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'dateRang' }
        },
        {
          // 是否库存
          title: this.language.isNoStored, key: 'stored', width: 200, isShowSort: true,
          searchable: true,
          type: 'render',
          renderTemplate: this.isNoStorageTemp,
          searchConfig: {
            type: 'select', selectType: 'multiple', selectInfo: [
              { label: this.language.yes, value: '1' },
              { label: this.language.no, value: '2' }
            ]
          },
          handleFilter: ($event) => {
            console.log('www', $event);
          },
        },
        {
          title: this.language.remark, key: 'remark', width: 200, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.operate, searchable: true,
          searchConfig: { type: 'operate' }, key: '',
          width: 120, fixedStyle: { fixedRight: true, style: { right: '0px' } }
        },
      ],
      bordered: false,
      showSearch: false,
      searchReturnType: 'Array',
      operation: [],
      topButtons: [],
      leftBottomButtons: [],
      sort: (event: SortCondition) => {
        this.queryCondition.filterConditions = this.filterEvent;
        this.queryCondition.sortCondition.sortField = event.sortField;
        this.queryCondition.sortCondition.sortRule = event.sortRule;
        this.refreshData();
      },
      handleSearch: (event) => {
        const filterEvent = {};
        event.forEach(item => {
          filterEvent[item.filterField] = item.filterValue;
          if (item.filterField === 'createTime') {
            filterEvent['relation'] = String(item.operator);
          }
        });
        this.filterEvent = event;
        this.refreshData(filterEvent);
      }
    };
  }

  // 点击关闭 或者 取消
  closeTable() {
    this.display.rule = false;
    this.resultAndClose.emit();
  }

  /**
   * 选择告警
   * param event
   * param data
   */
  selectedAlarmChange( data) {
    this._selectedAlarm = data;
  }

  okText() {
    this.resultAndClose.emit(this._selectedAlarm);
  }

  ngOnInit() {
    this.initTableConfig();
    this.refreshData();
  }

}
