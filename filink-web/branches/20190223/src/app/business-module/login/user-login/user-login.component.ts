import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {MenuManageService} from '../../../core-module/api-service/system-setting';
import {Result} from '../../../shared-module/entity/result';
import {CookieService} from 'ngx-cookie-service';
import {LoginService} from '../../../core-module/api-service/login';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';
import {MD5Service} from '../../../shared-module/util/md5.service';
import { CodeValidator } from 'code-validator';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.scss']
})
export class UserLoginComponent implements OnInit {
  base64: SafeUrl;
  value: string;
  cv = new CodeValidator({
    width: 95,
    height: 35,
    length: 4
  });

  validateForm: FormGroup = new FormGroup({});
  // 路由跳转链接
  link: '';
  // 登入loading
  loginLoading = false;


  constructor(private fb: FormBuilder,
              private $message: FiLinkModalService,
              private $cookieService: CookieService,
              private $loginService: LoginService,
              private $MD5Service: MD5Service,
              private $sanitizer: DomSanitizer,
              private router: Router) {
  }

  ngOnInit() {
    // 如果token存在  则直接进入首页
    if (sessionStorage.getItem('token')) {
      this.router.navigate(['business/index']).then();
      return;
    }
    this.initFormData();
    this.random();
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
  initFormData (userName?, password?) {
    if (userName || password) {
      this.random();
    } else {
      userName = null;
      password = null;
      // 判断是否有记住密码
      if (this.$cookieService.get('filink-user') &&  this.$cookieService.get('filink-password')) {
        userName = this.$cookieService.get('filink-user');
        password = this.$cookieService.get('filink-password');
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
    for (const i of Object.keys(this.validateForm.controls)) {
      this.validateForm.controls[i].markAsDirty();
      this.validateForm.controls[i].updateValueAndValidity();
    }

    const loginDate = new FormData();
    loginDate.append('username', this.validateForm.getRawValue().userName);
    loginDate.append('password', this.validateForm.getRawValue().password);
    this.loginLoading = true;
    this.$loginService.login(loginDate).subscribe((result: Result) => {
      this.loginLoading = false;
      if (result.code === 0) {
        if (result.data.code === 0) {
          this.remember();
          sessionStorage.setItem('token', result.data.data.accessToken.value);
          sessionStorage.setItem('userInfo', JSON.stringify(result.data.data.loginInfo));
          sessionStorage.setItem('menuList', JSON.stringify(result.data.data.showMenuTemplate.menuInfoTrees));
          this.findLink(result.data.data.showMenuTemplate.menuInfoTrees);
          this.router.navigate([this.link]).then();
        } else {
          this.initFormData(this.validateForm.getRawValue().userName, this.validateForm.getRawValue().password);
          this.$message.warning(result.data.msg);
        }
      } else {
        this.initFormData(this.validateForm.getRawValue().userName, this.validateForm.getRawValue().password);
        this.$message.error(result.msg);
      }
    }, () => {
      this.loginLoading = false;
    });
  }

  /**
   * 判断是否记住密码
   */
  remember() {
    if (this.validateForm.getRawValue().remember) {
      this.$cookieService.set('filink-user', this.validateForm.getRawValue().userName);
      this.$cookieService.set('filink-password', this.validateForm.getRawValue().password);
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
          if (!this.link) {
            this.link = menuInfoTrees[i].menuHref;
          }
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
}
