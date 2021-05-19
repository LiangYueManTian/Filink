import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { PageBean } from '../../../../../shared-module/entity/pageBean';
import { TableConfig } from '../../../../../shared-module/entity/tableConfig';
import { Router } from '@angular/router';
import { NzI18nService } from 'ng-zorro-antd';
import { FormItem } from '../../../../../shared-module/component/form/form-config';
import { FormOperate } from '../../../../../shared-module/component/form/form-opearte.service';
import { AlarmService } from '../../../../../core-module/api-service/alarm';
import { Result } from '../../../../../shared-module/entity/result';
import { AlarmLanguageInterface } from '../../../../../../assets/i18n/alarm/alarm-language.interface';
import { QueryCondition, SortCondition } from '../../../../../shared-module/entity/queryCondition';
import { FiLinkModalService } from '../../../../../shared-module/service/filink-modal/filink-modal.service';
import { AlarmStoreService } from '../../../../../core-module/store/alarm.store.service';

@Component({
  selector: 'app-current-alarm-set',
  templateUrl: './current-alarm-set.component.html',
  styleUrls: ['./current-alarm-set.component.scss']
})
export class CurrentAlarmSetComponent implements OnInit {
  _dataSet = [];
  alarmNameArray = [];
  alarmLevelArray = [];
  alarmId: string = '';
  pageBean: PageBean = new PageBean(10, 1, 1);
  tableConfig: TableConfig;
  queryCondition: QueryCondition = new QueryCondition();
  public language: AlarmLanguageInterface;
  @ViewChild('alarmDefaultLevelTemp') alarmDefaultLevelTemp: TemplateRef<any>;
  @ViewChild('alarmLevelTemp') alarmLevelTemp: TemplateRef<any>;
  @ViewChild('alarmConfirmTemp') alarmConfirmTemp: TemplateRef<any>;
  @ViewChild('tableDefaultTemp') tableDefaultTemp: TemplateRef<any>;
  isVisibleEdit: boolean = false;
  tableColumnEdit: FormItem[];
  formStatusAdd: FormOperate;
  formStatusEdit: FormOperate;
  alarmDefaultLevel;
  alarmName;
  constructor(
    private $message: FiLinkModalService,
    private $router: Router,
    private $nzI18n: NzI18nService,
    private $alarmService: AlarmService,
    private $alarmStoreService: AlarmStoreService) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('alarm');
    this.initTableConfig();
    this.initEditForm();
    this.refreshData();
  }

  pageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }

  selectDataChange(event) {}


  /**
    * 获取当前告警设置列表信息
    */
  refreshData() {
    this.tableConfig.isLoading = true;
    this.$alarmService.queryAlarmSetList(this.queryCondition).subscribe((res: Result) => {
      this.pageBean.Total = res.totalCount;
      this.tableConfig.isLoading = false;
      // this._dataSet = res.data;
      this._dataSet = res.data.map(item => {
        item.defaultStyle = this.$alarmStoreService.getAlarmColorByLevel(item.alarmDefaultLevel);
        item.style = this.$alarmStoreService.getAlarmColorByLevel(item.alarmLevel);
        return item;
      });
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }


  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: false,
      showSizeChanger: true,
      scroll: { x: '1000px', y: '600px' },
      noIndex: true,
      columnConfig: [
        { type: 'select', fixedStyle: { fixedLeft: true, style: { left: '0px' } }, width: 62 },
        {
          type: 'serial-number', width: 62, title: this.language.serialNumber,
          fixedStyle: { fixedLeft: true, style: { left: '60px' } }
        },
        {
          title: this.language.alarmName, key: 'alarmName', width: 100,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' },
          fixedStyle: { fixedLeft: true, style: { left: '122px' } }
        },
        {
          title: this.language.alarmDefaultLevel, key: 'alarmDefaultLevel', width: 100,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' },
          type: 'render',
          renderTemplate: this.alarmDefaultLevelTemp,
        },
        {
          title: this.language.alarmLevel, key: 'alarmLevel', width: 100,
          configurable: true,
          searchable: true,
          isShowSort: true,
          type: 'render',
          renderTemplate: this.alarmLevelTemp,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.alarmAutomaticConfirmation, key: 'alarmAutomaticConfirmation', width: 100,
          configurable: true,
          searchable: true,
          isShowSort: true,
          type: 'render',
          renderTemplate: this.alarmConfirmTemp,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.operate,
          searchConfig: { type: 'operate' }, key: '', width: 80, fixedStyle: { fixedRight: true, style: { right: '0px' } }
        },
      ],
      showPagination: false,
      bordered: false,
      showSearch: false,
      searchReturnType: 'object',
      topButtons: [],
      operation: [
        {
          text: this.language.update,
          className: 'iconfont fiLink-edit',
          handle: (data) => {
            this.queryAlarmLevel();
            this.alarmId = data.id;
            this.$alarmService.queryAlarmLevelSetById(this.alarmId).subscribe((res: Result) => {
              const d = res.data[0];
              this.alarmName = d.alarmName;
              if (d.alarmDefaultLevel === '1') {
                this.alarmDefaultLevel = this.language.urgent;
              }
              if (d.alarmDefaultLevel === '2') {
                this.alarmDefaultLevel = this.language.main;
              }
              if (d.alarmDefaultLevel === '3') {
                this.alarmDefaultLevel = this.language.secondary;
              }
              if (d.alarmDefaultLevel === '4') {
                this.alarmDefaultLevel = this.language.prompt;
              }
              this.formStatusEdit.resetData(d);
            });
            this.isVisibleEdit = true;
          }
        }
      ],
      sort: (event: SortCondition) => {
        this.queryCondition.sortCondition.sortField = event.sortField;
        this.queryCondition.sortCondition.sortRule = event.sortRule;
        this.refreshData();
      },
      handleSearch: (event) => {
        this.queryCondition.filterConditions = event;
        this.refreshData();
      }
    };
  }


  /**
   * 编辑告警设置
   */
  public initEditForm() {
    this.tableColumnEdit = [
      {
        label: this.language.alarmName, key: 'alarmName',
        type: 'input', require: true,
        col: 24,
        disabled: true,
        modelChange: (controls, $event, key) => {
        },
        openChange: (a, b, c) => { },
        rule: [], asyncRules: []
      },
      {
        label: this.language.alarmDefaultLevel, key: 'alarmDefaultLevel',
        type: 'custom', require: true,
        col: 24,
        disabled: true,
        template: this.tableDefaultTemp,
        modelChange: (controls, $event, key) => {
        },
        openChange: (a, b, c) => { },
        rule: [], asyncRules: []
      },
      {
        label: this.language.alarmLevel, key: 'alarmLevel',
        type: 'select', require: false,
        col: 24,
        selectInfo: {
          data: this.alarmLevelArray,
          label: 'label',
          value: 'value'
        },
        modelChange: (controls, $event, key) => { },
        openChange: (a, b, c) => { },
        rule: [], asyncRules: []
      },
      {
        label: this.language.alarmAutomaticConfirmation,
        key: 'alarmAutomaticConfirmation',
        type: 'radio',
        require: false,
        col: 24,
        initialValue: '0',
        radioInfo: {
          data: [
            { label: this.language.yes, value: '1' },
            { label: this.language.no, value: '0' },
          ],
          label: 'label',
          value: 'value'
        },
        modelChange: (controls, $event, key, formOperate: FormOperate) => { },
        rule: [],
        asyncRules: []
      },
    ];
  }


  formInstance2(event) {
    this.formStatusEdit = event.instance;
  }

  /**
   * 编辑告警设置
   */
  editHandle(): void {
    this.alarmNameArray = [];
    this.alarmLevelArray = [];
    const editData = this.formStatusEdit.getData();
    editData.id = this.alarmId;
    editData.alarmName = this.alarmName;
    this.$alarmService.updateAlarmCurrentSet(editData).subscribe((res: Result) => {
      if (res.code === 0) {
        this.$message.success(res.msg);
        this.isVisibleEdit = false;
        this.refreshData();
      } else {
        this.$message.error(res.msg);
      }

    });
  }

  editHandleCancel() {
    this.isVisibleEdit = false;
    this.alarmNameArray = [];
    this.alarmLevelArray = [];
  }


  /**
   * 查询所有告警级别
   */
  queryAlarmLevel() {
    this.$alarmService.queryAlarmLevel().subscribe(res => {
      if (res['data'].length > 0) {
        for (let i = 0; i < res['data'].length; i++) {
          this.alarmLevelArray.push({ 'label': res['data'][i]['alarmLevelName'], 'value': res['data'][i]['alarmLevelCode'] });
        }
      }
    });
  }


  /**
   * 跳转到告警级别设置页面
   */
  navigateToLevelSet() {
    this.$router.navigate(['business/alarm/alarm-level-set']).then();
  }

  /**
   * 获取当前名称级别
   * param alarmName
   */
  getAlarmItem(alarmName) {
    return this.alarmNameArray.find(item => item.label === alarmName);
  }
}

