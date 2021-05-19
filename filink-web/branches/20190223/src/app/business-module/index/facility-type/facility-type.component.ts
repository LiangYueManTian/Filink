import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {MapStoreService} from '../../../core-module/store/map.store.service';
import {FacilityName} from '../map/facility-name';
import {NzI18nService} from 'ng-zorro-antd';

@Component({
  selector: 'app-facility-type',
  templateUrl: './facility-type.component.html',
  styleUrls: ['./facility-type.component.scss']
})
export class FacilityTypeComponent extends FacilityName implements OnInit {
  list = [];
  isFacilityTypeAllChecked;
  @Output() facilityTypeEvent = new EventEmitter();

  constructor(
              private $mapStoreService: MapStoreService,
              public $nzI18n: NzI18nService,
              ) {
    super($nzI18n);
  }

  ngOnInit() {
    this.isFacilityTypeAllChecked = true;
    this.list = this.facilityTypeListArr;
    // this.facilityTypeList = this.$mapStoreService.facilityTypeList.map(item => {
    //   return {
    //     value: item.value,
    //     label: item.label,
    //     iconClass: item.iconClass,
    //     checked: bol ? true : item.checked,
    //   };
    // });
  }

  fold() {
    this.facilityTypeEvent.emit({type: 'close'});
  }

  /**
   * 设施类型过滤
   * event
   * id
   */
  facilityTypeChange(event, id) {
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
      this.isFacilityTypeAllChecked = this.list.every(item => item.checked);
    }
    this.list.forEach(item => {
      if (item.checked) {
        arr.push(item.value + '');
      }
    });
    this.$mapStoreService.facilityTypeList = this.list;
    this.facilityTypeEvent.emit({type: 'update'});
    // this.selectedFacilityTypeIdsArr = arr;
    // this.updateMarkers();
    // this.closeFacilityPanel();
    // this.closeOverlayInfoWindow();
  }

  openFacilityTypeSetting() {
    this.facilityTypeEvent.emit({type: 'setting'});
  }
}
