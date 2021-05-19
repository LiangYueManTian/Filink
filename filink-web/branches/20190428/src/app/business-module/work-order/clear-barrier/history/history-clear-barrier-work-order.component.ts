import {AfterViewInit, Component, OnInit} from '@angular/core';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {ClearBarrierService} from '../../../../core-module/api-service/work-order/clear-barrier';
import {Result} from '../../../../shared-module/entity/result';
import {NzI18nService} from 'ng-zorro-antd';
import {ChartUtil} from '../../../../shared-module/util/chart-util';
import {FACILITY_TYPE_NAME} from '../../../../shared-module/const/facility';
import {IndexLanguageInterface} from '../../../../../assets/i18n/index/index.language.interface';
import {WorkOrderLanguageInterface} from '../../../../../assets/i18n/work-order/work-order.language.interface';

@Component({
  selector: 'app-history-clear-barrier-work-order',
  templateUrl: './history-clear-barrier-work-order.component.html',
  styleUrls: ['./history-clear-barrier-work-order.component.scss']
})
export class HistoryClearBarrierWorkOrderComponent implements OnInit, AfterViewInit {
  canvasRadius;
  canvasLength;
  ringChartOption;
  pieChartOption;
  barChartOption;
  errorReasonStatisticsChartType;   // 故障原因统计报表显示的类型  chart 图表   text 文字
  processingSchemeStatisticsChartType;   // 处理方案统计报表显示的类型  chart 图表   text 文字
  deviceTypeStatisticsChartType;   // 设施类型统计报表显示的类型  chart 图表   text 文字
  statusStatisticsChartType;   // 工单状态统计报表显示的类型  chart 图表   text 文字
  completedPercent;  // 已完工工单百分比
  singleBackPercent;  // 已退单工单百分比
  indexLanguage: IndexLanguageInterface;
  workOrderLanguage: WorkOrderLanguageInterface;

  constructor(public $nzI18n: NzI18nService,
              private $clearBarrierService: ClearBarrierService,
              private $message: FiLinkModalService,
  ) {
  }

  ngOnInit() {
    this.indexLanguage = this.$nzI18n.getLocaleData('index');
    this.workOrderLanguage = this.$nzI18n.getLocaleData('workOrder');
    this.canvasRadius = 45;
    this.canvasLength = this.canvasRadius * 2;
  }

  ngAfterViewInit() {
    setTimeout(() => {
      // this.getStatisticsByErrorReason();
      // this.getStatisticsByProcessingScheme();
      // this.getStatisticsByDeviceType();
      // this.getStatisticsByStatus();
    }, 0);
  }

  /**
   * 获取故障原因统计
   */
  getStatisticsByErrorReason() {
    this.$clearBarrierService.getStatisticsByErrorReason().subscribe((result: Result) => {
      if (result.code === 0) {
        console.log(result);
        if (result.data.length === 0) {
          this.errorReasonStatisticsChartType = 'text';
        } else {
          this.errorReasonStatisticsChartType = 'chart';
          const data = [], name = [];
          result.data.forEach(item => {
            name.push(item.error_reason);
            data.push({
              value: item.num,
              name: item.error_reason
            });
          });
          this.ringChartOption = ChartUtil.setRingChartOption(data, name);
        }
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * 获取设施类型统计
   */
  getStatisticsByDeviceType() {
    this.$clearBarrierService.getStatisticsByDeviceType().subscribe((result: Result) => {
      if (result.code === 0) {
        console.log(result);
        if (result.data.length === 0) {
          this.deviceTypeStatisticsChartType = 'text';
        } else {
          this.deviceTypeStatisticsChartType = 'chart';
          const name = [], data = [];
          result.data.forEach(item => {
            name.push(this.getFacilityTypeName(item.deviceType));
            data.push(item.num);
          });
          this.barChartOption = ChartUtil.setBarChartOption(data, name);
        }
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * 获取处理方案统计
   */
  getStatisticsByProcessingScheme() {
    this.$clearBarrierService.getStatisticsByProcessingScheme().subscribe((result: Result) => {
      if (result.code === 0) {
        console.log(result);
        if (result.data.length === 0) {
          this.processingSchemeStatisticsChartType = 'text';
        } else {
          this.processingSchemeStatisticsChartType = 'chart';
          const data = [], name = [];
          result.data.forEach(item => {
            name.push(item.processing_scheme);
            data.push({
              value: item.num,
              name: item.processing_scheme
            });
          });
          this.pieChartOption = ChartUtil.setPieChartOption(data, name);
        }
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * 获取工单状态统计
   */
  getStatisticsByStatus() {
    this.$clearBarrierService.getStatisticsByStatus().subscribe((result: Result) => {
      if (result.code === 0) {
        if (result.data.hisTotalCount === 0) {
          this.statusStatisticsChartType = 'text';
        } else {
          this.statusStatisticsChartType = 'chart';
          const completedCount = result.data.completed || 0;
          const singleBackCount = result.data.singleBack || 0;
          const totalCount = result.data.hisTotalCount;
          setTimeout(() => {
            this.getPercent('canvas_completed', '#339eff', completedCount, totalCount);
            this.getPercent('canvas_singleBack', '#65d688', singleBackCount, totalCount);
            this.completedPercent = (100 * completedCount / totalCount).toFixed(2) + '%';
            this.singleBackPercent = (100 * singleBackCount / totalCount).toFixed(2) + '%';
          }, 0);
        }
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  // 计算环的角度
  getPercent(id, color, num = 0, total) {
    console.log((-0.5 + (num / total) * 2));
    const endingAngle = (-0.5 + (num / total) * 2) * Math.PI;
    this.drawCircle(id, color, endingAngle);
  }

  // 画环
  drawCircle(id, color, endingAngle = 1.5 * Math.PI, startingAngle = -0.5 * Math.PI) {
    const canvas = document.getElementById(id);
    const context = canvas['getContext']('2d');
    const centerX = this.canvasRadius;
    const centerY = this.canvasRadius;
    context.beginPath();
    context.lineWidth = 8;
    context.strokeStyle = '#eff0f4';
    // 创建变量,保存圆弧的各方面信息
    const radius = this.canvasRadius - context.lineWidth / 2;
    // 画完整的环
    context.arc(centerX, centerY, radius, 0, 2 * Math.PI);
    context.stroke();

    context.beginPath();
    // 画部分的环
    // context.lineWidth = 3;
    context.strokeStyle = color;
    context.arc(centerX, centerY, radius, startingAngle, endingAngle);
    context.stroke();
  }

  /**
   * 获取设施类型名称
   * param deviceType
   * returns {any | string}
   */
  public getFacilityTypeName(deviceType) {
    return deviceType ? this.indexLanguage[FACILITY_TYPE_NAME[deviceType]] : '';
  }
}
