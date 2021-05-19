import { Component, OnInit } from '@angular/core';
import { FormItem } from '../../../../../shared-module/component/form/form-config';
import { FormOperate } from '../../../../../shared-module/component/form/form-opearte.service';
import { NzI18nService } from 'ng-zorro-antd';
import { AlarmLanguageInterface } from '../../../alarm-language.interface';
import { AlarmService } from '../../../../../core-module/api-service/alarm/alarm-manage/alarm.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FiLinkModalService } from '../../../../../shared-module/service/filink-modal/filink-modal.service';

@Component({
  selector: 'app-history-alarm-set',
  templateUrl: './history-alarm-set.component.html',
  styleUrls: ['./history-alarm-set.component.scss']
})
export class HistoryAlarmSetComponent implements OnInit {
  pageTitle: string;
  formColumn: FormItem[] = [];
  formStatus: FormOperate;
  public language: AlarmLanguageInterface;
  constructor(
    private $nzI18n: NzI18nService,
    private $alarmService: AlarmService,
    private $active: ActivatedRoute,
    private $router: Router,
    private $message: FiLinkModalService) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('alarm');
    this.initForm();
    this.initColumn();
    this.pageTitle = this.language.historyAlarmSettings;
  }

  formInstance(event) {
    this.formStatus = event.instance;
  }

  private initColumn() {
    this.formColumn = [
      {
        label: this.language.delayTime,
        key: 'delay',
        type: 'input',
        initialValue: 2,
        require: true,
        rule: [{ required: true },
          {pattern: /^([1-9]\d*|[0]{1,1})$/, msg: this.language.enterNormalTime },
          { min: 0, max: 720, msg: this.language.enterMaxTime}],
        asyncRules: []
      }
    ];
  }

  /**
  * 初始化设置
  */
  initForm() {
    this.$alarmService.queryAlarmDelay(null).subscribe(res => {
      if (res['code'] === 0) {
        const initData = res['data']['delay'];
        this.formStatus.resetData({ delay: String(initData) });
      }
    });
  }

  /**
   * 历史告警设置
   */
  submit() {
    const data = this.formStatus.getData();
    const formData = { 'alarmDelay': data };
    this.$alarmService.updateAlarmDelay(formData).subscribe(res => {
      if (res['code'] === 0) {
        this.$message.success(res['msg']);
      } else {
        this.$message.error(res['msg']);
      }
    });
  }


}
