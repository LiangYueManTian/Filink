import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {Result} from '../../../shared-module/entity/result';
import {CookieService} from 'ngx-cookie-service';
import {LoginService} from '../../../core-module/api-service/login';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';
import {MD5Service} from '../../../shared-module/util/md5.service';
import {CodeValidator} from 'code-validator';
import {DomSanitizer, SafeUrl} from '@angular/platform-browser';
import {Observable} from 'rxjs';
import {NzI18nService} from 'ng-zorro-antd';
import {Base64} from '../../../shared-module/util/base64';
import {SessionUtil} from '../../../shared-module/util/session-util';

@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.scss']
})
export class UserLoginComponent implements OnInit, AfterViewInit, OnDestroy {
  base64: SafeUrl;
  value: string;
  cv = new CodeValidator({
    width: 95,
    height: 35,
    length: 4
  });

  validateForm: FormGroup;
  // 路由跳转链接
  link = '/business/system/about';
  // 登入loading
  loginLoading = false;
  // 国际化
  language: any;
  // 用户定时监控
  loginTimer = null;

  constructor(private fb: FormBuilder,
              private $message: FiLinkModalService,
              private $cookieService: CookieService,
              private $loginService: LoginService,
              private $MD5Service: MD5Service,
              private $sanitizer: DomSanitizer,
              private $nzI18n: NzI18nService,
              private router: Router) {
    this.language = $nzI18n.getLocale();
    this.initFormData();
  }

  ngOnInit() {
    // 如果token存在  则直接进入首页
    if (SessionUtil.getToken()) {
      if (localStorage.getItem('menuList') && localStorage.getItem('menuList') !== 'undefined') {
        this.findLink(JSON.parse(localStorage.getItem('menuList')));
      }
      this.router.navigate([this.link]).then();
      return;
    } else {
      this.random();
    }
  }

  ngAfterViewInit(): void {
    this.random();
    this.loginTimer = setInterval(() => {
      // 如果token存在  则直接进入首页
      if (SessionUtil.getToken()) {
        if (localStorage.getItem('menuList') && localStorage.getItem('menuList') !== 'undefined') {
          this.findLink(JSON.parse(localStorage.getItem('menuList')));
        }
        clearInterval(this.loginTimer);
        this.loginTimer = null;
        this.router.navigate([this.link]).then();
        return;
      }
    }, 1000);
  }

  /**
   * 切换验证码
   */
  random() {
    const res = this.cv.random();
    this.base64 = this.$sanitizer.bypassSecurityTrustUrl(res.base);
    this.value = res.value;
  }

  /**
   * 初始化表单数据
   */
  initFormData(userName?, password?) {
    if (userName || password) {
      // this.random();
    } else {
      userName = null;
      password = null;
      // 判断是否有记住密码
      if (this.$cookieService.get('filink-user') && this.$cookieService.get('filink-password')) {
        userName = Base64.decode(this.$cookieService.get('filink-user'));
        password = Base64.decode(this.$cookieService.get('filink-password'));
      }
    }
    this.validateForm = this.fb.group({
      userName: [userName, [Validators.required]],
      password: [password, [Validators.required]],
      authCode: ['', [Validators.required], this.createCodeAsyncRules()],
      remember: [true]
    });
  }

  /**
   * 失去焦点初始化  防止按钮被挤下去
   */
  initHint(type) {
    this.validateForm = this.fb.group({
      userName: [this.validateForm.getRawValue().userName, [Validators.required]],
      password: [this.validateForm.getRawValue().password, [Validators.required]],
      authCode: [this.validateForm.getRawValue().authCode, [Validators.required], this.createCodeAsyncRules()],
      remember: [this.validateForm.getRawValue().remember]
    });
    if (type === 'authCode') {
      this.validateForm.controls['authCode'].markAsDirty();
    }
  }

  /**
   * 登入提交
   */
  submit() {
    // 解决登入无client信息问题
    localStorage.removeItem('token');
    const loginDate = new FormData();
    loginDate.append('username', this.validateForm.getRawValue().userName);
    loginDate.append('password', this.validateForm.getRawValue().password);
    this.loginLoading = true;
    this.$loginService.login(loginDate).subscribe((result: Result) => {
      this.loginLoading = false;
      if (result.code === 0) {
        if (result.data.code === 0) {
          this.remember();
          SessionUtil.setToken(result.data.data.accessToken.value, result.data.data.loginInfo.expireTime);
          localStorage.setItem('userInfo', JSON.stringify(result.data.data.loginInfo));
          localStorage.setItem('menuList', JSON.stringify(result.data.data.showMenuTemplate.menuInfoTrees));
          this.findLink(result.data.data.showMenuTemplate.menuInfoTrees);
          this.router.navigate([this.link]).then();
        } else {
          this.initFormData(this.validateForm.getRawValue().userName, this.validateForm.getRawValue().password);
          this.random();
          this.$message.warning(result.data.msg);
        }
      } else {
        this.initFormData(this.validateForm.getRawValue().userName, this.validateForm.getRawValue().password);
        this.$message.error(result.msg);
        this.random();
      }
    }, () => {
      this.loginLoading = false;
      this.random();
    });
  }

  /**
   * 判断是否记住密码
   */
  remember() {
    if (this.validateForm.getRawValue().remember) {
      this.$cookieService.set('filink-user', Base64.encode(this.validateForm.getRawValue().userName));
      this.$cookieService.set('filink-password', Base64.encode(this.validateForm.getRawValue().password));
    } else {
      this.$cookieService.delete('filink-user');
      this.$cookieService.delete('filink-password');
    }
  }

  /**
   * 寻找跳转链接
   */
  findLink(menuInfoTrees) {
    if (menuInfoTrees) {
      for (let i = 0; i < menuInfoTrees.length; i++) {
        if (menuInfoTrees[i].children && menuInfoTrees[i].children.length > 0) {
          this.findLink(menuInfoTrees[i].children);
        } else {
            this.link = menuInfoTrees[i].menuHref;
          break;
        }
      }
    }
  }

  /**
   * 验证码异步效验
   */
  createCodeAsyncRules() {
    const asyncRules = [];
    asyncRules.push((control: FormControl) => {
      return Observable.create(observer => {
        if (control.value.toLocaleUpperCase() === this.value) {
          observer.next(null);
          observer.complete();
        } else {
          observer.next({error: true, duplicated: true});
          observer.complete();
        }
      });
    });
    return asyncRules;
  }

  /**
   * 组件销毁时清除定时器
   */
  ngOnDestroy() {
    clearInterval(this.loginTimer);
    this.loginTimer = null;
  }
}
