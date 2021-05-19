import {Routes} from '@angular/router';
import {SystemSettingComponent} from './system-setting.component';
import {MemuManagementComponent} from './memu-management/memu-management.component';
import {MenuAddComponent} from './memu-management/menu-add/menu-add.component';
import {LogManagementComponent} from './log-management/log-management.component';
import {AgreementManagementComponent} from './agreement-management/agreement-management.component';
import {FacilityAgreementComponent} from './agreement-management/facility-agreement/facility-agreement.component';
import {SecurityPolicyComponent} from './security-policy/security-policy.component';
import {AccessControlComponent} from './security-policy/access-control/access-control.component';
import {IdSecurityPolicyComponent} from './security-policy/id-security-policy/id-security-policy.component';
import {SystemParameterComponent} from './system-parameter/system-parameter.component';


export const ROUTER_CONFIG: Routes = [
  {
    path: '',
    component: SystemSettingComponent,
    children: [
      {
        path: 'menu',
        data: {
          breadcrumb: [{label: 'systemSetting'}, {label: 'menuManagement'}]
        },
        component: MemuManagementComponent
      },
      {
        path: 'menu-add',
        data: {
          breadcrumb: [{label: 'systemSetting'},
                       {label: 'menuManagement', url: '/business/system/menu'},
            {label: 'menuAdd'}]
        },
        component: MenuAddComponent
      },
      {
        path: 'menu-update/:id',
        data: {
          breadcrumb: [{label: 'systemSetting'},
            {label: 'menuManagement', url: '/business/system/menu'},
            {label: 'menuUpdate'}]
        },
        component: MenuAddComponent
      },
      {
        path: 'log',
        data: {
          breadcrumb: [
            {label: 'systemSetting'},
            {label: 'logManagement'},
            {label: 'operateLog'}]
        },
        component: LogManagementComponent
      },
      {
        path: 'log/security',
        data: {
          breadcrumb: [
            {label: 'systemSetting'},
            {label: 'logManagement'},
            {label: 'securityLog'}
          ]
        },
        component: LogManagementComponent
      },
      {
        path: 'log/system',
        data: {
          breadcrumb: [
            {label: 'systemSetting'},
            {label: 'logManagement'},
            {label: 'systemLog'}]
        },
        component: LogManagementComponent
      },
      {
        path: 'agreement',
        component: AgreementManagementComponent,
        children: [
          {
            path: 'facility',
            component: FacilityAgreementComponent,
            data: {
              breadcrumb: [{label: 'systemSetting'},
                           {label: 'agreementManagement'},
                           {label: 'facilityAgreement'}]
            },
          }
        ]
      },
      {
        path: 'security-policy',
        component: SecurityPolicyComponent,
        children: [
          {
            path: 'access-control',
            component: AccessControlComponent,
            data: {
              breadcrumb: [{label: 'systemSetting'},
                {label: 'securityPolicy'}]
            },
          },
          {
            path: 'account',
            component: IdSecurityPolicyComponent,
            data: {
              breadcrumb: [{label: 'systemSetting'},
                {label: 'securityPolicy'}]
            },
          },
          {
            path: 'password',
            component: IdSecurityPolicyComponent,
            data: {
              breadcrumb: [{label: 'systemSetting'},
                {label: 'securityPolicy'}]
            },
          }
        ]
      },
      {
        path: 'system-parameter/:setting-type',
        component: SystemParameterComponent,
        data: {
          breadcrumb: [{label: 'systemSetting'},
            {label: 'systemParameter'}]
        },
      }
    ]
  },
];