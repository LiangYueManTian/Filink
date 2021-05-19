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
  IntelligentEntranceGuardComponent,
} from './facility-manage';
import {PartsListComponent} from './parts-manage/parts-list/parts-list.component';
import {PartsDetailsComponent} from './parts-manage/parts-detail/parts-detail.component';
import {FacilityUtilService} from './facility-util.service';
import {FacilitySliderComponent} from './facility-manage/facility-list/facility-slider/facility-slider.component';
import {NgxEchartsModule} from 'ngx-echarts';
import {SwiperModule} from 'ngx-swiper-wrapper';
import {SetAreaDeviceComponent} from './area-manage/set-area-device/set-area-device.component';
import {AreaManageComponent} from './area-manage/area-manage.component';
import {PhotoViewerComponent} from './facility-manage/photo-viewer/photo-viewer.component';
import {TimerSelectorComponent} from './facility-manage/photo-viewer/timer-selector/timer-selector.component';
import {PhotoViewFilterComponent} from './facility-manage/photo-viewer/photo-view-filter/photo-view-filter.component';
import {ImageListComponent} from './facility-manage/photo-viewer/image-list/image-list.component';
import {FacilityWorkOrderComponent} from './facility-manage/facility-view-detail/facility-work-order/facility-work-order.component';
import {IndexModule} from '../index/index.module';
import {GuardIconClassPipe} from './facility-manage/facility-view-detail/intelligent-entrance-guard/guard-icon-class.pipe';

@NgModule({
  declarations: [FacilityComponent, FacilityListComponent,
    AreaManageComponent,
    AreaListComponent, AreaDetailComponent, FacilityDetailComponent,
    FacilitySliderComponent, FacilityViewDetailComponent, InfrastructureDetailsComponent,
    BasicOperationComponent, FacilityAlarmComponent, FacilityViewDetailLogComponent,
    FacilityImgViewComponent, IntelligentEntranceGuardComponent,
    ConfigurationStrategyComponent, FacilityLogComponent, SetAreaDeviceComponent, PartsListComponent, PartsDetailsComponent,
    PhotoViewerComponent, TimerSelectorComponent, PhotoViewFilterComponent, ImageListComponent, FacilityWorkOrderComponent,
    GuardIconClassPipe],
  imports: [
    SharedModule,
    NgxEchartsModule,
    SwiperModule,
    IndexModule,
    RouterModule.forChild(ROUTER_CONFIG)
  ],
  providers: [FacilityUtilService]
})
export class FacilityModule {
}

