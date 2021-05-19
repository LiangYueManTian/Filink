import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormItem} from '../../../../shared-module/component/form/form-config';
import {FormOperate} from '../../../../shared-module/component/form/form-opearte.service';
import {NzI18nService} from 'ng-zorro-antd';
import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs';
import {Result} from '../../../../shared-module/entity/result';
import {MD5Service} from '../../../../shared-module/util/md5.service';
import {UserService} from '../../../../core-module/api-service/user/user-manage';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {SessionUtil} from '../../../../shared-module/util/session-util';
import { Router } from '@angular/router';

@Component({
  selector: 'app-update-password',
  templateUrl: './update-password.component.html',
  styleUrls: ['./update-password.component.scss']
})
export class UpdatePasswordComponent implements OnInit {

  @Output() cancelEvent = new EventEmitter();

  formStatus: FormOperate;
  formColumn: FormItem[] = [];
  language: any; // 国际化
  constructor(
    private $nzI18n: NzI18nService,
    private $MD5Service: MD5Service,
    private $userService: UserService,
    private $message: FiLinkModalService,
    private $router: Router
  ) {
    this.language = $nzI18n.getLocale();
  }

  ngOnInit() {
    this.formColumn = [
      {
        label: '原始密码',
        key: 'password',
        width: 500,
        type: 'input',
        inputType: 'password',
        require: true,
        rule: [{required: true}],
        asyncRules: [],
        col: 20,
      },
      {
        label: '新密码',
        key: 'newPassword',
        type: 'input',
        width: 500,
        inputType: 'password',
        require: true,
        rule: [{required: true}],
        asyncRules: [],
        col: 20
      },
      {
        label: '确认密码',
        width: 500,
        require: true,
        key: 'confirmNewPassword',
        inputType: 'password',
        type: 'input',
        rule: [{required: true}],
        col: 20,
        asyncRules: [
          {
            asyncRule: (control: FormControl) => {
              return Observable.create(observer => {
                const data = this.formStatus.getData();
                if (control.value === data.newPassword) {
                  observer.next(null);
                  observer.complete();
                } else {
                  observer.next({error: true, duplicated: true});
                  observer.complete();
                }
              });
            },
            asyncCode: 'duplicated', msg: '确认密码和新密码不一致！'
          }
        ]
      },
    ];
  }

  /**
   * 通用表单方法
   * param event
   */
  formInstance(event) {
    this.formStatus = event.instance;
  }

  /**
   * 确定
   */
  handleOk() {
    const data = this.formStatus.getData();
    const sendData = {
        userId: SessionUtil.getUserId(),
         token: sessionStorage.getItem('token'),
        newPWD: data.newPassword,
        oldPWD: data.password,
    confirmPWD: data.confirmNewPassword
    };
    this.$userService.modifyPassword(sendData).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.info(result.msg);
        if (sessionStorage.getItem('userInfo')) {
          const userInfo = JSON.parse(sessionStorage.getItem('userInfo'));
          const data_ = {
            userid: userInfo.id,
            token: sessionStorage.getItem('token')
          };
          this.$userService.logout(data_).subscribe((res: Result) => {
          });
        }
        sessionStorage.clear();
        this.$router.navigate(['']).then();
      } else {
        this.$message.error(result.msg);
      }
    });
  }

  /**
   * 取消
   */
  handleCancel() {
    this.cancelEvent.emit();
  }
}
