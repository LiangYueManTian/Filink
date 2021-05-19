import {
  Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, TemplateRef,
  ViewChild
} from '@angular/core';
import {Router} from '@angular/router';
import {Result} from '../../../../shared-module/entity/result';
import {NzI18nService} from 'ng-zorro-antd';
import {IndexTable} from '../../index.table';
import {ClearBarrierService} from '../../../../core-module/api-service/work-order/clear-barrier';
import {ImageViewService} from '../../../../shared-module/service/picture-view/image-view.service';
import {WORK_ORDER_STATUS_CLASS} from '../../../../shared-module/const/work-order';

@Component({
  selector: 'app-clear-barrier-table',
  templateUrl: './clear-barrier-table.component.html',
  styleUrls: ['./clear-barrier-table.component.scss']
})
export class ClearBarrierTableComponent extends IndexTable implements OnInit, OnChanges {
  @Input() facilityId: string;
  @Output() alarmEvent = new EventEmitter();
  @ViewChild('statusTemp') statusTemp: TemplateRef<any>;
  constructor(
    public $nzI18n: NzI18nService,
    private $router: Router,
    private $clearBarrierService: ClearBarrierService,
    private $imageViewService: ImageViewService,
  ) {
    super($nzI18n);
  }

  ngOnInit() {
    this.initTableConfig();
    this.refreshData();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes && changes.facilityId && changes.facilityId.previousValue) {
      this.refreshData();
    }
  }

  /**
   * 跳转至对应告警
   * param id
   */
  goToCurrentAlarmById(id) {
    this.$router.navigate([`/business/alarm/current-alarm`], {queryParams: {id}}).then();
  }

  /**
   * 跳转至对应工单
   * param id
   */
  goToWorkOrderById(id) {
    this.$router.navigate([`business/work-order/clear-barrier/unfinished`], {queryParams: {id}}).then();
  }

  refreshData() {
    // this.createQueryConditions();
    this.tableConfig.isLoading = true;
    this.$clearBarrierService.getFiveWorkOrder(this.facilityId).subscribe((result: Result) => {
      this.tableConfig.isLoading = false;
      if (result.code === 0) {
        this._dataSet = result.data;
        this._dataSet.forEach(item => {
          item.statusName = this.getWorkOrderStatusName(item.status);
          item.statusClass = this.getStatusClass(item.status);
          item.isViewPhoto = item.devicePicRespList;
        });
      } else {
      }
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   * 获取工单类型样式
   * param status
   * returns {string}
   */
  getStatusClass(status) {
    return `iconfont icon-fiLink ${WORK_ORDER_STATUS_CLASS[status]}`;
  }

  /**
   * 创建查询条件
   */
  createQueryConditions() {
    this.queryCondition.pageCondition = {
      pageNum: this.pageBean.pageIndex,
      pageSize: this.pageBean.pageSize
    };

    // 时间降序
    this.queryCondition.sortCondition = {
      sortField: 'cTime',
      sortRule: 'desc'
    };

    // 设施id过滤
    this.queryCondition.bizCondition = {
      deviceIds: [this.facilityId]
    };
  }

  initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      scroll: {x: '366px', y: '250px'},
      topButtons: [],
      noIndex: true,
      columnConfig: [
        {
          title: this.workOrderLanguage.name, key: 'title', width: 76,
          searchable: false
        },
        {
          title: this.workOrderLanguage.status, key: 'statusName', width: 73,
          searchable: false,
          type: 'render',
          renderTemplate: this.statusTemp,
        },
        {
          title: this.workOrderLanguage.expectedCompleteTime, key: 'cTime', width: 72, pipe: 'date',
          searchable: false,
        },
        {
          title: this.workOrderLanguage.accountabilityUnitName, key: 'accountabilityDeptName', width: 70,
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
          text: this.indexLanguage.viewWorkOrder,
          className: 'fiLink-work-order',
          handle: (currentIndex) => {
            this.goToWorkOrderById(currentIndex.procId);
          }
        },
        {
          text: this.indexLanguage.viewAlarm,
          className: 'fiLink-alarm-facility',
          handle: (currentIndex) => {
            this.goToCurrentAlarmById(currentIndex.refAlarm);
          }
        },
        {
          text: this.indexLanguage.viewPhoto,
          key: 'isViewPhotoIcon',
          className: 'fiLink-view-photo',
          handle: (currentIndex) => {
            // this.alarmEvent.emit({type: 'photoView', id: currentIndex.procId});
            this.$imageViewService.showPictureView(currentIndex.devicePicRespList);
          }
        },
      ]
    };
  }
}
