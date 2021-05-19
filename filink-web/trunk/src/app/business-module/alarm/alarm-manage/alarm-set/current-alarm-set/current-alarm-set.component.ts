import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {PageBean} from '../../../../../shared-module/entity/pageBean';
import {TableConfig} from '../../../../../shared-module/entity/tableConfig';
import {Router} from '@angular/router';
import {NzI18nService} from 'ng-zorro-antd';
import {FormItem} from '../../../../../shared-module/component/form/form-config';
import {FormOperate} from '../../../../../shared-module/component/form/form-opearte.service';
import {AlarmService} from '../../../../../core-module/api-service/alarm';
import {Result} from '../../../../../shared-module/entity/result';
import {AlarmLanguageInterface} from '../../../../../../assets/i18n/alarm/alarm-language.interface';
import {QueryCondition, SortCondition} from '../../../../../shared-module/entity/queryCondition';
import {FiLinkModalService} from '../../../../../shared-module/service/filink-modal/filink-modal.service';
import {AlarmStoreService} from '../../../../../core-module/store/alarm.store.service';
import {getAlarmLevel, getAlarmType} from '../../../../facility/facility.config';

/**
 * 告警设置 当前告警设置
 */
@Component({
  selector: 'app-current-alarm-set',
  templateUrl: './current-alarm-set.component.html',
  styleUrls: ['./current-alarm-set.component.scss']
})
export class CurrentAlarmSetComponent implements OnInit {
  // 表格数据源
  _dataSet = [];
  // 告警名称
  alarmNameArray = [];
  // 告警级别
  alarmLevelArray = [];
  // 告警id
  alarmId: string = '';
  // 翻页对象
  pageBean: PageBean = new PageBean(10, 1, 1);
  // 表格配置
  tableConfig: TableConfig;
  // 查询条件
  queryCondition: QueryCondition = new QueryCondition();
  // 国际化接口
  public language: AlarmLanguageInterface;
  // 表格告警级别过滤模板
  @ViewChild('alarmDefaultLevelTemp') alarmDefaultLevelTemp: TemplateRef<any>;
  // 表格定制级别过滤模板
  @ViewChild('alarmLevelTemp') alarmLevelTemp: TemplateRef<any>;
  // 表格确认状态过滤模板
  @ViewChild('alarmConfirmTemp') alarmConfirmTemp: TemplateRef<any>;
  @ViewChild('tableDefaultTemp') tableDefaultTemp: TemplateRef<any>;
  // 编辑页面弹窗
  isVisibleEdit: boolean = false;
  // 告警编辑页面表单项
  tableColumnEdit: FormItem[];
  // 告警编辑表单实例
  formStatusEdit: FormOperate;
  // 告警默认级别
  alarmDefaultLevel;
  // 告警名称
  alarmName;
  // 保存按钮加载中
  isLoading: boolean = false;

  constructor(private $message: FiLinkModalService,
              private $router: Router,
              private $nzI18n: NzI18nService,
              private $alarmService: AlarmService,
              private $alarmStoreService: AlarmStoreService) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('alarm');
    // 初始化表格配置
    this.initTableConfig();
    // 初始化表单
    this.initEditForm();
    // 查询列表数据
    this.refreshData();
  }

  /**
   * 表格翻页查询
   * param event
   */
  pageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }

  selectDataChange(event) {
  }


  /**
   * 获取当前告警设置列表信息
   */
  refreshData() {
    this.tableConfig.isLoading = true;
    this.$alarmService.queryAlarmSetList(this.queryCondition).subscribe((res: Result) => {
      this.pageBean.Total = res.totalCount;
      this.tableConfig.isLoading = false;
      this._dataSet = res.data.map(item => {
        item.defaultStyle = this.$alarmStoreService.getAlarmColorByLevel(item.alarmDefaultLevel);
        item.style = this.$alarmStoreService.getAlarmColorByLevel(item.alarmLevel);
        item.alarmName = getAlarmType(this.$nzI18n, item.alarmCode);
        return item;
      });
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   * 表格配置初始化
   */
  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      primaryKey: '02-3-1',
      showSearchSwitch: false,
      showSizeChanger: true,
      scroll: {x: '1000px', y: '600px'},
      noIndex: true,
      columnConfig: [
        {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 62},
        {
          type: 'serial-number', width: 62, title: this.language.serialNumber,
          fixedStyle: {fixedLeft: true, style: {left: '60px'}}
        },
        {
          title: this.language.alarmName, key: 'alarmName', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'},
          fixedStyle: {fixedLeft: true, style: {left: '122px'}}
        },
        {
          title: this.language.alarmDefaultLevel, key: 'alarmDefaultLevel', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'},
          type: 'render',
          renderTemplate: this.alarmDefaultLevelTemp,
        },
        {
          title: this.language.alarmLevel, key: 'alarmLevel', width: 200,
          configurable: true,
          searchable: true,
          isShowSort: true,
          type: 'render',
          renderTemplate: this.alarmLevelTemp,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.alarmAutomaticConfirmation, key: 'alarmAutomaticConfirmation', width: 200,
          configurable: true,
          searchable: true,
          isShowSort: true,
          type: 'render',
          renderTemplate: this.alarmConfirmTemp,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.operate,
          searchConfig: {type: 'operate'}, key: '', width: 80, fixedStyle: {fixedRight: true, style: {right: '0px'}}
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
          permissionCode: '02-3-1-1',
          className: 'iconfont fiLink-edit',
          handle: (data) => {
            this.queryAlarmLevel();
            this.alarmId = data.id;
            this.$alarmService.queryAlarmLevelSetById(this.alarmId).subscribe((res: Result) => {
              const d = res.data[0];
              this.alarmName = getAlarmType(this.$nzI18n, d.alarmCode);
              d.alarmName = this.alarmName;
              // if (d.alarmDefaultLevel === '1') {
              //   this.alarmDefaultLevel = this.language.urgent;
              // }
              // if (d.alarmDefaultLevel === '2') {
              //   this.alarmDefaultLevel = this.language.main;
              // }
              // if (d.alarmDefaultLevel === '3') {
              //   this.alarmDefaultLevel = this.language.secondary;
              // }
              // if (d.alarmDefaultLevel === '4') {
              //   this.alarmDefaultLevel = this.language.prompt;
              // }
              this.alarmDefaultLevel = getAlarmLevel(this.$nzI18n, d.alarmDefaultLevel);
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
        openChange: (a, b, c) => {
        },
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
        openChange: (a, b, c) => {
        },
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
        modelChange: (controls, $event, key) => {
        },
        openChange: (a, b, c) => {
        },
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
            {label: this.language.yes, value: '1'},
            {label: this.language.no, value: '0'},
          ],
          label: 'label',
          value: 'value'
        },
        modelChange: (controls, $event, key, formOperate: FormOperate) => {
        },
        rule: [],
        asyncRules: []
      },
    ];
  }

  /**
   * 告警编辑弹窗表单实例
   * param event
   */
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
    this.isLoading = true;
    this.$alarmService.updateAlarmCurrentSet(editData).subscribe((res: Result) => {
      this.isLoading = false;
      if (res.code === 0) {
        this.$message.success(res.msg);
        this.isVisibleEdit = false;
        this.refreshData();
      } else {
        this.$message.error(res.msg);
      }

    }, () => {
      this.isLoading = false;
    });
  }

  /**
   * 告警编辑取消
   */
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
          this.alarmLevelArray.push(
            {'label': getAlarmLevel(this.$nzI18n, res['data'][i]['alarmLevelCode']), 'value': res['data'][i]['alarmLevelCode']}
          );

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

}

