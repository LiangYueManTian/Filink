import {Component, Input, OnChanges, OnInit, SimpleChanges, TemplateRef, ViewChild} from '@angular/core';
import {PageBean} from '../../../shared-module/entity/pageBean';
import {TableConfig} from '../../../shared-module/entity/tableConfig';
import {NzI18nService} from 'ng-zorro-antd';
import {Router} from '@angular/router';
import {CommonLanguageInterface} from '../../../../assets/i18n/common/common.language.interface';
import {AlarmService} from '../../../core-module/api-service/alarm';
import {Result} from '../../../shared-module/entity/result';
import {IndexLanguageInterface} from '../../../../assets/i18n/index/index.language.interface';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';
import {AlarmStoreService} from '../../../core-module/store/alarm.store.service';

@Component({
  selector: 'app-facility-alarm-panel',
  templateUrl: './facility-alarm-panel.component.html',
  styleUrls: ['./facility-alarm-panel.component.scss']
})
export class FacilityAlarmPanelComponent implements OnInit, OnChanges {
  @Input() facilityId: string;
  public indexLanguage: IndexLanguageInterface;
  public commonLanguage: CommonLanguageInterface;
  pageBean: PageBean = new PageBean(5, 1, 0);
  currentAlarmDataSet = [];
  historyAlarmDataSet = [];
  currentAlarmTableConfig: TableConfig;
  historyAlarmTableConfig: TableConfig;
  currentAlarmQueryConditions = {
    pageCondition: {},
    filterConditions: [],
    sortCondition: {},
    bizCondition: {}
  };
  historyAlarmQueryConditions = {
    pageCondition: {},
    filterConditions: [],
    sortCondition: {},
    bizCondition: {}
  };
  @ViewChild('alarmNameTemp') alarmNameTemp: TemplateRef<any>;
  constructor(
    private $nzI18n: NzI18nService,
    private $router: Router,
    public $alarmService: AlarmService,
    public $alarmStoreService: AlarmStoreService,
    private $message: FiLinkModalService,
  ) { }

  ngOnInit() {
    this.indexLanguage = this.$nzI18n.getLocaleData('index');
    this.commonLanguage = this.$nzI18n.getLocaleData('common');
    this.createQueryConditions();
    this.initCurrentAlarmTableConfig();
    this.initHistoryAlarmTableConfig();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes && changes.facilityId && changes.facilityId.previousValue) {
      console.log(this.facilityId);
      this.createQueryConditions();
      this.getCurrentAlarm();
      this.getHistoryAlarm();
    }
  }

  /**
   * 初始化当前告警表格配置
   */
  initCurrentAlarmTableConfig() {
    this.currentAlarmTableConfig = {
      isDraggable: true,
      isLoading: false,
      noIndex: true,
      scroll: {x: '400px', y: '250px'},
      topButtons: [],
      columnConfig: [
        {
          title: this.indexLanguage.alarmName, key: 'alarmName', width: 80,
          searchable: false,
          type: 'render',
          renderTemplate: this.alarmNameTemp,
        },
        {
          title: this.indexLanguage.time, key: 'alarmNearTime', width: 80, pipe: 'date',
          searchable: false,
        },
        {
          title: this.indexLanguage.extraInfo, key: 'extraMsg',
          searchable: false,
        },
        {
          title: this.commonLanguage.operate, searchable: false,
          searchConfig: {type: 'operate'}, key: '', width: 100, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: false,
      bordered: false,
      showSearch: false,
      operation: [
        {
          text: this.indexLanguage.viewAlarm,
          className: 'fiLink-alarm-facility',
          handle: (currentIndex) => {
            this.goToCurrentAlarmById(currentIndex.id);
          }
        },
        // {
        //   text: this.indexLanguage.viewWorkOrder,
        //   className: 'fiLink-work-order',
        //   handle: () => {
        //   }
        // },
        // {
        //   text: this.indexLanguage.viewPhoto,
        //   className: 'fiLink-view-photo',
        //   handle: () => {
        //   }
        // },
      ],
      sort: (e) => {
        console.log(e);
      },
      handleSearch: (event) => {
        console.log(event);
      }
    };
    this.getCurrentAlarm();
  }

  /**
   * 初始化历史告警表格配置
   */
  initHistoryAlarmTableConfig() {
    this.historyAlarmTableConfig = {
      isDraggable: true,
      isLoading: false,
      scroll: {x: '400px', y: '250px'},
      topButtons: [],
      noIndex: true,
      columnConfig: [
        {
          title: this.indexLanguage.alarmName, key: 'alarmName', width: 80,
          searchable: false,
          type: 'render',
          renderTemplate: this.alarmNameTemp,
        },
        {
          title: this.indexLanguage.happenTime, key: 'alarmBeginTime', width: 80, pipe: 'date',
          searchable: false,
        },
        {
          title: this.indexLanguage.handleTime, key: 'alarmCleanTime', pipe: 'date',
          searchable: false,
        },
        {
          title: this.commonLanguage.operate, searchable: false,
          searchConfig: {type: 'operate'}, key: '', width: 100, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: false,
      bordered: false,
      showSearch: false,
      operation: [
        {
          text: this.indexLanguage.viewAlarm,
          className: 'fiLink-alarm-facility',
          handle: (currentIndex) => {
            this.goToHistoryAlarmById(currentIndex.id);
          }
        },
        // {
        //   text: this.indexLanguage.viewWorkOrder,
        //   className: 'fiLink-work-order',
        //   handle: () => {
        //   }
        // },
        // {
        //   text: this.indexLanguage.viewPhoto,
        //   className: 'fiLink-view-photo',
        //   handle: () => {
        //   }
        // },
      ],
      sort: (e) => {
        console.log(e);
      },
      handleSearch: (event) => {
        console.log(event);
      }
    };
    this.getHistoryAlarm();
  }

  /**
   * 获取当前告警
   */
  getCurrentAlarm() {
    this.currentAlarmTableConfig.isLoading = true;
    this.$alarmService.queryAlarmDeviceId(this.facilityId).subscribe((result: Result) => {
      this.currentAlarmTableConfig.isLoading = false;
      this.currentAlarmDataSet = result.data.map(item => {
        item.style = this.$alarmStoreService.getAlarmColorByLevel(item.alarmFixedLevel);
        return item;
      });
    }, () => {
      this.historyAlarmTableConfig.isLoading = false;
    });
  }

  /**
   * 获取历史告警
   */
  getHistoryAlarm() {
    this.historyAlarmTableConfig.isLoading = true;
    this.$alarmService.queryAlarmHistoryList(this.historyAlarmQueryConditions).subscribe((result: Result) => {
      this.historyAlarmTableConfig.isLoading = false;
      this.historyAlarmDataSet = result.data.map(item => {
        item.style = this.$alarmStoreService.getAlarmColorByLevel(item.alarmFixedLevel);
        return item;
      });
    }, () => {
      this.historyAlarmTableConfig.isLoading = false;
    });
  }

  pageChange(event) {
    console.log(event);
  }

  /**
   * 创建查询条件
   */
  createQueryConditions() {
    this.currentAlarmQueryConditions.pageCondition = {
      pageNum: this.pageBean.pageIndex,
      pageSize: this.pageBean.pageSize
    };

    // 时间降序
    // this.currentAlarmQueryConditions.sortCondition = {
    //   sortField: 'alarmBeginTime',
    //   sortRule: 'desc'
    // };

    // 设施id过滤
    this.currentAlarmQueryConditions.filterConditions = [
      {
        operator: 'eq',
        filterField: 'alarmSource',
        filterValue: this.facilityId
      }
    ];

    this.historyAlarmQueryConditions.pageCondition = {
      pageNum: this.pageBean.pageIndex,
      pageSize: this.pageBean.pageSize
    };

    // 时间降序
    this.historyAlarmQueryConditions.sortCondition = {
      sortField: 'alarmCleanTime',
      sortRule: 'desc'
    };

    // 设施id过滤
    this.historyAlarmQueryConditions.filterConditions = [
      {
        operator: 'eq',
        filterField: 'alarmSource',
        filterValue: this.facilityId
      }
    ];
  }

  goToCurrentAlarmList() {
    this.$router.navigate([`/business/alarm/current-alarm`], {queryParams: {deviceId: this.facilityId}}).then();
  }

  goToCurrentAlarmById(id) {
    this.$router.navigate([`/business/alarm/current-alarm`], {queryParams: {id}}).then();
  }

  goToHistoryAlarmList() {
    this.$router.navigate([`/business/alarm/history-alarm`], {queryParams: {deviceId: this.facilityId}}).then();
  }

  goToHistoryAlarmById(id) {
    this.$router.navigate([`/business/alarm/history-alarm`], {queryParams: {id}}).then();
  }
}
