/**
 * Created by xiaoconghu on 2019/1/15.
 */
import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';

@Component({
  selector: 'xc-date-picker',
  templateUrl: './date-picker.component.html',
  styleUrls: ['./date-picker.component.scss']
})
export class DatePickerComponent implements OnInit, OnChanges {
  startValue: Date = null;
  endValue: Date = null;
  endOpen: boolean = false;
  @Input()
  rangValue;
  @Output()
  rangValueChange = new EventEmitter();
  constructor() {
  }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes && changes.rangValue) {
      if (changes.rangValue.currentValue === undefined && changes.rangValue.currentValue === null) {
        this.startValue = null;
        this.endValue = null;
      }
    }
  }


  disabledStartDate = (startValue: Date): boolean => {
    if (!startValue || !this.endValue) {
      return false;
    }
    return startValue.getTime() > this.endValue.getTime();
  }

  disabledEndDate = (endValue: Date): boolean => {
    if (!endValue || !this.startValue) {
      return false;
    }
    return endValue.getTime() <= this.startValue.getTime();
  }

  onStartChange(date: Date): void {
    this.startValue = date;
    this.rangValueChange.emit([this.startValue, this.endValue]);
  }

  onEndChange(date: Date): void {
    this.endValue = date;
    this.rangValueChange.emit([this.startValue, this.endValue]);
  }

  handleStartOpenChange(open: boolean): void {
    if (!open) {
      this.endOpen = true;
    }
    console.log('handleStartOpenChange', open, this.endOpen);
  }

  handleEndOpenChange(open: boolean): void {
    console.log(open);
    this.endOpen = open;
  }
}
