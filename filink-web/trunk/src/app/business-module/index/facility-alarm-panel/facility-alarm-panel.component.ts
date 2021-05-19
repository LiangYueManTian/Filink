import {AfterViewInit, Component, Input} from '@angular/core';
import {NzI18nService} from 'ng-zorro-antd';
import {Router} from '@angular/router';
import {IndexTable} from '../index.table';
import {MapService} from '../../../core-module/api-service/index/map';
import {Result} from '../../../shared-module/entity/result';
import {PictureViewService} from '../../../core-module/api-service/facility/picture-view-manage/picture-view.service';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';
import {ImageViewService} from '../../../shared-module/service/picture-view/image-view.service';
import {index_alarm_chat, index_day_number} from '../index-const';
import {AlarmService} from '../../../core-module/api-service/alarm/alarm-manage';
import {alarmNameColorEnum, getAlarmType} from '../../facility/facility.config';
import {TimerSelectorService} from '../../facility/facility-manage/photo-viewer/timer-selector/timer-selector.service';
import {CommonUtil} from '../../../shared-module/util/common-util';
/**
 * 设施告警组件
 */
@Component({
  selector: 'app-facility-alarm-panel',
  templateUrl: './facility-alarm-panel.component.html',
  styleUrls: ['./facility-alarm-panel.component.scss'],
  providers: [TimerSelectorService]
})
export class FacilityAlarmPanelComponent extends IndexTable implements AfterViewInit {
  // 设施id
  @Input() facilityId: string;
  // 下拉框选项
  public  selectedControl = index_alarm_chat.oneWeek;
  // 当前事件
  public  countTime;
  // 是否显示历史告警表格
  public  noHistoryAlarmTable = false;
  // 显示历史告警表格
  public  alarmChatTime  = [
    {
      label: this.indexLanguage.AllTime.oneWeek,
      type: 1,
    },
    {
      label: this.indexLanguage.AllTime.oneMonth,
      type: 2,
    },
    {
      label: this.indexLanguage.AllTime.threeMonth,
      type: 3,
    },
  ];
  // 环形图配置
  public alarmChartOption = {};
  constructor(public $nzI18n: NzI18nService,
              private $router: Router,
              public $alarmService: AlarmService,
              private $timerSelectorService: TimerSelectorService,
              private $mapService: MapService,
              private $message: FiLinkModalService,
              private $pictureViewService: PictureViewService,
              private $imageViewService: ImageViewService,
              ) {
    super($nzI18n);
  }

  ngAfterViewInit() {
    // 初始化一周数据
    this.controlChange();
  }
  /**
   * 设施统计当前告警名称信息
   */
  getQueryAlarmLevelGroups(body) {
      this.$alarmService.currentSourceNameStatistics(body).subscribe((result: Result) => {
        if (result.code === 0) {
          if (result.data.length === 0) {
            return;
          } else {
            this.noHistoryAlarmTable = true;
            const arr = [];
            Object.keys(result.data).forEach(item => {
              const value = {    value: result.data[item],
                name: getAlarmType(this.$nzI18n, item),
                itemStyle: {color: alarmNameColorEnum[item]}
              };
              arr.push(value);
            });
            this.alarmChartOption = {
              tooltip: {
                trigger: 'item',
                formatter: '{a} <br/>{b}: {c} ({d}%)'
              },
              legend: {
                orient: 'vertical',
                left: 'left',
                data: []
              },
              series: [
                {
                  name: this.indexLanguage.dataShare,
                  type: 'pie',
                  radius: ['50%', '70%'],
                  avoidLabelOverlap: false,
                  label: {
                    normal: {
                      show: false,
                      position: 'center'
                    },
                    emphasis: {
                      show: true,
                      textStyle: {
                        fontSize: '16',
                        fontWeight: 'bold'
                      }
                    }
                  },
                  labelLine: {
                    normal: {
                      show: false
                    }
                  },
                  data: arr
                }
              ]
            };
          }
        }
      });
    }

  /**
   * 告警chart 时间段
   */
  public  controlChange() {

    if (this.selectedControl === index_alarm_chat.oneWeek) {
      this.countTime = this.$timerSelectorService.getDateRang(index_day_number.oneWeek);
    } else if (this.selectedControl === index_alarm_chat.oneMonth) {
      this.countTime = this.$timerSelectorService.getDateRang(index_day_number.oneMonth);
    } else if (this.selectedControl === index_alarm_chat.threeMonth) {
      this.countTime = this.$timerSelectorService.getDateRang(index_day_number.threeMonth);
    }
    const params = {
      deviceId: this.facilityId,
      beginTime: CommonUtil.getTimeStamp(new Date(this.countTime[0])),
      endTime: CommonUtil.getTimeStamp(new Date(this.countTime[1]))
    };
    this.getQueryAlarmLevelGroups(params);
  }

  /**
   * 告警回传  查看图片
   */
  alarmEvent(event) {
    if (event.type === 'photoView') {
      this.getPicUrlByProcId(event.id);
    }
  }

  /**
   * 查看图片
   * param ids
   */
  getPicUrlByProcId(id) {
    this.$pictureViewService.getPicUrlByAlarmId(id).subscribe((result: Result) => {
      if (result.code === 0) {
        if (result.data.length === 0) {
          this.$message.warning(this.commonLanguage.noPictureNow);
        } else {
          this.$imageViewService.showPictureView(result.data);
        }
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * 跳转至当前告警列表，并过滤出该设施的告警
   */
  goToCurrentAlarmList() {
    this.$router.navigate([`/business/alarm/current-alarm`], {queryParams: {deviceId: this.facilityId}}).then();
  }

  /**
   * 跳转至历史告警列表，并过滤出该设施的告警
   */
  goToHistoryAlarmList() {
    this.$router.navigate([`/business/alarm/history-alarm`], {queryParams: {deviceId: this.facilityId}}).then();
  }
}
