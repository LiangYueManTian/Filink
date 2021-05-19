import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {LockService} from '../../../../../core-module/api-service/lock';
import {Result} from '../../../../../shared-module/entity/result';
import {FiLinkModalService} from '../../../../../shared-module/service/filink-modal/filink-modal.service';
import {FacilityService} from '../../../../../core-module/api-service/facility/facility-manage';
import {FacilityDeployStatusClassName, FacilityStatusColor, getDeployStatus, getDeviceStatus} from '../../../facility.config';
import {DateHelperService, NzI18nService, NzNotificationService} from 'ng-zorro-antd';
import {FacilityLanguageInterface} from '../../../../../../assets/i18n/facility/facility.language.interface';
import {DateFormatString} from '../../../../../shared-module/entity/dateFormatString';
import {NativeWebsocketImplService} from '../../../../../core-module/websocket/native-websocket-impl.service';
import {FacilityMissionService} from '../../../../../core-module/mission/facility.mission.service';

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
  public selectedControl = 0;
  public lockControlInfo: any = {actualValue: {}};
  public lockControlInfoAll = [];
  public deviceInfo: any = {};
  private lockTimer: any;
  public currentTime;
  private controlLoopTimer;
  public language: FacilityLanguageInterface;

  constructor(private $lockService: LockService,
              private $nzI18n: NzI18nService,
              private $facilityService: FacilityService,
              private $dateHelper: DateHelperService,
              private $wsService: NativeWebsocketImplService,
              private $nzNotificationService: NzNotificationService,
              private $refresh: FacilityMissionService,
              private $modalService: FiLinkModalService) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
    this.getLockInfo();
    this.getLockControlInfo();
    // this.getDeviceInfo();
    this.$refresh.refreshChangeHook.subscribe((event) => {
      if (event) {
        if (this.controlLoopTimer) {
          window.clearInterval(this.controlLoopTimer);
          this.controlLoopTimer = null;
        }
        this.getLockControlInfo();
      }
    });
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

  refreshLock() {
    this.getLockInfo();
  }

  openLock() {
    if (this.lockControlInfo.hostType === '0') {
      this.$modalService.info(this.language.unOpenLock);
      return;
    }
    const slotNum = [];
    this.lockInfo.forEach(item => {
      if (item.checked) {
        slotNum.push(item.doorNum);
      }
    });
    const body = {deviceId: this.deviceId, doorNumList: slotNum};
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
      // todo 此处1个设施对应多个主控页面在build3体现 此处页面展示方式需要大改
      this.lockControlInfoAll = result.data;
      if (result.data && result.data[0]) {
        this.lockControlInfo = this.lockControlInfoAll[0];
        this.convertDeviceInfo(this.lockControlInfo);
        if (result.data[0].actualValue) {
          this.lockControlInfo.actualValue = JSON.parse(this.lockControlInfo.actualValue);
        }
      }
      // 查询最近一条设施日志的时间
      this.$facilityService.deviceLogTime(this.deviceId).subscribe((_result: Result) => {
        if (_result.code === 0) {
          this.currentTime = _result.data.recentLogTime;
        } else {
          this.currentTime = null;
        }
        // 开启定时器轮询
        if (!this.controlLoopTimer) {
          this.controlLoopTimer = window.setInterval(() => {
            this.getLockControlInfo();
          }, 60000);
        }
      });
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

  /**
   * 主控改变
   * param event 主控的索引
   */
  controlChange(event) {

    this.lockControlInfo = this.lockControlInfoAll[event];
    this.convertDeviceInfo(this.lockControlInfo);
    if (this.lockControlInfo.actualValue && typeof this.lockControlInfo.actualValue === 'string') {
      this.lockControlInfo.actualValue = JSON.parse(this.lockControlInfo.actualValue);
    }

  }

  /**
   * 转换数据
   * deviceStatus
   * deployStatus
   * param data
   */
  convertDeviceInfo(data) {
    if (data.deviceStatus && data.deployStatus) {
      this.deviceInfo['deviceStatusLabel'] = getDeviceStatus(this.$nzI18n, data.deviceStatus);
      this.deviceInfo['deployStatusLabel'] = getDeployStatus(this.$nzI18n, data.deployStatus);
      this.deviceInfo['deviceStatusBgColor'] = FacilityStatusColor[data.deviceStatus];
      this.deviceInfo['deployStatusIconClass'] = FacilityDeployStatusClassName[data.deployStatus].className;
    }
  }
}
