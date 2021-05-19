import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ReactiveFormsModule} from '@angular/forms';
import {NoopInterceptor} from './interceptor/noop-interceptor';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {SocketIO, WebSocketFactory} from './websocket/websocket.service';
import {MapStoreService} from './store/map.store.service';
import {AreaService, FacilityService, LoginService} from './api-service';
import { UserService } from './api-service';
import { AlarmService } from './api-service';
import {MapService} from './api-service';
import {MenuManageService} from './api-service';
import {LogManageService} from './api-service/system-setting/log-manage/log-manage.service';
import {AgreementManageService} from './api-service/system-setting/agreement-manage/agreement-manage.service';
import {LockService} from './api-service/lock';
import {RoleDeactivateGuardService} from './guard-service/role-deactivate-guard.service';
import {NativeWebsocketImplService} from './websocket/native-websocket-impl.service';
import {AlarmStoreService} from './store/alarm.store.service';

const SERVICE_PROVIDERS = [
  {provide: MapStoreService, useClass: MapStoreService},
  {provide: AlarmStoreService, useClass: AlarmStoreService},
  {provide: AreaService, useClass: AreaService},
  {provide: FacilityService, useClass: FacilityService},
  {provide: UserService, useClass: UserService },
  {provide: AlarmService, useClass: AlarmService },
  {provide: MapService, useClass: MapService},
  {provide: LockService, useClass: LockService},
  MenuManageService,
  LogManageService,
  AgreementManageService,
  LoginService,
  NativeWebsocketImplService,
  RoleDeactivateGuardService
];


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
  ],
  exports: [ReactiveFormsModule],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: NoopInterceptor, multi: true },
    { provide: SocketIO, useFactory: () => WebSocketFactory.forRoot() },
    ...SERVICE_PROVIDERS
  ]
})
export class CoreModule {
}
