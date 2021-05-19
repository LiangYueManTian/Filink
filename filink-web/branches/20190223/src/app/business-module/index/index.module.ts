import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {IndexComponent} from './index.component';
import {SharedModule} from '../../shared-module/shared-module.module';
import {RouterModule} from '@angular/router';
import {ROUTER_CONFIG} from './index.routes';
import {BMapComponent} from './b-map/b-map.component';
import {GMapComponent} from './g-map/g-map.component';
import {FacilityListComponent} from './facility-list/facility-list.component';
import {LogicAreaComponent} from './logic-area/logic-area.component';
import {FacilityDetailPanelComponent} from './facility-detail-panel/facility-detail-panel.component';
import {FacilityAlarmPanelComponent} from './facility-alarm-panel/facility-alarm-panel.component';
import {LogOrderPanelComponent} from './log-order-panel/log-order-panel.component';
import {ClustererFacilityListComponent} from './clusterer-facility-list/clusterer-facility-list.component';
import { MapComponent } from './map/map.component';
import { FacilityTypeComponent } from './facility-type/facility-type.component';
import { FacilityStatusComponent } from './facility-status/facility-status.component';

@NgModule({
  declarations: [
    IndexComponent,
    BMapComponent,
    GMapComponent,
    FacilityListComponent,
    LogicAreaComponent,
    FacilityDetailPanelComponent,
    FacilityAlarmPanelComponent,
    LogOrderPanelComponent,
    ClustererFacilityListComponent,
    MapComponent,
    FacilityTypeComponent,
    FacilityStatusComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule.forChild(ROUTER_CONFIG)
  ]
})
export class IndexModule {
}
