import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {NzI18nService} from 'ng-zorro-antd';

@Component({
  selector: 'app-ref-alarm',
  templateUrl: './ref-alarm.component.html',
  styleUrls: ['./ref-alarm.component.scss']
})
export class RefAlarmComponent implements OnInit, OnChanges {
  @Input() refAlarmMessage;
  language;
  constructor(private $Nz18n: NzI18nService) {
  }

  ngOnInit() {
    this.language = this.$Nz18n.getLocale();
    console.log(this.refAlarmMessage);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.refAlarmMessage) {
      console.log(this.refAlarmMessage.alarmName);
    }
  }

}
