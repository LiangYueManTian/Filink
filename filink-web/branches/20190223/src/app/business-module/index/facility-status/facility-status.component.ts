import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FacilityName} from '../map/facility-name';
import {MapStoreService} from '../../../core-module/store/map.store.service';
import {NzI18nService} from 'ng-zorro-antd';

@Component({
  selector: 'app-facility-status',
  templateUrl: './facility-status.component.html',
  styleUrls: ['./facility-status.component.scss']
})
export class FacilityStatusComponent extends FacilityName implements OnInit {
  list = [];
  isFacilityStatusAllChecked;
  @Output() facilityStatusEvent = new EventEmitter();
  constructor(
    private $mapStoreService: MapStoreService,
    public $nzI18n: NzI18nService,
  ) {
    super($nzI18n);
  }

  ngOnInit() {
    this.isFacilityStatusAllChecked = true;
    this.list = this.facilityStatusListArr;
  }

  fold() {
    this.facilityStatusEvent.emit({type: 'close'});
  }

  /**
   * 设施状态过滤
   * event
   */
  facilityStatusChange(event, id) {
    const arr = [];
    if (id === 0) {
      if (event) {  // 全选
        this.list.forEach(item => {
          item.checked = true;
        });
      } else {  // 全不选
        this.list.forEach(item => {
          item.checked = false;
        });
      }
    } else {
      this.isFacilityStatusAllChecked = this.list.every(item => item.checked);
    }
    this.$mapStoreService.facilityStatusList = this.list;
    this.facilityStatusEvent.emit({type: 'update'});
    // this.selectedFacilityStatusArr = arr;
    // this.updateMarkers();
    // this.closeFacilityPanel();
    // this.closeOverlayInfoWindow();
  }

}
