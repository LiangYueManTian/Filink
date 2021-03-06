import {AfterViewInit, Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {MapSelectorService} from '../map-selector.service';
import {MapDrawingService} from '../map-drawing.service';
import {GMapSelectorService} from '../g-map-selector.service';
import {FiLinkModalService} from '../../../service/filink-modal/filink-modal.service';
import {CommonLanguageInterface} from '../../../../../assets/i18n/common/common.language.interface';
import {NzI18nService} from 'ng-zorro-antd';
import {RuleUtil} from '../../../util/rule-util';
import {FormLanguageInterface} from '../../../../../assets/i18n/form/form.language.interface';
import {IndexLanguageInterface} from '../../../../../assets/i18n/index/index.language.interface';

declare const MAP_TYPE;

declare const BMap: any;
declare const BMapLib: any;
declare const BMAP_ANCHOR_TOP_LEFT: any;
declare const BMAP_ANCHOR_TOP_RIGHT: any;

/**
 * 地理位置选择器
 */
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
  searchKey: any = '';
  language: CommonLanguageInterface;
  formLanguage: FormLanguageInterface;
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

  indexLanguage: IndexLanguageInterface;
  options: string[] = [];

  constructor(private $message: FiLinkModalService,
              private $ruleUtil: RuleUtil,
              private $i18n: NzI18nService) {
  }

  ngOnInit() {
    this.mapType = MAP_TYPE;
    this.language = this.$i18n.getLocaleData('common');
    this.formLanguage = this.$i18n.getLocaleData('form');
    this.indexLanguage = this.$i18n.getLocaleData('index');
  }

  ngOnChanges(changes: SimpleChanges): void {
  }

  ngAfterViewInit(): void {

  }

  afterModelOpen() {
    this.searchKey = '';
    if (!this.mapInstance) {
      this.initMap();

    }
    if (this.overlays) {
      this.mapService.clearOverlay(this.overlays);
    }
    if (this.point && this.point.lat && this.point.lng) {
      const marker = this.mapService.createPoint(this.point.lng, this.point.lat);
      this.mapService.setCenterPoint(this.point.lat, this.point.lng);
      this.overlays = this.mapService.addOverlay(marker);
      this.mapService.getLocation(this.overlays, (result) => {
        this.address = result;
        // 组件初始化地图使用外界传入的地址
        this.address.address = this.facilityAddress;
      });
    } else {
      this.address = {
        address: '',
        point: {lat: '', lng: ''},
        addressComponents: {
          province: '',
          city: '',
          district: ''
        }
      };
    }
  }

  afterModelClose() {

  }

  handleOk() {
    // 详细地址名称必填
    if (!this.address.address) {
      this.$message.error(this.language.addressRequired);
      return;
    }
    // 详细地址名称非空校验
    if (/^\s+$/.test(this.address.address)) {
      this.$message.error(this.$ruleUtil.getNameCustomRule().msg);
      return;
    }
    // 详情地址名称正则校验
    if (!new RegExp(this.$ruleUtil.getNameRule().pattern).test(this.address.address)) {
      this.$message.error(this.$ruleUtil.getNameRule().msg);
      return;
    }
    // 详细地址名称长度校验
    if (this.address.address.length > this.$ruleUtil.getRemarkMaxLengthRule().maxLength) {
      this.$message.error(
        `${this.formLanguage.maxLengthMsg}${this.$ruleUtil.getRemarkMaxLengthRule().maxLength}${this.formLanguage.count}`);
      return;
    }
    this.selectDataChange.emit(this.address);
    this.handleCancel();
  }

  handleCancel() {
    this.xcVisible = false;
  }

  location() {
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

  zoomOut() {
    this.mapService.zoomIn();
  }

  zoomIn() {
    this.mapService.zoomOut();
  }

  public initMap() {
    // 实例化地图服务类
    if (this.mapType === 'baidu') {
      this.mapService = new MapSelectorService('mapContainer');
      this.mapService.addLocationSearchControl('_suggestId', '_searchResultPanel');
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
