import {Component, Input, OnDestroy, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {LockService} from '../../../../../core-module/api-service/lock';
import {Result} from '../../../../../shared-module/entity/result';
import {FiLinkModalService} from '../../../../../shared-module/service/filink-modal/filink-modal.service';
import {FacilityService} from '../../../../../core-module/api-service/facility/facility-manage';
import {
  activeStatusEnum,
  DeviceTypeCode,
  FacilityDeployStatusClassName,
  FacilityStatusColor,
  getDeployStatus,
  getDeviceStatus,
  getOperator,
  getSolarCell,
  getSourceType,
  HostTypeEnum,
  lockStatusEnum,
  moduleTypeCode,
  moduleTypeName,
  sourceType,
  wellCover
} from '../../../facility.config';
import {DateHelperService, NzI18nService, NzModalService, NzNotificationService} from 'ng-zorro-antd';
import {FacilityLanguageInterface} from '../../../../../../assets/i18n/facility/facility.language.interface';
import {NativeWebsocketImplService} from '../../../../../core-module/websocket/native-websocket-impl.service';
import {FacilityMissionService} from '../../../../../core-module/mission/facility.mission.service';
import {DeviceStatisticalService} from '../../../../../core-module/api-service/statistical/device-statistical';
import {CommonUtil} from '../../../../../shared-module/util/common-util';
import {TimerSelectorService} from '../../photo-viewer/timer-selector/timer-selector.service';
import {DeviceChartUntil} from '../device-chart-until';
import {CommonLanguageInterface} from '../../../../../../assets/i18n/common/common.language.interface';
import {PageBean} from '../../../../../shared-module/entity/pageBean';
import {TableConfig} from '../../../../../shared-module/entity/tableConfig';

/**
 * 设施详情智能门禁组件
 */
@Component({
  selector: 'app-intelligent-entrance-guard',
  templateUrl: './intelligent-entrance-guard.component.html',
  styleUrls: ['./intelligent-entrance-guard.component.scss'],
  providers: [TimerSelectorService]
})
export class IntelligentEntranceGuardComponent implements OnInit, OnDestroy {
  // 设施id
  @Input()
  deviceId: string;
  // 设施类型
  @Input()
  deviceType: string;
  // 序列号
  @Input()
  serialNum: string;
  // 开锁日期选择范围
  dateRange;
  // 门禁阈值日期选择范围
  _dateRange;
  // 所有已被选择
  allChecked: boolean = false;
  // 半选状态
  indeterminate = false;
  // 选择状态
  checked: boolean = false;
  // 开锁次数统计eCharts配置
  option: any = {};
  // 传感器阈值统计eCharts配置
  _option: any = {};
  // 电子锁信息
  public lockInfo: any = [];
  // 已选择主控
  public selectedControl = 0;
  // 已选主控信息
  public lockControlInfo: any = {actualValue: {}, hostName: ''};
  // 所有主控信息
  public lockControlInfoAll = [];
  // 设施信息
  public deviceInfo: any = {};
  // 轮询实例
  private lockTimer: any;
  // 当前是日志
  public currentTime;
  // 主控轮询实例
  private controlLoopTimer;
  // 设施语言包
  public language: FacilityLanguageInterface;
  // 公共语言包
  public commonLanguage: CommonLanguageInterface;
  // 传感器阈值类型
  public sensorType: string = 'temperature';
  // 设施传感器值
  private deviceSensorValue: any = {};
  // 开锁次数
  public openCount: number = 0;
  // 没有开锁内容
  public noOpenCount;
  // 没有传感器阀值信息
  public noSensorData;
  // 开锁次数加载中
  public openCountLoading = false;
  // 传感器阀值加载中
  public sensorLoading = false;
  // 设施类型code
  deviceTypeCode: any;
  // 主控类型code
  hostTypeCode: any;
  // 锁状态
  lockStatus: any;
  // 通信模式code
  moduleTypeCode: any;
  // 通信模式值
  moduleTypeValue: any;
  // 激活状态
  activeStatus: any;
  // 分页实体
  pageBean: PageBean = new PageBean(10, 1, 1);
  // 主空删除table配置
  controlInfoConfig: TableConfig;
  @ViewChild('controlInfoTemp') controlInfoTemp: TemplateRef<any>;
  constructor(private $lockService: LockService,
              private $nzI18n: NzI18nService,
              private $facilityService: FacilityService,
              private $dateHelper: DateHelperService,
              private $wsService: NativeWebsocketImplService,
              private $nzNotificationService: NzNotificationService,
              private $refresh: FacilityMissionService,
              private $deviceStatisticalService: DeviceStatisticalService,
              private $timerSelectorService: TimerSelectorService,
              private $modal: NzModalService,
              private $modalService: FiLinkModalService) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
    this.commonLanguage = this.$nzI18n.getLocaleData('common');
    const weekDate = this.$timerSelectorService.getWeekRange();
    this.deviceTypeCode = DeviceTypeCode;
    this.hostTypeCode = HostTypeEnum;
    this.lockStatus = lockStatusEnum;
    this.moduleTypeCode = moduleTypeCode;
    this.moduleTypeValue = moduleTypeName;
    this.activeStatus = activeStatusEnum;
    this._dateRange = [new Date(weekDate[0]), new Date(weekDate[1])];
    this.dateRange = [new Date(weekDate[0]), new Date(weekDate[1])];
    // 获取锁信息
    this.getLockInfo();
    // 获取主控信息
    this.getLockControlInfo();
    // 获取传感器阈值
    this.getSensorTypeData();
    // 开锁消息的监听
    this.$refresh.refreshChangeHook.subscribe((event) => {
      if (event) {
        this.getLockInfo(true);
      }
    });
    const start = CommonUtil.dateFmt('yyyyMMdd', this.timeConvert(this._dateRange[0]));
    const end = CommonUtil.dateFmt('yyyyMMdd', this.timeConvert(this._dateRange[1]));
    this.queryUnlockingTimesByDeviceId(start, end);
    // 删除主控table配置
    this.controlInfoConfig = {
      noIndex: true,
      columnConfig: [
        {type: 'serial-number', width: 62, title: this.language.serialNumber},
        {title: this.language.hostName, key: 'hostName', width: 100},
        {title: this.language.operate, key: '', width: 80},
      ],
      operation: [
        {
          text: this.language.deleteHandle,
          permissionCode: '03-1-5-5',
          needConfirm: true,
          className: 'fiLink-delete red-icon',
          handle: (currentIndex) => {
            this.$lockService.deleteLockAndControlById({controlId: currentIndex.hostId}).subscribe((result: Result) => {
              if (result.code === 0) {
                this.$modalService.success(result.msg);
                location.reload();
              } else {
                this.$modalService.error(result.msg);
              }
            });
          }
        },
      ],
    };
  }

  /**
   * 电子锁统计时间框值变化
   * param event
   */
  onChange(event) {
    if (!event) {
      this.openCountLoading = true;
      const start = CommonUtil.dateFmt('yyyyMMdd', this.timeConvert(this._dateRange[0]));
      const end = CommonUtil.dateFmt('yyyyMMdd', this.timeConvert(this._dateRange[1]));
      this.queryUnlockingTimesByDeviceId(start, end);
    }
  }

  /**
   * 传感器统计时间框值变化
   * param event
   */
  sensorDateChange(event) {
    if (!event) {
      this.sensorLoading = true;
      this.getSensorTypeData();
    }
  }

  ngOnDestroy(): void {
    if (this.lockTimer) {
      window.clearInterval(this.lockTimer);
    }
    if (this.controlLoopTimer) {
      window.clearInterval(this.controlLoopTimer);
    }
  }

  /**
   * 获取电子锁信息
   * param {boolean} noFirst 不是第一次请求防止正在勾选设施开锁进行了刷新
   */
  getLockInfo(noFirst = false) {
    this.$lockService.getLockInfo(this.deviceId).subscribe((result: Result) => {
      if (result.data && result.data.length) {
        if (noFirst) {
          result.data.forEach((item: any, index) => {
            if (this.deviceType === DeviceTypeCode.Well) {
              // 外盖
              if (item.doorNum === wellCover.outCover) {
                this.lockInfo[0].outCoverStatus = item.doorStatus;
              } else {
                this.lockInfo[0].innerCoverStatus = item.doorStatus;
                this.lockInfo[0].lockStatus = item.lockStatus;
                this.lockInfo[0].doorNum = item.doorNum;
              }
            } else {
              this.lockInfo[index].lockStatus = item.lockStatus;
              this.lockInfo[index].doorStatus = item.doorStatus;
            }
          });
        } else {
          // 当为设施类型为人井内外盖合并为一条数据
          if (this.deviceType === DeviceTypeCode.Well) {
            this.lockInfo = [{}];
            result.data.forEach(item => {
              // 外盖
              if (item.doorNum === '2') {
                this.lockInfo[0].outCoverStatus = item.doorStatus;
              } else {
                this.lockInfo[0].innerCoverStatus = item.doorStatus;
                this.lockInfo[0].lockStatus = item.lockStatus;
                this.lockInfo[0].doorNum = item.doorNum;
              }
            });
          } else {
            this.lockInfo = result.data || [];
          }
        }
      }
    });
  }

  /**
   * 刷新
   */
  refreshLock() {
    this.allChecked = false;
    this.indeterminate = false;
    this.getLockInfo();
  }

  /**
   * 开锁
   */
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
        }, 20000);
      } else {
        this.$modalService.error(result.msg);
      }
    });
  }

  /**
   * 获取主控信息
   */
  getLockControlInfo() {
    this.$lockService.getLockControlInfo(this.deviceId).subscribe((result: Result) => {
      this.lockControlInfoAll = result.data;
      if (result.data && result.data[0]) {
        this.lockControlInfo = this.lockControlInfoAll[0];
        this.convertDeviceInfo(this.lockControlInfo);
        // 获取传感器阈值
        // this.getSensorTypeData();
        if (result.data[0].actualValue) {
          this.lockControlInfo.actualValue = JSON.parse(this.lockControlInfo.actualValue);
          this.convertActualValue(this.lockControlInfo.actualValue);
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

  /**
   * 全选
   * param event
   */
  checkAll(event) {
    this.indeterminate = false;
    this.lockInfo.forEach(item => {
      item.checked = event;
    });
  }

  // 点击锁
  checkItem() {
    this.checkStatus();
  }

  // 改变勾选状态
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
      this.convertActualValue(this.lockControlInfo.actualValue);
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
    //  转化供电方式 太阳能
    if (data.sourceType) {
      data['_sourceType'] = getSourceType(this.$nzI18n, data.sourceType);
    }
    if (data.solarCell) {
      data['_solarCell'] = getSolarCell(this.$nzI18n, data.solarCell);
    }
  }

  /**
   * 转换电子锁内部ActualValue
   * param data {any}
   */
  convertActualValue(data: any) {
    //  转换运营商
    if (data.operator && data.operator.data) {
      data.operator['value'] = getOperator(this.$nzI18n, data.operator.data);
    }
    // 转换电量 当供电方式为适配器的时候电量保持100%
    if (this.lockControlInfo.sourceType === sourceType.adapter) {
      data.electricity.data = 100;
    }
  }

  /**
   * 点击现在传感器类型
   * param event
   */
  selectItem(event) {
    this.sensorType = event;
    this._option = DeviceChartUntil.setLineTimeChartOption(this.setLineChart(this.deviceSensorValue, this.sensorType));
  }

  /**
   * 获取传感器数据
   */
  getSensorTypeData() {
    const statisticalData = {};
    statisticalData['deviceId'] = this.deviceId;
    statisticalData['startTime'] = this.dateRange[0].getTime();
    statisticalData['endTime'] = this.dateRange[1].getTime();
    this.$deviceStatisticalService.queryDeviceSensor(statisticalData).subscribe((result: Result) => {
      this.sensorLoading = false;
      this.deviceSensorValue = result.data;
      this._option = DeviceChartUntil.setLineTimeChartOption(this.setLineChart(result.data, this.sensorType));
    });
  }

  /**
   * 查询开锁次数
   * param start
   * param end
   */
  queryUnlockingTimesByDeviceId(start, end) {
    const body = {deviceId: this.deviceId, startDate: start, endDate: end};
    this.$lockService.queryUnlockingTimesByDeviceId(body).subscribe((result: Result) => {
      this.openCountLoading = false;
      const data = result.data || [];
      const date = [];
      const arr = [];
      this.openCount = 0;
      data.forEach((item: any) => {
        arr.push([CommonUtil.dateFmt('yyyy-MM-dd', this.$dateHelper.parseDate(item.statisticsDate)), item.unlockingCount]);
        this.openCount += Number(item.unlockingCount);
      });
      if (arr.length === 0) {
        this.noOpenCount = true;
      } else {
        this.noOpenCount = false;
      }
      this.option = DeviceChartUntil.setUnlockingChartOption(date, arr);
    });
  }

  /**
   * 转换line数据
   * param _data
   * param event
   * returns {any[]}
   */
  setLineChart(_data, event) {
    const data = [];
    Object.keys(_data).forEach(key => {
      const dataObj = {};
      const dataItem = [];
      let chartData = [];
      _data[key].forEach(_item => {
        chartData.push(CommonUtil.dateFmt('yyyy-MM-dd hh:mm:ss', new Date(_item.currentTime)));
        chartData.push(Number(_item[event]));
        dataItem.push(chartData);
        dataObj['name'] = key;
        dataObj['value'] = dataItem;
        chartData = [];
      });
      data.push(dataObj);

    });
    if (data.length === 0) {
      this.noSensorData = true;
    } else {
      this.noSensorData = false;
    }
    return data;
  }

  /**
   * 时间转换
   * param time
   */
  timeConvert(time) {
    return new Date(CommonUtil.sendBackEndTime(time.getTime()));
  }

  deleteControl() {
    const modal = this.$modal.create({
      nzTitle: this.language.viewHost,
      nzContent: this.controlInfoTemp,
      nzOkText: this.language.handleCancel,
      nzCancelText: this.language.handleOk,
      nzOkType: 'danger',
      nzClassName: 'custom-create-modal',
      nzMaskClosable: false,
      nzFooter: [
        {
          label: this.language.handleCancel,
          type: 'danger',
          onClick: () => {
            modal.destroy();
          }
        },
      ]
    });
  }
}
