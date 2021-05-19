import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlarmComponent } from './alarm.component';
import { SharedModule } from '../../shared-module/shared-module.module';
import { RouterModule } from '@angular/router';
import { ROUTER_CONFIG } from './alarm.routes';
// import { AlarmManageComponent } from './alarm-manage/alarm-manage.component';
import { CurrentAlarmComponent } from './alarm-manage/current-alarm/current-alarm.component';
import { HistoryAlarmComponent } from './alarm-manage/history-alarm/history-alarm.component';
import { AlarmSetComponent } from './alarm-manage/alarm-set/alarm-set.component';
import { CurrentAlarmSetComponent } from './alarm-manage/alarm-set/current-alarm-set/current-alarm-set.component';
import { HistoryAlarmSetComponent } from './alarm-manage/alarm-set/history-alarm-set/history-alarm-set.component';
import { AlarmLevelSetComponent } from './alarm-manage/alarm-set/current-alarm-set/alarm-level-set/alarm-level-set.component';
import {AlarmManageComponent} from './alarm-manage/alarm-manage.component';

@NgModule({
    declarations: [AlarmComponent, CurrentAlarmComponent, HistoryAlarmComponent,
      AlarmManageComponent,
        AlarmSetComponent, CurrentAlarmSetComponent,
        HistoryAlarmSetComponent, AlarmLevelSetComponent
    ],
    imports: [
        CommonModule,
        SharedModule,
        RouterModule.forChild(ROUTER_CONFIG)

    ]
})
export class AlarmModule {
}
