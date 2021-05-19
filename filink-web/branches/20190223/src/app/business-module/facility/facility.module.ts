import {NgModule} from '@angular/core';
import {FacilityComponent} from './facility.component';
import {ROUTER_CONFIG} from './facility.routes';
import {RouterModule} from '@angular/router';
import {AreaListComponent} from './area-manage/area-list/area-list.component';
import {AreaDetailComponent} from './area-manage/area-detail/area-detail.component';
import {SharedModule} from '../../shared-module/shared-module.module';
import {
  BasicOperationComponent,
  ConfigurationStrategyComponent,
  FacilityAlarmComponent,
  FacilityDetailComponent,
  FacilityImgViewComponent,
  FacilityListComponent,
  FacilityLogComponent,
  FacilityViewDetailComponent,
  FacilityViewDetailLogComponent,
  InfrastructureDetailsComponent,
  IntelligentEntranceGuardComponent
} from './facility-manage';
import {FacilityUtilService} from './facility-util.service';
import {FacilitySliderComponent} from './facility-manage/facility-list/facility-slider/facility-slider.component';
import {NgxEchartsModule} from 'ngx-echarts';
import {SwiperModule} from 'ngx-swiper-wrapper';
import {SetAreaDeviceComponent} from './area-manage/set-area-device/set-area-device.component';
import {AreaManageComponent} from './area-manage/area-manage.component';

@NgModule({
  declarations: [FacilityComponent, FacilityListComponent,
    AreaManageComponent,
    AreaListComponent, AreaDetailComponent, FacilityDetailComponent,
    FacilitySliderComponent, FacilityViewDetailComponent, InfrastructureDetailsComponent,
    BasicOperationComponent, FacilityAlarmComponent, FacilityViewDetailLogComponent,
    FacilityImgViewComponent, IntelligentEntranceGuardComponent,
    ConfigurationStrategyComponent, FacilityLogComponent, SetAreaDeviceComponent],
  imports: [
    SharedModule,
    NgxEchartsModule,
    SwiperModule,
    RouterModule.forChild(ROUTER_CONFIG)
  ],
  providers: [FacilityUtilService]
})
export class FacilityModule {
}

