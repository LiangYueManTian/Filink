import {AfterViewInit, Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {MapSelectorService} from '../map-selector.service';
import {MapDrawingService} from '../map-drawing.service';
import {GMapSelectorService} from '../g-map-selector.service';
import {FiLinkModalService} from '../../../service/filink-modal/filink-modal.service';
import {CommonLanguageInterface} from '../../../../../assets/i18n/common/common.language.interface';
import {NzI18nService} from 'ng-zorro-antd';

declare const BMap: any;
declare const BMapLib: any;
declare const BMAP_ANCHOR_TOP_LEFT: any;
declare const BMAP_ANCHOR_TOP_RIGHT: any;

@Component({
  selector: 'xc-map-component',
  templateUrl: './map-component.component.html',
  styleUrls: ['./map-component.component.scss']
})
export class MapComponentComponent implements OnInit, AfterViewInit, OnChanges {
  mapInstance;
  mapDrawUtil: MapDrawingService;
  mapService: MapSelectorService | GMapSelectorService;
  overlays;
  searchKey;
  language: CommonLanguageInterface;
  @Input()
  point;
  @Input()
  facilityAddress;
  @Output() xcVisibleChange = new EventEmitter<boolean>();
  @Output() selectDataChange = new EventEmitter<any[]>();
  _xcVisible = false;
  public mapType: string;
  public address: any = {
    address: '',
    point: {lat: '', lng: ''},
    addressComponents: {
      province: '',
      city: '',
      district: ''
    }
  };

  get xcVisible() {
    return this._xcVisible;
  }

  @Input()
  set xcVisible(params) {
    this._xcVisible = params;
    this.xcVisibleChange.emit(this._xcVisible);
  }

  constructor(private $message: FiLinkModalService,
              private $i18n: NzI18nService) {
  }

  ngOnInit() {
    this.mapType = localStorage.getItem('mapType');
    this.language = this.$i18n.getLocaleData('common');
  }

  ngOnChanges(changes: SimpleChanges): void {
  }

  ngAfterViewInit(): void {

  }

  afterModelOpen() {
    if (!this.mapInstance) {
      this.initMap();

    }
    if (this.point && this.point.lat && this.point.lng) {
      if (this.overlays) {
        this.mapService.clearOverlay(this.overlays);
      }
      const marker = this.mapService.createPoint(this.point.lng, this.point.lat);
      this.mapService.setCenterPoint(this.point.lat, this.point.lng);
      this.overlays = this.mapService.addOverlay(marker);
      this.mapService.getLocation(this.overlays, (result) => {
        this.address = result;
        // 组件初始化地图使用外界传入的地址
        this.address.address = this.facilityAddress;
      });
    }
  }

  afterModelClose() {

  }

  handleOk() {
    this.selectDataChange.emit(this.address);
    this.handleCancel();
  }

  handleCancel() {
    this.xcVisible = false;
  }

  location() {
    console.log(this.searchKey);
    const key = this.searchKey.trimLeft().trimRight();
    if (!key) {
      return;
    }
    this.mapService.searchLocation(key, (results, status) => {
      if (status === 'OK') {
        this.mapInstance.setCenter(results[0].geometry.location);
      } else {
        // this.$message.error('无结果');
      }
    });
  }

  private initMap() {
    // 实例化地图服务类
    if (this.mapType === 'baidu') {
      this.mapService = new MapSelectorService('mapContainer');
    } else {
      this.mapService = new GMapSelectorService('mapContainer');
    }
    // 没有传点的时候 定位到用户当前的位置
    if (this.point && this.point.lat && this.point.lng) {
    } else {
      this.mapService.locateToUserCity();
    }
    this.mapInstance = this.mapService.mapInstance;
    // 实例化鼠标绘制工具
    this.mapDrawUtil = this.mapService.mapDrawUtil;
    this.mapDrawUtil.open();
    // 添加鼠标绘制工具监听事件，用于获取绘制结果
    this.mapDrawUtil.addEventListener('overlaycomplete', (e) => {
      if (this.overlays) {
        this.mapService.clearOverlay(this.overlays);
      }
      this.overlays = e.overlay;
      this.mapService.getLocation(this.overlays, (result) => {
        this.address = result;
      });
    });
  }
}
