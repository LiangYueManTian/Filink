import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {RoleDeactivateGuardService} from './core-module/guard-service/role-deactivate-guard.service';

const routes: Routes = [
  {path: '', pathMatch: 'full', redirectTo: '/login'},
  {path: 'login', loadChildren: './business-module/login/login.module#LoginModule'},
  {path: 'business', loadChildren: './business-module/business.module#BusinessModule', canActivateChild: [RoleDeactivateGuardService]},
  {path: '**', redirectTo: 'business/index'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
