import {AfterViewInit, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FacilityService} from '../../../../../core-module/api-service/facility/facility-manage';
import {Result} from '../../../../../shared-module/entity/result';
import {FacilityInfo} from '../../../../../core-module/entity/facility/facility';
import {MapSelectorService} from '../../../../../shared-module/component/map-selector/map-selector.service';
import {GMapSelectorService} from '../../../../../shared-module/component/map-selector/g-map-selector.service';
import {Router} from '@angular/router';
import {DATE_FORMAT_STR, getDeviceType} from '../../../facility.config';
import {NzI18nService} from 'ng-zorro-antd';
import {FacilityLanguageInterface} from '../../../../../../assets/i18n/facility/facility.language.interface';
import {FiLinkModalService} from '../../../../../shared-module/service/filink-modal/filink-modal.service';

@Component({
  selector: 'app-infrastructure-details',
  templateUrl: './infrastructure-details.component.html',
  styleUrls: ['./infrastructure-details.component.scss']
})
export class InfrastructureDetailsComponent implements OnInit, AfterViewInit, OnDestroy {
  infoData = [];
  @Input()
  deviceId: string;
  facilityInfo: FacilityInfo = new FacilityInfo();
  private mapType: string;
  private mapService: MapSelectorService | GMapSelectorService;
  private point: any = {lat: 0, lng: 0};
  public language: FacilityLanguageInterface;
  public currentTime;
  private loopTimer;

  constructor(private $facilityService: FacilityService,
              private $modalService: FiLinkModalService,
              private $nzI18n: NzI18nService,
              private $router: Router) {
  }

  ngOnInit() {
    this.mapType = localStorage.getItem('mapType');
    this.language = this.$nzI18n.getLocaleData('facility');
    this.getFacilityInfo();
  }

  ngAfterViewInit(): void {
    this.initMap();
  }

  /**
   * 获取设备信息
   */
  getFacilityInfo() {
    this.$facilityService.queryDeviceById(this.deviceId).subscribe((result: Result) => {
      if (result.code === 0) {
        this.currentTime = this.$nzI18n.formatDateCompatible(new Date(), DATE_FORMAT_STR);
        this.facilityInfo = result.data;
        if (this.facilityInfo.positionBase) {
          const position = this.facilityInfo.positionBase.split(',');
          const _lng = parseFloat(position[0]);
          const _lat = parseFloat(position[1]);
          this.point.lat = _lat;
          this.point.lng = _lng;
          const marker = this.mapService.createMarker(this.facilityInfo);
          this.mapService.setCenterPoint(this.point.lat, this.point.lng);
          this.mapService.addMarkerMap(marker);
        }
        this.infoData = [
          {label: `${this.language.infrastructureName} :`, value: this.facilityInfo.deviceName},
          {label: `${this.language.infrastructureCode} :`, value: this.facilityInfo.deviceCode},
          {label: `${this.language.deviceType_a} :`, value: getDeviceType(this.$nzI18n, this.facilityInfo.deviceType)},
          {
            label: `${this.language.provinceName}${this.language.cityName}${this.language.districtName} :`,
            value: `${this.facilityInfo.provinceName}${this.facilityInfo.cityName}${this.facilityInfo.districtName}`
          },
          {label: `${this.language.logicalArea} :`, value: this.facilityInfo.areaInfo ? this.facilityInfo.areaInfo.areaName : ''},
          {label: `${this.language.address} :`, value: this.facilityInfo.address},
          {
            label: `${this.language.accountabilityUnit} :`,
            value: this.facilityInfo.areaInfo ? this.facilityInfo.areaInfo.accountabilityUnitName : ''
          },
        ];
      } else if (result.code === 130204) {
        this.$modalService.error(result.msg);
        window.history.go(-1);
      }
      // todo 二期备用
      // if (!this.loopTimer) {
      //   this.loopTimer = window.setInterval(() => {
      //     this.getFacilityInfo();
      //   }, 3000);
      // }
    });
  }

  clickMap() {
    this.$router.navigate(['/business/index'], {queryParams: {id: this.deviceId}}).then();
  }

  private initMap() {
// 实例化地图服务类
    if (this.mapType === 'baidu') {
      this.mapService = new MapSelectorService('thumbnail', true);
      // this.mapService.locateToUserCity();
    } else {
      this.mapService = new GMapSelectorService('thumbnail', true);
    }

  }

  ngOnDestroy(): void {
    if (this.loopTimer) {
      window.clearInterval(this.loopTimer);
    }
  }
}

