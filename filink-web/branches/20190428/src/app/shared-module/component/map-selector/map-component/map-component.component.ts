import {AfterViewInit, Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {MapSelectorService} from '../map-selector.service';
import {MapDrawingService} from '../map-drawing.service';
import {GMapSelectorService} from '../g-map-selector.service';
import {FiLinkModalService} from '../../../service/filink-modal/filink-modal.service';
import {CommonLanguageInterface} from '../../../../../assets/i18n/common/common.language.interface';
import {NzI18nService} from 'ng-zorro-antd';
import {RuleUtil} from '../../../util/rule-util';
import {FormLanguageInterface} from '../../../../../assets/i18n/form/form.language.interface';

declare const BMap: any;
declare const BMapLib: any;
declare const BMAP_ANCHOR_TOP_LEFT: any;
declare const BMAP_ANCHOR_TOP_RIGHT: any;
declare const MAP_TYPE;


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

  constructor(private $message: FiLinkModalService,
              private $ruleUtil: RuleUtil,
              private $i18n: NzI18nService) {
  }

  ngOnInit() {
    this.mapType = MAP_TYPE;
    this.language = this.$i18n.getLocaleData('common');
    this.formLanguage = this.$i18n.getLocaleData('form');
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
        // ????????????????????????????????????????????????
        this.address.address = this.facilityAddress;
      });
    }
  }

  afterModelClose() {

  }

  handleOk() {
    // ????????????????????????
    if (!this.address.address) {
      this.$message.error(this.language.addressRequired);
      return;
    }
    // ??????????????????????????????
    if (/^\s+$/.test(this.address.address)) {
      this.$message.error(this.$ruleUtil.getNameCustomRule().msg);
      return;
    }
    // ??????????????????????????????
    if (!new RegExp(this.$ruleUtil.getNameRule().pattern).test(this.address.address)) {
      this.$message.error(this.$ruleUtil.getNameRule().msg);
      return;
    }
    // ??????????????????????????????
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
    console.log(this.searchKey);
    const key = this.searchKey.trimLeft().trimRight();
    if (!key) {
      return;
    }
    this.mapService.searchLocation(key, (results, status) => {
      if (status === 'OK') {
        this.mapInstance.setCenter(results[0].geometry.location);
      } else {
        // this.$message.error('?????????');
      }
    });
  }

  private initMap() {
    // ????????????????????????
    if (this.mapType === 'baidu') {
      this.mapService = new MapSelectorService('mapContainer');
    } else {
      this.mapService = new GMapSelectorService('mapContainer');
    }
    // ????????????????????? ??????????????????????????????
    if (this.point && this.point.lat && this.point.lng) {
    } else {
      this.mapService.locateToUserCity();
    }
    this.mapInstance = this.mapService.mapInstance;
    // ???????????????????????????
    this.mapDrawUtil = this.mapService.mapDrawUtil;
    this.mapDrawUtil.open();
    // ???????????????????????????????????????????????????????????????
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
