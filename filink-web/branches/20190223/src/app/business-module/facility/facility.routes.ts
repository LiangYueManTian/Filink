import {FacilityComponent} from './facility.component';
import {Routes} from '@angular/router';
import {AreaListComponent} from './area-manage/area-list/area-list.component';
import {AreaDetailComponent} from './area-manage/area-detail/area-detail.component';
import {
  ConfigurationStrategyComponent,
  FacilityDetailComponent,
  FacilityListComponent,
  FacilityLogComponent,
  FacilityViewDetailComponent
} from './facility-manage';
import {SetAreaDeviceComponent} from './area-manage/set-area-device/set-area-device.component';

/**
 * Created by xiaoconghu on 2019/1/3.
 */
export const ROUTER_CONFIG: Routes = [
  {
    path: '',
    component: FacilityComponent,
    children: [
      {
        path: 'area-list',
        component: AreaListComponent,
        data: {
          breadcrumb: [{label: 'facilityManage'}, {label: 'areaList'}]
        }
      },
      {
        path: 'area-detail/:type',
        component: AreaDetailComponent,
        data: {
          breadcrumb: [{label: 'facilityManage'}, {label: 'areaList', url: 'area-list'}, {label: 'area'}]
        }
      },
      {
        path: 'facility-list',
        component: FacilityListComponent,
        data: {
          breadcrumb: [{label: 'facilityManage'}, {label: 'facilityList'}]
        }
      },
      {
        path: 'facility-detail/:type',
        component: FacilityDetailComponent,
        data: {
          breadcrumb: [{label: 'facilityManage'}, {label: 'facilityList', url: 'facility-list'}, {label: 'facility'}]
        }
      },
      {
        path: 'facility-detail-view',
        component: FacilityViewDetailComponent,
        data: {
          breadcrumb: [
            {label: 'facilityManage'},
            {label: 'facilityList', url: 'facility-list'},
            {label: 'facilityView'}
          ]
        }
      },
      {
        path: 'facility-log',
        component: FacilityLogComponent,
        data: {
          breadcrumb: [{label: 'facilityManage'}, {label: 'facilityLog'}]
        }
      },
      {
        path: 'set-area-device',
        component: SetAreaDeviceComponent
      },
      {
        path: 'facility-Config',
        component: ConfigurationStrategyComponent,
        data: {
          breadcrumb: [{label: 'facilityManage'},
            {label: 'facilityList', url: 'facility-list'}, {label: 'facilityConfig'}]
        }
      }
    ]
  }
];
