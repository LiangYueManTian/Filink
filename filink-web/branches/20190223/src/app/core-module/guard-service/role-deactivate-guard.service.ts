/**
 * Created by WH1709040 on 2018/2/12.
 */

import {Injectable} from '@angular/core';
import {
  CanActivate, Router,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  CanActivateChild
} from '@angular/router';
import {SessionUtil} from '../../shared-module/util/session-util';

@Injectable()
export class RoleDeactivateGuardService implements CanActivate, CanActivateChild {
  constructor(private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (SessionUtil.getUserId()) {
      return true;
    }
    this.router.navigate(['/']).then();
    // let url: string = state.url;
    // if (CommonUtils.isUserLogin()) {
    //     let flag: boolean = false;
    //     let roleList = CommonUtils.getRoleList();
    //     roleList.forEach(item => {
    //         if (item.url) {
    //             if (url.includes(item.url)) {
    //                 flag =  true;
    //             }
    //         }
    //     });
    //     return flag;
    // } else {
    //     this.router.navigate(['/login']).then(() => {});
    //     return false;
    // }
    // return true;
  }

  canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    return this.canActivate(route, state);
  }


}
