import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {LockService} from '../../../../../core-module/api-service/lock';
import {Result} from '../../../../../shared-module/entity/result';
import {FiLinkModalService} from '../../../../../shared-module/service/filink-modal/filink-modal.service';
import {FacilityService} from '../../../../../core-module/api-service/facility/facility-manage';
import {DATE_FORMAT_STR, FacilityStatusColor, getDeployStatus, getDeviceStatus} from '../../../facility.config';
import {NzI18nService} from 'ng-zorro-antd';
import {FacilityLanguageInterface} from '../../../../../../assets/i18n/facility/facility.language.interface';

@Component({
  selector: 'app-intelligent-entrance-guard',
  templateUrl: './intelligent-entrance-guard.component.html',
  styleUrls: ['./intelligent-entrance-guard.component.scss']
})
export class IntelligentEntranceGuardComponent implements OnInit, OnDestroy {
  @Input()
  deviceId: string;
  @Input()
  serialNum: string;
  dateRange;
  _dateRange;
  allChecked: boolean = false;
  indeterminate = false;
  checked: boolean = false;
  option = {
    width: '90%',
    height: '50%',
    xAxis: {
      type: 'category',
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    },
    yAxis: {
      type: 'value'
    },
    grid: {
      top: '10px'
    },
    series: [
      {
        data: [20, 25, 35, 25, 10, 20, 30],
        type: 'line',
        smooth: true
      }]
  };
  _option = {
    xAxis: {
      type: 'category',
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: [20, 25, 35, 25, 10, 20, 30],
      type: 'line',
      smooth: true
    }]
  };
  public lockInfo: any = [];
  public lockControlInfo: any = {actualValue: {}};
  private lockTimer: any;
  infoData = [];
  public currentTime;
  private controlLoopTimer;
  public language: FacilityLanguageInterface;

  constructor(private $lockService: LockService,
              private $nzI18n: NzI18nService,
              private $facilityService: FacilityService,
              private $modalService: FiLinkModalService,) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
    this.getLockInfo();
    this.getLockControlInfo();
    // this.getDeviceInfo();
  }

  onChange(event) {

  }

  ngOnDestroy(): void {
    if (this.lockTimer) {
      window.clearInterval(this.lockTimer);
    }
    if (this.controlLoopTimer) {
      window.clearInterval(this.controlLoopTimer);
    }
  }

  getLockInfo(noFirst = false) {
    this.$lockService.getLockInfo(this.deviceId).subscribe((result: Result) => {
      console.log(result);
      if (result.data && result.data.length) {
        if (noFirst) {
          result.data.forEach((item: any, index) => {
            this.lockInfo[index].lockStatus = item.lockStatus;
            this.lockInfo[index].doorStatus = item.doorStatus;
          });
        } else {
          this.lockInfo = result.data || [];
        }
      }
    });
  }

  openLock() {
    const slotNum = [];
    this.lockInfo.forEach(item => {
      if (item.checked) {
        slotNum.push(item.lockNum);
      }
    });
    const body = {deviceId: this.deviceId, slotNumList: slotNum};
    if (this.lockTimer) {
      window.clearInterval(this.lockTimer);
    }
    this.$lockService.openLock(body).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$modalService.success(result.msg);
        this.lockInfo.forEach(item => {
          item.checked = false;
        });
        this.allChecked = false;
        this.indeterminate = false;
        this.lockTimer = window.setInterval(() => {
          this.getLockInfo(true);
        }, 3000);
      } else {
        this.$modalService.error(result.msg);
      }
    });
  }

  getLockControlInfo() {
    this.$lockService.getLockControlInfo(this.deviceId).subscribe((result: Result) => {
      if (result.data && result.data.actualValue) {
        this.lockControlInfo = result.data;
        const actualValue = JSON.parse(result.data.actualValue);
        this.lockControlInfo.actualValue = actualValue;
        this.infoData = [
          {label: '???????????? :', value: '??????', iconClass: '', fill: 'normal'},
          {label: '???????????? :', value: '??????', iconClass: 'icon-vector-object'},
          {label: '???????????? :', value: this.lockControlInfo.softwareVersion},
          {label: '???????????? :', value: this.lockControlInfo.hardwareVersion},
          {label: '????????????????????????  :', value: '2018-12-21 18:21:21'},
          {label: '???????????? :', value: actualValue.moduleType === '1' ? '2G??????' : 'NB??????', iconClass: 'fontStyle fiLink-communication-model'},
          {label: '???????????? :', value: `${actualValue.wirelessModuleSignal}db`, iconClass: 'icon-signal-intensity-40'},
          {label: '?????? :', value: `${actualValue.electricity}%`, iconClass: 'icon-electric-quantity-60'},
          {label: '????????? :', value: '??????', iconClass: 'icon-charging'},
          {label: '?????????????????? :', value: '??????', bgColor: '#0ac929', fill: 'activate'},
        ];
      } else {
        this.infoData = [
          {label: '???????????? :', value: 'NA'},
          {label: '???????????? :', value: 'NA'},
          {label: '???????????? :', value: 'NA'},
          {label: '???????????? :', value: 'NA'},
          {label: '????????????????????????  :', value: 'NA'},
          {label: '???????????? :', value: 'NA'},
          {label: '???????????? :', value: 'NA'},
          {label: '?????? :', value: 'NA'},
          {label: '????????? :', value: 'NA'},
          {label: '?????????????????? :', value: 'NA'},
        ];
      }
      this.getDeviceInfo();
    });
  }

  checkAll(event) {
    this.indeterminate = false;
    this.lockInfo.forEach(item => {
      item.checked = event;
    });
  }

  checkItem() {
    this.checkStatus();
  }

  checkStatus() {
    const allChecked = this.lockInfo.every(value => value.checked === true);
    const allUnChecked = this.lockInfo.every(value => !value.checked);
    this.allChecked = allChecked;
    this.indeterminate = (!allChecked) && (!allUnChecked);
  }

  getDeviceInfo() {
    this.$facilityService.queryDeviceById(this.deviceId).subscribe((result: Result) => {
      const map = {
        '1': {
          class: 'icon_deploy_arm',
          name: 'arm'
        },        // ??????
        '2': {
          class: 'icon_deploy_unarm',
          name: 'unarm'
        },      // ?????????
        '3': {
          class: 'icon_deploy_disable',
          name: 'disable'
        },    // ??????
        '4': {
          class: 'icon_deploy_maintain',
          name: 'maintain'
        },   // ??????
        '5': {
          class: 'icon_deploy_remove',
          name: 'remove'
        },     // ??????
      };
      if (result.data) {
        this.infoData[0].fill = 'normal';
        this.infoData[0].value = getDeviceStatus(this.$nzI18n, result.data.deviceStatus);
        this.infoData[0].bgColor = FacilityStatusColor[result.data.deviceStatus];
        this.infoData[1].value = getDeployStatus(this.$nzI18n, result.data.deployStatus);
        this.infoData[1].iconClass = map[result.data.deployStatus].class;
      }
      this.currentTime = this.$nzI18n.formatDateCompatible(new Date(), DATE_FORMAT_STR);
      // todo ????????????
      // if (!this.controlLoopTimer) {
      //   this.controlLoopTimer = window.setInterval(() => {
      //     this.getLockControlInfo();
      //   }, 3000);
      // }
    });
  }
}
