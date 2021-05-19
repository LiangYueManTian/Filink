import { Component, OnInit , Output , TemplateRef, EventEmitter, ViewChild} from '@angular/core';
import {PageBean} from '../../../../../shared-module/entity/pageBean';
import {TableConfig} from '../../../../../shared-module/entity/tableConfig';
import {AlarmService} from '../../../../../core-module/api-service/alarm';
import {AlarmLanguageInterface} from '../../../../../../assets/i18n/alarm/alarm-language.interface';
import {DateHelperService, NzI18nService} from 'ng-zorro-antd';
import {Result} from '../../../../../shared-module/entity/result';
import { Router} from '@angular/router';
import {FiLinkModalService} from '../../../../../shared-module/service/filink-modal/filink-modal.service';
import {QueryCondition, SortCondition} from '../../../../../shared-module/entity/queryCondition';
import {DateFormatString} from '../../../../../shared-module/entity/dateFormatString';

@Component({
  selector: 'app-template-table',
  templateUrl: './template-table.component.html',
  styleUrls: ['./template-table.component.scss']
})
export class TemplateTableComponent implements OnInit {

  public _dataSettemplate = [];
  public pageBeantemplate: PageBean = new PageBean(100, 1, 1);
  public tableConfigTemplate: TableConfig;
  public language: AlarmLanguageInterface;
  display = {
    templateTable: true
  };
  filterEvent;
  queryCondition: QueryCondition = new QueryCondition();
  _selectedAlarm;

  @ViewChild('radioTemp') radioTemp: TemplateRef<any>;

  @Output() resultAndClose = new EventEmitter();

  constructor(
    public $router: Router,
    public $nzI18n: NzI18nService,
    public $alarmService: AlarmService,
    public $message: FiLinkModalService,
    private $dateHelper: DateHelperService,
  ) { }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('alarm');
    this.templateListConfig();
    this.queryTemplateData();
  }

  colsePopUp() {
    this.display.templateTable = false;
    this.resultAndClose.emit();
  }

  pageTemplateChange(event) {
    this.queryCondition.filterConditions = this.filterEvent;
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.queryTemplateData();
  }

  // 点击确认
  okText() {
    this.resultAndClose.emit(this._selectedAlarm);
  }

  /**
   * 选择列表数据
   * param event
   * param data
   */
  selectedAlarmChange(event, data) {
    this._selectedAlarm = data;
  }

  // 获取模板列表信息
  queryTemplateData() {
    return new Promise((resolve, reject) => {
      this.$alarmService.queryAlarmTemplateList(this.queryCondition).subscribe((res: Result) => {
        if ( res.code === 0 ) {
          if ( res.data && res.data.length ) {
            this._dataSettemplate = res.data.map(item => {
              if (item.createTime) {
                item.createTime = this.$dateHelper.format(new Date(Number(item.createTime)), DateFormatString.DATE_FORMAT_STRING);
              }
              return item;
           });
          } else {
            this._dataSettemplate = [];
          }

        }
        resolve();
      });
    });
  }

  // 模板列表
  templateListConfig() {
    this.tableConfigTemplate = {
      isDraggable: true,
      isLoading: false,
      // showSearchSwitch: true,
      showSizeChanger: true,
      noIndex: true,
      notShowPrint: true,
      scroll: {x: '800px', y: '300px'},
      columnConfig: [
        // {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 32},
        {
          title: '',
          type: 'render',
          renderTemplate: this.radioTemp,
          fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 42
        },
        {
          type: 'serial-number', width: 52, title: this.language.serialNumber,
          fixedStyle: {fixedLeft: true, style: {left: '12px'}}
        },
        {
          // 模板名称
          title: this.language.templateName, key: 'templateName', width: 100, isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'},
          fixedStyle: {fixedLeft: true, style: {left: '24px'}}
        },
        {
          // 创建时间
          title: this.language.createTime, key: 'createTime', width: 200, isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          // 创建用户
          title: this.language.createUser, key: 'createUser', width: 100, isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.remark, key: 'remark', width: 100, isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.operate, searchable: true,
          searchConfig: {type: 'operate'}, key: '', width: 80, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      // showPagination: true,
      bordered: false,
      showSearch: false,
      searchReturnType: 'Object',
      topButtons: [
        {
          // 新增
          text:  '+    ' + this.language.add,
          handle: () => {
            this.$router.navigate(['business/alarm/current-alarm/add']).then();
          }
        }
      ],
      operation: [
        {
          // 编辑
          text: this.language.update,
          className: 'fiLink-edit',
          handle: (currentIndex) => {
            this.$router.navigate(['business/alarm/current-alarm/update'], {
              queryParams: { id: currentIndex.id }
            }).then();
          }
        },
        {
          text: this.language.deleteHandle,
          needConfirm: true,
          className: 'fiLink-delete red-icon',
          handle: (data) => {
            // if (data.status === '1') {
            //   this.$message.warning('开启状态禁止删除');
            // } else {
              const ids = data.id;
              if (ids) {
                // this.delTemplate([ids]);
                this.$alarmService.deleteAlarmTemplateList([ids]).subscribe((res: Result) => {
                    if (res.code === 0) {
                      this.queryTemplateData();
                      this.$message.success(res.msg);
                    } else {
                      this.$message.info(res.msg);
                    }
                });
              }
            // }
          }
        },
      ],
      leftBottomButtons: [],
      sort: (event: SortCondition) => {
        this.queryCondition.filterConditions = this.filterEvent;
        this.queryCondition.bizCondition.sortField = event.sortField;
        this.queryCondition.bizCondition.sortRule = event.sortRule;
        this.queryTemplateData();
      },
    };
  }
}
