import { Component, OnInit } from '@angular/core';
import {NzI18nService} from 'ng-zorro-antd';

@Component({
  selector: 'add-alarm-filter',
  templateUrl: './add-alarm-filter.component.html',
  styleUrls: ['./add-alarm-filter.component.scss']
})
export class AddAlarmFilterComponent implements OnInit {
  pageTitle = '新增告警过滤';
  formStatus;
  formColumn;
  language;
  isLoading: boolean = true;
  constructor(public $nzI18n: NzI18nService) { }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('alarm');
    this.initForm();
  }

  formInstance(event) {
    this.formStatus = event.instance;
  }
  public initForm() {
    this.formColumn = [
      {
        label: this.language.name,
        key: 'name',
        type: 'input',
        require: true,
        width: 350,
        labelHeight: 80,
        rule: [{ required: true }, { minLength: 6 }, { maxLength: 32 },
          { pattern: /^[A-Za-z0-9\u4e00-\u9fa5\-_ ]+$/, msg:  '错误提示'}],
      },
      {
        label: this.language.remark,
        key: 'remark',
        type: 'input',
        require: true,
        width: 350,
        rule: [{ required: true }, { maxLength: 32 }],
        modelChange: (controls, event, key, formOperate) => { }
      },
      {
        label: this.language.alarmobject,
        key: 'alarmobject',
        type: 'input',
        require: false,
        width: 350,
      },
      {
        label: this.language.alarmName,
        key: 'alarmName',
        type: 'input',
        require: false,
        width: 350,
      },
      {
        label: this.language.isInventory,
        key: 'isInventory',
        type: 'radio',
        require: true,
        width: 350,
        rule: [{ required: true }],
        initialValue: '1',
        radioInfo: {
          data: [
            { label: this.language.enable, value: '1' },
            { label: this.language.disable, value: '0' },
          ],
          label: 'label',
          value: 'value'
        },
        modelChange: (controls, event, key, formOperate) => { }
      },
      {
        label: this.language.deptId,
        key: 'deptId',
        type: 'custom',
        require: true,
        width: 350,
        rule: [{ required: true }],
        asyncRules: [],
        template: ''
      },
      {
        label: this.language.roleId,
        key: 'roleId',
        type: 'select',
        require: true,
        width: 350,
        rule: [{ required: true }],
        asyncRules: [],
        selectInfo: {
          data: '',
          label: 'label',
          value: 'value'
        },
        modelChange: (controls, $event, key) => {
        },
        openChange: (a, b, c) => { }
      },
      {
        label: this.language.address,
        key: 'address',
        type: 'input',
        require: false,
        width: 350,
        rule: [{ maxLength: 64 }],
        asyncRules: []
      },
      {
        label: this.language.countValidityTime,
        key: 'countValidityTime',
        type: 'custom',
        require: false,
        width: 350,
        col: 24,
        rule: [],
        asyncRules: [],
        template: '',
        modelChange: (controls, $event, key) => {
        },
        openChange: (a, b, c) => {
        }
      },
    ];
    // setTimeout(() => {
    //   this.formStatus.group.controls['maxUsers'].disable();
    // }, 0);
  }

  submit() {}
  cancel() {}
}
