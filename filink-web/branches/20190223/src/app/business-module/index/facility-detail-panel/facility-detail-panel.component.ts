import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {FacilityService} from '../../../core-module/api-service/facility/facility-manage';
import {Result} from '../../../shared-module/entity/result';
import {IndexLanguageInterface} from '../../../../assets/i18n/index/index.language.interface';
import {NzI18nService, NzModalService} from 'ng-zorro-antd';
import {MapStoreService} from '../../../core-module/store/map.store.service';
import {CommonUtil} from '../../../shared-module/util/common-util';
import {facilityStatusList, facilityTypeList} from '../facility';
import {LockService} from '../../../core-module/api-service/lock';
import {Router} from '@angular/router';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';
import {CommonLanguageInterface} from '../../../../assets/i18n/common/common.language.interface';

@Component({
  selector: 'app-facility-detail-panel',
  templateUrl: './facility-detail-panel.component.html',
  styleUrls: ['./facility-detail-panel.component.scss']
})
export class FacilityDetailPanelComponent implements OnInit, OnChanges {
  @Input() facilityId: string;
  indexLanguage: IndexLanguageInterface;
  commonLanguage: CommonLanguageInterface;
  facilityInfo = {
    areaInfo: {},
    deployStatus: {},
    facilityStatus: {}
  };
  facilityStatusNameObj;
  facilityIconSizeValue;
  facilityStatusObj = {};
  facilityTypeObj = {};
  monitorInfoList = [];
  doorAndLockList = [];
  serialNum;

  constructor(
    private $facilityService: FacilityService,
    private $lockService: LockService,
    private $nzI18n: NzI18nService,
    private $router: Router,
    private $mapStoreService: MapStoreService,
    private $modal: NzModalService,
    private $message: FiLinkModalService,
  ) { }

  ngOnInit() {
    this.facilityIconSizeValue = '18-24';
    this.indexLanguage = this.$nzI18n.getLocaleData('index');
    this.commonLanguage = this.$nzI18n.getLocaleData('common');
    this.setFacilityStatusObj();
    this.setFacilityTypeObj();
    this.facilityStatusNameObj = this.$mapStoreService.getFacilityStatusNameObj();
    this.getFacilityDetail(this.facilityId);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes && changes.facilityId && changes.facilityId.previousValue) {
      this.getFacilityDetail(this.facilityId);
    }
  }

  setFacilityStatusObj() {
    facilityStatusList().forEach(item => {
      this.facilityStatusObj[item.value] = {
        text: this.indexLanguage[item.label],
        bgColor: item.bgColor
      };
    });
  }

  setFacilityTypeObj() {
    facilityTypeList().forEach(item => {
      this.facilityTypeObj[item.value] = this.indexLanguage[item.label];
    });
  }

  getFacilityDetail(id) {
    this.$facilityService.queryDeviceById(id).subscribe((result: Result) => {
      console.log(result);
      const _facilityInfo = result.data;
      this.serialNum = _facilityInfo.serialNum;
      _facilityInfo['facilityName'] = `${this.facilityTypeObj[_facilityInfo['deviceType']]}-${_facilityInfo['deviceName']}`;
      _facilityInfo['facilityStatus'] =  this.facilityStatusObj[_facilityInfo['deviceStatus']];
      const iconUrl = CommonUtil.getFacilityIconUrl(this.facilityIconSizeValue, _facilityInfo.deviceType, _facilityInfo.deviceStatus);
      _facilityInfo['iconUrl'] = `url(${iconUrl})`;
      _facilityInfo['deployStatus'] = this.getFacilityDeployStatus(_facilityInfo['deployStatus']);
      console.log(_facilityInfo);
      this.facilityInfo = _facilityInfo;
      this.getLockInfo(this.facilityId);
      this.getLockControlInfo(this.facilityId);
    });
  }

  getFacilityDeployStatus(status) {
    status = status + '';
    const map = {
      '1': {
        class: 'icon_deploy_arm',
        name: 'arm'
      },        // 布防
      '2': {
        class: 'icon_deploy_unarm',
        name: 'unarm'
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
    };
    return {
      name: this.indexLanguage[map[status]['name']],
      class: map[status]['class']
    };
  }

  getLockControlInfo(id) {
    this.monitorInfoList = [];
    this.$lockService.getLockControlInfo(id).subscribe((result: Result) => {
      if (result.data && result.data.actualValue) {
        const data = JSON.parse(result.data.actualValue);
        Object.keys(data).forEach(key => {
          const info = this.setMonitorInfo(key, data[key]);
          if (info) {
            this.monitorInfoList.push(info);
          }
        });
      }
    }, err => {
      this.$message.error(err.msg);
    });
  }

  getLockInfo(id) {
    this.doorAndLockList = [];
    this.$lockService.getLockInfo(id).subscribe((result: Result) => {
      this.doorAndLockList = result.data;
    }, err => {
      this.$message.error(err.msg);
    });
  }

  setMonitorInfo(key, value) {
    if (key === 'moduleType') {  // 通信模式
      return {
        key: key,
        text: this.indexLanguage.communicationModel,
        value: value === '1' ? this.indexLanguage.moduleType1 : this.indexLanguage.moduleType2,
        // iconClass: 'icon-communication-model'
        iconClass: 'iconfont icon-communication-model fiLink-communication-model'
      };
    } else if (key === 'lean') {  //  倾斜
      return {
        key: key,
        text: this.indexLanguage.tilt,
        value: value + '°',
        // iconClass: 'icon-tilt'
        iconClass: 'iconfont icon-tilt fiLink-tilt'
      };
    } else if (key === 'temperature') {  // 温度
      return {
        key: key,
        text: this.indexLanguage.temperature,
        value: value + '°',
        // iconClass: 'icon-temperature'
        iconClass: 'iconfont icon-fiLink fiLink-temperature'
      };
    } else if (key === 'humidity') {  // 湿度
      return {
        key: key,
        text: this.indexLanguage.humidity,
        value: value + '%',
        // iconClass: 'icon-humidity'
        iconClass: 'iconfont icon-fiLink fiLink-humidity'
      };
    } else if (key === 'electricity') {  // 电量
      return {
        key: key,
        text: this.indexLanguage.electricQuantity,
        value: value + '%',
        iconClass: this.getElectricQuantityIconClass(value)
      };
    } else if (key === 'leach') {  // 水浸
      return {
        key: key,
        text: this.indexLanguage.waterImmersion,
        value: value + '%',
        // iconClass: 'icon-water-immersion'
        iconClass: 'iconfont icon-fiLink fiLink-water-immersion'
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

  getSignalIntensityIconClass(value) {
    const _value = parseInt(value, 10);
    return 'icon-signal-intensity-40';
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

  getElectricQuantityIconClass(value) {
    const _value = parseInt(value, 10);
    return 'icon-electric-quantity-60';
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

  clickLock(item) {
    if (item.lockStatus === '2') {   // 0 无效  1异常  2正常
      if (this.facilityInfo['deviceType'] === '001' || this.facilityInfo['deviceType'] === '030') {  // 人井和光交箱可远程开锁
        this.openPrompt(item);
      }
    } else {
    }
  }

  openPrompt(item) {
    this.$modal.create({
      nzTitle: '远程开锁',
      nzContent: '是否远程开锁',
      nzOnOk: () => {
        this.openLock(item);
      }
    });
  }

  openLock(item) {
    console.log(item);
    const body = {deviceId: this.facilityId, slotNumList: [item.lockNum]};
    this.$lockService.openLock(body).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
        // item.lockStatus = '1';
      } else {
        this.$message.error(result.msg);
      }
    },  () => {
    });
  }

  goToFacilityDetailById() {
    this.$router.navigate([`/business/facility/facility-detail-view`],
      {queryParams: {id: this.facilityId, deviceType: this.facilityInfo['deviceType']}}).then();
  }
}
