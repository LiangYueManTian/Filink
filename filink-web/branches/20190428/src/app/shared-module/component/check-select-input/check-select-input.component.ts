import {Component, forwardRef, Input, OnInit} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';

@Component({
  selector: 'check-select-input',
  templateUrl: './check-select-input.component.html',
  styleUrls: ['./check-select-input.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CheckSelectInputComponent),
      multi: true
    }
  ]
})
export class CheckSelectInputComponent implements OnInit , ControlValueAccessor {
  @Input() placeholder = '设施类型';
  @Input() checkList: Array<Option> = [{
    label: '光交箱',
    value: 1
  }, {
    label: '人井',
    value: 2
  }, {
    label: '配线架',
    value: 3
  }, {
    label: '接头盒',
    value: 4
  }, {
    label: '分纤箱',
    value: 5
  }];
  isCollapsed = true;
  checkedList: Array<Option> = [];
  _checkedStr = '';
  onModelChange: Function = () => { };
  constructor() { }

  ngOnInit() {
  }

  get checkedStr(): any {
    return this._checkedStr;
  }

  set checkedStr(v: any) {
      this._checkedStr = v;
      this.onModelChange(this.checkedList);
  }
  // 给自定义组件赋值时调用
  writeValue(value: Array<any>) {
    if (value) {
      this.checkedList = value;
      this.checkList.forEach(item => {
        (value.filter(el => el.label === item.label)).length > 0 ? item.checked = true : item.checked = false;
      });
      this.checkItem();
    } else {
      this.checkedList = [];
    }
  }
  // 勾选事件
  checkItem(obj?: Option) {
    if (obj) {
      this.checkedList = this.checkList.map(item => {
        if (item.value === obj.value) {
          item.checked = !obj.checked;
        }
        return item;
      });
    }
    this.checkedList = this.checkList.filter(item => item.checked);
    const arr = this.checkedList.map(item => item.label);
    this.checkedStr = arr.join('，');
  }

  registerOnChange(fn: any) {
    this.onModelChange = fn;
  }
  registerOnTouched(fn: any) {
  }
}

export interface Option {
  label: string;
  value: any;
  checked?: boolean;
}
