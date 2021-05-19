import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {IndexComponent} from './index.component';
import {SharedModule} from '../../shared-module/shared-module.module';
import {RouterModule} from '@angular/router';
import {ROUTER_CONFIG} from './index.routes';
import {FacilityListComponent} from './index-facility-list/facility-list.component';
import {LogicAreaComponent} from './logic-area/logic-area.component';
import {FacilityDetailPanelComponent} from './facility-detail-panel/facility-detail-panel.component';
import {FacilityAlarmPanelComponent} from './facility-alarm-panel/facility-alarm-panel.component';
import {LogOrderPanelComponent} from './log-order-panel/log-order-panel.component';
import {ClustererFacilityListComponent} from './clusterer-facility-list/clusterer-facility-list.component';
import {MapComponent} from './map/map.component';
import {FacilityTypeComponent} from './facility-type/facility-type.component';
import {FacilityStatusComponent} from './facility-status/facility-status.component';
import {MyCollectionComponent} from './my-collection/my-collection.component';
import {FacilityLogTableComponent} from './log-order-panel/facility-log-table/facility-log-table.component';
import {ClearBarrierTableComponent} from './log-order-panel/clear-barrier-table/clear-barrier-table.component';
import {InspectionTableComponent} from './log-order-panel/inspection-table/inspection-table.component';
import {CurrentAlarmTableComponent} from './facility-alarm-panel/current-alarm-table/current-alarm-table.component';
import {HistoryAlarmTableComponent} from './facility-alarm-panel/history-alarm-table/history-alarm-table.component';

@NgModule({
  declarations: [
    IndexComponent,
    FacilityListComponent,
    LogicAreaComponent,
    FacilityDetailPanelComponent,
    FacilityAlarmPanelComponent,
    LogOrderPanelComponent,
    ClustererFacilityListComponent,
    MapComponent,
    FacilityTypeComponent,
    FacilityStatusComponent,
    MyCollectionComponent,
    FacilityLogTableComponent,
    ClearBarrierTableComponent,
    InspectionTableComponent,
    CurrentAlarmTableComponent,
    HistoryAlarmTableComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule.forChild(ROUTER_CONFIG)
  ],
  exports: [
    ClearBarrierTableComponent,
    InspectionTableComponent,
    FacilityLogTableComponent,
    CurrentAlarmTableComponent,
    HistoryAlarmTableComponent
  ]
})
export class IndexModule {
}
