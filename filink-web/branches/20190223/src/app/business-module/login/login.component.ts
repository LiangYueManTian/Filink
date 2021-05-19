import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  // 是否是用户登入  切换登入方式
  autoLoginType = true;
  loginTitle = '手机登入在这';
  constructor(private fb: FormBuilder, private router: Router) {
  }

  ngOnInit(): void {
  }

  changeAutoLoginType() {
    this.autoLoginType = !this.autoLoginType;
    if (this.autoLoginType) {
      this.loginTitle = '手机登入在这';
    } else {
      this.loginTitle = '用户登入在这';
    }
  }
}
