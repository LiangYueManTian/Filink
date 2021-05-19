import {Component, Input} from '@angular/core';
import {NzI18nService} from 'ng-zorro-antd';
import {Router} from '@angular/router';
import {IndexTable} from '../index.table';
import {DeviceDetailCode} from '../../facility/facility.config';

/**
 * 日志和工单组件
 */
@Component({
  selector: 'app-log-order-panel',
  templateUrl: './log-order-panel.component.html',
  styleUrls: ['./log-order-panel.component.scss']
})
export class LogOrderPanelComponent extends IndexTable {
  // 设施id
  @Input() facilityId: string;
  // 权限code
  @Input() facilityPowerCode = [];
  // 权限码
  public powerCode = DeviceDetailCode;
  constructor(
    public $nzI18n: NzI18nService,
    private $router: Router,
  ) {
    super($nzI18n);
  }

  /**
   * 跳转
   */
  navigatorTo(url) {
    this.$router.navigate([url], {queryParams: {id: this.facilityId}}).then();
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
    this.$router.navigate([`/business/work-order/clear-barrier/unfinished-list`], {queryParams: {deviceId: this.facilityId}}).then();
  }

  /**
   * 跳转至未完工巡检工单列表
   */
  goToInspectionList() {
    this.$router.navigate([`/business/work-order/inspection/unfinished-list`], {queryParams: {deviceId: this.facilityId}}).then();
  }
}
