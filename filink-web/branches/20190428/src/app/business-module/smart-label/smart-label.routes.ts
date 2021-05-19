import {Routes} from '@angular/router';
import {SmartLabelComponent} from './smart-label.component';
import {RealisticPictureComponent} from './realistic-picture/realistic-picture.component';
import {TempletPictureComponent} from './templet-picture/templet-picture.component';


export const ROUTER_CONFIG: Routes = [
  {
    path: '',
    component: SmartLabelComponent,
    children: [
      {
        path: 'picture',
        component: RealisticPictureComponent,
      },
      {
        path: 'templet',
        component: TempletPictureComponent,
      }
    ]
  }
];
