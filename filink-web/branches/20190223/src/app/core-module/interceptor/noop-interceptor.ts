/**
 * @author xiaoconghu
 * @createTime 2018/9/20
 */
import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {HttpErrorResponse, HttpHandler, HttpHeaderResponse, HttpInterceptor, HttpRequest, HttpResponse} from '@angular/common/http';
import {catchError, retry} from 'rxjs/internal/operators';
import {Observable, of, throwError} from 'rxjs';

import {basicAuthConfig} from '../basic-auth';
import {Base64} from '../../shared-module/util/base64';
import {FiLinkModalService} from '../../shared-module/service/filink-modal/filink-modal.service';
import {SessionUtil} from '../../shared-module/util/session-util';
import {NzI18nService} from 'ng-zorro-antd';
import {NativeWebsocketImplService} from '../websocket/native-websocket-impl.service';

@Injectable()
export class NoopInterceptor implements HttpInterceptor {

  language: any;
  // 防止多次弹框
  timer = null;
  // 是否是弹框
  flag = true;
  constructor(private $router: Router,
              private $nzI18n: NzI18nService,
              private websocketService: NativeWebsocketImplService,
              private $message: FiLinkModalService) {
    this.language = this.$nzI18n.getLocale();
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<| HttpHeaderResponse | HttpResponse<any>> {
    let req = null;
    if (sessionStorage.getItem('token')) {
      req = request.clone({setHeaders: {
        Authorization: sessionStorage.getItem('token'),
        userId: SessionUtil.getUserId(),
        roleId: '',
        roleName: '',
        userName: Base64.encode(SessionUtil.getUserInfo().userNickname)
      }});
    } else {
      req = request.clone({setHeaders: {Authorization:
            `Basic ${this.createBasicAuth(basicAuthConfig.username, basicAuthConfig.password)}`}});
    }
    return next.handle(req).pipe(
      retry(1),
      catchError((err: HttpErrorResponse) => this.handleData(err))
    );
  }

  /**
   * 生成basicAuth
   * param name
   * param pass
   */
   createBasicAuth(name, pass) {
    return btoa(encodeURIComponent(`${name}:${pass}`).replace(/%([0-9A-F]{2})/g,
      function(match, p1) {
        return String.fromCharCode(Number('0x' + p1));
      }));
  }

  private handleData(event: HttpResponse<any> | HttpErrorResponse): Observable<any> {
    // 业务处理：一些通用操作
    let errorStr = '';
    switch (event.status) {
      case 401:
        errorStr = this.language.httpMsg.noLogin;
        // 退出登入则清除用户信息切断开websocket
        sessionStorage.clear();
        this.websocketService.close();
        this.$router.navigate(['/login']).then();
        break;
      case 404:
        errorStr =  this.language.httpMsg.notFound;
        break;
      default:
        errorStr =  this.language.httpMsg.serverError;
    }
    if (this.flag) {
      this.$message.error(errorStr);
      this.flag = false;
      setTimeout(() => {
        this.flag = true;
      }, 3 * 1000);
    }
    return throwError(errorStr);
  }
}

