import {
  Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, TemplateRef,
  ViewChild
} from '@angular/core';
import {Router} from '@angular/router';
import {Result} from '../../../../shared-module/entity/result';
import {NzI18nService} from 'ng-zorro-antd';
import {IndexTable} from '../../index.table';
import {AlarmStoreService} from '../../../../core-module/store/alarm.store.service';
import {AlarmService} from '../../../../core-module/api-service/alarm';

@Component({
  selector: 'app-current-alarm-table',
  templateUrl: './current-alarm-table.component.html',
  styleUrls: ['./current-alarm-table.component.scss']
})
export class CurrentAlarmTableComponent extends IndexTable implements OnInit, OnChanges {
  @Input() facilityId: string;
  @Output() alarmEvent = new EventEmitter();
  @ViewChild('alarmNameTemp') alarmNameTemp: TemplateRef<any>;

  constructor(public $nzI18n: NzI18nService,
              private $alarmService: AlarmService,
              private $alarmStoreService: AlarmStoreService,
              private $router: Router, ) {
    super($nzI18n);
  }

  ngOnInit() {
    this.initTableConfig();
    this.refreshData();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes && changes.facilityId && changes.facilityId.previousValue) {
      this.createQueryConditions();
      this.refreshData();
    }
  }

  refreshData() {
    this.tableConfig.isLoading = true;
    this.$alarmService.queryAlarmDeviceId(this.facilityId).subscribe((result: Result) => {
      this.tableConfig.isLoading = false;
      if (result.code === 0) {
        this._dataSet = result.data.map(item => {
          item.style = this.$alarmStoreService.getAlarmColorByLevel(item.alarmFixedLevel);
          item.isShowWOrkOrderIcon = item.isOrder;
          item.isViewPhotoIcon = item.isPicture;
          return item;
        });
      } else {
      }
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   * 创建查询条件
   */
  createQueryConditions() {
    this.queryCondition.pageCondition = {
      pageNum: this.pageBean.pageIndex,
      pageSize: this.pageBean.pageSize
    };
    // 设施id过滤
    this.queryCondition.filterConditions = [
      {
        operator: 'eq',
        filterField: 'alarmSource',
        filterValue: this.facilityId
      }
    ];
  }

  /**
   * 跳转至对应告警
   * param id
   */
  goToCurrentAlarmById(id) {
    this.$router.navigate([`/business/alarm/current-alarm`], {queryParams: {id: id}}).then();
  }

  /**
   * 跳转至对应工单
   * param id
   */
  goToWorkOrderByAlarmId(id) {
    this.$router.navigate([`business/work-order/clear-barrier/unfinished`],
      {queryParams: {alarmId: id}}).then();
  }

  initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      noIndex: true,
      scroll: {x: '366px', y: '250px'},
      topButtons: [],
      columnConfig: [
        {
          title: this.indexLanguage.alarmName, key: 'alarmName', width: 96,
          searchable: false,
          type: 'render',
          renderTemplate: this.alarmNameTemp,
        },
        {
          title: this.indexLanguage.time, key: 'alarmNearTime', width: 90, pipe: 'date',
          searchable: false,
        },
        {
          title: this.indexLanguage.extraInfo, key: 'extraMsg', width: 60,
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
        {
          text: this.indexLanguage.viewWorkOrder,
          key: 'isShowWOrkOrderIcon',
          className: 'fiLink-work-order',
          handle: (currentIndex) => {
            this.goToWorkOrderByAlarmId(currentIndex.id);
          }
        },
        {
          text: this.indexLanguage.viewPhoto,
          key: 'isViewPhotoIcon',
          className: 'fiLink-view-photo',
          handle: (currentIndex) => {
            this.alarmEvent.emit({type: 'photoView', id: currentIndex.id});
          }
        },
      ]
    };
  }
}

