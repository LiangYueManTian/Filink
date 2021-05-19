import {Component, Input, OnInit} from '@angular/core';
import {NzI18nService} from 'ng-zorro-antd';
import {Router} from '@angular/router';
import {IndexTable} from '../index.table';
import {Result} from '../../../shared-module/entity/result';
import {PictureViewService} from '../../../core-module/api-service/facility/picture-view-manage/picture-view.service';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';
import {ImageViewService} from '../../../shared-module/service/picture-view/image-view.service';

@Component({
  selector: 'app-facility-alarm-panel',
  templateUrl: './facility-alarm-panel.component.html',
  styleUrls: ['./facility-alarm-panel.component.scss']
})
export class FacilityAlarmPanelComponent extends IndexTable implements OnInit {
  @Input() facilityId: string;

  constructor(public $nzI18n: NzI18nService,
              private $router: Router,
              private $message: FiLinkModalService,
              private $pictureViewService: PictureViewService,
              private $imageViewService: ImageViewService,
              ) {
    super($nzI18n);
  }

  ngOnInit() {
  }

  /**
   * 告警回传  查看图片
   */
  alarmEvent(event) {
    if (event.type === 'photoView') {
      this.getPicUrlByProcId(event.id);
    }
  }

  /**
   * 查看图片
   * param ids
   */
  getPicUrlByProcId(id) {
    this.$pictureViewService.getPicUrlByAlarmId(id).subscribe((result: Result) => {
      if (result.code === 0) {
        if (result.data.length === 0) {
          this.$message.warning(this.commonLanguage.noPictureNow);
        } else {
          this.$imageViewService.showPictureView(result.data);
        }
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * 跳转至当前告警列表，并过滤出该设施的告警
   */
  goToCurrentAlarmList() {
    this.$router.navigate([`/business/alarm/current-alarm`], {queryParams: {deviceId: this.facilityId}}).then();
  }

  /**
   * 跳转至历史告警列表，并过滤出该设施的告警
   */
  goToHistoryAlarmList() {
    this.$router.navigate([`/business/alarm/history-alarm`], {queryParams: {deviceId: this.facilityId}}).then();
  }
}
