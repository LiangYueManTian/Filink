import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {FacilityService} from '../../../core-module/api-service/facility/facility-manage';
import {Result} from '../../../shared-module/entity/result';
import {DateHelperService, NzI18nService, NzModalService} from 'ng-zorro-antd';
import {CommonUtil} from '../../../shared-module/util/common-util';
import {LockService} from '../../../core-module/api-service/lock';
import {Router} from '@angular/router';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';
import {MapService} from '../../../core-module/api-service/index/map';
import {DateFormatString} from '../../../shared-module/entity/dateFormatString';
import {FacilityName} from '../map/facility-name';
import {FACILITY_STATUS_COLOR} from '../../../shared-module/const/facility';

@Component({
  selector: 'app-facility-detail-panel',
  templateUrl: './facility-detail-panel.component.html',
  styleUrls: ['./facility-detail-panel.component.scss']
})
export class FacilityDetailPanelComponent extends FacilityName implements OnInit, OnChanges {
  @Input() facilityId: string;
  @Output() facilityDetailEvent = new EventEmitter();
  facilityInfo = { // 设施信息
    areaInfo: {},
    deployStatus: {},
    facilityStatusObj: {}
  };
  facilityIconSizeValue;  // 设施图标大小
  monitorInfoList = [];   // 主控信息列表
  doorAndLockList = [];   // 门锁信息列表
  serialNum;    // 版本号
  isCollected = false;   // 是否收藏设施
  heartbeatTime;      // 心跳时间
  isShowSelect = false;  // 是否显示下拉框   有主控信息时显示下拉框
  controlInfoObj = {};   // 主控信息
  controlOption = [];   // 主控列表
  selectedControlId;   // 选中的主控id
  devicePicList = [];
  devicePicUrl = '';
  constructor(public $nzI18n: NzI18nService,
              private $facilityService: FacilityService,
              private $lockService: LockService,
              private $router: Router,
              private $mapService: MapService,
              private $modal: NzModalService,
              private $message: FiLinkModalService,
              private $dateHelper: DateHelperService,
              ) {
    super($nzI18n);
  }

  ngOnInit() {
    this.facilityIconSizeValue = '18-24';
    this.getFacilityDetail(this.facilityId);
    this.getDevicePic(this.facilityId);
    this.getHeartbeatTime();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes && changes.facilityId && changes.facilityId.previousValue) {
      this.getFacilityDetail(this.facilityId);
      this.getDevicePic(this.facilityId);
      this.getHeartbeatTime();
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
    this.$facilityService.queryDeviceById(id).subscribe((result: Result) => {
      // console.log(result);
      if (result.code === 0) {
        const _facilityInfo = result.data;
        this.serialNum = _facilityInfo.serialNum;
        _facilityInfo['facilityName'] = `${this.getFacilityTypeName(_facilityInfo['deviceType'])} ${_facilityInfo['deviceName']}`;
        _facilityInfo['facilityStatusObj'] = {
          text: this.getFacilityStatusName(_facilityInfo['deviceStatus']),
          bgColor: FACILITY_STATUS_COLOR[_facilityInfo['deviceStatus']]
        };
        const iconUrl = CommonUtil.getFacilityIconUrl(this.facilityIconSizeValue, _facilityInfo.deviceType, _facilityInfo.deviceStatus);
        _facilityInfo['iconUrl'] = `url(${iconUrl})`;
        _facilityInfo['deployStatus'] = this.getFacilityDeployStatus(_facilityInfo['deployStatus']);
        this.isCollected = ('' + _facilityInfo['isCollecting']) !== '0';   // 0 未收藏 1 已收藏
        // console.log(_facilityInfo);
        this.facilityInfo = _facilityInfo;
        // 清空主控和电子锁信息
        this.monitorInfoList = [];
        this.controlOption = [];
        this.doorAndLockList = [];
        this.isShowSelect = false;
        if (this.facilityInfo['deviceType'] === '001' || this.facilityInfo['deviceType'] === '030') {  // 人井和光交箱有电子锁和主控信息
          this.getLockInfo(this.facilityId);
          this.getLockControlInfo(this.facilityId);
        } else {
        }
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
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
        if (result.data && result.data.length > 0) {
          result.data.forEach(item => {
            const arr = [];
            let data;
            if (item.actualValue) {
              data = JSON.parse(item.actualValue);
            } else {
              data = {};
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
          this.selectedControlId = this.controlOption[0]['value'];
          this.monitorInfoList = this.controlInfoObj[this.selectedControlId];
          this.isShowSelect = true;
        } else {
          this.isShowSelect = false;
        }
      } else {
        this.$message.error(result.msg);
      }
    }, err => {
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
        this.doorAndLockList = result.data.map(item => {
          item.lockStatusClassName = item.lockStatus === '1' ? 'icon-lock-opening' :
            item.lockStatus === '2' ? 'icon-lock-normal' : 'icon-lock-invalid';
          item.doorStatusClassName = item.doorStatus === '1' ? 'icon-door-opening' : 'icon-door-normal';
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
    if (key === 'moduleType') {  // 通信模式
      return {
        key: key,
        text: this.indexLanguage.communicationModel,
        value: value === '1' ? this.indexLanguage.moduleType1 : this.indexLanguage.moduleType2,
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
      return {
        key: key,
        text: this.indexLanguage.electricQuantity,
        value: value + '%',
        iconClass: this.getElectricQuantityIconClass(value)
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
        value: value + 'db',
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
    if (item.lockStatus === '2') {   // 0 无效  1异常  2正常
      if (this.facilityInfo['deviceType'] === '001' || this.facilityInfo['deviceType'] === '030') {  // 人井和光交箱可远程开锁
        this.openPrompt(item);
      }
    } else {
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
    // console.log(item);
    const body = {deviceId: this.facilityId, doorNumList: [item.doorNum]};
    this.$lockService.openLock(body).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
        // item.lockStatus = '1';
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
        this.facilityDetailEvent.emit({type: 'unFollowDevice', data: this.facilityInfo});
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
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
        this.facilityDetailEvent.emit({type: 'focusDevice', data: this.facilityInfo});
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * 获取心跳时间
   */
  getHeartbeatTime() {
    this.$facilityService.queryHeartbeatTime(this.facilityId).subscribe((result: Result) => {
      if (result.code === 0) {
        if (result.data && result.data.recentLogTime) {
          this.heartbeatTime = this.$dateHelper.format(new Date(result.data.recentLogTime), DateFormatString.DATE_FORMAT_STRING);
        } else {
          this.heartbeatTime = 'NA';
        }
      } else {
        // this.$message.error(result.msg);
      }
    });
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
    this.$facilityService.picRelationInfo(body).subscribe((result: Result) => {
      if (result.code === 0 && result.data && result.data.length > 0) {
        this.devicePicList = result.data;
        this.devicePicUrl = result.data[0].picUrlBase;
      } else {
        this.devicePicUrl = 'assets/img/facility/no_img_6.svg';
      }
    });
  }

  clickImage() {
    if (this.devicePicList && this.devicePicList.length > 0) {
      // this.$imageViewService.showPictureView(this.devicePicList);
    }
  }
}
