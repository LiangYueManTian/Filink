import { NgModule } from '@angular/core';
import { SystemSettingComponent } from './system-setting.component';
import { MemuManagementComponent } from './memu-management/memu-management.component';
import {SharedModule} from '../../shared-module/shared-module.module';

import {RouterModule} from '@angular/router';
import {ROUTER_CONFIG} from './system-setting.routes';
import {ColumnConfigService} from './column-config.service';
import { MenuAddComponent } from './memu-management/menu-add/menu-add.component';
import { MenuTreeComponent } from './memu-management/menu-tree/menu-tree.component';
import {MenuTreeOperateService} from './memu-management/menu-tree/menu-tree-operate.service';
import { LogManagementComponent } from './log-management/log-management.component';
import { FacilityAgreementComponent } from './agreement-management/facility-agreement/facility-agreement.component';
import { AgreementManagementComponent } from './agreement-management/agreement-management.component';
import { SecurityPolicyComponent } from './security-policy/security-policy.component';
import { AccessControlComponent } from './security-policy/access-control/access-control.component';
import { AccessControlDetialComponent } from './security-policy/access-control/access-control-detial/access-control-detial.component';
import { IdSecurityPolicyComponent } from './security-policy/id-security-policy/id-security-policy.component';
import { SystemParameterComponent } from './system-parameter/system-parameter.component';

@NgModule({
  declarations: [
    SystemSettingComponent,
    MemuManagementComponent,
    MenuAddComponent,
    MenuTreeComponent,
    LogManagementComponent,
    FacilityAgreementComponent,
    AgreementManagementComponent,
    SecurityPolicyComponent,
    AccessControlComponent,
    AccessControlDetialComponent,
    IdSecurityPolicyComponent,
    SystemParameterComponent],
  imports: [
    SharedModule,
    RouterModule.forChild(ROUTER_CONFIG)
  ],
  providers: [ColumnConfigService, MenuTreeOperateService]
  ,
})

export class SystemSettingModule { }
