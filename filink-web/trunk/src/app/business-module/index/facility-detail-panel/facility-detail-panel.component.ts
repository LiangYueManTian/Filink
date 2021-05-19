import {AfterViewInit, Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges} from '@angular/core';
import {Router} from '@angular/router';
import {FacilityService} from '../../../core-module/api-service/facility/facility-manage';
import {ImageViewService} from '../../../shared-module/service/picture-view/image-view.service';
import {BoxInfoBean} from '../../../core-module/entity/facility/box-info-bean';
import {SmartService} from '../../../core-module/api-service/facility/smart/smart.service';
import {OdnDeviceService} from '../../../core-module/api-service/statistical/odn-device';
import {MapStoreService} from '../../../core-module/store/map.store.service';
import {Result} from '../../../shared-module/entity/result';
import {DateHelperService, NzI18nService, NzModalService} from 'ng-zorro-antd';
import {CommonUtil} from '../../../shared-module/util/common-util';
import {LockService} from '../../../core-module/api-service/lock';
import {indexChart} from '../index-charts';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';
import {MapService} from '../../../core-module/api-service/index/map';
import {DateFormatString} from '../../../shared-module/entity/dateFormatString';
import {FacilityName} from '../map/facility-name';
import {FACILITY_STATUS_COLOR} from '../../../shared-module/const/facility';
import {SmartLabelConfig} from '../../facility/smart-label.config';
import {FacilityLanguageInterface} from '../../../../assets/i18n/facility/facility.language.interface';
import {DeviceDetailCode, wellCover} from '../../facility/facility.config';
import {index_facility_type, lock_status_type} from '../index-const';

/**
 * 设施详情组件
 */
@Component({
  selector: 'app-facility-detail-panel',
  templateUrl: './facility-detail-panel.component.html',
  styleUrls: ['./facility-detail-panel.component.scss']
})
export class FacilityDetailPanelComponent extends FacilityName implements OnInit, OnChanges, OnDestroy, AfterViewInit {
  // 设施id
  @Input() facilityId: string;
  // 是否显示实景图信息
  @Input() isShowBusinessPicture: boolean;
  // 权限code
  @Input() facilityPowerCode = [];
  // 设施详情回传
  @Output() facilityDetailEvent = new EventEmitter();
  // 权限码
  public powerCode = DeviceDetailCode;
  // 智能标签信息
  public smartLabelInfo: BoxInfoBean = new BoxInfoBean();
  // 设施信息
  public facilityInfo = {
    areaInfo: {},
    deployStatus: {},
    facilityStatusObj: {}
  };
  // 锁类型
  public lockType = lock_status_type;
  // 设施类型
  public dvType = index_facility_type;
  // 设施图标大小
  public facilityIconSizeValue;
  // 主控信息列表
  public monitorInfoList = [];
  // 门锁信息列表
  public doorAndLockList = [];
  // 版本号
  public serialNum;
  // 是否收藏设施
  public isCollected = false;
  // 心跳时间
  public heartbeatTime;
  // 是否显示下拉框   有主控信息时显示下拉框
  public isShowSelect = false;
  // 主控信息
  public controlInfoObj = {};
  // 主控列表
  public controlOption = [];
  // 选中的主控id
  public selectedControlId;
  // 主控类型
  public hostType =  '1';
  // 供电方式
  public sourceType;
  // 人井门类型
   public wellType = wellCover;
  // 国际化
  public language: FacilityLanguageInterface;
  public isShowSmartTag = false;
  // 端口状态
  public pieChartOption;
  // 是否显示端口状态
  public isShowPortStatus = false;
  // 端口使用率
  public portChartOption;
  // 是否显示端口使用率
  public isShowPortRate = false;
  // 轮询
  public timer;
  // 设施图片列表
  public devicePicList = [];
  // 设施图片url
  public devicePicUrl = '';
  constructor(public $nzI18n: NzI18nService,
              private $facilityService: FacilityService,
              private $lockService: LockService,
              private $router: Router,
              private $mapService: MapService,
              private $smartService: SmartService,
              private $modal: NzModalService,
              private $message: FiLinkModalService,
              private $dateHelper: DateHelperService,
              private $odnDeviceService: OdnDeviceService,
              private $mapStoreService: MapStoreService,
              private $imageViewService: ImageViewService,
              ) {
    super($nzI18n);
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
    this.facilityIconSizeValue = '18-24';
    // 页面初始化
    this.getAllData(this);
  }
  ngAfterViewInit() {
    const that  = this;
    this.timer = setInterval(function () {
      that.getAllData(that);
    }, 20000);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes && changes.facilityId && changes.facilityId.previousValue) {
      // 当设施id发生变化的时候，关闭定时器
      if (this.timer) {
        clearInterval(this.timer);
      }
      // 如果设施id为空
      if (!this.facilityId) {
        return;
      }
      this.getFacilityDetail(this.facilityId);
      this.getDevicePic(this.facilityId);
      this.getHeartbeatTime();
    }
  }

  ngOnDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }

  /**
   * 更新收藏状态
   * param type 推送类型
   */
  updateCollectionStatus(type) {
    this.isCollected = type === 'focusDevice';
  }

  /**
   * 获取设施详情
   * param id
   */
  getFacilityDetail(id) {
    if (this.facilityPowerCode.includes(this.powerCode.infrastructureDetails)) {
      this.$facilityService.queryDeviceById(id).subscribe((result: Result) => {
        if (result.code === 0) {
          const _facilityInfo = result.data;
          this.serialNum = _facilityInfo.serialNum;
          _facilityInfo['facilityName'] = _facilityInfo['deviceName'];
          _facilityInfo['facilityTypeName'] = this.getFacilityTypeName(_facilityInfo['deviceType']);
          _facilityInfo['facilityStatusObj'] = {
            text: this.getFacilityStatusName(_facilityInfo['deviceStatus']),
            bgColor: FACILITY_STATUS_COLOR[_facilityInfo['deviceStatus']]
          };
          _facilityInfo['facilityTypeClassName'] = CommonUtil.getFacilityIconClassName(_facilityInfo.deviceType);
          _facilityInfo['deployStatus'] = this.getFacilityDeployStatus(_facilityInfo['deployStatus']);
          // 0 未收藏 1 已收藏
          this.isCollected = ('' + _facilityInfo['isCollecting']) !== '0';
          this.facilityInfo = _facilityInfo;
          // 清空主控和电子锁信息
          this.monitorInfoList = [];
          this.controlOption = [];
          this.doorAndLockList = [];
          this.isShowSelect = false;
          // 根据智能标签是否显示有电子锁和主控信息
          if (this.facilityPowerCode.includes(this.powerCode.intelligentEntranceGuard)) {
            this.getLockInfo(this.facilityId);
            this.getLockControlInfo(this.facilityId);
          }

          if (this.facilityPowerCode.includes(this.powerCode.intelligentLabelDetail) &&
            this.facilityPowerCode.includes(this.powerCode.intelligentLabelSetting)) {
            // 智能标签信息
            this.getSmartLabelInfo();
          }

        } else {
          this.$message.error(result.msg);
          if (result.code === 130204) {
            clearInterval(this.timer);
            this.$mapStoreService.deleteMarker(this.facilityId);
            this.facilityDetailEvent.emit({type: 'refresh', data: ''});
          }
        }
      });
    }
  }

  /**
   * 获取不同部署状态对应显示
   * param status
   * returns {any}
   */
  getFacilityDeployStatus(status) {
    status = status + '';
    const map = {
      '1': {
        class: 'icon_deploy_arm',
        name: 'arm'
      },        // 布防
      '2': {
        class: 'icon_deploy_unarm',
        name: 'unArm'
      },      // 未布防
      '3': {
        class: 'icon_deploy_disable',
        name: 'disable'
      },    // 停用
      '4': {
        class: 'icon_deploy_maintain',
        name: 'maintain'
      },   // 维护
      '5': {
        class: 'icon_deploy_remove',
        name: 'remove'
      },     // 拆除
      '6': {
        class: 'iconfont icon-fiLink-s fiLink-deploying',
        name: 'deploying'
      },     // 部署中
    };
    if (map[status]) {
      return {
        name: this.indexLanguage[map[status]['name']],
        class: map[status]['class']
      };
    } else {
      return {
        name: '',
        class: ''
      };
    }

  }

  /**
   * 获取主控信息
   * param id
   */
  getLockControlInfo(id) {
    this.$lockService.getLockControlInfo(id).subscribe((result: Result) => {
      if (result.code === 0) {
        if (result.data) {
          if (result.data.length === 0) {
            return;
          } else {
            this.hostType = result.data[0].hostType;
            this.sourceType = result.data[0].sourceType;
            result.data.forEach(item => {
              const arr = [];
              let data;
              if (item.actualValue) {
                data = JSON.parse(item.actualValue);
              } else {
                data = {};
               // return;
              }
              Object.keys(data).forEach(key => {
                const info = this.setMonitorInfo(key, data[key]);
                if (info) {
                  arr.push(info);
                }
              });
              this.controlInfoObj[item.controlId] = arr;
              this.controlOption.push({
                value: item.controlId,
                label: item.hostName
              });
            });
            if (this.controlOption[0]) {
              this.selectedControlId = this.controlOption[0]['value'];
            }
            this.monitorInfoList = this.controlInfoObj[this.selectedControlId];
            this.isShowSelect = true;
          }
        } else {
          this.isShowSelect = false;
        }
      } else {
        this.$message.error(result.msg);
      }
    });
  }

  /**
   * 切换主控显示
   */
  changeControl(event) {
    this.monitorInfoList = this.controlInfoObj[this.selectedControlId];
  }

  /**
   * 获取锁信息
   * param id
   */
  getLockInfo(id) {
    this.$lockService.getLockInfo(id).subscribe((result: Result) => {
      if (result.code === 0) {
        const that = this;
        this.doorAndLockList = result.data.map(item => {
          item.lockStatusClassName = item.lockStatus === that.lockType.unusual ? 'icon-lock-opening' :
          item.lockStatus === that.lockType.normal ? 'icon-lock-normal' : 'icon-lock-invalid';
          item.doorStatusClassName = item.doorStatus === that.lockType.unusual ? 'icon-door-opening' : 'icon-door-normal';
          return item;
        });
      } else {
        this.$message.error(result.msg);
      }
    }, err => {
    });
  }

  /**
   * 设置获取到的主控信息
   * param key
   * param info
   * returns {{key: any; text: string; value: string; iconClass: string}}
   */
  setMonitorInfo(key, info) {

    const value = info.data || '';
    const className = info.alarmFlag === '2' ? 'icon-tilt' : 'icon-fiLink';
    let type = '';
    switch (value) {
      case '1':
        type = this.indexLanguage.moduleType1;
        break;
      case '2':
        type = this.indexLanguage.moduleType2;
        break;
      case '3':
        type = this.indexLanguage.moduleType3;
        break;
      default:
    }
    if (key === 'moduleType') {  // 通信模式
      return {
        key: key,
        text: this.indexLanguage.communicationModel,
        value: type,
        iconClass: 'iconfont icon-communication-model fiLink-communication-model'
      };
    } else if (key === 'lean') {  //  倾斜
      return {
        key: key,
        text: this.indexLanguage.tilt,
        value: value + '°',
        iconClass: `iconfont fiLink-tilt ${className}`
      };
    } else if (key === 'temperature') {  // 温度
      return {
        key: key,
        text: this.indexLanguage.temperature,
        value: value + '℃',
        iconClass: `iconfont fiLink-temperature ${className}`
      };
    } else if (key === 'humidity') {  // 湿度
      return {
        key: key,
        text: this.indexLanguage.humidity,
        value: value + '%',
        iconClass: `iconfont fiLink-humidity ${className}`
      };
    } else if (key === 'electricity') {  // 电量
      // 供电方式为适配器，默认为100%
      const elect = this.sourceType === '0' ? 100 : value;
      return {
        key: key,
        text: this.indexLanguage.electricQuantity,
        value: elect + '%',
        iconClass: this.getElectricQuantityIconClass(elect)
      };
    } else if (key === 'leach') {  // 水浸  (无值)
      return {
        key: key,
        text: this.indexLanguage.waterImmersion,
        value: '',  // value,
        iconClass: `iconfont fiLink-water-immersion ${className}`
      };
    } else if (key === 'wirelessModuleSignal') {  // 信号强度
      return {
        key: key,
        text: this.indexLanguage.signalIntensity,
        value: value + 'dB',
        iconClass: this.getSignalIntensityIconClass(value)
      };
    } else if (key === 'solarPanel') {  // 太阳能板
      return {
        key: key,
        text: this.indexLanguage.solarPanel,
        value: value,
        iconClass: 'icon-discharge'
      };
    } else {

    }
  }

  /**
   * 获取不同信号强度对应的图标
   * param value
   * returns {string}
   */
  getSignalIntensityIconClass(value) {
    const _value = parseInt(value, 10);
    if (_value === 0) {
      return 'icon-signal-intensity-0';
    } else if (_value > 0 && _value <= 20) {
      return 'icon-signal-intensity-20';
    } else if (_value > 20 && _value <= 40) {
      return 'icon-signal-intensity-40';
    } else if (_value > 40 && _value <= 60) {
      return 'icon-signal-intensity-60';
    } else if (_value > 60 && _value <= 80) {
      return 'icon-signal-intensity-80';
    } else if (_value > 80 && _value <= 100) {
      return 'icon-signal-intensity-100';
    } else {
      return 'icon-signal-intensity';
    }
  }

  /**
   * 获取不同电量对应的图标
   * param value
   * returns {string}
   */
  getElectricQuantityIconClass(value) {
    const _value = parseInt(value, 10);
    if (_value === 0) {
      return 'icon-electric-quantity-0';
    } else if (_value > 0 && _value <= 20) {
      return 'icon-electric-quantity-20';
    } else if (_value > 20 && _value <= 40) {
      return 'icon-electric-quantity-40';
    } else if (_value > 40 && _value <= 60) {
      return 'icon-electric-quantity-60';
    } else if (_value > 60 && _value <= 80) {
      return 'icon-electric-quantity-80';
    } else if (_value > 80 && _value <= 100) {
      return 'icon-electric-quantity-100';
    } else {
      return 'icon-electric-quantity';
    }
  }

  /**
   * 点击锁
   * param item
   */
  clickLock(item) {
    // 0 无效  1异常  2正常
    if (item.lockStatus === this.lockType.normal) {
      const type = this.dvType;
      // 人井、光交箱、室外柜可远程开锁
      const e = this.facilityInfo['deviceType'];
      if (e === type.opticalBox || e === type.manWell || e === type.outDoorCabinet) {
        this.openPrompt(item);
      }
    }
  }

  /**
   * 打开开锁询问框
   * param item
   */
  openPrompt(item) {
    this.$modal.create({
      nzTitle: this.indexLanguage.openLockTitle,
      nzContent: this.indexLanguage.openLockContent,
      nzOnOk: () => {
        this.openLock(item);
      }
    });
  }

  /**
   * 开锁
   * param item
   */
  openLock(item) {
    const body = {deviceId: this.facilityId, doorNumList: [item.doorNum]};
    this.$lockService.openLock(body).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * 跳转至设施详情页面
   */
  goToFacilityDetailById() {
    this.$router.navigate([`/business/facility/facility-detail-view`],
      {queryParams: {id: this.facilityId, deviceType: this.facilityInfo['deviceType']}}).then();
  }

  /**
   * 收藏或取消收藏
   */
  collectionChange() {
    if (this.isCollected) {
      this.unCollect();
    } else {
      this.collect();
    }
  }

  /**
   * 取消收藏
   */
  unCollect() {
    this.$mapService.unCollectFacility(this.facilityId).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(this.indexLanguage.unCollectSuccess);
        this.isCollected = false;
        this.facilityDetailEvent.emit({type: 'unFollowDevice', data: this.setFacilityInfo()});
      } else {
        this.$message.error(result.msg);
      }
    });
  }

  /**
   * 收藏
   */
  collect() {
    this.$mapService.collectFacility(this.facilityId).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(this.indexLanguage.collectSuccess);
        this.isCollected = true;
        this.facilityDetailEvent.emit({type: 'focusDevice', data: this.setFacilityInfo()});
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  setFacilityInfo() {
    return {
      deviceId: this.facilityInfo['deviceId'],
      deviceType: this.facilityInfo['deviceType'],
      deviceName: this.facilityInfo['deviceName'],
      deviceStatus: this.facilityInfo['deviceStatus'],
      deviceCode: this.facilityInfo['deviceCode'],
      address: this.facilityInfo['address'],
      positionBase: this.facilityInfo['positionBase'],
      areaId: this.facilityInfo.areaInfo['areaId'],
      areaName: this.facilityInfo.areaInfo['areaName']
    };
  }

  /**
   * 获取心跳时间
   */
  getHeartbeatTime() {
    if (this.facilityPowerCode.includes(this.powerCode.intelligentEntranceGuard)) {
      this.$facilityService.queryHeartbeatTime(this.facilityId).subscribe((result: Result) => {
        if (result.code === 0) {
          if (result.data && result.data.recentLogTime) {
            this.heartbeatTime = this.$dateHelper.format(new Date(result.data.recentLogTime), DateFormatString.DATE_FORMAT_STRING);
          } else {
            this.heartbeatTime = 'NA';
          }
        }
      });
    }
  }

  /**
   * 查询智能标签信息
   */
  public getSmartLabelInfo() {
    this.$smartService.queryFacilityBusInfoById(this.facilityId, this.facilityInfo['deviceType']).subscribe((result: Result) => {
      if (result.code === 0 && result.data.boxList && result.data.boxList.length > 0) {
        this.isShowSmartTag = true;
        this.smartLabelInfo = result.data.boxList[0] as BoxInfoBean;
        this.translateProperty(this.smartLabelInfo);
      }
    });
  }

  /**
   * 获取端口状态
   */
  getFiberPortStatistics() {
    if (this.isShowBusinessPicture && this.facilityPowerCode.includes(this.powerCode.intelligentLabelDetail)
      && this.facilityPowerCode.includes(this.powerCode.intelligentLabelSetting)) {
      this.$odnDeviceService.queryDevicePortStatistics(this.facilityId).subscribe((result: Result) => {
        // 没有权限
        if (!result.data) {
          return;
        }
        if (result.code === 0 && result.data.totalCount !== 0) {
          this.isShowPortRate = true;
          this.setOption(result.data, 2);
        }
      });
    }
  }


  /**
   * 获取端口使用率
   */
  deviceUsePortStatistics() {
    if (this.isShowBusinessPicture && this.facilityPowerCode.includes(this.powerCode.intelligentLabelDetail)
      && this.facilityPowerCode.includes(this.powerCode.intelligentLabelSetting)) {
      this.$mapService.queryDeviceUsePortStatistics(this.facilityId).subscribe((result: Result) => {
        // 没有权限
        if (!result.data) {
          return;
        }
        if (result.code === 0 && result.data.totalCount !== 0) {
          this.isShowPortStatus = true;
          this.setOption(result.data, 1);
        }
      });
    }
  }

  public setOption(data, type) {
    let dataCount = [];
    const tips = {
      text: this.indexLanguage.portNumber,
      name: this.indexLanguage.dataShare,
    };
     if (type === 1) {
       dataCount = [
         {value: data.usedPortCount, name: this.indexLanguage.used},
         {value: data.unusedPortCount, name: this.indexLanguage.noUsed}
       ];
       this.portChartOption = indexChart.setPortChartOption(data, dataCount, tips);
     } else if (type === 2) {
       dataCount = [ {value: data.usedCount, name: this.language.Occupy},
         {value: data.unusedCount, name: this.language.Free},
         {value: data.exceptionCount, name: this.language.Exception},
         {value: data.advanceCount, name: this.language.PreOccupy},
         {value: data.virtualCount, name: this.language.VirtualOccupy}];
       this.pieChartOption = indexChart.setPortChartOption(data, dataCount, tips);
     }
  }

  public clickImage() {
    if (this.devicePicList && this.devicePicList.length > 0) {
      // this.$imageViewService.showPictureView(this.devicePicList);
    }
  }

  /**
   * 获取设施图片
   * param id
   */
  private getDevicePic(id) {
    const body = {
      resource: '3',
      deviceId: id,
      picNum: '1'
    };
    if (this.facilityPowerCode.includes(this.powerCode.infrastructureDetails)) {
      this.$facilityService.picRelationInfo(body).subscribe((result: Result) => {
        if (result.code === 0 && result.data && result.data.length > 0) {
          this.devicePicList = result.data;
          this.devicePicUrl = result.data[0].picUrlBase;
        } else {
          this.devicePicUrl = 'assets/img/facility/no_img_6.svg';
        }
      });
    }
  }
  /**
   * 智能标签
   * param 转换
   */
  private translateProperty(smartLabelInfo: any) {
    if (smartLabelInfo.follow !== null) {
      smartLabelInfo.follow = SmartLabelConfig.getFollowEnum(this.$nzI18n, smartLabelInfo.follow);
    }
    if (smartLabelInfo.standard !== null) {
      smartLabelInfo.standard = SmartLabelConfig.getStandardEnum(this.$nzI18n, smartLabelInfo.standard);
    }
    if (smartLabelInfo.layMode !== null) {
      smartLabelInfo.layMode = SmartLabelConfig.getLayModeEnum(this.$nzI18n, smartLabelInfo.layMode);
    }
    if (smartLabelInfo.sealMode !== null) {
      smartLabelInfo.sealMode = SmartLabelConfig.getSealModeEnum(this.$nzI18n, smartLabelInfo.sealMode);
    }
    if (smartLabelInfo.installationMode !== null) {
      smartLabelInfo.installationMode = SmartLabelConfig.getInstallationModeEnum(this.$nzI18n, smartLabelInfo.installationMode);
    }
    if (smartLabelInfo.deviceForm !== null) {
      smartLabelInfo.deviceForm = SmartLabelConfig.getDeviceFormEnum(this.$nzI18n, smartLabelInfo.deviceForm);
    }
    if (smartLabelInfo.labelType !== null) {
      smartLabelInfo.labelType = SmartLabelConfig.getLabelTypeEnum(this.$nzI18n, smartLabelInfo.labelType);
    }
    if (smartLabelInfo.labelState !== null) {
      smartLabelInfo.labelState = SmartLabelConfig.getLabelStateEnum(this.$nzI18n, smartLabelInfo.labelState);
    }
  }

  /**
   *
   * 轮询设施详情
   */
  private getAllData(that) {
    // 如果设施id为空
    if (!that.facilityId) {
      // 当设施id发生变化的时候，关闭定时器
      if (that.timer) {
        clearInterval(that.timer);
      }
      return;
    }
    that.getFacilityDetail(that.facilityId);
    that.getDevicePic(that.facilityId);
    that.getHeartbeatTime();
    that.getFiberPortStatistics();
    that.deviceUsePortStatistics();
  }
}
