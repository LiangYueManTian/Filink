import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TimerSelectorService} from './timer-selector.service';
import {DateType, TimeItem} from './timeSelector';
import {CommonUtil} from '../../../../../shared-module/util/common-util';

@Component({
  selector: 'app-timer-selector',
  templateUrl: './timer-selector.component.html',
  styleUrls: ['./timer-selector.component.scss'],
  providers: [TimerSelectorService]
})
export class TimerSelectorComponent implements OnInit {
  @Input() timeList: Array<TimeItem>;
  @Output() changeFilter = new EventEmitter();
  constructor(private $timerSelectorService: TimerSelectorService) { }
  // 默认为今天
  dateType: DateType = DateType.DAY;
  // 日期
  date = null;
  ngOnInit() {
    this.dateType = this.timeList[0].value;
    setTimeout(() => {
      this.changeDateType(this.dateType);
    }, 0);
  }

  /**
   * 切换日期类型
   * param item
   */
  changeDateType(item) {
    this.dateType = item;
    switch (this.dateType) {
      case DateType.DAY:
        this.date = this.$timerSelectorService.getDayRange();
        break;
      case DateType.WEEK:
        this.date = this.$timerSelectorService.getWeekRange();
        break;
      case DateType.MONTH:
        this.date = this.$timerSelectorService.getMonthRange();
        break;
      case DateType.YEAR:
        this.date = this.$timerSelectorService.getYearRange();
        break;
      default:
        this.date = [CommonUtil.dateFmt('yyyy-MM-dd hh:mm:ss', this.date[0]), CommonUtil.dateFmt('yyyy-MM-dd 23:59:59', this.date[1])];
    }
    this.changeFilter.emit({startTime: this.date[0], endTime: this.date[1]});
  }
}
