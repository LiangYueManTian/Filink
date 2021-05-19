import {AfterViewInit, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FacilityService} from '../../../../../core-module/api-service/facility/facility-manage';
import {Result} from '../../../../../shared-module/entity/result';
import {FacilityInfo} from '../../../../../core-module/entity/facility/facility';
import {MapSelectorService} from '../../../../../shared-module/component/map-selector/map-selector.service';
import {GMapSelectorService} from '../../../../../shared-module/component/map-selector/g-map-selector.service';
import {Router} from '@angular/router';
import {getDeviceType} from '../../../facility.config';
import {DateHelperService, NzI18nService} from 'ng-zorro-antd';
import {FacilityLanguageInterface} from '../../../../../../assets/i18n/facility/facility.language.interface';
import {FiLinkModalService} from '../../../../../shared-module/service/filink-modal/filink-modal.service';
import {ImageViewService} from '../../../../../shared-module/service/picture-view/image-view.service';

declare const MAP_TYPE;

/**
 * 设施详情基础信息组件
 */
@Component({
  selector: 'app-infrastructure-details',
  templateUrl: './infrastructure-details.component.html',
  styleUrls: ['./infrastructure-details.component.scss']
})
export class InfrastructureDetailsComponent implements OnInit, AfterViewInit, OnDestroy {
  // 设施id
  @Input()
  deviceId: string;
  // 设施信息
  facilityInfo: FacilityInfo = new FacilityInfo();
  // 地图类型
  private mapType: string;
  // 地图服务
  private mapService: MapSelectorService | GMapSelectorService;
  // 设施点位置
  private point: any = {lat: 0, lng: 0};
  // 设施语言包
  public language: FacilityLanguageInterface;
  // 轮询实例
  private loopTimer;
  // 无数据图标地址
  devicePicUrl = 'assets/img/facility/no_img_6.svg';
  // 设施图标列表
  private devicePicList: any[];

  constructor(private $facilityService: FacilityService,
              private $modalService: FiLinkModalService,
              private $nzI18n: NzI18nService,
              private $dateHelper: DateHelperService,
              private $imageViewService: ImageViewService,
              private $router: Router) {
  }

  ngOnInit() {
    this.mapType = MAP_TYPE;
    this.language = this.$nzI18n.getLocaleData('facility');
    this.getFacilityInfo();
    this.getDevicePic();
  }

  /**
   *  页面初始化
   */
  ngAfterViewInit(): void {
    this.initMap();
  }

  /**
   * 获取设备信息
   */
  getFacilityInfo() {
    this.$facilityService.queryDeviceById(this.deviceId).subscribe((result: Result) => {
      if (result.code === 0) {
        this.facilityInfo = result.data;
        // 获取点信息
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
        // 翻译设施类型
        if (this.facilityInfo.deviceType) {
          this.facilityInfo.deviceType = getDeviceType(this.$nzI18n, this.facilityInfo.deviceType);
        }
      } else if (result.code === 130204) {
        this.$modalService.error(result.msg);
        window.history.go(-1);
      }
      // 开启定时器轮询
      if (!this.loopTimer) {
        this.loopTimer = window.setInterval(() => {
          this.getFacilityInfo();
        }, 60000);
      }
    });
  }

  /**
   * 点击图标跳转到首页
   */
  clickMap() {
    this.$router.navigate(['/business/index'], {queryParams: {id: this.deviceId}}).then();
  }

  /**
   * 初始化地图
   */
  private initMap() {
    // 实例化地图服务类
    if (this.mapType === 'baidu') {
      this.mapService = new MapSelectorService('thumbnail', true);
    } else {
      this.mapService = new GMapSelectorService('thumbnail', true);
    }

  }

  /**
   * 获取设施图标
   */
  private getDevicePic() {
    const body = {
      resource: '3', // 3为实景图
      deviceId: this.deviceId,
      picNum: '1' // 查询1张
    };
    this.$facilityService.picRelationInfo(body).subscribe((result: Result) => {
      if (result.code === 0 && result.data && result.data.length > 0) {
        this.devicePicList = result.data;
        this.devicePicUrl = result.data[0].picUrlBase;
      }
    });
  }

  /**
   * 点击图标
   */
  clickImage() {
    if (this.devicePicList && this.devicePicList.length > 0) {
      this.$imageViewService.showPictureView(this.devicePicList);
    }
  }

  /**
   * 组件销毁清除定时器
   */
  ngOnDestroy(): void {
    if (this.loopTimer) {
      window.clearInterval(this.loopTimer);
    }
  }
}

