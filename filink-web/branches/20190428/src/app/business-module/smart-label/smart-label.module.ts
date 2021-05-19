import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {CoreModule} from '../../core-module/core-module.module';
import {SharedModule} from '../../shared-module/shared-module.module';
import {ROUTER_CONFIG} from './smart-label.routes';
import {SmartLabelComponent} from './smart-label.component';
import {RealisticPictureComponent} from './realistic-picture/realistic-picture.component';
import { TempletPictureComponent } from './templet-picture/templet-picture.component';


@NgModule({
  declarations: [SmartLabelComponent, RealisticPictureComponent, TempletPictureComponent],
  imports: [
    CommonModule,
    CoreModule,
    SharedModule,
    RouterModule.forChild(ROUTER_CONFIG)
  ]
})
export class SmartLabelModule {
}
