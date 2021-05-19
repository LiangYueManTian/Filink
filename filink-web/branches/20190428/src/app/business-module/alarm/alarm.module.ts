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
import {AlarmFilterComponent} from './alarm-manage/alarm-set/alarm-filter/alarm-filter.component';
import {ModifyAlarmFilterComponent} from './alarm-manage/alarm-set/alarm-filter/modify-alarm-filter/modify-alarm-filter.component';
import {AddAlarmFilterComponent} from './alarm-manage/alarm-set/alarm-filter/add-alarm-filter/add-alarm-filter.component';
import { AlarmFiltrationComponent } from './alarm-manage/alarm-filtration/alarm-filtration.component';
import { AlarmFiltrationAddComponent } from './alarm-manage/alarm-filtration/alarm-filtration-add/alarm-filtration-add.component';
import { AlarmRemoteNotificationComponent } from './alarm-manage/alarm-remote-notification/alarm-remote-notification.component';
import { AlarmWorkOrderComponent } from './alarm-manage/alarm-work-order/alarm-work-order.component';
import { RemoteAddComponent } from './alarm-manage/alarm-remote-notification/remote-add/remote-add.component';
import { WorkOrderAddComponent } from './alarm-manage/alarm-work-order/work-order-add/work-order-add.component';
import {FacilityUtilService} from '../facility/facility-util.service';
import { CurrentAlarmAddComponent } from './alarm-manage/current-alarm/current-alarm-add/current-alarm-add.component';
import { TemplateTableComponent } from './alarm-manage/current-alarm/template-table/template-table.component';
import { AlarmFiltrationRuleComponent } from './alarm-manage/alarm-filtration/alarm-filtration-rule/alarm-filtration-rule.component';

@NgModule({
    declarations: [AlarmComponent, CurrentAlarmComponent, HistoryAlarmComponent,
      AlarmManageComponent,
        AlarmSetComponent, CurrentAlarmSetComponent,
        HistoryAlarmSetComponent, AlarmLevelSetComponent, AlarmFilterComponent,
        ModifyAlarmFilterComponent, AddAlarmFilterComponent, AlarmFiltrationComponent, AlarmFiltrationAddComponent,
        AlarmRemoteNotificationComponent, AlarmWorkOrderComponent, RemoteAddComponent, WorkOrderAddComponent,
        CurrentAlarmAddComponent, TemplateTableComponent, AlarmFiltrationRuleComponent],
    imports: [
        CommonModule,
        SharedModule,
        RouterModule.forChild(ROUTER_CONFIG)
    ],
    providers: [FacilityUtilService]
})
export class AlarmModule {
}
