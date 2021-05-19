import {Component, OnInit, ViewChild} from '@angular/core';
import {NzI18nService} from 'ng-zorro-antd';
import {Result} from '../../../../shared-module/entity/result';
import {ClearBarrierService} from '../../../../core-module/api-service/work-order/clear-barrier';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {WorkOrderLanguageInterface} from '../../../../../assets/i18n/work-order/work-order.language.interface';
import {UnfinishedClearBarrierWorkOrderTableComponent} from './table';

@Component({
  selector: 'app-unfinished-clear-barrier-work-order',
  templateUrl: './unfinished-clear-barrier-work-order.component.html',
  styleUrls: ['./unfinished-clear-barrier-work-order.component.scss']
})
export class UnfinishedClearBarrierWorkOrderComponent implements OnInit {
  totalCount;   // 全部未完成工单数目
  assignedCount;  // 待指派工单数目
  pendingCount;  // 待处理工单数目
  processingCount;  // 处理中工单数目
  increaseCount;   // 新增数目
  workOrderLanguage: WorkOrderLanguageInterface;
  @ViewChild('table') table: UnfinishedClearBarrierWorkOrderTableComponent;

  constructor(public $nzI18n: NzI18nService,
              private $clearBarrierService: ClearBarrierService,
              private $message: FiLinkModalService,
  ) {
  }

  ngOnInit() {
    this.workOrderLanguage = this.$nzI18n.getLocaleData('workOrder');
    // this.getStatistics();
  }

  /**
   * 工单列表事件回传
   */
  workOrderEvent(event) {
    // this.getStatistics();
  }

  /**
   * 获取统计信息
   */
  getStatistics() {
    this.getTotalCount();
    this.getAssignedCount();
    this.getPendingCount();
    this.getProcessingCount();
    this.getIncreaseCount();
  }

  filterByStatus(status?) {   // 0 全部  1  待指派  2 待处理  3 处理中
    this.table.filterByStatus(status);
  }

  /**
   * 获取全部未完成工单数目
   */
  getTotalCount() {
    this.$clearBarrierService.getUnfinishedStatistics().subscribe((result: Result) => {
      if (result.code === 0) {
        this.totalCount = result.data;
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * 获取待指派工单数目
   */
  getAssignedCount() {
    this.$clearBarrierService.getAssignedStatistics().subscribe((result: Result) => {
      if (result.code === 0) {
        this.assignedCount = result.data;
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * 获取待处理工单数目
   */
  getPendingCount() {
    this.$clearBarrierService.getPendingStatistics().subscribe((result: Result) => {
      if (result.code === 0) {
        this.pendingCount = result.data;
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * 获取处理中工单数目
   */
  getProcessingCount() {
    this.$clearBarrierService.getProcessingStatistics().subscribe((result: Result) => {
      if (result.code === 0) {
        this.processingCount = result.data;
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * 获取新增工单数目
   */
  getIncreaseCount() {
    this.$clearBarrierService.getIncreaseStatistics().subscribe((result: Result) => {
      if (result.code === 0) {
        this.increaseCount = result.data;
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

}

