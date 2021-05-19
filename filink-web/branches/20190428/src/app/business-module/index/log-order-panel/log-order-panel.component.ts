import {Component, Input, OnInit} from '@angular/core';
import {NzI18nService} from 'ng-zorro-antd';
import {Router} from '@angular/router';
import {IndexTable} from '../index.table';

@Component({
  selector: 'app-log-order-panel',
  templateUrl: './log-order-panel.component.html',
  styleUrls: ['./log-order-panel.component.scss']
})
export class LogOrderPanelComponent extends IndexTable implements OnInit {
  @Input() facilityId: string;

  constructor(
    public $nzI18n: NzI18nService,
    private $router: Router,
  ) {
    super($nzI18n);
  }

  ngOnInit() {
  }


  /**
   * 跳转至设施日志列表
   */
  goToFacilityLogList() {
    this.$router.navigate([`/business/facility/facility-log`], {queryParams: {id: this.facilityId}}).then();
  }

  /**
   * 跳转至未完工销障工单列表
   */
  goToClearBarrierList() {
    this.$router.navigate([`/business/work-order/clear-barrier/unfinished`], {queryParams: {deviceId: this.facilityId}}).then();
  }

  /**
   * 跳转至未完工巡检工单列表
   */
  goToInspectionList() {
    this.$router.navigate([`/business/work-order/inspection/unfinished`], {queryParams: {deviceId: this.facilityId}}).then();
  }
}
