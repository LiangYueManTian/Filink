import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {CoreModule} from '../../core-module/core-module.module';
import {SharedModule} from '../../shared-module/shared-module.module';
import {SmartLabelComponent} from './smart-label.component';
import {ROUTER_CONFIG} from './smart-label.routes';


@NgModule({
  declarations: [SmartLabelComponent],
  imports: [
    CommonModule,
    CoreModule,
    SharedModule,
    RouterModule.forChild(ROUTER_CONFIG)
  ]
})
export class SmartLabelModule {
}
